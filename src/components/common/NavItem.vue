<template>
  <router-link
      :to="to"
      class="nav-item"
      :class="{ active: isActive }"
  >
    <span class="nav-icon" aria-hidden="true">{{ icon }}</span>
    <span class="nav-label">{{ label }}</span>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps({
  to: { type: [String, Object], required: true }, // 跳转目标
  icon: { type: String, default: '•' }, // 简单图标
  label: { type: String, required: true } // 文本
})

const route = useRoute()

// 当前路由匹配就高亮
const isActive = computed(() => {
  const target = typeof props.to === 'string'
      ? props.to
      : (props.to?.path || '')

  if (!target) return false

  // /student/courses 或 /student/courses/123 都算激活
  return route.path === target || route.path.startsWith(target + '/')
})
</script>

<style scoped>
/* 导航项容器 */
.nav-item {
  display: flex;  /* 横向布局 */
  align-items: center;
  gap: 10px; /* 图标与文字间距 */
  padding: 12px 14px;  /* 内边距 */
  border-radius: 12px;
  text-decoration: none;
  color: #334155;
  transition: all 0.18s ease;
}

/* 悬停状态 */
.nav-item:hover {
  background: rgba(255, 255, 255, 0.45);
  color: #0f172a;
}

/* 激活状态 */
.nav-item.active {
  background: rgba(79, 70, 229, 0.12);
  border: 1px solid rgba(79, 70, 229, 0.18);
  color: #4f46e5;
}

.nav-icon {
  width: 22px;
  text-align: center;
  font-size: 16px;
}
.nav-label {
  font-size: 14px;
  font-weight: 600;
}
</style>