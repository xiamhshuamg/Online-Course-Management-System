<template>
  <div class="auth-container">
    <div class="auth-overlay"></div>

    <div class="auth-content">
      <div class="register-card">
        <h2>创建账号</h2>
        <p class="subtitle">注册后即可开始学习/管理课程</p>

        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label>姓名</label>
            <input v-model="form.name" type="text" placeholder="请输入姓名" required />
          </div>

          <div class="form-group">
            <label>邮箱</label>
            <input v-model="form.email" type="email" placeholder="请输入邮箱" required />
          </div>

          <div class="form-group">
            <label>密码</label>
            <input v-model="form.password" type="password" placeholder="请输入密码" required />
          </div>

          <div class="form-group">
            <label>身份</label>
            <select v-model="form.role">
              <option value="STUDENT">学生</option>
              <option value="TEACHER">教师</option>
            </select>
          </div>

          <div class="form-group">
            <label>简介</label>
            <textarea v-model="form.bio" placeholder="简单介绍一下你自己"></textarea>
          </div>

          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? "注册中..." : "注册" }}
          </button>
        </form>

        <p class="switch">
          已有账号？
          <a href="#" @click.prevent="goLogin">去登录</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue"
import { useRouter } from "vue-router"
import { registerApi } from "../../api/auth"
import toast from "../../utils/toast"
import { useUserStore } from "../../store/user"

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)

const form = reactive({
  name: "",
  email: "",
  password: "",
  role: "STUDENT",
  bio: ""
})

const goLogin = () => {
  router.replace("/login").catch(() => {
    window.location.href = "/login"
  })
}

const handleRegister = async () => {
  loading.value = true
  try {
    const payload = { ...form }
    const res = await registerApi(payload)
    userStore.setUser(res)
    toast.success("注册成功", "欢迎加入平台")
    router.replace("/home").catch(() => {
      window.location.href = "/home"
    })
  } catch (err) {
    toast.error("注册失败", err?.message || "请检查输入内容")
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  width: 100vw;
  height: 100vh;
  position: relative;
  background-image: url("src/assets/login-bg.png");
  background-size: cover;
  background-position: center;
  overflow: hidden;
}

.auth-content {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.register-card {
  width: 460px;
  padding: 26px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.10);
  border: 1px solid rgba(255, 255, 255, 0.22);
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.18);
  backdrop-filter: blur(10px);
}

h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: rgba(17, 24, 39, 0.92);
}

.subtitle {
  margin-top: 6px;
  margin-bottom: 18px;
  color: rgba(17, 24, 39, 0.58);
  font-size: 13px;
}

.form-group {
  margin-bottom: 14px;
}

label {
  display: block;
  margin-bottom: 6px;
  font-size: 13px;
  font-weight: 700;
  color: rgba(17, 24, 39, 0.78);
}

input {
  width: 100%;
  padding: 10px;
  border-radius: 12px;
  border: 1px solid rgba(17, 24, 39, 0.12);
  background: rgba(255, 255, 255, 0.78);
  outline: none;
}

select {
  width: 100%;
  padding: 10px;
  border-radius: 12px;
  border: 1px solid rgba(17, 24, 39, 0.12);
  background: rgba(255, 255, 255, 0.78);
  outline: none;
}

textarea {
  width: 100%;
  padding: 10px;
  border-radius: 12px;
  border: 1px solid rgba(17, 24, 39, 0.12);
  background: rgba(255, 255, 255, 0.78);
  outline: none;/*去掉浏览器默认的焦点外框*/
  min-height: 86px;
  resize: vertical;/*上下拖动改变高度*/
}

.btn-primary {
  width: 100%;
  padding: 12px;
  border-radius: 12px;
  border: none;
  background: #4f46e5;
  color: #fff;
  font-weight: 800;
  cursor: pointer;
}

.btn-primary:disabled {
  opacity: 0.70;
  cursor: not-allowed;
}

.switch {
  margin-top: 16px;
  text-align: center;
  font-size: 13px;
  color: rgba(17, 24, 39, 0.65);
}

.switch a {
  color: #4f46e5;
  font-weight: 800;
  text-decoration: none;
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
</style>
