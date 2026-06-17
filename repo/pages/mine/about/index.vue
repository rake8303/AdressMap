<template>
  <view class="about-container">
    <view class="header-section text-center">
      <image style="width: 150rpx; height: 150rpx;" src="/static/logo200.png" mode="widthFix"></image>
      <uni-title type="h2" title="販売店営業アプリ"></uni-title>
    </view>

    <view class="content-section">
      <view class="menu-list">
        <view class="list-cell list-cell-arrow">
          <view class="menu-item-box">
            <view>バージョン</view>
            <view class="text-right">v{{ version }}</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow">
          <view class="menu-item-box">
            <view>お問い合わせ</view>
            <view class="text-right">support@example.jp</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow">
          <view class="menu-item-box">
            <view>サポート窓口</view>
            <view class="text-right">03-0000-0000</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow">
          <view class="menu-item-box">
            <view>Webサイト</view>
            <view class="text-right">
              <uni-link :href="url" :text="url" showUnderLine="false"></uni-link>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="copyright">
      <view>Copyright © 2026 Sales Team App</view>
    </view>
  </view>
</template>

<script setup>
import { useConfigStore } from '@/store'

const url = useConfigStore().config.appInfo.site_url
const version = useConfigStore().config.appInfo.version
</script>

<style lang="scss" scoped>
page {
  background-color: #f8f8f8;
}

.copyright {
  margin-top: 50rpx;
  text-align: center;
  line-height: 60rpx;
  color: #999;
}

.header-section {
  display: flex;
  padding: 30rpx 0 0;
  flex-direction: column;
  align-items: center;
}
</style>
