<template>
  <PageContainer title="课程广场" desc="发现感兴趣的课程，开启学习之旅">
    <div class="filter-bar card">
      <div class="filter-item">
        <label>关键词</label>
        <input v-model="filters.q" class="search-input" placeholder="搜索课程标题..." @keyup.enter="loadCourses" />
      </div>
      <div class="filter-item">
        <label>分类</label>
        <select v-model="filters.category" class="filter-select" @change="loadCourses">
          <option value="">全部</option>
          <option value="programming">编程</option>
          <option value="math">数学</option>
          <option value="language">语言</option>
          <option value="science">科学</option>
        </select>
      </div>
      <button class="btn-primary" @click="loadCourses">搜索</button>
    </div>

    <!-- 修复：调整网格布局，美化卡片样式 -->
    <div class="course-grid">
      <div v-for="course in list" :key="course.id" class="course-card" @click="goToDetail(course.id)">
        <div class="card-cover">
          <!-- 这里使用渐变色 -->
          <span class="category-tag">{{ course.categoryName || course.category }}</span>
        </div>
        <div class="card-body">
          <h3 class="course-title" :title="course.title">{{ course.title }}</h3>
          <p class="teacher-name">
            <span class="icon">👨‍🏫</span> {{ course.teacherName }}
          </p>
          <div class="card-footer">
            <span class="price">¥ {{ course.price }}</span>
            <button class="btn-outline">查看详情</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="list.length === 0 && !loading" class="empty-state">
      暂无相关课程
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PageContainer from '../../components/common/PageContainer.vue'
import { fetchPublicCourses } from '../../api/student'

const router = useRouter()
const list = ref([])
const loading = ref(false)
const filters = ref({
  q: '',
  category: ''
})

const loadCourses = async () => {
  loading.value = true
  try {
    const res = await fetchPublicCourses(filters.value)
    list.value = res || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/student/courses/${id}`)
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
/* css 一行一个样式 */
.filter-bar {
  display: flex;
  gap: 16px;
  padding: 24px;
  align-items: flex-end;
  margin-bottom: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  flex-wrap: wrap;
  border: 1px solid #f3f4f6;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.search-input {
  padding: 10px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  min-width: 240px;
  outline: none;
  transition: border-color 0.2s;
}

.filter-select {
  padding: 10px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  min-width: 150px;
  outline: none;
  transition: border-color 0.2s;
  background-color: white;
}

.search-input:focus {
  border-color: #4f46e5;
}

.filter-select:focus {
  border-color: #4f46e5;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

/* 修复：美化课程框，去掉了之前的丑边框，增加了阴影层次 */
.course-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #f3f4f6;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 20px -5px rgba(0, 0, 0, 0.1);
  border-color: #e0e7ff;
}

.card-cover {
  height: 160px;
  background: linear-gradient(135deg, #818cf8 0%, #4f46e5 100%);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-tag {
  background: rgba(255, 255, 255, 0.95);
  padding: 6px 14px;
  border-radius: 99px;
  font-size: 13px;
  font-weight: 600;
  color: #4f46e5;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.card-body {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.course-title {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
  height: 54px;
}

.teacher-name {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.card-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f3f4f6;
}

.price {
  font-size: 20px;
  font-weight: 800;
  color: #ef4444;
}

.btn-primary {
  padding: 10px 24px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.2s;
}

.btn-primary:hover {
  background-color: #4338ca;
}

.btn-outline {
  padding: 8px 16px;
  background: transparent;
  border: 1px solid #e5e7eb;
  color: #4b5563;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-outline:hover {
  border-color: #4f46e5;
  color: #4f46e5;
  background-color: #eef2ff;
}

.empty-state {
  text-align: center;
  padding: 60px;
  color: #9ca3af;
  background: white;
  border-radius: 12px;
  margin-top: 20px;
}
</style>