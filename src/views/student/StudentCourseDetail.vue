<template>
  <PageContainer title="课程详情">
    <template #extra>
      <button class="btn-secondary" @click="router.back()">返回</button>
    </template>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="course" class="detail-layout">
      <!-- 课程头部信息 -->
      <div class="header-card card">
        <div class="info-main">
          <span class="category-badge">{{ course.category }}</span>
          <h1 class="main-title">{{ course.title }}</h1>
          <p class="teacher">讲师：{{ course.teacherName }}</p>
          <p class="description">{{ course.description }}</p>
        </div>
        <div class="action-box">
          <div class="price-tag">¥ {{ course.price }}</div>

          <!-- ：根据报名状态显示不同按钮 -->
          <button v-if="isEnrolled" class="btn-enroll success" @click="goToStudy">
            已报名，进入学习
          </button>
          <button v-else class="btn-enroll" @click="handleEnroll">
            立即报名
          </button>

        </div>
      </div>

      <!-- 章节预览 -->
      <div class="content-card card">
        <h3 class="section-title">课程大纲</h3>
        <div class="chapter-list">
          <div v-for="chapter in course.chapters" :key="chapter.id" class="chapter-item">
            <span class="chapter-index">第 {{ chapter.sortOrder }} 章</span>
            <span class="chapter-name">{{ chapter.title }}</span>
            <span class="lock-icon">
              {{ isEnrolled ? '🔓 已解锁' : '🔒 报名后解锁' }}
            </span>
          </div>
          <div v-if="!course.chapters || course.chapters.length === 0" class="empty-text">
            暂无章节信息
          </div>
        </div>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageContainer from '../../components/common/PageContainer.vue'
import { fetchPublicCourseDetail, enrollCourse, fetchMyEnrollments } from '../../api/student'
import toast from '../../utils/toast'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id
const course = ref(null)
const loading = ref(true)
const myEnrollmentList = ref([])

// 计算属性，用于判断当前课程是否已报名
const isEnrolled = computed(() => {
  if (!myEnrollmentList.value || myEnrollmentList.value.length === 0) return false
  // 假设后端返回的列表中包含 courseId 字段，如果是 id 字段请自行调整
  return myEnrollmentList.value.some(item => String(item.courseId) === String(courseId))
})

const loadDetail = async () => {
  loading.value = true
  try {
    // 1. 获取课程详情
    // http.js 已经返回了 data 部分，所以这里直接赋值给 response 即可
    const response = await fetchPublicCourseDetail(courseId);

    // 去掉 .data
    course.value = response;

    // 2. 获取报名记录
    const enrollRes = await fetchMyEnrollments();

    // 去掉 .data，并增加空数组保底
    myEnrollmentList.value = enrollRes || [];

  } catch (error) {
    console.error('Error loading course details:', error);
    toast.error('加载课程详情失败');
  } finally {
    loading.value = false
  }
};

const handleEnroll = async () => {
  if (!confirm(`确认报名 "${course.value.title}" 吗？费用 ¥${course.value.price}`)) return

  try {
    await enrollCourse(courseId)
    toast.success('报名成功', '快去"我的课程"开始学习吧')
    // 重新加载数据以刷新按钮状态
    await loadDetail()
  } catch (e) {
    // 错误在 API 拦截器处理
    console.error(e)
  }
}

const goToStudy = () => {
  router.push(`/student/courses/${courseId}/content`)
}

onMounted(() => {
  loadDetail();
})
</script>

<style scoped>
.detail-layout {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.header-card {
  display: flex;
  justify-content: space-between;
  gap: 40px;
}

.info-main {
  flex: 1;
}

.category-badge {
  background: #e0e7ff;
  color: #4f46e5;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
}

.main-title {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
  margin: 16px 0;
}

.teacher {
  font-size: 15px;
  color: #4b5563;
  margin-bottom: 24px;
}

.description {
  color: #6b7280;
  line-height: 1.6;
  white-space: pre-wrap;
}

.action-box {
  width: 280px;
  background: #f9fafb;
  padding: 24px;
  border-radius: 12px;
  text-align: center;
  height: fit-content;
}

.price-tag {
  font-size: 32px;
  font-weight: 800;
  color: #ef4444;
  margin-bottom: 20px;
}

.btn-enroll {
  width: 100%;
  padding: 14px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-enroll:hover {
  background: #4338ca;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

.btn-enroll.success {
  background: #10b981;
}
.btn-enroll.success:hover {
  background: #059669;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 20px;
  border-left: 4px solid #4f46e5;
  padding-left: 12px;
}

.chapter-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.chapter-index {
  font-weight: 600;
  color: #9ca3af;
  width: 80px;
}

.chapter-name {
  flex: 1;
  font-weight: 500;
  color: #374151;
}

.lock-icon {
  font-size: 12px;
  color: #9ca3af;
}

.btn-secondary {
  padding: 8px 16px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
}

@media (max-width: 768px) {
  .header-card {
    flex-direction: column;
  }
  .action-box {
    width: 100%;
  }
}
</style>