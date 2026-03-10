<template>
  <router-link :to="to" class="item" :class="{ active }">
    <div class="dot"></div>
    <div class="label">{{ label }}</div>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps({
  to: { type: String, required: true },
  label: { type: String, required: true }
})

const route = useRoute()
const active = computed(() => route.path === props.to)
</script>

<style scoped>
.item{
  display:flex;
  align-items:center;
  gap: 10px;
  padding: 14px 16px;
  margin: 4px 0;
  border-radius: 12px;
  transition: .15s;
  color: #475569;
}
.item:hover{
  background: #f8fafc; /* 悬停时使用极浅灰色 */
  color: var(--primary)
}
.item.active{
  background: #eff6ff; /* 激活状态使用浅蓝色背景 */
  color: var(--primary);
}
.dot{
  width: 8px; height: 8px;
  border-radius: 999px;
  background: #cbd5e1;
}
.item.active .dot{ background: rgba(37,99,235,.95); }
.label{ font-weight: 650; font-size: 16px; }
</style>

