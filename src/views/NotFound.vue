<!-- FILE: src/views/NotFound.vue -->
<template>
  <div class="wrap">
    <el-empty description="页面不存在">
      <el-button type="primary" @click="goHome">回到首页</el-button>
      <el-button @click="goLogin">去登录</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.replace('/home')
}

const goLogin = () => {
  router.replace('/login')
}
</script>

<style scoped>
/* 容器 */
.wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 18px;
}
</style>