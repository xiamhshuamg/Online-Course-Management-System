<template>
  <PageContainer title="我的课程" desc="已报名的课程列表">
    <div class="course-list">
      <div v-for="item in list" :key="item.enrollmentId" class="my-course-card">
        <div class="card-info">
          <h3 class="course-name">{{ item.courseTitle }}</h3>
          <div class="meta-row">
            <span class="status-active">学习中</span>
            <span class="paid-amount">实付: ¥{{ item.paidAmount }}</span>
          </div>
        </div>
        <div class="card-actions">
          <button class="btn-learn" @click="goToContent(item.courseId)">
            进入学习 →
          </button>
          <button class="btn-homework" @click="goToAssignments(item.courseId)">
            查看作业
          </button>
        </div>
      </div>

      <div v-if="list.length === 0" class="empty-state">
        <p>你还没有报名任何课程</p>
        <button class="btn-go" @click="router.push('/student/courses')">去选课</button>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PageContainer from '../../components/common/PageContainer.vue'
import { fetchMyEnrollments } from '../../api/student'

const router = useRouter()
const list = ref([])

const loadData = async () => {
  try {
    const res = await fetchMyEnrollments()
    // res 是 List<MyEnrollmentResponse>
    list.value = res || []
  } catch (e) {
    console.error(e)
  }
}

const goToContent = (courseId) => {
  router.push(`/student/courses/${courseId}/content`)
}

const goToAssignments = (courseId) => {
  router.push(`/student/assignments?courseId=${courseId}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.course-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.my-course-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #f3f4f6;
  transition: box-shadow 0.2s;
}

.my-course-card:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.course-name {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 8px 0;
}

.meta-row {
  display: flex;
  gap: 12px;
  font-size: 14px;
}

.status-active {
  color: #059669;
  background: #d1fae5;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.paid-amount {
  color: #6b7280;
}

.card-actions {
  display: flex;
  gap: 12px;
}

.btn-learn {
  padding: 10px 20px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

.btn-learn:hover {
  background: #4338ca;
}

.btn-homework {
  padding: 10px 16px;
  background: white;
  color: #4b5563;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
}

.btn-homework:hover {
  border-color: #4f46e5;
  color: #4f46e5;
}

.empty-state {
  text-align: center;
  padding: 60px;
  background: white;
  border-radius: 12px;
  color: #6b7280;
}

.btn-go {
  margin-top: 16px;
  padding: 10px 24px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

@media (max-width: 640px) {
  .my-course-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  .card-actions {
    width: 100%;
  }
  .btn-learn, .btn-homework {
    flex: 1;
  }
}
</style>