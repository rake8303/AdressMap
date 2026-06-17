<template>
  <view class="page">
    <view class="toolbar">
      <view class="toolbar__title">販売店一覧</view>
      <view class="toolbar__meta">{{ currentFlowLabel }}</view>
    </view>

    <view class="search-panel">
      <input v-model="query.keyword" class="search-input" placeholder="店舗名・住所で検索" confirm-type="search" @confirm="reloadList" />
      <scroll-view class="chip-row" scroll-x>
        <view class="chip" :class="{ 'chip--active': selectedRegions.length === 0 }" @click="selectAllRegions">全エリア</view>
        <view
          v-for="region in regionOptions"
          :key="region"
          class="chip"
          :class="{ 'chip--active': selectedRegions.includes(region) }"
          @click="toggleRegion(region)"
        >{{ region }}</view>
      </scroll-view>
    </view>

    <scroll-view
      class="list-scroll"
      scroll-y
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="reloadList"
      @scrolltolower="loadMore"
    >
      <view v-if="loading && outletList.length === 0" class="empty-box">販売店を読み込み中です...</view>
      <view v-else-if="outletList.length === 0" class="empty-box">条件に一致する販売店がありません。</view>
      <view v-else class="list-stack">
        <view v-for="item in outletList" :key="item.id" class="outlet-card" @click="openDetail(item.id)">
          <view class="outlet-card__header">
            <view>
              <view class="outlet-card__title">{{ item.jpCompanyName || '名称未設定' }}</view>
              <view class="outlet-card__sub">{{ item.region || '未設定エリア' }} | {{ item.primaryFlow || '商流未設定' }}</view>
            </view>
            <view class="outlet-card__badge">{{ item.latestSalesText }}</view>
          </view>
          <view class="outlet-card__address">{{ item.headquartersAddress || '住所未設定' }}</view>
          <view class="outlet-card__footer">
            <text>{{ item.latestHistoryText }}</text>
            <text class="outlet-card__link">詳細</text>
          </view>
        </view>
      </view>
      <view v-if="outletList.length > 0" class="load-more">{{ hasMore ? 'さらに読み込む' : '最後まで表示しました' }}</view>
    </scroll-view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { listHistory } from '@/api/system/history'
import { listMonthlySales } from '@/api/system/monthlySales'
import { listOutlet } from '@/api/system/outlet'
import { useUserStore } from '@/store'
import {
  canViewAllData,
  formatHistoryRemark,
  formatNumber,
  formatShortDateTime,
  getCurrentBusinessFlowRole,
  getLatestHistoryRecord,
  getLatestMonthlySalesRecord,
  getPrimaryBusinessFlow
} from '@/utils/outlet'

const userStore = useUserStore()
const loading = ref(false)
const refreshing = ref(false)
const hasMore = ref(true)
const pageNum = ref(1)
const pageSize = 20
const total = ref(0)
const query = ref({ keyword: '' })
const selectedRegions = ref([])
const regionOptions = ref([])
const outletList = ref([])
const filteredOutlets = ref([])

const currentFlowLabel = computed(() => {
  const flow = getCurrentBusinessFlowRole(userStore)
  return canViewAllData(userStore) ? '参照範囲: 全商流' : `参照範囲: ${flow || '担当商流'}`
})

function mergeOutletData(outlets, histories, salesRows) {
  const historyMap = new Map()
  histories.forEach(item => {
    const key = String(item.outletId || '')
    if (!historyMap.has(key)) historyMap.set(key, [])
    historyMap.get(key).push(item)
  })

  const salesMap = new Map()
  salesRows.forEach(item => {
    const key = String(item.outletId || '')
    if (!salesMap.has(key)) salesMap.set(key, [])
    salesMap.get(key).push(item)
  })

  return outlets.map(item => {
    const latestHistory = getLatestHistoryRecord(historyMap.get(String(item.id || '')))
    const latestSales = getLatestMonthlySalesRecord(salesMap.get(String(item.id || '')))
    return {
      ...item,
      primaryFlow: getPrimaryBusinessFlow(item.agentList),
      latestSalesText: latestSales ? `${latestSales.salesMonth} / ${formatNumber(latestSales.quantity)} 台` : '販売実績なし',
      latestHistoryText: latestHistory
        ? `${formatShortDateTime(latestHistory.updatedAt || latestHistory.createdAt)} | ${formatHistoryRemark(latestHistory.remark)}`
        : '訪問履歴なし'
    }
  })
}

async function enrichOutlets(rows = []) {
  let histories = []
  let salesRows = []
  if (rows.length > 0) {
    const outletIds = rows.map(item => String(item.id || ''))
    const [historyRes, salesRes] = await Promise.all([
      listHistory({ pageNum: 1, pageSize: 500 }).catch(() => ({ rows: [] })),
      listMonthlySales({ pageNum: 1, pageSize: 800 }).catch(() => ({ rows: [] }))
    ])
    histories = (historyRes.rows || []).filter(item => outletIds.includes(String(item.outletId || '')))
    salesRows = (salesRes.rows || []).filter(item => outletIds.includes(String(item.outletId || '')))
  }
  return mergeOutletData(rows, histories, salesRows)
}

