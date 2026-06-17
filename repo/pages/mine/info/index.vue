<template>
  <view class="container">
    <uni-list>
      <uni-list-item showExtraIcon="true" :extraIcon="{ type: 'person-filled' }" title="表示名" :rightText="user.nickName" />
      <uni-list-item showExtraIcon="true" :extraIcon="{ type: 'phone-filled' }" title="電話番号" :rightText="user.phonenumber" />
      <uni-list-item showExtraIcon="true" :extraIcon="{ type: 'email-filled' }" title="メール" :rightText="user.email" />
      <uni-list-item showExtraIcon="true" :extraIcon="{ type: 'auth-filled' }" title="役職" :rightText="postGroup" />
      <uni-list-item showExtraIcon="true" :extraIcon="{ type: 'staff-filled' }" title="ロール" :rightText="roleGroup" />
      <uni-list-item showExtraIcon="true" :extraIcon="{ type: 'calendar-filled' }" title="作成日" :rightText="user.createTime" />
    </uni-list>
  </view>
</template>

<script setup>
import { getUserProfile } from '@/api/system/user'
import { ref } from 'vue'

const user = ref({})
const roleGroup = ref('')
const postGroup = ref('')

function getUser() {
  getUserProfile().then(response => {
    user.value = response.data
    roleGroup.value = response.roleGroup
    postGroup.value = response.postGroup
  })
}

getUser()
</script>

<style lang="scss">
page {
  background-color: #ffffff;
}
</style>
