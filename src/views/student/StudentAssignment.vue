<template>
  <PageContainer title="作业中心" desc="查看并完成你的所有作业">
    <div class="filter-bar card">
      <div class="label">筛选课程：</div>
      <select v-model="selectedCourseId" class="course-select" @change="handleFilterChange">
        <option value="">-- 显示全部 --</option>
        <option v-for="c in myCourses" :key="c.courseId" :value="c.courseId">
          {{ c.courseTitle }}
        </option>
      </select>
      <button class="btn-refresh" @click="loadData" style="margin-left: 10px">刷新</button>
    </div>

    <div class="card">
      <div v-if="loading" class="loading">加载中...</div>

      <table v-else class="data-table">
        <thead>
        <tr>
          <th>作业标题</th>
          <th>所属课程</th>
          <th>类型</th>
          <th>截止日期</th>
          <th>状态</th>
          <th>分数</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="a in filteredAssignments" :key="a.id">
          <td class="font-medium">{{ a.title }}</td>
          <td class="course-name">{{ getCourseName(a.courseId) }}</td>
          <td>{{ a.type === 'EXAM' ? '考试' : '作业' }}</td>
          <td>{{ formatDate(a.dueAt) }}</td>
          <td>
            <span class="status-badge" :class="getStatusClass(a)">
              {{ formatStatus(a) }}
            </span>
          </td>
          <td>{{ (a.score !== undefined && a.score !== null) ? a.score : '-' }}</td>
          <td>
            <td>
              <!-- 已评分：不给提交 -->
              <button v-if="isGraded(a)" class="btn-primary" disabled>
                已评分
              </button>

              <!-- 未评分：正常提交/重交 -->
              <button v-else class="btn-primary" @click.stop="openSubmitModal(a)">
                {{ a.submittedAt ? '查看/重交' : '去完成' }}
              </button>
            </td>
          </td>
        </tr>
        <tr v-if="filteredAssignments.length === 0">
          <td colspan="7" class="empty-text">暂无作业</td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 提交作业弹窗 -->
    <Modal v-model:visible="showModal" :title="'提交: ' + (currentAssignment?.title || '')" @confirm="handleSubmit">
      <div class="questions-preview" v-if="questions.length">
        <h4>题目预览:</h4>
        <ul class="q-list">
          <li v-for="(q, idx) in questions" :key="q.id">
            {{ idx + 1 }}. {{ q.prompt }} ({{ q.score }}分)
          </li>
        </ul>
      </div>

      <div class="form-group">
        <label>你的答案 (请直接填写)</label>
        <textarea
            v-model="submitContent"
            class="submit-area"
            rows="8"
            placeholder="在此处输入你的答案..."
        ></textarea>
        <p class="hint">填写完成后请点击下方“确定”按钮提交。</p>
      </div>
    </Modal>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import PageContainer from '../../components/common/PageContainer.vue'
import Modal from '../../components/common/Modal.vue'
import toast from '../../utils/toast'

import {
  fetchMyEnrollments,
  fetchAllStudentAssignments,
  fetchAssignmentQuestions,
  submitAssignment,
  fetchMySubmission
} from '../../api/student'

const route = useRoute()
const myCourses = ref([])
const selectedCourseId = ref(route.query.courseId ? Number(route.query.courseId) : '')
const allAssignments = ref([])
const loading = ref(false)

const showModal = ref(false)
const currentAssignment = ref(null)
const questions = ref([])
const submitContent = ref('')
const isGraded = (a) => a.score !== undefined && a.score !== null

const filteredAssignments = computed(() => {
  if (!selectedCourseId.value) return allAssignments.value
  return allAssignments.value.filter(a => a.courseId === selectedCourseId.value)
})

const getCourseName = (cid) => {
  const c = myCourses.value.find(x => x.courseId === cid)
  return c ? c.courseTitle : '未知课程'
}

const loadData = async () => {
  loading.value = true
  try {
    const enrollRes = await fetchMyEnrollments()
    myCourses.value = enrollRes || []

    const assignRes = await fetchAllStudentAssignments()
    allAssignments.value = assignRes || []

    // 检查提交状态
    await checkSubmissionStatus()
  } catch (e) {
    toast.error('加载失败', e?.message || '未知错误')
  } finally {
    loading.value = false
  }
}

