<template>
  <div class="app-root">
    <router-view />
    <!-- 全局 Toast 宿主：toast.success/error 才能显示 -->
    <ToastHost />
  </div>
</template>

<script setup>
import ToastHost from './components/common/ToastHost.vue'
</script>

<style>
.app-root {
  min-height: 100vh;
}
</style>
