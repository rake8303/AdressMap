<template>
  <view class="normal-login-container">
    <view class="logo-content align-center justify-center flex">
      <image style="width: 100rpx; height: 100rpx;" :src="globalConfig.appInfo.logo" mode="widthFix"></image>
      <view class="brand-block">
        <text class="title">販売店営業アプリ</text>
        <text class="subtitle">新規登録</text>
      </view>
    </view>

    <view class="login-form-content">
      <view class="input-item flex align-center">
        <view class="iconfont icon-user icon"></view>
        <input v-model="registerForm.username" class="input" type="text" placeholder="アカウントを入力" maxlength="30" />
      </view>
      <view class="input-item flex align-center">
        <view class="iconfont icon-password icon"></view>
        <input v-model="registerForm.password" type="password" class="input" placeholder="パスワードを入力" maxlength="20" />
      </view>
      <view class="input-item flex align-center">
        <view class="iconfont icon-password icon"></view>
        <input v-model="registerForm.confirmPassword" type="password" class="input" placeholder="パスワードを再入力" maxlength="20" />
      </view>
      <view v-if="captchaEnabled" class="input-item input-item--captcha flex align-center">
        <view class="iconfont icon-code icon"></view>
        <input v-model="registerForm.code" type="number" class="input" placeholder="認証コードを入力" maxlength="4" />
        <view class="login-code">
          <image :src="codeUrl" @click="getCode" class="login-code-img"></image>
        </view>
      </view>
      <view class="action-btn">
        <button @click="handleRegister()" class="register-btn cu-btn block bg-blue lg round">登録する</button>
      </view>
    </view>
    <view class="xieyi text-center">
      <text @click="handleUserLogin" class="text-blue">登録済みのアカウントでログイン</text>
    </view>
  </view>
</template>

<script setup>
import { getCodeImg, register } from '@/api/login'
import { ref, getCurrentInstance } from 'vue'
import { useConfigStore } from '@/store'

const { proxy } = getCurrentInstance()
const globalConfig = useConfigStore().config
const codeUrl = ref('')
const captchaEnabled = ref(true)
const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  code: '',
  uuid: ''
})

function handleUserLogin() {
  proxy.$tab.navigateTo('/pages/login')
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = `data:image/gif;base64,${res.img}`
      registerForm.value.uuid = res.uuid
    }
  })
}

async function handleRegister() {
  if (registerForm.value.username === '') {
    proxy.$modal.msgError('アカウントを入力してください')
  } else if (registerForm.value.password === '') {
    proxy.$modal.msgError('パスワードを入力してください')
  } else if (registerForm.value.confirmPassword === '') {
    proxy.$modal.msgError('確認用パスワードを入力してください')
  } else if (registerForm.value.password !== registerForm.value.confirmPassword) {
    proxy.$modal.msgError('パスワードが一致しません')
  } else if (registerForm.value.code === '' && captchaEnabled.value) {
    proxy.$modal.msgError('認証コードを入力してください')
  } else {
    proxy.$modal.loading('登録処理中です。しばらくお待ちください...')
    userRegister()
  }
}

async function userRegister() {
  register(registerForm.value).then(() => {
    proxy.$modal.closeLoading()
    uni.showModal({
      title: '登録完了',
      content: `アカウント ${registerForm.value.username} を登録しました。ログイン画面へ移動しますか。`,
      success: function (res) {
        if (res.confirm) {
          uni.redirectTo({ url: '/pages/login' })
        }
      }
    })
  }).catch(() => {
    if (captchaEnabled.value) {
      getCode()
    }
  })
}

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

    .register-btn {
      margin-top: 40px;
      height: 45px;
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

  .xieyi {
    color: #333;
    margin-top: 20px;
  }
}
</style>
