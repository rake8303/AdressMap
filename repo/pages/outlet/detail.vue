<template>
  <scroll-view class="page detail-page" scroll-y refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="loadDetail">
    <view class="hero-card">
      <view class="hero-card__title">{{ outlet.jpCompanyName || '名称未設定' }}</view>
      <view class="hero-card__sub">{{ outlet.region || '未設定エリア' }} | {{ primaryFlow || '商流未設定' }}</view>
      <view class="hero-card__address">{{ outlet.headquartersAddress || '住所未設定' }}</view>
      <view class="hero-card__actions">
        <view class="ghost-button" @click="copyAddress">住所をコピー</view>
        <view class="primary-button" @click="goCreateHistory">訪問記録を追加</view>
      </view>
    </view>

    <view class="summary-grid">
      <view class="summary-card">
        <text class="summary-card__label">直近月次販売台数</text>
        <text class="summary-card__value">{{ latestSalesRecord ? formatNumber(latestSalesRecord.quantity) : 'N/A' }}</text>
        <text class="summary-card__meta">{{ latestSalesRecord?.salesMonth || '販売実績なし' }}</text>
      </view>
      <view class="summary-card">
        <text class="summary-card__label">直近訪問</text>
        <text class="summary-card__value summary-card__value--small">{{ latestHistoryRecord ? formatShortDateTime(latestHistoryRecord.updatedAt || latestHistoryRecord.createdAt) : 'N/A' }}</text>
        <text class="summary-card__meta">{{ latestHistoryRecord?.agent || '訪問履歴なし' }}</text>
      </view>
    </view>

    <view class="section-card">
      <view class="section-card__title">基本情報</view>
      <view class="info-row">
        <text class="info-row__label">略称</text>
        <text class="info-row__value">{{ outlet.shortCompanyName || 'N/A' }}</text>
      </view>
      <view class="info-row">
        <text class="info-row__label">担当者</text>
        <text class="info-row__value">{{ outlet.contactPerson || 'N/A' }}</text>
      </view>
      <view class="info-row">
        <text class="info-row__label">会社種別</text>
        <text class="info-row__value">{{ outlet.cpnType || 'N/A' }}</text>
      </view>
      <view class="info-row">
        <text class="info-row__label">商流</text>
        <text class="info-row__value">{{ primaryFlow || 'N/A' }}</text>
      </view>
    </view>

    <view class="section-card">
      <view class="section-card__head">
        <text class="section-card__title">訪問履歴</text>
        <text class="section-card__meta">{{ historyList.length }} 件</text>
      </view>
      <view v-if="historyList.length === 0" class="empty-inline">訪問履歴はありません。</view>
      <view v-else class="timeline">
        <view v-for="item in historyList.slice(0, 6)" :key="item.historyId || item.updatedAt" class="timeline-item">
          <view class="timeline-item__dot"></view>
          <view class="timeline-item__content">
            <view class="timeline-item__meta">{{ formatDateTime(item.updatedAt || item.createdAt) }} | {{ item.agent || '商流未設定' }}</view>
            <view class="timeline-item__text">{{ formatHistoryRemark(item.remark) }}</view>
          </view>
        </view>
      </view>
    </view>

    <view class="section-card">
      <view class="section-card__head">
        <text class="section-card__title">月次販売台数</text>
        <text class="section-card__meta">{{ salesList.length }} 件</text>
      </view>
      <view v-if="salesList.length === 0" class="empty-inline">月次販売台数のデータはありません。</view>
      <view v-else class="sales-stack">
        <view v-for="item in salesList.slice(0, 6)" :key="`${item.salesId || item.salesMonth}-${item.productName || ''}`" class="sales-item">
          <view>
            <view class="sales-item__month">{{ item.salesMonth || '月未設定' }}</view>
            <view class="sales-item__product">{{ item.productName || '標準商品' }}</view>
          </view>
          <view class="sales-item__quantity">{{ formatNumber(item.quantity) }}</view>
        </view>
      </view>
    </view>
  </scroll-view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { listHistory } from '@/api/system/history'
import { listMonthlySales } from '@/api/system/monthlySales'
import { getOutlet } from '@/api/system/outlet'
import {
  formatDateTime,
  formatHistoryRemark,
  formatNumber,
  formatShortDateTime,
  getLatestHistoryRecord,
  getLatestMonthlySalesRecord,
  getPrimaryBusinessFlow
} from '@/utils/outlet'

const outletId = ref('')
const refreshing = ref(false)
const outlet = ref({})
const historyList = ref([])
const salesList = ref([])

