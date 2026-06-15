<template>
  <el-config-provider :locale="elementLocale">
    <router-view />
  </el-config-provider>
</template>

<script setup>
import { computed, nextTick, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import ja from 'element-plus/es/locale/lang/ja'
import useSettingsStore from '@/store/modules/settings'
import { handleThemeStyle } from '@/utils/theme'

const { locale } = useI18n()
const elementLocale = computed(() => locale.value === 'ja-JP' ? ja : zhCn)

onMounted(() => {
  nextTick(() => {
    // 初始化主题样式
    handleThemeStyle(useSettingsStore().theme)
  })
})
</script>
