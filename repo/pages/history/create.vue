<template>
  <view class="page create-page">
    <view class="hero-card">
      <view class="hero-card__eyebrow">訪問記録追加</view>
      <view class="hero-card__title">{{ outletName || '対象販売店' }}</view>
      <view class="hero-card__meta">{{ currentFlowLabel }}</view>
    </view>

    <view class="form-card">
      <view class="form-label">訪問メモ</view>
      <textarea
        v-model="remark"
        class="remark-input"
        maxlength="500"
        placeholder="例: 今月の陳列状況を確認し、補充要望と次回販促施策をすり合わせた。"
      />
      <view class="form-tip">次回の担当者が読み返しやすい、実務的な表現で入力してください。</view>
    </view>

    <view class="action-bar">
      <view class="ghost-button" @click="goBack">キャンセル</view>
      <view class="primary-button" :class="{ 'primary-button--disabled': submitting }" @click="submit">
        {{ submitting ? '保存中...' : '訪問記録を保存' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { addHistory } from '@/api/system/history'
import { useUserStore } from '@/store'
import { buildHistoryPayload, canViewAllData, getCurrentBusinessFlowRole } from '@/utils/outlet'

const userStore = useUserStore()
const outletId = ref('')
const outletName = ref('')
const remark = ref('')
const submitting = ref(false)

const currentFlowLabel = computed(() => {
  const flow = getCurrentBusinessFlowRole(userStore)
  return canViewAllData(userStore) ? '保存時の権限判定はバックエンド基準です' : `担当商流: ${flow || '未設定'}`
})

function goBack() {
  uni.navigateBack()
}

async function submit() {
  if (submitting.value) return
  if (!remark.value.trim()) {
    uni.showToast({ title: '訪問メモを入力してください', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    await addHistory(buildHistoryPayload({
      outletId: outletId.value,
      remark: remark.value,
      userStore
    }))
    uni.showToast({ title: '訪問記録を保存しました', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 500)
  } finally {
    submitting.value = false
  }
}

onLoad((options) => {
  outletId.value = options?.outletId || ''
  outletName.value = decodeURIComponent(options?.name || '')
})
</script>

<style lang="scss" scoped>
page {
  background: #f3f4f6;
}

.create-page {
  min-height: 100vh;
  padding: 24rpx;
}

.hero-card,
.form-card {
  background: #fff;
  border-radius: 24rpx;
}

.hero-card {
  padding: 28rpx;
  background: linear-gradient(135deg, #17365d 0%, #2d5d8f 100%);
  color: #fff;
}

.hero-card__eyebrow {
  font-size: 22rpx;
  opacity: 0.8;
}

.hero-card__title {
  margin-top: 12rpx;
  font-size: 36rpx;
  font-weight: 700;
}

.hero-card__meta {
  margin-top: 14rpx;
  font-size: 24rpx;
  opacity: 0.9;
}

.form-card {
  margin-top: 20rpx;
  padding: 24rpx;
}

.form-label {
  color: #111827;
  font-size: 28rpx;
  font-weight: 700;
}

.remark-input {
  width: 100%;
  min-height: 360rpx;
  margin-top: 18rpx;
  padding: 20rpx;
  box-sizing: border-box;
  border-radius: 20rpx;
  background: #f9fafb;
  font-size: 26rpx;
  line-height: 1.8;
}

.form-tip {
  margin-top: 16rpx;
  color: #6b7280;
  font-size: 22rpx;
}

.action-bar {
  display: flex;
  gap: 16rpx;
  margin-top: 24rpx;
}

.ghost-button,
.primary-button {
  flex: 1;
  text-align: center;
  padding: 22rpx 16rpx;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 700;
}

.ghost-button {
  background: #fff;
  color: #374151;
}

.primary-button {
  background: #17365d;
  color: #fff;
}

.primary-button--disabled {
  opacity: 0.65;
}
</style>