const primaryFlow = computed(() => getPrimaryBusinessFlow(outlet.value.agentList))
const latestHistoryRecord = computed(() => getLatestHistoryRecord(historyList.value))
const latestSalesRecord = computed(() => getLatestMonthlySalesRecord(salesList.value))

async function loadDetail() {
  if (!outletId.value) return
  refreshing.value = true
  try {
    const [outletRes, historyRes, salesRes] = await Promise.all([
      getOutlet(outletId.value),
      listHistory({ outletId: outletId.value, pageNum: 1, pageSize: 100 }),
      listMonthlySales({ outletId: outletId.value, pageNum: 1, pageSize: 100 })
    ])
    outlet.value = outletRes.data || {}
    historyList.value = historyRes.rows || []
    salesList.value = [...(salesRes.rows || [])].sort((left, right) => String(right.salesMonth || '').localeCompare(String(left.salesMonth || '')))
  } finally {
    refreshing.value = false
    uni.stopPullDownRefresh()
  }
}

function copyAddress() {
  if (!outlet.value.headquartersAddress) {
    uni.showToast({ title: 'コピーできる住所がありません', icon: 'none' })
    return
  }
  uni.setClipboardData({
    data: outlet.value.headquartersAddress,
    success: () => {
      uni.showToast({ title: '住所をコピーしました', icon: 'none' })
    }
  })
}

function goCreateHistory() {
  const name = encodeURIComponent(outlet.value.jpCompanyName || '')
  uni.navigateTo({ url: `/pages/history/create?outletId=${outletId.value}&name=${name}` })
}

onLoad((options) => {
  outletId.value = options?.id || ''
})

onShow(() => {
  loadDetail()
})
</script>

<style lang="scss" scoped>
page {
  background: #f3f4f6;
}

.detail-page {
  min-height: 100vh;
  padding: 24rpx;
  box-sizing: border-box;
}

.hero-card,
.section-card,
.summary-card {
  background: #fff;
  border-radius: 24rpx;
}

.hero-card {
  padding: 28rpx;
  background: linear-gradient(135deg, #163456 0%, #204f7f 100%);
  color: #fff;
}

.hero-card__title {
  font-size: 38rpx;
  font-weight: 700;
}

.hero-card__sub,
.hero-card__address {
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  opacity: 0.9;
}

.hero-card__actions {
  margin-top: 24rpx;
  display: flex;
  gap: 16rpx;
}

.ghost-button,
.primary-button {
  padding: 16rpx 22rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 700;
}

.ghost-button {
  background: rgba(255, 255, 255, 0.14);
}

.primary-button {
  background: #e8b75b;
  color: #17365d;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
  margin-top: 20rpx;
}

.summary-card {
  padding: 24rpx;
}

.summary-card__label,
.summary-card__meta,
.section-card__meta {
  color: #6b7280;
  font-size: 22rpx;
}

.summary-card__value {
  display: block;
  margin-top: 16rpx;
  color: #111827;
  font-size: 42rpx;
  font-weight: 700;
}

.summary-card__value--small {
  font-size: 32rpx;
}

.summary-card__meta {
  display: block;
  margin-top: 10rpx;
}

.section-card {
  margin-top: 20rpx;
  padding: 24rpx;
}

.section-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-card__title {
  color: #111827;
  font-size: 30rpx;
  font-weight: 700;
}

.info-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  padding: 18rpx 0;
  border-bottom: 1px solid #f1f5f9;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row__label {
  color: #6b7280;
  font-size: 24rpx;
}

.info-row__value {
  color: #111827;
  font-size: 24rpx;
  text-align: right;
}

.timeline {
  margin-top: 16rpx;
}

.timeline-item {
  display: flex;
  gap: 16rpx;
  padding: 18rpx 0;
}

.timeline-item__dot {
  width: 18rpx;
  height: 18rpx;
  margin-top: 10rpx;
  border-radius: 50%;
  background: #17365d;
  flex-shrink: 0;
}

.timeline-item__meta {
  color: #6b7280;
  font-size: 22rpx;
}

.timeline-item__text {
  margin-top: 8rpx;
  color: #111827;
  font-size: 24rpx;
  line-height: 1.75;
}

.sales-stack {
  margin-top: 16rpx;
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.sales-item {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 20rpx;
  border-radius: 18rpx;
  background: #f9fafb;
}

.sales-item__month {
  color: #111827;
  font-size: 26rpx;
  font-weight: 700;
}

.sales-item__product {
  margin-top: 6rpx;
  color: #6b7280;
  font-size: 22rpx;
}

.sales-item__quantity {
  align-self: center;
  color: #17365d;
  font-size: 30rpx;
  font-weight: 700;
}

.empty-inline {
  padding-top: 20rpx;
  color: #6b7280;
  font-size: 24rpx;
}
</style>
