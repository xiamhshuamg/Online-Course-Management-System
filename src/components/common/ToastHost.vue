<template>
  <div class="toast-host">
    <div
        v-for="t in toasts"
        :key="t.id"
        class="toast"
        :class="t.type"
        role="status"
    >
      <div class="title">{{ t.title }}</div>
      <div v-if="t.desc" class="desc">{{ t.desc }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import toast from '../../utils/toast'

/**
 * 说明：
 * - toast.js 里维护一个响应式队列
 * - 这里作为全局宿主展示，不影响任何业务逻辑
 */
const toasts = computed(() => toast.state.items)
</script>

<style scoped>
.toast-host {
  position: fixed;
  top: 16px;
  right: 16px;
  display: flex;
  flex-direction: column;/*竖排*/
  gap: 10px;
  z-index: 9999;
  pointer-events: none;/*容器不被点击*/
}

.toast {
  width: 280px;
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 12px;
  box-shadow: var(--shadow);
  padding: 12px;
  pointer-events: auto;
}

.title {
  font-weight: 700;
}

.desc {
  margin-top: 4px;
  color: var(--muted);
  font-size: 13px;
  line-height: 1.4;
}

.toast.success {
  border-color: rgba(22, 163, 74, 0.35);
}

.toast.warning {
  border-color: rgba(245, 158, 11, 0.35);
}

.toast.danger {
  border-color: rgba(220, 38, 38, 0.35);
}

.toast.info {
  border-color: rgba(59, 130, 246, 0.35);
}
</style>
