import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 1. 初始化时，优先从 localStorage 读取
  const token = ref(localStorage.getItem('token') || '')

  // 安全地解析 user，防止 JSON 错误
  let storedUser = {}
  try {
    storedUser = JSON.parse(localStorage.getItem('user') || '{}')
  } catch (e) {
    storedUser = {}
  }
  const user = ref(storedUser)

  // --- Getters (计算属性) ---
  // AppLayout.vue 依赖这些属性来显示不同的菜单
  const isAdmin = computed(() => (user.value.role || '').toUpperCase() === 'ADMIN')
  const isTeacher = computed(() => (user.value.role || '').toUpperCase() === 'TEACHER')
  const isStudent = computed(() => {
    const role = (user.value.role || '').toUpperCase()
    // 如果角色是 STUDENT，或者已登录但角色为空(默认为学生)，则返回 true
    return role === 'STUDENT'
  })

  // 补充 userName getter，防止侧边栏底部显示空白
  const userName = computed(() => user.value.name || '未登录')

  // 2. 登录成功时，保存 Token 到本地
  function setUser(data) {
    token.value = data.token
    user.value = {
      id: data.userId,
      name: data.name,
      email: data.email,
      role: data.role
    }
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  // 3. 退出登录
  function clearUser() {
    token.value = ''
    user.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 必须将新增的 computed 属性导出
  return {
    token,
    user,
    setUser,
    clearUser,
    isAdmin,
    isTeacher,
    isStudent,
    userName
  }
})