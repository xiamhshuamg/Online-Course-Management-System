<template>
  <PageContainer title="教师工作台" desc="欢迎回来，这里是您的教学概览。">

    <div v-if="loading" class="loading-box">
      数据加载中...
    </div>

    <div v-else>
      <!-- 顶部统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="label">已发布课程</div>
          <div class="value">{{ stats.courseCount || 0 }}</div>
          <div class="icon">📚</div>
        </div>

        <div class="stat-card">
          <div class="label">待审核/草稿</div>
          <div class="value pending">{{ stats.pendingCourses || 0 }}</div>
          <div class="icon">⏳</div>
        </div>

        <div class="stat-card">
          <div class="label">待批改作业</div>
          <div class="value pending">{{ stats.assignmentCount || 0 }}</div>
          <div class="icon">📝</div>
        </div>

        <div class="stat-card">
          <div class="label">学生报名总数</div>
          <div class="value">{{ stats.enrollCount || 0 }}</div>
          <div class="icon">👥</div>
        </div>
      </div>

      <!-- 新增：高性能运营数据报表 -->
      <div class="section-header">
        <h3>📊 课程运营数据 (实时视图)</h3>
        <button class="btn-sm" @click="loadDetailedStats">刷新数据</button>
      </div>

      <div class="card table-wrapper">
        <table class="data-table">
          <thead>
          <tr>
            <th>课程名称</th>
            <th>教师</th>
            <th>状态</th>
            <th>报名人数</th>
            <th>总收入</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="item in detailedStats" :key="item.courseId">
            <td class="font-medium">{{ item.courseTitle }}</td>
            <td>{{ item.teacherName }}</td>
            <td>
              <span :class="['status-dot', getStatusColor(item.courseStatus)]"></span>
              {{ item.courseStatus }}
            </td>
            <td>{{ item.enrolledCount }} 人</td>
            <td class="money">¥ {{ item.totalRevenue }}</td>
          </tr>
          <tr v-if="detailedStats.length === 0">
            <td colspan="5" class="muted text-center">暂无详细数据</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PageContainer from '../../components/common/PageContainer.vue'
import { fetchTeacherOverview } from '../../api/teacher'
import { fetchCourseStatsView } from '../../api/opt' // 引入新接口
import toast from '../../utils/toast'

const stats = ref({})
const detailedStats = ref([])
const loading = ref(true)

const loadOverview = async () => {
  try {
    stats.value = await fetchTeacherOverview()
  } catch (e) {
    console.error(e)
  }
}

const loadDetailedStats = async () => {
  try {
    // 调用高性能视图接口
    detailedStats.value = await fetchCourseStatsView()
  } catch (e) {
    console.error("加载详细统计失败", e)
    toast.error('加载运营数据失败')
  }
}

const getStatusColor = (status) => {
  if (status === 'PUBLISHED') return 'green'
  if (status === 'PENDING_APPROVAL') return 'orange'
  return 'gray'
}

onMounted(async () => {
  loading.value = true
  await Promise.all([loadOverview(), loadDetailedStats()])
  loading.value = false
})
</script>

<style scoped>
.loading-box {
  padding: 40px;
  text-align: center;
  color: #6b7280;
  font-size: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  position: relative;
  border: 1px solid #f3f4f6;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.label {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.value {
  font-size: 32px;
  font-weight: 700;
  color: #111827;
}

.value.pending { color: #f59e0b; }

.icon {
  position: absolute;
  right: 20px;
  bottom: 20px;
  font-size: 24px;
  opacity: 0.2;
}

/* 新增样式 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  font-size: 18px;
  color: #111827;
  margin: 0;
}

.btn-sm {
  padding: 6px 12px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.btn-sm:hover {
  background: #f9fafb;
}

.table-wrapper {
  background: white;
  border-radius: 12px;
  border: 1px solid #f3f4f6;
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th, .data-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #f3f4f6;
}

.data-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #6b7280;
  font-size: 13px;
}

.font-medium {
  font-weight: 600;
  color: #374151;
}

.money {
  font-family: monospace;
  font-weight: 600;
  color: #059669;
}

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
}
.status-dot.green { background: #10b981; }
.status-dot.orange { background: #f59e0b; }
.status-dot.gray { background: #9ca3af; }

.text-center { text-align: center; }
.muted { color: #9ca3af; }
</style>