<template>
  <PageContainer
      :title="assignmentId ? '单项作业评分' : '提交与评分中心'"
      :desc="assignmentId ? `作业ID: ${assignmentId}` : '查看所有课程的学生作业提交情况'"
  >
    <template #extra>
      <div class="actions">
        <!-- 批量评分按钮：仅在单项作业模式下显示 -->
        <button v-if="assignmentId" class="btn primary" @click="openBatchModal">⚡ 一键批量评分</button>

        <button v-if="assignmentId" class="btn" @click="viewAll">查看所有看板</button>
        <button class="btn-refresh" @click="loadData">🔄 刷新列表</button>
        <button class="btn-refresh" @click="router.back()">返回</button>
      </div>
    </template>

    <div class="card">
      <div v-if="loading" class="loading">正在从服务器加载数据...</div>

      <table v-else class="table">
        <thead>
        <tr>
          <!-- 视图接口的 ID 字段名略有不同，做个兼容显示 -->
          <th>ID</th>
          <th v-if="!assignmentId">所属课程 / 作业</th>
          <th>学生信息</th>
          <th>提交时间</th>
          <th>状态</th>
          <!-- 视图模式下没有分数具体值，显示状态即可 -->
          <th>得分</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="sub in submissions" :key="sub.id || sub.submissionId">
          <td class="mono">#{{ sub.id || sub.submissionId }}</td>

          <td v-if="!assignmentId" class="assignment-title">
            <div class="course-tag">{{ sub.courseTitle }}</div>
            <div>{{ sub.assignmentTitle }}</div>
          </td>

          <td>
            <div class="student-info">
              <!-- 兼容视图字段 studentName -->
              <span class="name">{{ sub.studentName || '未知姓名' }}</span>
              <span v-if="sub.studentId" class="uid" style="color: #9ca3af; font-size: 12px;">ID: {{ sub.studentId }}</span>
            </div>
          </td>
          <td class="mono">{{ formatTime(sub.submittedAt) }}</td>
          <td>
              <span class="badge" :class="sub.status === 'GRADED' ? 'success' : 'warning'">
                {{ sub.status === 'GRADED' ? '已评分' : '待评分' }}
              </span>
          </td>
          <!-- 视图模式(sub.score为undefined)显示 '-' -->
          <td style="font-weight: bold">{{ (sub.score !== undefined && sub.score !== null) ? sub.score : '-' }}</td>
          <td>
            <!-- 视图模式下我们也允许点击评分，但需要 assignmentId 才能调用 grade 接口。
                 fetchGradingDashboardView 返回了 submissionId, 但 grade 接口只需要 submissionId。
                 所以这里只要传 sub 进去即可。 -->
            <button class="btn small primary" @click="openModal(sub)">
              {{ sub.status === 'GRADED' ? '修改' : '评分' }}
            </button>
          </td>
        </tr>
        <tr v-if="!loading && submissions.length === 0">
          <td :colspan="assignmentId ? 6 : 7" class="empty">
            <div class="empty-state">
              <p>暂无学生提交记录</p>
              <p class="hint">请确认学生是否已在学生端点击“提交”按钮。</p>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 评分弹窗 -->
    <Modal v-model:visible="showModal" title="作业评分" @confirm="submitGrade" width="800px">
      <div class="modal-layout">
        <div class="grade-form">
          <div class="info-box">
            <div class="info-row">
              <span class="label">作业:</span>
              <span>{{ currentSub?.assignmentTitle }}</span>
            </div>
            <div class="info-row">
              <span class="label">学生:</span>
              <span>{{ currentSub?.studentName || '未名' }}</span>
            </div>
            <div class="info-row" style="margin-top: 8px;">
              <span class="label">提交内容:</span>
            </div>
            <div class="content-preview">
              <pre class="code-block">{{ parseContent(currentSub?.contentJson) }}</pre>
            </div>
          </div>

          <div class="field">
            <label>评分 (0-100)</label>
            <input type="number" v-model="gradeForm.score" class="input" min="0" max="100" />
          </div>
          <div class="field">
            <label>教师评语</label>
            <textarea v-model="gradeForm.feedback" class="textarea" rows="3" placeholder="请输入反馈意见..."></textarea>
          </div>
        </div>

        <!-- 评分审计日志区域 -->
        <div class="audit-log">
          <h4>评分历史 (Audit)</h4>
          <div v-if="auditLoading" class="audit-loading">加载历史中...</div>
          <ul v-else class="audit-list">
            <li v-for="log in auditLogs" :key="log.id" class="audit-item">
              <div class="audit-time">{{ formatTime(log.changedAt) }}</div>
              <div class="audit-change">
                分数: <span class="old">{{ log.oldScore ?? '无' }}</span>
                ➔ <span class="new">{{ log.newScore }}</span>
              </div>
              <div class="audit-user">Operated by User#{{ log.changedByUserId }}</div>
            </li>
            <li v-if="auditLogs.length === 0" class="no-audit">暂无修改记录</li>
          </ul>
        </div>
      </div>
    </Modal>

    <!-- 批量评分弹窗 -->
    <Modal v-model:visible="showBatchModal" title="一键批量评分" @confirm="submitBatchGrade">
      <div class="batch-content">
        <p class="warning-text">⚠️ 注意：此操作将覆盖该作业下 <strong>所有提交</strong> 的分数。</p>
        <div class="field">
          <label>统一给予分数</label>
          <input type="number" v-model="batchScore" class="input" placeholder="例如: 100" />
        </div>
        <p class="hint">适用于全员满分奖励或统一给分场景。</p>
      </div>
    </Modal>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageContainer from '../../components/common/PageContainer.vue'
