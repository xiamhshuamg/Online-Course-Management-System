<template>
  <PageContainer title="发布作业" desc="第一步：创建作业信息；第二步：添加题目。">
    <template #extra>
      <button class="btn" @click="router.back()">返回列表</button>
    </template>

    <div class="layout-grid">
      <div class="card form-card">
        <h3 class="card-title">1. 基本信息</h3>
        <div class="field">
          <label>作业标题</label>
          <input v-model="form.title" class="input" placeholder="例如：期中考试" :disabled="created" />
        </div>
        <div class="field">
          <label>说明/描述</label>
          <textarea v-model="form.description" class="textarea" rows="3" :disabled="created"></textarea>
        </div>
        <div class="row">
          <div class="col field">
            <label>类型</label>
            <select v-model="form.type" class="select" :disabled="created">
              <option value="HOMEWORK">普通作业</option>
              <option value="EXAM">限时考试</option>
            </select>
          </div>
          <div class="col field">
            <label>满分</label>
            <input type="number" v-model="form.maxScore" class="input" :disabled="created" />
          </div>
        </div>
        <div class="field">
          <label>截止时间</label>
          <input type="datetime-local" v-model="form.dueAt" class="input" :disabled="created" />
        </div>

        <div class="actions">
          <button v-if="!created" class="btn primary" @click="handleCreate" :disabled="loading">
            {{ loading ? '创建中...' : '确认创建并下一步' }}
          </button>
          <div v-else class="success-mark"> 作业已创建 (ID: {{ assignmentId }})</div>
        </div>
      </div>

      <div class="card question-card" :class="{ disabled: !created }">
        <h3 class="card-title">2. 添加题目</h3>
        <div v-if="!created" class="placeholder">请先在左侧完成作业创建</div>

        <div v-else>
          <div class="field">
            <label>题目内容 (Prompt)</label>
            <textarea v-model="qForm.prompt" class="textarea" rows="2"></textarea>
          </div>
          <div class="row">
            <div class="col field">
              <label>题型</label>
              <select v-model="qForm.type" class="select">
                <option value="MULTIPLE_CHOICE">选择题</option>
                <option value="FILL_BLANK">填空题</option>
                <option value="ESSAY">问答题</option>
              </select>
            </div>
            <div class="col field">
              <label>分值</label>
              <input type="number" v-model="qForm.score" class="input" />
            </div>
          </div>

          <div class="field" v-if="qForm.type === 'MULTIPLE_CHOICE'">
            <label>选项配置 (JSON格式)</label>
            <textarea v-model="qForm.optionsJson" class="textarea code" rows="3"
                      placeholder='["A. 选项1", "B. 选项2", "C. 选项3", "D. 选项4"]'></textarea>
          </div>

          <div class="field">
            <label>参考答案</label>
            <input v-model="qForm.answerKeyJson" class="input" placeholder="例如：A 或 答案文本" />
          </div>

          <button class="btn primary full" @click="handleAddQuestion">添加此题</button>

          <div class="added-list">
            <h4>本次已添加:</h4>
            <ul>
              <li v-for="(q, i) in addedQuestions" :key="i">
                <span class="tag">{{ q.type }}</span> {{ q.prompt }} ({{ q.score }}分)
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import PageContainer from '../../components/common/PageContainer.vue'
import toast from '../../utils/toast'
import { createAssignment, addQuestion } from '../../api/teacher'

const router = useRouter()
const route = useRoute()
const courseId = route.query.courseId

const loading = ref(false)
const created = ref(false)
const assignmentId = ref(null)
const addedQuestions = ref([])

const form = ref({
  title: '',
  description: '',
  type: 'HOMEWORK',
  maxScore: 100,
  dueAt: ''
})

const qForm = ref({
  prompt: '',
  type: 'MULTIPLE_CHOICE',
  score: 5,
  optionsJson: '["A. ", "B. ", "C. ", "D. "]',
  answerKeyJson: ''
})

const handleCreate = async () => {
  if (!form.value.title) return toast.warning('请输入标题')
  loading.value = true
  try {
    // 格式化时间为 ISO
    const payload = { ...form.value }
    if (payload.dueAt) payload.dueAt = new Date(payload.dueAt).toISOString()

    const res = await createAssignment(courseId, payload)
    // 后端返回对象包含 id
    assignmentId.value = res.id
    created.value = true
    toast.success('作业创建成功', '请在右侧添加题目')
  } catch (e) {
    toast.error('创建失败', e.message)
  } finally {
    loading.value = false
  }
}

const handleAddQuestion = async () => {
  if (!qForm.value.prompt) return toast.warning('请输入题目内容')

  try {
    await addQuestion(assignmentId.value, qForm.value)
    toast.success('题目添加成功')
    addedQuestions.value.unshift({...qForm.value})
    qForm.value.prompt = ''
    qForm.value.answerKeyJson = ''
  } catch (e) {
    const msg =
        e?.response?.data?.message ||
        e?.response?.data?.msg ||
        e?.response?.data?.error ||
        e?.message ||
        '未知错误'

    toast.error('添加题目失败', msg)
  }
}
</script>

<style scoped>
.layout-grid {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.card {
  flex: 1;
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.field {
  margin-bottom: 12px;
}
.field label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #4b5563;
  margin-bottom: 4px;
}
.input, .select, .textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
}
.row {
  display: flex;
  gap: 12px;
}
.col {
  flex: 1;
}
.disabled {
  opacity: 0.5;
  pointer-events: none;
}
.placeholder {
  text-align: center;
  color: #9ca3af;
  padding: 40px;
}
.success-mark {
  color: #059669;
  font-weight: bold;
  background: #d1fae5;
  padding: 10px;
  border-radius:6px;
  text-align: center;
}
.full {
  width: 100%;
  margin-top: 10px;
}
.code {
  font-family: monospace;
  font-size: 12px;
}
.added-list {
  margin-top: 20px;
  border-top: 1px solid #e5e7eb;
  padding-top: 10px;
}
.tag {
  font-size: 10px;
  background: #e0e7ff;
  color: #4338ca;
  padding: 2px 4px;
  border-radius: 4px;
}
</style>