<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script setup>
const url = ref('http://doc.販売店代理店/ruoyi-vue')

function goto() {
  window.open(url.value)
}
</script>