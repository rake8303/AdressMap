<template>
  <view class="help-container">
    <view v-for="(item, findex) in list" :key="findex" :title="item.title" class="list-title">
      <view class="text-title">
        <view :class="item.icon"></view>{{ item.title }}
      </view>
      <view class="childList">
        <view
          v-for="(child, zindex) in item.childList"
          :key="zindex"
          class="question"
          hover-class="hover"
          @click="handleText(child)"
        >
          <view class="text-item">{{ child.title }}</view>
          <view class="line" v-if="zindex !== item.childList.length - 1"></view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()

const list = ref([
  {
    icon: 'iconfont icon-help',
    title: '基本操作',
    childList: [
      {
        title: 'このアプリで何ができますか？',
        content: '販売店情報の確認、訪問記録の登録、月次販売台数の確認ができます。'
      },
      {
        title: '訪問記録はどこから追加できますか？',
        content: '販売店詳細画面の「訪問記録を追加」から登録できます。'
      },
      {
        title: '月次販売台数はどの値が表示されますか？',
        content: '対象店舗に紐づく最新月の月次販売台数を優先して表示します。'
      }
    ]
  },
  {
    icon: 'iconfont icon-community',
    title: '権限と表示範囲',
    childList: [
      {
        title: '商流ごとの権限はどう反映されますか？',
        content: 'ログイン中のロールに応じて、表示できる販売店、訪問履歴、月次販売台数が自動的に制御されます。'
      },
      {
        title: '他商流の販売店が見えないのはなぜですか？',
        content: 'common、readonly、admin 以外の商流紐付けロールは、自分の商流に属するデータのみ参照できます。'
      },
      {
        title: '表示内容がおかしい場合は？',
        content: '一度再読込し、それでも解消しない場合はサポート窓口へ連絡してください。'
      }
    ]
  }
])

function handleText(item) {
  proxy.$tab.navigateTo(`/pages/common/textview/index?title=${item.title}&content=${item.content}`)
}
</script>

<style lang="scss" scoped>
page {
  background-color: #f8f8f8;
}

.help-container {
  margin-bottom: 100rpx;
  padding: 30rpx;
}

.list-title {
  margin-bottom: 30rpx;
}

.childList {
  background: #ffffff;
  box-shadow: 0px 0px 10rpx rgba(193, 193, 193, 0.2);
  border-radius: 16rpx;
  margin-top: 10rpx;
}

.line {
  width: 100%;
  height: 1rpx;
  background-color: #F5F5F5;
}

.text-title {
  color: #303133;
  font-size: 32rpx;
  font-weight: bold;
  margin-left: 10rpx;

  .iconfont {
    font-size: 16px;
    margin-right: 10rpx;
  }
}

.text-item {
  font-size: 28rpx;
  padding: 24rpx;
}

.question {
  color: #606266;
  font-size: 28rpx;
}
</style>
