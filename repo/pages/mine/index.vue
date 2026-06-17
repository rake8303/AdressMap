<template>
  <view class="mine-container" :style="{ height: `${windowHeight}px` }">
    <view class="header-section">
      <view class="flex padding justify-between">
        <view class="flex align-center">
          <view v-if="!avatar" class="cu-avatar xl round bg-white">
            <view class="iconfont icon-people text-gray icon"></view>
          </view>
          <image v-if="avatar" @click="handleToAvatar" :src="avatar" class="cu-avatar xl round" mode="widthFix"></image>
          <view v-if="!name" @click="handleToLogin" class="login-tip">
            ログインしてください
          </view>
          <view v-if="name" @click="handleToInfo" class="user-info">
            <view class="u_title">{{ name }}</view>
            <view class="u_subtitle">アカウント情報を確認</view>
          </view>
        </view>
        <view @click="handleToInfo" class="flex align-center profile-link">
          <text>プロフィール</text>
          <view class="iconfont icon-right"></view>
        </view>
      </view>
    </view>

    <view class="content-section">
      <view class="mine-actions grid col-4 text-center">
        <view class="action-item" @click="goOutletList">
          <view class="iconfont icon-community text-blue icon"></view>
          <text class="text">販売店一覧</text>
        </view>
        <view class="action-item" @click="goVisitCreate">
          <view class="iconfont icon-service text-green icon"></view>
          <text class="text">訪問登録</text>
        </view>
        <view class="action-item" @click="handleHelp">
          <view class="iconfont icon-help text-mauve icon"></view>
          <text class="text">ヘルプ</text>
        </view>
        <view class="action-item" @click="handleAbout">
          <view class="iconfont icon-aixin text-pink icon"></view>
          <text class="text">このアプリ</text>
        </view>
      </view>

      <view class="menu-list">
        <view class="list-cell list-cell-arrow" @click="handleToEditInfo">
          <view class="menu-item-box">
            <view class="iconfont icon-user menu-icon"></view>
            <view>プロフィール編集</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow" @click="handleHelp">
          <view class="menu-item-box">
            <view class="iconfont icon-help menu-icon"></view>
            <view>よくある質問</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow" @click="handleAbout">
          <view class="menu-item-box">
            <view class="iconfont icon-aixin menu-icon"></view>
            <view>このアプリについて</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow" @click="handleToSetting">
          <view class="menu-item-box">
            <view class="iconfont icon-setting menu-icon"></view>
            <view>設定</view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { useUserStore } from '@/store'
import { computed, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()
const name = useUserStore().name
const avatar = computed(() => useUserStore().avatar)
const windowHeight = computed(() => uni.getSystemInfoSync().windowHeight - 50)

function handleToInfo() {
  proxy.$tab.navigateTo('/pages/mine/info/index')
}

function handleToEditInfo() {
  proxy.$tab.navigateTo('/pages/mine/info/edit')
}

function handleToSetting() {
  proxy.$tab.navigateTo('/pages/mine/setting/index')
}

function handleToLogin() {
  proxy.$tab.reLaunch('/pages/login')
}

function handleToAvatar() {
  proxy.$tab.navigateTo('/pages/mine/avatar/index')
}

function handleHelp() {
  proxy.$tab.navigateTo('/pages/mine/help/index')
}

function handleAbout() {
  proxy.$tab.navigateTo('/pages/mine/about/index')
}

function goOutletList() {
  uni.switchTab({ url: '/pages/work/index' })
}

function goVisitCreate() {
  proxy.$modal.showToast('訪問記録は販売店詳細画面から登録してください')
}
</script>

<style lang="scss" scoped>
page {
  background-color: #f5f6f7;
}

.mine-container {
  width: 100%;
  height: 100%;

  .header-section {
    padding: 15px 15px 45px 15px;
    background: linear-gradient(135deg, #17365d 0%, #2d5d8f 100%);
    color: white;

    .login-tip {
      font-size: 18px;
      margin-left: 10px;
    }

    .cu-avatar {
      border: 2px solid #eaeaea;

      .icon {
        font-size: 40px;
      }
    }

    .user-info {
      margin-left: 15px;
    }

    .u_title {
      font-size: 18px;
      line-height: 30px;
      font-weight: 700;
    }

    .u_subtitle {
      font-size: 13px;
      opacity: 0.85;
    }
  }

  .profile-link {
    font-size: 14px;
  }

  .content-section {
    position: relative;
    top: -50px;

    .mine-actions {
      margin: 15px 15px;
      padding: 20px 0px;
      border-radius: 8px;
      background-color: white;

      .action-item {
        .icon {
          font-size: 28px;
        }

        .text {
          display: block;
          font-size: 13px;
          margin: 8px 0px;
        }
      }
    }
  }
}
</style>
