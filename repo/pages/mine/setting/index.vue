<template>
  <view class="setting-container" :style="{ height: `${windowHeight}px` }">
    <view class="menu-list">
      <view class="list-cell list-cell-arrow" @click="handleToPwd">
        <view class="menu-item-box">
          <view class="iconfont icon-password menu-icon"></view>
          <view>パスワード変更</view>
        </view>
      </view>
      <view class="list-cell list-cell-arrow" @click="handleToUpgrade">
        <view class="menu-item-box">
          <view class="iconfont icon-refresh menu-icon"></view>
          <view>更新確認</view>
        </view>
      </view>
      <view class="list-cell list-cell-arrow" @click="handleCleanTmp">
        <view class="menu-item-box">
          <view class="iconfont icon-clean menu-icon"></view>
          <view>キャッシュ削除</view>
        </view>
      </view>
    </view>
    <view class="cu-list menu">
      <view class="cu-item item-box">
        <view class="content text-center" @click="handleLogout">
          <text class="text-black">ログアウト</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { useUserStore } from '@/store'
import { computed, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()
const windowHeight = computed(() => uni.getSystemInfoSync().windowHeight - 50)

function handleToPwd() {
  proxy.$tab.navigateTo('/pages/mine/pwd/index')
}

function handleToUpgrade() {
  proxy.$modal.showToast('更新機能は次期対応です')
}

function handleCleanTmp() {
  proxy.$modal.showToast('キャッシュ削除機能は次期対応です')
}

function handleLogout() {
  proxy.$modal.confirm('ログアウトしてログイン画面へ戻りますか？').then(() => {
    useUserStore().logOut().then(() => {}).finally(() => {
      proxy.$tab.reLaunch('/pages/index')
    })
  })
}
</script>

<style lang="scss" scoped>
.page {
  background-color: #f8f8f8;
}

.item-box {
  background-color: #FFFFFF;
  margin: 30rpx;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 10rpx;
  border-radius: 8rpx;
  color: #303133;
  font-size: 32rpx;
}
</style>
