<template>
  <div class="layout-wrapper">
    <aside class="sidebar" :class="{ 'mobile-hidden': !sidebarOpen }">
      <div class="sidebar-header">
        <div class="logo">OCMS</div>
        <div class="role-badge">{{ roleText }}</div>
      </div>

      <nav class="sidebar-nav">
        <!-- 管理员菜单 -->
        <template v-if="userStore.isAdmin">
          <NavItem to="/admin/statistics" icon="📊" label="统计概览" />
          <NavItem to="/admin/users" icon="👥" label="用户管理" />
          <NavItem to="/admin/course-review" icon="📑" label="课程审核" />
          <NavItem to="/admin/teacher-review" icon="👨‍🏫" label="教师审核" />
          <NavItem to="/admin/settings" icon="⚙️" label="系统设置" />
        </template>

        <!-- 教师菜单 -->
        <template v-if="userStore.isTeacher">
          <NavItem to="/teacher/dashboard" icon="🖥️" label="工作台" />
          <NavItem to="/teacher/courses" icon="📚" label="课程管理" />
          <NavItem to="/teacher/assignments" icon="📝" label="作业与考试" />
          <NavItem to="/teacher/grading" icon="✍️" label="批改与反馈" />
          <NavItem to="/teacher/discussion" icon="💬" label="讨论区管理" />
        </template>

        <!-- 学生菜单 -->
        <template v-if="userStore.isStudent">
          <NavItem to="/student/courses" icon="📖" label="课程广场" />
          <NavItem to="/student/my-courses" icon="🎓" label="我的课程" />
          <NavItem to="/student/assignments" icon="✍️" label="作业与考试" />
          <NavItem to="/student/progress" icon="📈" label="学习进度" />
          <NavItem to="/student/discussion" icon="💬" label="讨论区" />
        </template>
      </nav>

      <div class="sidebar-footer">
        <div class="user-row">
          <div class="user-name">{{ userStore.user?.name || '未登录' }}</div>
          <button class="logout-btn" @click="handleLogout">退出</button>
        </div>
      </div>
    </aside>

    <div
        v-if="sidebarOpen"
        class="sidebar-overlay"
        @click="sidebarOpen = false"
    />

    <main class="main-content">
      <header class="top-bar">
        <div class="top-left">
          <button class="menu-btn" @click="sidebarOpen = true">☰</button>
          <div class="top-title">OCMS</div>
        </div>
      </header>

      <div class="page-wrap">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import NavItem from './NavItem.vue'
import toast from '../utils/toast'

const userStore = useUserStore()
const router = useRouter()
const sidebarOpen = ref(false)

const roleText = computed(() => {
  if (userStore.isAdmin) return '管理员'
  if (userStore.isTeacher) return '教师'
  return '学生'
})

const handleLogout = () => {
  userStore.clearUser()
  toast.success('已退出登录')
  router.replace('/login')
}
</script>

<style scoped>
.layout-wrapper {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background: linear-gradient(180deg, rgba(79, 70, 229, 0.06), rgba(17, 24, 39, 0.02));
}

/* 侧边栏样式 */
.sidebar {
  width: 260px;
  background: rgba(248, 250, 252, 0.92);
  border-right: 1px solid rgba(17, 24, 39, 0.10);
  display: flex;/*禁止被挤压变窄*/
  flex-direction: column;
  flex-shrink: 0;
  box-shadow: 10px 0 40px rgba(17, 24, 39, 0.06);
  backdrop-filter: blur(10px);/*模糊背景*/
}

/* 侧边栏头部 */
.sidebar-header {
  padding: 18px 18px 12px 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-weight: 900;
  font-size: 20px;
  letter-spacing: 1px;/*增加字符间距*/
  color: #111827;
}

.role-badge {
  font-size: 12px;
  font-weight: 800;
  padding: 6px 10px;
  border-radius: 999px;
  color: rgba(79, 70, 229, 0.92);
  background: rgba(79, 70, 229, 0.10);
  border: 1px solid rgba(79, 70, 229, 0.16);
}

/* 菜单 */
.sidebar-nav {
  padding: 8px 10px 10px 10px;
  overflow-y: auto;/*竖向滚动条*/
  flex: 1;
}

/* 底部用户区 */
.sidebar-footer {
  padding: 12px 16px 16px 16px;
  border-top: 1px solid rgba(17, 24, 39, 0.08);
}

.user-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.user-name {
  font-size: 13px;
  font-weight: 800;
  color: rgba(17, 24, 39, 0.78);
  overflow: hidden;
  text-overflow: ellipsis;/*...*/
  white-space: nowrap;/*不换行*/
}

.logout-btn {
  font-size: 13px;
  font-weight: 800;
  color: #ef4444;
  background: rgba(239, 68, 68, 0.08);
  border: 1px solid rgba(239, 68, 68, 0.18);
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 10px;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.12);
  transform: translateY(-1px);
}

/* 主内容区 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部栏 */
.top-bar {
  height: 54px;
  display: none;/*响应式隐藏*/
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  background: rgba(248, 250, 252, 0.88);
  border-bottom: 1px solid rgba(17, 24, 39, 0.08);
  backdrop-filter: blur(10px);
}

.top-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.menu-btn {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  border: 1px solid rgba(17, 24, 39, 0.10);
  background: rgba(255, 255, 255, 0.75);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.menu-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(17, 24, 39, 0.10);
}

.top-title {
  font-weight: 900;
  color: #111827;
  letter-spacing: 1px;
}

/* 页面内容容器 */
.page-wrap {
  flex: 1;
  overflow: auto;
  background: transparent;
}

/* 移动端：侧边栏抽屉模式 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 50;
    transform: translateX(0);
    transition: transform 0.25s ease;
  }

  .mobile-hidden {
    transform: translateX(-100%);
  }

  .sidebar-overlay {
    position: fixed;
    inset: 0;
    background: rgba(17, 24, 39, 0.45);
    z-index: 40;
    backdrop-filter: blur(2px);
  }

  .top-bar {
    display: flex;
  }
}
</style>
