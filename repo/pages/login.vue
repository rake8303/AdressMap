<template>
  <view class="normal-login-container">
    <view class="logo-content align-center justify-center flex">
      <image style="width: 100rpx; height: 100rpx;" :src="globalConfig.appInfo.logo" mode="widthFix"></image>
      <view class="brand-block">
        <text class="title">販売店営業アプリ</text>
        <text class="subtitle">ログイン</text>
      </view>
    </view>

    <view class="login-form-content">
      <view class="input-item flex align-center">
        <view class="iconfont icon-user icon"></view>
        <input v-model="loginForm.username" class="input" type="text" placeholder="アカウントを入力" maxlength="30" />
      </view>
      <view class="input-item flex align-center">
        <view class="iconfont icon-password icon"></view>
        <input v-model="loginForm.password" type="password" class="input" placeholder="パスワードを入力" maxlength="20" />
      </view>
      <view v-if="captchaEnabled" class="input-item input-item--captcha flex align-center">
        <view class="iconfont icon-code icon"></view>
        <input v-model="loginForm.code" type="number" class="input" placeholder="認証コードを入力" maxlength="4" />
        <view class="login-code">
          <image :src="codeUrl" @click="getCode" class="login-code-img"></image>
        </view>
      </view>

      <view class="action-btn">
        <button @click="handleLogin" class="login-btn cu-btn block bg-blue lg round">ログイン</button>
      </view>

      <view class="reg text-center" v-if="register">
        <text class="text-grey1">アカウントをお持ちでない方は </text>
        <text @click="handleUserRegister" class="text-blue">新規登録</text>
      </view>

      <view class="xieyi text-center">
        <text class="text-grey1">ログインすると </text>
        <text @click="handleUserAgrement" class="text-blue">利用規約</text>
        <text class="text-grey1"> と </text>
        <text @click="handlePrivacy" class="text-blue">プライバシーポリシー</text>
        <text class="text-grey1"> に同意したものとみなします。</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getToken } from '@/utils/auth'
import { getCodeImg } from '@/api/login'
import { useConfigStore, useUserStore } from '@/store'

const { proxy } = getCurrentInstance()
const globalConfig = useConfigStore().config
const codeUrl = ref('')
const captchaEnabled = ref(true)
const register = ref(false)
const loginForm = ref({
  username: 'admin',
  password: 'admin123',
  code: '',
  uuid: ''
})

function handleUserRegister() {
  proxy.$tab.redirectTo('/pages/register')
}

function handlePrivacy() {
  const site = globalConfig.appInfo.agreements[0]
  proxy.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`)
}

function handleUserAgrement() {
  const site = globalConfig.appInfo.agreements[1]
  proxy.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`)
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = `data:image/gif;base64,${res.img}`
      loginForm.value.uuid = res.uuid
    }
  })
}

async function handleLogin() {
  if (loginForm.value.username === '') {
    proxy.$modal.msgError('アカウントを入力してください')
  } else if (loginForm.value.password === '') {
    proxy.$modal.msgError('パスワードを入力してください')
  } else if (loginForm.value.code === '' && captchaEnabled.value) {
    proxy.$modal.msgError('認証コードを入力してください')
  } else {
    proxy.$modal.loading('ログイン中です。しばらくお待ちください...')
    pwdLogin()
  }
}

async function pwdLogin() {
  useUserStore().login(loginForm.value).then(() => {
    proxy.$modal.closeLoading()
    loginSuccess()
  }).catch(() => {
    if (captchaEnabled.value) {
      getCode()
    }
  })
}

function loginSuccess() {
  useUserStore().getInfo().then(() => {
    proxy.$tab.reLaunch('/pages/index')
  })
}

onLoad(() => {
  //#ifdef H5
  if (getToken()) {
    proxy.$tab.reLaunch('/pages/index')
  }
  //#endif
})

getCode()
</script>

<style lang="scss" scoped>
page {
  background-color: #ffffff;
}

.normal-login-container {
  width: 100%;

  .logo-content {
    width: 100%;
    text-align: center;
    padding-top: 15%;

    image {
      border-radius: 8px;
    }
  }

  .brand-block {
    display: flex;
    flex-direction: column;
    margin-left: 16px;
    text-align: left;
  }

  .title {
    font-size: 21px;
    font-weight: 700;
    color: #17365d;
  }

  .subtitle {
    margin-top: 4px;
    font-size: 14px;
    color: #6b7280;
  }

  .login-form-content {
    text-align: center;
    margin: 20px auto;
    margin-top: 15%;
    width: 80%;

    .input-item {
      margin: 20px auto;
      background-color: #f5f6f7;
      height: 45px;
      border-radius: 20px;

      .icon {
        font-size: 38rpx;
        margin-left: 10px;
        color: #999;
      }

      .input {
        width: 100%;
        font-size: 14px;
        line-height: 20px;
        text-align: left;
        padding-left: 15px;
      }
    }

    .input-item--captcha {
      width: 100%;
      justify-content: space-between;
      padding-right: 12px;
      box-sizing: border-box;
    }

    .login-btn {
      margin-top: 40px;
      height: 45px;
    }

    .reg {
      margin-top: 15px;
    }

    .xieyi {
      color: #333;
      margin-top: 20px;
      line-height: 1.8;
    }

    .login-code {
      height: 38px;
      flex-shrink: 0;

      .login-code-img {
        height: 38px;
        width: 200rpx;
        border-radius: 12rpx;
      }
    }
  }
}
</style>
