<template>
  <!-- 登录页容器：背景图 + 遮罩 -->
  <div
      class="auth-container"
      :style="{ '--bg-url': `url(${bgUrl})` }"
  >
    <!-- 主体卡片 -->
    <div class="shell">
      <!-- 左侧信息区 -->
      <div class="left">
        <div class="brand">OCMS</div>
        <div class="slogan">在线课程管理系统</div>

        <ul class="points">
          <li>课程发布 / 报名学习 / 作业考试</li>
          <li>教师端批改与课程管理</li>
          <li>管理员审核与系统配置</li>
        </ul>
      </div>

      <!-- 右侧表单 -->
      <div class="right">
        <el-card
            class="card"
            shadow="never"
        >
          <div class="title">欢迎回来</div>
          <div class="subtitle">使用你的账号登录系统</div>

          <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-position="top"
              class="form"
              @submit.prevent="handleLogin"
          >
            <el-form-item
                label="邮箱"
                prop="email"
            >
              <el-input
                  v-model="form.email"
                  placeholder="请输入邮箱"
                  autocomplete="email"
                  clearable
              />
            </el-form-item>

            <el-form-item
                label="密码"
                prop="password"
            >
              <el-input
                  v-model="form.password"
                  type="password"
                  placeholder="请输入密码"
                  autocomplete="current-password"
                  show-password
                  clearable
              />
            </el-form-item>

            <el-button
                class="btn"
                type="primary"
                :loading="loading"
                native-type="submit"
            >
              登录
            </el-button>

            <div class="footer">
              <span>还没有账号？</span>
              <router-link
                  to="/register"
                  class="link"
              >
                去注册
              </router-link>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { loginApi } from '../../api/auth'
import toast from '../../utils/toast'

import bgUrl from '../../assets/login-bg.png'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)
// 表单数据
const form = reactive({
  email: '',
  password: ''
})

// 表单校验规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }/*什么时候触发校验*/
  ]
}

// 登录逻辑
const handleLogin = async () => {
  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) return

  loading.value = true

  try {
    const payload = {
      email: form.email,
      password: form.password
    }

    const res = await loginApi(payload)

    if (!res || !res.token) {
      throw new Error('服务器未返回 Token')
    }

    userStore.setUser(res)

    toast.success('欢迎回来', `你好，${res.name || '同学'}`)

    const role = String(res.role || '').toUpperCase()
    if (role === 'ADMIN') {
      router.replace('/admin/users')
    } else if (role === 'TEACHER') {
      router.replace('/teacher/dashboard')
    } else {
      router.replace('/student/courses')
    }
  } catch (error) {
    const msg = error?.message === 'Unauthorized'
        ? '邮箱或密码错误'
        : String(error?.message || error)

    toast.error('登录失败', msg)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28px;
  position: relative;
  overflow: hidden;
  --bg-url: none;
}

/* 背景图层 */
.auth-container::before {
  content: "";
  position: absolute;
  inset: 0;/*铺满整个容器*/
  background-image: var(--bg-url);
  background-size: cover;
  background-position: center;
  transform: scale(1.06);
}

/* 注册页背景遮罩层 */
.auth-container::after {
  content: "";
  position: absolute;
  inset: 0;
  background: radial-gradient(900px 520px at 12% 10%, rgba(245, 158, 11, 0.18), transparent 60%),
  radial-gradient(900px 520px at 82% 20%, rgba(16, 185, 129, 0.12), transparent 62%),
  linear-gradient(180deg, rgba(17, 24, 39, 0.35), rgba(17, 24, 39, 0.65));
  z-index: 0;
}

.shell {
  width: 100%;
  max-width: 980px;
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 22px;
  position: relative;
  z-index: 1;
}

/* 左侧信息区 */
.left {
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 16px;
  padding: 22px;
  backdrop-filter: blur(10px);
}

.brand {
  font-size: 22px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.92);
  margin-left: 38px;
}

.slogan {
  margin-top: 6px;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.78);
  align-content: center;
  justify-content: center;
  margin-left: 38px;
}

.points {
  margin-top: 14px;
  padding-left: 16px;
  color: rgba(255, 255, 255, 0.82);
  line-height: 1.9;
  font-size: 15px;
  margin-left: 38px;
}



/* 右侧表单区 */
.right {
  display: flex;
  align-items: center;
  justify-content: center;
}

.card {
  width: 100%;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.55);
  box-shadow: 0 18px 48px rgba(0, 0, 0, 0.22);
}

.title {
  font-size: 20px;
  font-weight: 900;
  color: #111827;
}

.subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: rgba(17, 24, 39, 0.66);
}

.form {
  margin-top: 10px;
}

.btn {
  width: 100%;
  height: 42px;
  border-radius: 12px;
  margin-top: 8px;
}

.footer {
  margin-top: 14px;
  display: flex;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(17, 24, 39, 0.70);
}

.link {
  color: #4f46e5;
  font-weight: 800;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

@media (max-width: 900px) {
  .shell {
    grid-template-columns: 1fr;
    max-width: 520px;
  }

  .left {
    display: none;
  }
}
</style>
