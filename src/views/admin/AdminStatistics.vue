<template>
  <PageContainer title="统计概览" desc="查看平台核心运营数据">
    <div v-if="loading" class="loading-state">加载中...</div>

    <div v-else class="stats-grid">
      <!-- 统计卡片 -->
      <div class="stat-card">
        <div class="stat-label">总学生数</div>
        <div class="stat-value">{{ stats.students }}</div>
        <div class="stat-icon bg-blue">👥</div>
      </div>

      <div class="stat-card">
        <div class="stat-label">总教师数</div>
        <div class="stat-value">{{ stats.teachers }}</div>
        <div class="stat-icon bg-yellow">👨‍🏫</div>
      </div>

      <div class="stat-card">
        <div class="stat-label">总报名数</div>
        <div class="stat-value">{{ stats.enrollments }}</div>
        <div class="stat-icon bg-green">📝</div>
      </div>

      <div class="stat-card">
        <div class="stat-label">总收入</div>
        <div class="stat-value">¥ {{ stats.revenue }}</div>
        <div class="stat-icon bg-purple">💰</div>
      </div>

      <div class="stat-card">
        <div class="stat-label">待审核课程</div>
        <div class="stat-value pending">{{ stats.coursePending }}</div>
        <div class="stat-icon bg-gray">📋</div>
      </div>

      <div class="stat-card">
        <div class="stat-label">待审核教师</div>
        <div class="stat-value pending">{{ stats.teacherPending }}</div>
        <div class="stat-icon bg-orange">⏳</div>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PageContainer from '../../components/common/PageContainer.vue'
import { fetchOverview } from '../../api/admin'

const loading = ref(true)
const stats = ref({
  students: 0,
  teachers: 0,
  admins: 0,
  teacherPending: 0,
  coursePending: 0,
  enrollments: 0,
  revenue: 0
})

const loadStats = async () => {
  loading.value = true
  try {
    const res = await fetchOverview()
    stats.value = res
  } catch (error) {
    // 错误处理已在 http 拦截器
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.loading-state {
  text-align: center;
  padding: 40px;
  color: #6b7280;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
  border: 1px solid #f3f4f6;
}

.stat-label {
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
}

.stat-value.pending {
  color: #f59e0b; /* 黄色高亮待处理项 */
}

.stat-icon {
  position: absolute;
  right: 16px;
  bottom: 16px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 24px;
  opacity: 0.2;
}

.bg-blue {
  background: #3b82f6;
  color: #1e40af;
}
.bg-yellow {
  background: #f59e0b;
  color: #92400e;
}
.bg-green {
  background: #10b981;
  color: #065f46;
}
.bg-purple {
  background: #8b5cf6;
  color: #5b21b6;
}
.bg-gray {
  background: #6b7280;
  color: #111827;
}
.bg-orange {
  background: #f97316;
  color: #9a3412;
}
</style>