<template>
  <div class="wrap">
    <div class="bar">
      <div class="inner" :style="{ width: safe + '%' }"></div>
    </div>
    <div class="text">{{ safe }}%</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ value: { type: Number, default: 0 } })
const safe = computed(() => Math.min(100, Math.max(0, Math.round(props.value || 0))))
</script>

<style scoped>
.wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.bar {
  flex: 1;
  height: 10px;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.70);
  overflow: hidden;
}

.inner {
  height: 100%;
  background: var(--primary);
}

.text {
  width: 44px;
  text-align: right;
  color: var(--muted);
  font-size: 12px;
}
</style>
