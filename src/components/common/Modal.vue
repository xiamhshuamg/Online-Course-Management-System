<template>
  <teleport to="body">
    <div
        v-if="isShown"
        class="modal-mask"
        role="dialog"
        aria-modal="true"
        @click.self="onCancel"
    >
      <div class="modal-card" :style="{ width }">
        <div class="modal-header">
          <div class="modal-title">{{ title }}</div>
          <button v-if="showClose" class="icon-btn" @click="onCancel" aria-label="关闭">
            ×
          </button>
        </div>

        <div class="modal-body">
          <slot />
        </div>

        <div v-if="!hideFooter" class="modal-footer">
          <slot name="footer">
            <button class="btn" @click="onCancel">取消</button>
            <button class="btn primary" @click="onConfirm">确定</button>
          </slot>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: { type: String, default: '' },

  /* 兼容三种控制方式：
     v-model:visible
     v-model (modelValue)
     open
  */
  visible: { type: Boolean, default: undefined },
  modelValue: { type: Boolean, default: undefined },
  open: { type: Boolean, default: false },

  width: { type: String, default: '720px' },
  showClose: { type: Boolean, default: true },
  hideFooter: { type: Boolean, default: false }
})

const emit = defineEmits([
  'update:visible',
  'update:modelValue',
  'close',
  'confirm',
  'cancel'
])

const isShown = computed(() => {
  if (props.visible !== undefined) return props.visible
  if (props.modelValue !== undefined) return props.modelValue
  return props.open
})

function closeAll() {
  emit('update:visible', false)
  emit('update:modelValue', false)
  emit('close')
}

function onCancel() {
  emit('cancel')
  closeAll()
}

function onConfirm() {
  emit('confirm')
}
</script>

<style scoped>
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 18px;
  z-index: 9999;
}

.modal-card {
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(17, 24, 39, 0.12);
  border-radius: 16px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.18);
  overflow: hidden;
  max-height: 86vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(17, 24, 39, 0.08);
}

.modal-title {
  font-size: 16px;
  font-weight: 800;
  color: #111827;
}

.icon-btn {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  border: 1px solid rgba(17, 24, 39, 0.12);
  background: rgba(255, 255, 255, 0.75);
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
}

.modal-body {
  padding: 16px;
  overflow: auto;
}

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid rgba(17, 24, 39, 0.08);
}

/*  给全项目一个基础按钮样式 */
.btn {
  padding: 8px 12px;
  border-radius: 10px;
  border: 1px solid rgba(17, 24, 39, 0.14);
  background: rgba(255, 255, 255, 0.80);
  cursor: pointer;
  font-weight: 700;
}

.btn:hover {
  background: rgba(255, 255, 255, 0.95);
}

.btn.primary {
  background: rgba(79, 70, 229, 0.92);
  border-color: rgba(79, 70, 229, 0.35);
  color: #ffffff;
}

.btn.primary:hover {
  background: rgba(79, 70, 229, 1);
}
</style>
