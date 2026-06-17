<template>
  <view class="container">
    <view class="example">
      <uni-forms ref="form" :model="user" labelWidth="88px">
        <uni-forms-item label="表示名" name="nickName">
          <uni-easyinput v-model="user.nickName" placeholder="表示名を入力" />
        </uni-forms-item>
        <uni-forms-item label="電話番号" name="phonenumber">
          <uni-easyinput v-model="user.phonenumber" placeholder="電話番号を入力" />
        </uni-forms-item>
        <uni-forms-item label="メール" name="email">
          <uni-easyinput v-model="user.email" placeholder="メールアドレスを入力" />
        </uni-forms-item>
        <uni-forms-item label="性別" name="sex" required>
          <uni-data-checkbox v-model="user.sex" :localdata="sexs" />
        </uni-forms-item>
      </uni-forms>
      <button type="primary" @click="submit">保存する</button>
    </view>
  </view>
</template>

<script setup>
import { getUserProfile, updateUserProfile } from '@/api/system/user'
import { ref, getCurrentInstance } from 'vue'
import { onReady } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const user = ref({
  nickName: '',
  phonenumber: '',
  email: '',
  sex: ''
})
const sexs = [
  { text: '男性', value: '0' },
  { text: '女性', value: '1' }
]
const rules = ref({
  nickName: {
    rules: [
      {
        required: true,
        errorMessage: '表示名を入力してください'
      }
    ]
  },
  phonenumber: {
    rules: [
      {
        required: true,
        errorMessage: '電話番号を入力してください'
      },
      {
        pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
        errorMessage: '正しい電話番号を入力してください'
      }
    ]
  },
  email: {
    rules: [
      {
        required: true,
        errorMessage: 'メールアドレスを入力してください'
      },
      {
        format: 'email',
        errorMessage: '正しいメールアドレスを入力してください'
      }
    ]
  }
})

function getUser() {
  getUserProfile().then(response => {
    user.value = response.data
  })
}

function submit() {
  proxy.$refs.form.validate().then(() => {
    updateUserProfile(user.value).then(() => {
      proxy.$modal.msgSuccess('プロフィールを更新しました')
    })
  })
}

onReady(() => {
  proxy.$refs.form.setRules(rules.value)
})

getUser()
</script>

<style lang="scss" scoped>
page {
  background-color: #ffffff;
}

.example {
  padding: 15px;
  background-color: #fff;
}

.segmented-control {
  margin-bottom: 15px;
}

.button-group {
  margin-top: 15px;
  display: flex;
  justify-content: space-around;
}

.form-item {
  display: flex;
  align-items: center;
  flex: 1;
}

.button {
  display: flex;
  align-items: center;
  height: 35px;
  line-height: 35px;
  margin-left: 10px;
}
</style>
