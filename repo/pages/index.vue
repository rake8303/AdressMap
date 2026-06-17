<template>
  <scroll-view class="page dashboard-page" scroll-y>
    <view class="hero">
      <view>
        <view class="hero__eyebrow">店活マップ</view>
        <view class="hero__title">重要な販売店と直近のフォロー状況をすぐ確認できます。</view>
        <view class="hero__meta">{{ currentFlowLabel }}</view>
      </view>
      <view class="hero__chip">{{ userName }}</view>
    </view>

    <view class="stats-grid">
      <view class="stat-card stat-card--accent">
        <text class="stat-card__label">販売店総数</text>
        <text class="stat-card__value">{{ dashboard.totalOutlets }}</text>
      </view>
      <view class="stat-card">
        <text class="stat-card__label">販売実績あり店舗</text>
        <text class="stat-card__value">{{ dashboard.activeSalesOutlets }}</text>
      </view>
      <view class="stat-card">
        <text class="stat-card__label">30日以内の訪問店舗</text>
        <text class="stat-card__value">{{ dashboard.recentVisitedOutlets }}</text>
      </view>
      <view class="stat-card">
        <text class="stat-card__label">直近月次販売台数合計</text>
        <text class="stat-card__value">{{ dashboard.totalLatestSales }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section__header">
        <text class="section__title">クイック操作</text>
      </view>
      <view class="quick-grid">
        <view class="quick-card" @click="goOutletList">
          <text class="quick-card__title">販売店一覧</text>
          <text class="quick-card__desc">エリア、商流、キーワードで対象店舗を絞り込めます。</text>
        </view>
        <view class="quick-card" @click="goOutletList">
          <text class="quick-card__title">最近のフォロー</text>
          <text class="quick-card__desc">直近の訪問や販売動向を見ながら次のアクションにつなげます。</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section__header">
        <text class="section__title">最近更新された販売店</text>
        <text class="section__action" @click="goOutletList">すべて表示</text>
      </view>
      <view v-if="loading" class="empty-box">販売店データを読み込み中です...</view>
      <view v-else-if="recentOutlets.length === 0" class="empty-box">表示できる販売店がありません。</view>
      <view v-else class="list-stack">
        <view v-for="item in recentOutlets" :key="item.id" class="outlet-card" @click="openOutlet(item.id)">
          <view class="outlet-card__top">
            <view>
              <view class="outlet-card__title">{{ item.jpCompanyName || '名称未設定' }}</view>
              <view class="outlet-card__sub">{{ item.region || '未設定エリア' }} | {{ getPrimaryBusinessFlow(item.agentList) || '商流未設定' }}</view>
            </view>
            <view class="outlet-card__sales">{{ item.latestSalesText }}</view>
          </view>
          <view class="outlet-card__remark">{{ item.latestHistoryText }}</view>
        </view>
      </view>
    </view>
  </scroll-view>
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
const recentOutlets = ref([])
const dashboard = ref({
  totalOutlets: 0,
  activeSalesOutlets: 0,
  recentVisitedOutlets: 0,
  totalLatestSales: '0'
})

const userName = computed(() => userStore.name || 'ゲスト')
const currentFlowLabel = computed(() => {
  const role = getCurrentBusinessFlowRole(userStore)
  return canViewAllData(userStore) ? '現在の参照範囲: 全商流' : `現在の担当商流: ${role || '未設定'}`
})

async function loadDashboard() {
  loading.value = true
  try {
    const outlets = await fetchAllVisibleOutlets()
    const outletIds = outlets.map(item => String(item.id || '')).filter(Boolean)
    const [historyRes, salesRes] = await Promise.all([
      listHistory({ pageNum: 1, pageSize: 1000 }).catch(() => ({ rows: [] })),
      listMonthlySales({ pageNum: 1, pageSize: 2000 }).catch(() => ({ rows: [] }))
    ])

    const histories = (historyRes.rows || []).filter(item => outletIds.includes(String(item.outletId || '')))
    const salesRows = (salesRes.rows || []).filter(item => outletIds.includes(String(item.outletId || '')))

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

    const merged = outlets.map(item => {
      const latestHistory = getLatestHistoryRecord(historyMap.get(String(item.id || '')))
      const latestSales = getLatestMonthlySalesRecord(salesMap.get(String(item.id || '')))
      return {
        ...item,
        latestHistoryAt: latestHistory?.updatedAt || latestHistory?.createdAt || '',
        latestHistoryText: latestHistory
          ? `${formatShortDateTime(latestHistory.updatedAt || latestHistory.createdAt)} | ${formatHistoryRemark(latestHistory.remark)}`
          : '訪問履歴なし',
        latestSalesValue: latestSales?.quantity || 0,
        latestSalesText: latestSales ? `${latestSales.salesMonth} / ${formatNumber(latestSales.quantity)} 台` : '販売実績なし'
      }
    })

    const latestSalesRows = merged.filter(item => item.latestSalesValue > 0)
    const recentVisitedRows = merged.filter(item => item.latestHistoryAt)
    recentOutlets.value = [...merged]
      .sort((left, right) => new Date(right.latestHistoryAt || right.updatedAt || 0) - new Date(left.latestHistoryAt || left.updatedAt || 0))
      .slice(0, 8)

    dashboard.value = {
      totalOutlets: outlets.length,
      activeSalesOutlets: latestSalesRows.length,
      recentVisitedOutlets: recentVisitedRows.length,
      totalLatestSales: formatNumber(latestSalesRows.reduce((sum, item) => sum + item.latestSalesValue, 0))
    }
  } finally {
    loading.value = false
  }
}

async function fetchAllVisibleOutlets() {
  const pageSize = 200
  const collected = []
  let pageNum = 1
  let total = 0

  do {
    const response = await fetchOutletPage({ pageNum, pageSize })
    const rows = response.rows || []
    total = Number(response.total || 0)
    collected.push(...rows)
    if (rows.length < pageSize) break
    pageNum += 1
  } while (collected.length < total)

  return collected
}

function fetchOutletPage(params) {
  return listOutlet(params)
}

function goOutletList() {
  uni.switchTab({ url: '/pages/work/index' })
}

function openOutlet(id) {
  uni.navigateTo({ url: `/pages/outlet/detail?id=${id}` })
}

onMounted(loadDashboard)
</script>

<style lang="scss" scoped>
page {
  background: #f3f4f6;
}

.page {
  min-height: 100vh;
}

.dashboard-page {
  padding: 24rpx;
  box-sizing: border-box;
}

.hero {
  background: linear-gradient(135deg, #17365d 0%, #2d5d8f 60%, #e6b655 100%);
  color: #fff;
  border-radius: 28rpx;
  padding: 32rpx;
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  box-shadow: 0 18rpx 36rpx rgba(23, 54, 93, 0.18);
}

.hero__eyebrow {
  font-size: 22rpx;
  opacity: 0.78;
}

.hero__title {
  margin-top: 12rpx;
  font-size: 40rpx;
  line-height: 1.4;
  font-weight: 700;
}

.hero__meta {
  margin-top: 16rpx;
  font-size: 24rpx;
  opacity: 0.88;
}

.hero__chip {
  align-self: flex-start;
  padding: 12rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.18);
  font-size: 24rpx;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
  margin-top: 24rpx;
}

.stat-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 24rpx;
}

.stat-card--accent {
  background: #fff7e6;
}

.stat-card__label {
  color: #6b7280;
  font-size: 24rpx;
}

.stat-card__value {
  display: block;
  margin-top: 16rpx;
  color: #111827;
  font-size: 42rpx;
  font-weight: 700;
}

.section {
  margin-top: 28rpx;
}

.section__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.section__title {
  font-size: 30rpx;
  font-weight: 700;
  color: #111827;
}

.section__action {
  font-size: 24rpx;
  color: #17365d;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
}

.quick-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 24rpx;
}

.quick-card__title {
  display: block;
  color: #111827;
  font-size: 28rpx;
  font-weight: 700;
}

.quick-card__desc {
  display: block;
  margin-top: 12rpx;
  color: #6b7280;
  font-size: 24rpx;
  line-height: 1.7;
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

.outlet-card__top {
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

.outlet-card__sales {
  color: #17365d;
  font-size: 24rpx;
  font-weight: 700;
  text-align: right;
}

.outlet-card__remark {
  margin-top: 18rpx;
  color: #374151;
  font-size: 24rpx;
  line-height: 1.7;
}

.empty-box {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx 32rpx;
  color: #6b7280;
  text-align: center;
}
</style>
