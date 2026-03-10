<template>
  <PageContainer title="课程管理" desc="管理您发布的课程，添加章节与资源">
    <template #extra>
      <button class="btn-refresh" @click="goToCreate">创建新课程</button>
    </template>

    <div class="card">
      <table class="data-table">
        <thead>
        <tr>
          <th>课程名称</th>
          <th>分类</th>
          <th>价格</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="course in courseList" :key="course.id">
          <td class="font-medium">{{ course.title }}</td>
          <td>{{ formatCategory(course.category) }}</td>
          <td>¥ {{ course.price }}</td>
          <td>
            <span :class="['status-badge', String(course.status || '').toLowerCase()]">
              {{ formatStatus(course.status) }}
            </span>
          </td>
          <td>
            <div class="actions">
              <button class="btn" @click="goToEdit(course.id)">管理内容</button>
              <!-- 新增删除按钮 -->
              <button class="btn danger" @click="handleDelete(course)">删除</button>
            </div>
          </td>
        </tr>

        <tr v-if="courseList.length === 0">
          <td colspan="5" class="empty-text">暂无课程，快去创建吧</td>
        </tr>
        </tbody>
      </table>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PageContainer from '../../components/common/PageContainer.vue'
import { fetchTeacherCourses } from '../../api/teacher'
import { cascadeDeleteCourse } from '../../api/opt' // 引入删除接口
import { formatCourseCategory } from '../../utils/courseCategory'
import toast from '../../utils/toast'

const router = useRouter()
const courseList = ref([])

const loadCourses = async () => {
  try {
    const res = await fetchTeacherCourses()
    // 过滤掉已删除的（虽然接口可能已经过滤了，但为了保险起见）
    courseList.value = (res || []).filter(c => c.status !== 'DELETED')
  } catch (e) {
    // http 拦截器里应该已经 toast 过了
  }
}

const goToCreate = () => {
  router.push('/teacher/course-edit')
}

const goToEdit = (id) => {
  router.push(`/teacher/course-edit?id=${id}`)
}

const handleDelete = async (course) => {
  if (!confirm(`确定要删除课程「${course.title}」吗？\n注意：此操作将级联删除相关作业和报名记录（软删除）。`)) return

  try {
    await cascadeDeleteCourse(course.id)
    toast.success('删除成功')
    await loadCourses()
  } catch (e) {
    toast.error('删除失败', e.message)
  }
}

const formatCategory = (val) => {
  return formatCourseCategory(val)
}

const formatStatus = (status) => {
  const map = {
    'DRAFT': '草稿',
    'PENDING_APPROVAL': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'PUBLISHED': '已发布',
    'DELETED': '已删除'
  }
  return map[status] || status
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.card {
  background: rgba(255, 255, 255, 0.92);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 10px 28px rgba(17, 24, 39, 0.10);
  border: 1px solid rgba(17, 24, 39, 0.08);
}

.actions {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 8px 12px;
  border-radius: 10px;
  border: 1px solid rgba(17, 24, 39, 0.14);
  background: rgba(255, 255, 255, 0.80);
  cursor: pointer;
  font-weight: 800;
}

.btn.primary {
  background: rgba(79, 70, 229, 0.92);
  border-color: rgba(79, 70, 229, 0.35);
  color: #fff;
}

.btn.danger {
  background: rgba(254, 226, 226, 0.6);
  border-color: rgba(239, 68, 68, 0.2);
  color: #b91c1c;
}
.btn.danger:hover {
  background: rgba(254, 226, 226, 1);
}

.btn-refresh {
  padding: 8px 16px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}
.btn-refresh:hover {
  background: #f9fafb;
}

.empty-text {
  text-align: center;
  padding: 20px;
  color: #6b7280;
}
</style>