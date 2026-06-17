<template>
  <div class="register">
    <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
      <div class="brand-mark">
        <img :src="logo" alt="店活マップ logo" class="brand-logo">
      </div>
      <h3 class="title">{{ title }}</h3>
      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          type="text"
          size="large"
          auto-complete="off"
          placeholder="アカウント"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          size="large"
          auto-complete="off"
          placeholder="パスワード"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          size="large"
          auto-complete="off"
          placeholder="確認用パスワード"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          size="large"
          v-model="registerForm.code"
          auto-complete="off"
          placeholder="認証コード"
          style="width: 63%"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="register-code">
          <img :src="codeUrl" @click="getCode" class="register-code-img" />
        </div>
      </el-form-item>
      <el-form-item style="width: 100%;">
        <el-button
          :loading="loading"
          size="large"
          type="primary"
          style="width: 100%;"
          @click.prevent="handleRegister"
        >
          <span v-if="!loading">登録する</span>
          <span v-else>登録中...</span>
        </el-button>
        <div style="float: right;">
          <router-link class="link-type" :to="'/login'">既存アカウントでログイン</router-link>
        </div>
      </el-form-item>
    </el-form>
    <div class="el-register-footer">
      <span>Copyright © 2026 店活マップ All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from "element-plus"
import { getCodeImg, register } from "@/api/login"
import logo from '@/assets/logo/logo.png'

const title = import.meta.env.VITE_APP_TITLE || '店活マップ'
const router = useRouter()
const { proxy } = getCurrentInstance()

const registerForm = ref({
  username: "",
  password: "",
  confirmPassword: "",
  code: "",
  uuid: ""
})

const equalToPassword = (rule, value, callback) => {
  if (registerForm.value.password !== value) {
    callback(new Error("パスワードが一致しません"))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, trigger: "blur", message: "アカウントを入力してください" },
    { min: 2, max: 20, message: "アカウントは2文字以上20文字以下で入力してください", trigger: "blur" }
  ],
  password: [
    { required: true, trigger: "blur", message: "パスワードを入力してください" },
    { min: 5, max: 20, message: "パスワードは5文字以上20文字以下で入力してください", trigger: "blur" },
    { pattern: /^[^<>\"'|\\\\]+$/, message: "使用できない文字が含まれています", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, trigger: "blur", message: "確認用パスワードを入力してください" },
    { required: true, validator: equalToPassword, trigger: "blur" }
  ],
  code: [{ required: true, trigger: "change", message: "認証コードを入力してください" }]
}

const codeUrl = ref("")
const loading = ref(false)
const captchaEnabled = ref(true)

function handleRegister() {
  proxy.$refs.registerRef.validate(valid => {
    if (valid) {
      loading.value = true
      register(registerForm.value).then(() => {
        const username = registerForm.value.username
        ElMessageBox.alert(
          `<span>アカウント <strong>${username}</strong> を登録しました。ログイン画面へ移動します。</span>`,
          "登録完了",
          {
            dangerouslyUseHTMLString: true,
            type: "success",
          }
        ).then(() => {
          router.push("/login")
        }).catch(() => {})
      }).catch(() => {
        loading.value = false
        if (captchaEnabled.value) {
          getCode()
        }
      })
    }
  })
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img
      registerForm.value.uuid = res.uuid
    }
  })
}

getCode()
</script>

<style lang='scss' scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}

.brand-mark {
  display: flex;
  justify-content: center;
  margin-bottom: 14px;
}

.brand-logo {
  width: 72px;
  height: 72px;
  border-radius: 18px;
  box-shadow: 0 12px 28px rgba(23, 54, 93, 0.22);
}

.title {
  margin: 0 auto 30px auto;
  text-align: center;
  color: #17365d;
  font-weight: 700;
}

.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;

  .el-input {
    height: 40px;

    input {
      height: 40px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0;
  }
}

.register-code {
  width: 33%;
  height: 40px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial, sans-serif;
  font-size: 12px;
  letter-spacing: 1px;
}

.register-code-img {
  height: 40px;
  padding-left: 12px;
}
</style>