async function fetchList(reset = false) {
  if (loading.value) return
  loading.value = true
  if (reset) {
    pageNum.value = 1
    hasMore.value = true
  }

  try {
    if (reset) {
      const allVisibleOutlets = await fetchAllVisibleOutlets()
      regionOptions.value = [...new Set(allVisibleOutlets.map(item => item.region).filter(Boolean))]
      filteredOutlets.value = filterOutletsByRegions(allVisibleOutlets)
      total.value = filteredOutlets.value.length
      outletList.value = []
      pageNum.value = 1
    }

    const start = (pageNum.value - 1) * pageSize
    const end = start + pageSize
    const pageRows = filteredOutlets.value.slice(start, end)
    const merged = await enrichOutlets(pageRows)
    outletList.value = reset ? merged : outletList.value.concat(merged)
    hasMore.value = end < filteredOutlets.value.length
    if (hasMore.value) pageNum.value += 1
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function reloadList() {
  refreshing.value = true
  return fetchList(true)
}

function loadMore() {
  if (!hasMore.value) return
  fetchList(false)
}

function filterOutletsByRegions(rows = []) {
  if (selectedRegions.value.length === 0) {
    return rows
  }
  return rows.filter(item => selectedRegions.value.includes(item.region))
}

async function fetchAllVisibleOutlets() {
  const collected = []
  const requestPageSize = 200
  let requestPageNum = 1
  let requestTotal = 0

  do {
    const response = await fetchOutletPage({
      pageNum: requestPageNum,
      pageSize: requestPageSize,
      jpCompanyName: query.value.keyword || undefined
    })
    const rows = response.rows || []
    requestTotal = Number(response.total || 0)
    collected.push(...rows)
    if (rows.length < requestPageSize) break
    requestPageNum += 1
  } while (collected.length < requestTotal)

  return collected
}

function fetchOutletPage(params) {
  return listOutlet(params)
}

function selectAllRegions() {
  selectedRegions.value = []
  reloadList()
}

function toggleRegion(region) {
  if (selectedRegions.value.includes(region)) {
    selectedRegions.value = selectedRegions.value.filter(item => item !== region)
  } else {
    selectedRegions.value = [...selectedRegions.value, region]
  }
  reloadList()
}

function openDetail(id) {
  uni.navigateTo({ url: `/pages/outlet/detail?id=${id}` })
}

onMounted(() => {
  fetchList(true)
})
</script>

<style lang="scss" scoped>
page {
  background: #f3f4f6;
}

.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.toolbar {
  padding: 28rpx 24rpx 16rpx;
  background: #f3f4f6;
}

.toolbar__title {
  font-size: 40rpx;
  font-weight: 700;
  color: #111827;
}

.toolbar__meta {
  margin-top: 8rpx;
  color: #6b7280;
  font-size: 24rpx;
}

.search-panel {
  padding: 0 24rpx 18rpx;
}

.search-input {
  height: 84rpx;
  border-radius: 22rpx;
  background: #fff;
  padding: 0 24rpx;
  font-size: 28rpx;
}

.chip-row {
  margin-top: 18rpx;
  white-space: nowrap;
}

.chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 14rpx 24rpx;
  margin-right: 16rpx;
  background: #fff;
  border-radius: 999rpx;
  color: #4b5563;
  font-size: 24rpx;
}

.chip--active {
  background: #17365d;
  color: #fff;
}

.list-scroll {
  flex: 1;
  padding: 0 24rpx 24rpx;
  box-sizing: border-box;
}

.list-stack {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.outlet-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 24rpx;
}

.outlet-card__header {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
}

.outlet-card__title {
  color: #111827;
  font-size: 30rpx;
  font-weight: 700;
}

.outlet-card__sub {
  margin-top: 8rpx;
  color: #6b7280;
  font-size: 24rpx;
}

.outlet-card__badge {
  max-width: 240rpx;
  text-align: right;
  color: #17365d;
  font-size: 24rpx;
  font-weight: 700;
}

.outlet-card__address {
  margin-top: 18rpx;
  color: #374151;
  font-size: 24rpx;
  line-height: 1.7;
}

.outlet-card__footer {
  margin-top: 18rpx;
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  color: #6b7280;
  font-size: 22rpx;
  line-height: 1.6;
}

.outlet-card__link {
  color: #17365d;
  white-space: nowrap;
  font-weight: 700;
}

.load-more,
.empty-box {
  margin-top: 24rpx;
  padding: 36rpx;
  text-align: center;
  color: #6b7280;
  font-size: 24rpx;
}
</style>
