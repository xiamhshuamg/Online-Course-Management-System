<!-- FILE: src/views/HomeRedirect.vue -->
<template>
  <div class="wrap">
    <el-card class="card" shadow="hover">
      <div class="title">正在进入系统...</div>
      <div class="desc">如果你停留在这里超过 2 秒，说明登录态或角色信息有问题，可以点下面按钮手动进入。</div>
      <el-button class="btn" type="primary" @click="goDefault">手动进入</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

function safeReplace(path) {
  router.replace(path).catch(() => {
    window.location.href = path
  })
}

function readRoleFromLocal() {
  try {
    const u = JSON.parse(localStorage.getItem('user') || '{}')
    return String(u?.role || '')
  } catch (e) {
    return ''
  }
}

function goDefault() {
  try {
    const token = userStore.token || localStorage.getItem('token') || ''
    if (!token) {
      safeReplace('/login')
      return
    }

    const role = String(userStore.user?.role || readRoleFromLocal()).toUpperCase()

    if (role === 'ADMIN') {
      safeReplace('/admin/statistics')
      return
    }

    if (role === 'TEACHER') {
      safeReplace('/teacher/dashboard')
      return
    }

    safeReplace('/student/courses')
  } catch (e) {
    // 出错就兜底去登录，避免卡死
    safeReplace('/login')
  }
}

onMounted(() => {
  // 给 UI 一帧时间渲染，避免你看到“按钮点不动”的假象
  setTimeout(goDefault, 0)
})
</script>

<style scoped>
.wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 18px;
}
.card {
  width: 520px;
  border-radius: 16px;
}
.title {
  font-size: 18px;
  font-weight: 800;
  color: #111827;
}
.desc {
  margin-top: 8px;
  font-size: 13px;
  color: var(--muted);
}
.btn {
  margin-top: 14px;
}
</style>