import Modal from '../../components/common/Modal.vue'
import toast from '../../utils/toast'
import { fetchSubmissions, gradeSubmission } from '../../api/teacher'
// 引入新的优化接口
import { fetchGradingDashboardView, batchGradeAssignment, fetchGradeAuditLog } from '../../api/opt'

const route = useRoute()
const router = useRouter()
const assignmentId = ref(route.query.assignmentId)

const loading = ref(false)
const submissions = ref([])

// 单个评分
const showModal = ref(false)
const currentSub = ref(null)
const gradeForm = ref({ score: 0, feedback: '' })

// 审计日志
const auditLogs = ref([])
const auditLoading = ref(false)

// 批量评分
const showBatchModal = ref(false)
const batchScore = ref(null)

// 核心修复：解析 JSON 格式的内容
const parseContent = (json) => {
  if (!json) return '（学生未提交内容）'
  try {
    const obj = JSON.parse(json)
    // 如果解析出的是对象且包含 content 字段（匹配你的数据库结构）
    if (obj && obj.content) {
      return obj.content
    }
    return JSON.stringify(obj, null, 2)
  } catch (e) {
    // 如果不是 JSON，直接显示原文
    return json
  }
}

const loadData = async () => {
  loading.value = true
  submissions.value = []
  try {
    if (assignmentId.value) {
      // 如果有作业ID，使用标准接口获取该作业详情列表
      const res = await fetchSubmissions(assignmentId.value)
      submissions.value = res || []
    } else {
      // 如果没有作业ID，使用【高性能视图接口】获取全局看板
      // 视图字段: submissionId, courseTitle, assignmentTitle, studentName, submittedAt, status
      const res = await fetchGradingDashboardView()
      submissions.value = res || []
    }
  } catch (e) {
    toast.error('加载提交失败', e.message)
  } finally {
    loading.value = false
  }
}

const viewAll = () => {
  router.push('/teacher/grading')
  assignmentId.value = undefined
  setTimeout(loadData, 100)
}

const formatTime = (t) => {
  if (!t) return '-'
  return new Date(t).toLocaleString()
}

const openModal = async (sub) => {
  // 兼容视图对象和普通对象 ID
  const subId = sub.id || sub.submissionId
  currentSub.value = sub

  // 默认分数处理
  gradeForm.value.score = sub.score !== undefined && sub.score !== null ? sub.score : 80
  gradeForm.value.feedback = sub.feedback || ''

  showModal.value = true

  // 加载审计日志
  auditLoading.value = true
  auditLogs.value = []
  try {
    auditLogs.value = await fetchGradeAuditLog(subId)
  } catch(e) {
    console.warn("加载日志失败", e)
  } finally {
    auditLoading.value = false
  }
}

const submitGrade = async () => {
  if (!currentSub.value) return
  const subId = currentSub.value.id || currentSub.value.submissionId

  try {
    await gradeSubmission(subId, gradeForm.value.score, gradeForm.value.feedback)
    toast.success('评分成功')
    showModal.value = false
    loadData()
  } catch (e) {
    toast.error('评分失败', e.message)
  }
}

// 批量评分逻辑
const openBatchModal = () => {
  batchScore.value = 100
  showBatchModal.value = true
}

const submitBatchGrade = async () => {
  if (!batchScore.value) return toast.warning('请输入分数')
  try {
    await batchGradeAssignment(assignmentId.value, batchScore.value)
    toast.success('批量评分执行成功')
    showBatchModal.value = false
    loadData()
  } catch (e) {
    toast.error('批量操作失败', e.message)
  }
}


onMounted(() => {
  loadData()
})
</script>

<style scoped>
.actions {
  display: flex;
  gap: 10px;
}

.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #6b7280;
}

.course-tag {
  font-size: 12px;
  color: #6b7280;
  display: inline-block;
  padding: 2px 6px;
  border-radius: 4px;
  margin-bottom: 4px;
}

.assignment-title {
  font-weight: 600;
  color: #4f46e5;
  max-width: 240px;
}

.info-box {
  background: #f9fafb;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid #e5e7eb;
}

.content-preview {
  margin-top: 8px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}

.code-block {
  font-family: monospace;
  font-size: 13px;
  white-space: pre-wrap;
  word-break: break-all;
  padding: 12px;
  margin: 0;
  color: #374151;
}

/* 弹窗布局：左边表单，右边日志 */
.modal-layout {
  display: flex;
  gap: 20px;
}

.grade-form {
  flex: 1;
}

.audit-log {
  width: 260px;
  border-left: 1px solid #e5e7eb;
  padding-left: 20px;
  font-size: 13px;
}

.audit-log h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #374151;
}

.audit-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 400px;
  overflow-y: auto;
}

.audit-item {
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #e5e7eb;
}

.audit-time {
  color: #9ca3af;
  font-size: 11px;
}

.audit-change .old {
  text-decoration: line-through;
  color: #ef4444;
}
.audit-change .new {
  color: #10b981;
  font-weight: bold;
}

.audit-user {
  margin-top: 2px;
  color: #6b7280;
  font-size: 11px;
}

.no-audit {
  color: #9ca3af;
  font-style: italic;
}

.field {
  margin-bottom: 16px;
}

.field label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
  font-size: 14px;
  color: #374151;
}

.input, .textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

.badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.badge.success { background: #d1fae5; color: #065f46; }
.badge.warning { background: #fef3c7; color: #92400e; }

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

.btn-refresh {
  padding: 8px 16px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.warning-text { color: #ef4444; font-weight: 600; }
</style>