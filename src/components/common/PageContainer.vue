<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">{{ title }}</h2>
        <p v-if="desc" class="page-desc">{{ desc }}</p>
      </div>

      <!--  恢复：右侧操作区（按钮/筛选器） -->
      <div class="header-right">
        <slot name="extra" />
      </div>
    </div>

    <div class="page-content">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  desc: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.page-container {
  padding: 24px;
  margin: 0 auto;          /*  居中 */
  width: 100%;
  max-width: 1200px;       /*  限制右侧内容最大宽度 */
  box-sizing: border-box;  /*  防止 padding 撑爆 */
}


.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.page-title {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: #111827;
}

.page-desc {
  margin: 0;
  font-size: 13px;
  color: rgba(17, 24, 39, 0.65);
  line-height: 1.5;
}

.header-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  flex-wrap: wrap;
}

.page-content {
  width: 100%;
}
</style>
