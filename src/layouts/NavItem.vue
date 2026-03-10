<template>
  <router-link :to="to" class="nav-item" :class="{ active: isActive }">
    <span class="nav-icon" aria-hidden="true">{{ icon }}</span>
    <span class="nav-label">{{ label }}</span>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps({
  to: { type: String, required: true },
  icon: { type: String, default: '•' },
  label: { type: String, required: true }
})

const route = useRoute()

const isActive = computed(() => {
  return route.path === props.to
})
</script>

<style scoped>
.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 12px;
  color: #374151;
  text-decoration: none;
  transition: background-color 160ms ease, color 160ms ease, transform 160ms ease;
}

.nav-item:hover {
  background-color: rgba(79, 70, 229, 0.08);
  transform: translateX(1px);
}

.nav-item.active {
  background-color: rgba(79, 70, 229, 0.12);
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