const checkSubmissionStatus = async () => {
  if (!allAssignments.value.length) return

  for (const assignment of allAssignments.value) {
    try {
      // 这里的 fetchMySubmission 现在返回 null 而不是抛错
      const sub = await fetchMySubmission(assignment.id)

      if (sub && sub.id) {
        assignment.submittedAt = sub.submittedAt
        assignment.score = sub.score

        let content = sub.contentJson
        try {
          const parsed = JSON.parse(sub.contentJson)
          if (parsed && typeof parsed === 'object' && parsed.content) {
            content = parsed.content
          } else if (typeof parsed === 'string') {
            content = parsed
          }
        } catch (err) {
          // 解析失败保留原样
        }
        assignment._tempContent = content
      } else {
        // 明确标记为未提交
        assignment.submittedAt = null
        assignment.score = null
      }
    } catch (e) {
      console.warn(`检查作业 ${assignment.id} 状态失败`, e)
      // 容错：单个失败不影响整体
    }
  }
}

const handleFilterChange = () => {}

const openSubmitModal = async (assignment) => {
  currentAssignment.value = assignment
  submitContent.value = assignment._tempContent || ''
  showModal.value = true

  // 加载题目
  try {
    const qs = await fetchAssignmentQuestions(assignment.id)
    questions.value = qs || []
  } catch (e) {
    questions.value = []
  }

  // 二次检查最新内容（防止列表数据陈旧）
  try {
    if (!assignment._tempContent) {
      const sub = await fetchMySubmission(assignment.id)
      if (sub && sub.contentJson) {
        let content = sub.contentJson
        try {
          const parsed = JSON.parse(sub.contentJson)
          if (parsed && parsed.content) content = parsed.content
        } catch (e) {}
        submitContent.value = content
      }
    }
  } catch (e) {
    // 忽略错误
  }
}

const handleSubmit = async () => {
  if (!submitContent.value || submitContent.value.trim() === '') {
    return toast.warning('内容不能为空', '请填写答案后再提交')
  }
  if (!currentAssignment.value) return

  try {
    // 构造 JSON 字符串
    const payload = JSON.stringify({
      content: submitContent.value,
      timestamp: new Date().toISOString()
    })

    await submitAssignment(currentAssignment.value.id, payload)

    toast.success('提交成功')
    showModal.value = false

    // 更新列表状态
    const target = allAssignments.value.find(a => a.id === currentAssignment.value.id)
    if (target) {
      target.submittedAt = new Date().toISOString()
      target._tempContent = submitContent.value
    }
  } catch (e) {
    toast.error('提交失败', e?.message || '未知错误')
  }
}

const formatDate = (str) => str ? new Date(str).toLocaleDateString() : '无截止'

const getStatusClass = (a) => {
  if (a.score !== undefined && a.score !== null) return 'graded'
  if (a.submittedAt) return 'submitted'
  return 'todo'
}

const formatStatus = (a) => {
  if (a.score !== undefined && a.score !== null) return '已评分'
  if (a.submittedAt) return '已提交'
  return '待完成'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.filter-bar {
  display: flex;
  align-items: center;
  padding: 16px;
  margin-bottom: 20px;
}
.label {
  font-size: 14px;
  font-weight: 600;
  color: #555;
  margin-right: 10px;
}
.course-select {
  padding: 8px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  width: 240px;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
}
.data-table th {
  padding: 12px 16px;
  border-bottom: 1px solid #f3f4f6;
  text-align: left;
  background: #f9fafb;
  font-weight: 600;
  color: #6b7280;
}
.data-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #f3f4f6;
  text-align: left;
}
.course-name {
  color: #6b7280;
  font-size: 13px;
}
.status-badge {
  padding: 2px 8px;
  border-radius: 99px;
  font-size: 12px;
}
.status-badge.todo {
  background: #f3f4f6;
  color: #374151;
}
.status-badge.submitted {
  background: #dbeafe;
  color: #1e40af;
}
.status-badge.graded {
  background: #d1fae5;
  color: #065f46;
}
.btn-primary {
  padding: 6px 12px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}
.btn-primary:hover {
  background: #4338ca;
}
.submit-area {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-family: monospace;
  resize: vertical;
}
.q-list {
  background: #f9fafb;
  padding: 12px 20px;
  border-radius: 6px;
  font-size: 14px;
  color: #374151;
  margin-bottom: 16px;
}
.hint {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
}
.empty-text {
  padding: 40px;
  text-align: center;
  color: #9ca3af;
}
.loading {
  text-align: center;
  padding: 40px;
  color: #666;
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