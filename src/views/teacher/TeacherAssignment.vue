<template>
  <PageContainer title="作业与考试管理" desc="选择课程以管理其下的作业和考试。">
    <template #extra>
      <button class="btn-refresh" @click="goToCreate">发布新作业</button>
    </template>

    <div class="card" style="padding: 16px; margin-bottom: 20px;">
      <div class="filter-row">
        <label>当前课程：</label>
        <select v-model="selectedCourseId" class="select" style="min-width: 250px;">
          <option value="">-- 请选择课程 --</option>
          <option v-for="c in courses" :key="c.id" :value="c.id">{{ c.title }}</option>
        </select>

        <button v-if="selectedCourseId" class="btn-refresh" @click="loadAssignments" style="margin-left: 10px">
          刷新列表
        </button>
      </div>
    </div>

    <div class="card">
      <div v-if="!selectedCourseId" class="empty-tip">
        请先选择一个课程来查看或管理作业
      </div>

      <div v-else>
        <div v-if="loading" class="empty-tip">
          正在加载作业列表...
        </div>

        <div v-else>
          <table class="table" v-if="assignments.length > 0">
            <thead>
            <tr>
              <th>ID</th>
              <th>作业名称</th>
              <th>类型</th>
              <th>截止时间</th>
              <th>满分</th>
              <th style="width: 200px">操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="item in assignments" :key="item.id">
              <td class="mono">#{{ item.id }}</td>
              <td style="font-weight: 600">{{ item.title }}</td>
              <td>
                  <span class="badge" :class="item.type === 'EXAM' ? 'warning' : 'info'">
                    {{ item.type === 'EXAM' ? '考试' : '作业' }}
                  </span>
              </td>
              <td class="mono">{{ formatTime(item.dueAt) }}</td>
              <td>{{ item.maxScore }}</td>
              <td>
                <div class="actions">
                  <button class="btn-refresh" @click="goToGrade(item.id)">去评分</button>
                </div>
              </td>
            </tr>
            </tbody>
          </table>

          <div v-else class="empty-tip">
            该课程暂无作业。<br>
            请点击右上角“发布新作业”。
          </div>
        </div>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import PageContainer from '../../components/common/PageContainer.vue'
import toast from '../../utils/toast'
import { fetchTeacherCourses, fetchCourseAssignments } from '../../api/teacher'

const router = useRouter()
const route = useRoute()
const courses = ref([])
const selectedCourseId = ref('')
const assignments = ref([])
const loading = ref(false)

const loadCourses = async () => {
  try {
    const res = await fetchTeacherCourses()
    courses.value = res || []

    const urlCourseId = String(route.query.courseId || '')
    const targetCourse = courses.value.find(c => String(c.id) === urlCourseId)

    if (targetCourse) {
      selectedCourseId.value = targetCourse.id
    } else if (courses.value.length > 0) {
      selectedCourseId.value = courses.value[0].id
    }
  } catch (e) {
    toast.error('加载课程失败', e.message)
  }
}

const loadAssignments = async () => {
  if (!selectedCourseId.value) return

  loading.value = true
  try {
    const res = await fetchCourseAssignments(selectedCourseId.value)
    assignments.value = res || []
  } catch (e) {
    console.error(e)
    assignments.value = []
    toast.error('获取作业失败', e.message)
  } finally {
    loading.value = false
  }
}

watch(selectedCourseId, (newVal) => {
  if (newVal) {
    loadAssignments()
  } else {
    assignments.value = []
  }
})

const goToCreate = () => {
  if (!selectedCourseId.value) {
    return toast.warning('请先在下拉框中选择一个课程')
  }
  router.push(`/teacher/assignments/manage?courseId=${selectedCourseId.value}`)
}

const goToGrade = (assignmentId) => {
  router.push(`/teacher/grading?assignmentId=${assignmentId}`)
}

const formatTime = (t) => {
  return t ? new Date(t).toLocaleString() : '无截止'
}

onMounted(loadCourses)
</script>

<style scoped>
.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.actions {
  display: flex;
  gap: 8px;
}
.empty-tip {
  padding: 40px;
  text-align: center;
  color: #9ca3af;
  background: #f9fafb;
  border-radius: 8px;
}
.small {
  padding: 4px 10px;
  font-size: 13px;
}
.outline {
  background: white;
  border: 1px solid #d1d5db;
  color: #374151;
}
.select {
  padding: 8px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
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
</style>