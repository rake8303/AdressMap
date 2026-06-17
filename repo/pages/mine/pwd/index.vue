<template>
  <view class="pwd-retrieve-container">
    <uni-forms ref="form" :value="user" labelWidth="96px">
      <uni-forms-item name="oldPassword" label="現在のパスワード">
        <uni-easyinput type="password" v-model="user.oldPassword" placeholder="現在のパスワードを入力" />
      </uni-forms-item>
      <uni-forms-item name="newPassword" label="新しいパスワード">
        <uni-easyinput type="password" v-model="user.newPassword" placeholder="新しいパスワードを入力" />
      </uni-forms-item>
      <uni-forms-item name="confirmPassword" label="確認用パスワード">
        <uni-easyinput type="password" v-model="user.confirmPassword" placeholder="新しいパスワードを再入力" />
      </uni-forms-item>
      <button type="primary" @click="submit">更新する</button>
    </uni-forms>
  </view>
</template>

<script setup>
import { updateUserPwd } from '@/api/system/user'
import { ref, reactive, getCurrentInstance } from 'vue'
import { onReady } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const user = reactive({
  oldPassword: undefined,
  newPassword: undefined,
  confirmPassword: undefined
})
const rules = ref({
  oldPassword: {
    rules: [
      {
        required: true,
        errorMessage: '現在のパスワードを入力してください'
      }
    ]
  },
  newPassword: {
    rules: [
      {
        required: true,
        errorMessage: '新しいパスワードを入力してください'
      },
      {
        minLength: 6,
        maxLength: 20,
        errorMessage: '6文字以上20文字以下で入力してください'
      }
    ]
  },
  confirmPassword: {
    rules: [
      {
        required: true,
        errorMessage: '確認用パスワードを入力してください'
      },
      {
        validateFunction: (rule, value) => user.newPassword === value,
        errorMessage: 'パスワードが一致しません'
      }
    ]
  }
})

onReady(() => {
  proxy.$refs.form.setRules(rules.value)
})

function submit() {
  proxy.$refs.form.validate().then(() => {
    updateUserPwd(user.oldPassword, user.newPassword).then(() => {
      proxy.$modal.msgSuccess('パスワードを更新しました')
    })
  })
}
</script>

<style lang="scss" scoped>
page {
  background-color: #ffffff;
}

.pwd-retrieve-container {
  padding-top: 36rpx;
  padding: 15px;
}
</style>
