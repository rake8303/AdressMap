<template>
  <el-dropdown trigger="click" @command="handleCommand">
    <div class="lang-select">
      <svg-icon icon-class="language" class="lang-icon" />
      <span>{{ currentLabel }}</span>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="zh-CN" :disabled="locale === 'zh-CN'">
          {{ $t('common.chinese') }}
        </el-dropdown-item>
        <el-dropdown-item command="ja-JP" :disabled="locale === 'ja-JP'">
          {{ $t('common.japanese') }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { setLocale } from '@/i18n'

const { locale, t } = useI18n()

const currentLabel = computed(() => locale.value === 'ja-JP' ? t('common.japanese') : t('common.chinese'))

function handleCommand(command) {
  setLocale(command)
}
</script>

<style lang="scss" scoped>
.lang-select {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 100%;
  padding: 0 8px;
  cursor: pointer;
  font-size: 14px;
}

.lang-icon {
  width: 16px;
  height: 16px;
}
</style>
