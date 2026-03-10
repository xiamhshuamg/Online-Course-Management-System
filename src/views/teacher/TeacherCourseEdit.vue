<!-- FILE: src/views/teacher/TeacherCourseEdit.vue -->
<template>
  <PageContainer :title="isEdit ? '编辑课程' : '创建课程'">
    <template #extra>
      <button class="btn-secondary" @click="router.back()">返回列表</button>
    </template>

    <div class="edit-layout">
      <!-- 左侧：课程基本信息 -->
      <div class="card base-info-card">
        <h3 class="card-title">基本信息</h3>

        <form @submit.prevent="handleSaveBaseInfo">
          <div class="form-group">
            <label>课程标题</label>
            <input
                v-model="form.title"
                class="form-input"
                required
                placeholder="请输入课程标题"
            />
          </div>

          <div class="form-group">
            <label>课程描述</label>
            <textarea
                v-model="form.description"
                class="form-textarea"
                rows="5"
                required
                placeholder="请输入详细的课程描述（至少 10 个字）"
            ></textarea>
            <div class="help-text">后端要求：描述至少 10 个字符，否则会 400 校验失败。</div>
          </div>

          <div class="form-row">
            <div class="form-group col">
              <label>价格 (元)</label>
              <input
                  type="number"
                  v-model="form.price"
                  class="form-input"
                  min="0"
                  step="0.01"
                  required
              />
            </div>

            <div class="form-group col">
              <label>分类</label>
              <select v-model="form.category" class="form-select" required>
                <option value="" disabled>请选择分类</option>
                <option
                    v-for="opt in categoryOptions"
                    :key="opt.value"
                    :value="opt.value"
                >
                  {{ opt.label }}
                </option>
              </select>
              <div class="help-text">统一用固定分类值，保证学生端筛选正常。</div>
            </div>
          </div>

          <button class="btn-primary full-width" type="submit" :disabled="saving">
            {{ saving ? '保存中...' : (isEdit ? '保存修改' : '立即创建') }}
          </button>
        </form>
      </div>

      <!-- 右侧：章节与资源（创建后才能操作） -->
      <div class="card chapter-card">
        <div class="card-header">
          <h3 class="card-title">章节与资源</h3>
          <button class="btn-primary" @click="openAddChapter" :disabled="!isEdit">
            + 添加章节
          </button>
        </div>

        <div v-if="!isEdit" class="empty-text">
          请先创建课程，创建成功后才可以添加章节与资源。
        </div>

        <div v-else class="chapter-list">
          <div v-for="ch in chapters" :key="ch.id" class="chapter-item">
            <div class="chapter-info">
              <div class="chapter-title">{{ ch.title }}</div>
              <div class="chapter-meta">排序：{{ ch.sortOrder }}</div>
            </div>

            <div class="chapter-actions">
              <button class="btn-secondary" @click="openAddResource(ch.id)">
                + 添加资源
              </button>
            </div>

            <div v-if="(ch.resources || []).length" class="resource-list">
              <div v-for="r in ch.resources" :key="r.id" class="resource-item">
                <div class="resource-name">{{ r.name }}</div>
                <div class="resource-meta">{{ r.type }} · {{ r.url }}</div>
              </div>
            </div>
          </div>

          <div v-if="chapters.length === 0" class="empty-text">
            暂无章节，请添加
          </div>
        </div>
      </div>
    </div>

    <!-- 添加章节弹窗 -->
    <Modal v-model:visible="showChapterModal" title="添加新章节" @confirm="submitChapter">
      <div class="form-group">
        <label>章节标题</label>
        <input v-model="chapterForm.title" class="form-input" placeholder="请输入章节名称" />
      </div>
      <div class="form-group">
        <label>排序</label>
        <input type="number" v-model="chapterForm.sortOrder" class="form-input" min="1" step="1" />
      </div>
    </Modal>

    <!-- 添加资源弹窗 -->
    <Modal v-model:visible="showResourceModal" title="添加资源" @confirm="submitResource">
      <div class="form-group">
        <label>资源名称</label>
        <input v-model="resourceForm.name" class="form-input" placeholder="例如: 课件PPT" />
      </div>
      <div class="form-group">
        <label>资源类型</label>
        <select v-model="resourceForm.type" class="form-select">
          <option value="VIDEO">视频</option>
          <option value="SLIDE">幻灯片</option>
          <option value="DOCUMENT">文档</option>
        </select>
      </div>
      <div class="form-group">
        <label>资源链接 (URL)</label>
        <input v-model="resourceForm.url" class="form-input" placeholder="https://..." />
      </div>
    </Modal>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import PageContainer from '../../components/common/PageContainer.vue'
import Modal from '../../components/common/Modal.vue'
import toast from '../../utils/toast'
import {
  fetchTeacherCourses,
  createCourse,
  updateCourse,
  addChapter,
  addResource
} from '../../api/teacher'
import { COURSE_CATEGORY_OPTIONS, normalizeCourseCategory } from '../../utils/courseCategory'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const saving = ref(false)

const courseId = ref(route.query.id || null)
const isEdit = computed(() => !!courseId.value)

const categoryOptions = COURSE_CATEGORY_OPTIONS

const form = ref({
  title: '',
  description: '',
  price: 0,
  category: ''
})

const chapters = ref([])

const showChapterModal = ref(false)
const showResourceModal = ref(false)
const currentChapterId = ref(null)

const chapterForm = ref({
  title: '',
  sortOrder: 1
})

const resourceForm = ref({
  name: '',
  type: 'VIDEO',
  url: ''
})

// 后端没有 getById，就从列表里找
const loadData = async () => {
  if (!isEdit.value) return

  loading.value = true
  try {
    const list = await fetchTeacherCourses()
    const target = (list || []).find(c => String(c.id) === String(courseId.value))

    if (!target) {
      toast.error('错误', '未找到该课程')
      router.push('/teacher/courses')
      return
    }

    form.value = {
      title: target.title || '',
      description: target.description || '',
      price: target.price ?? 0,
      category: normalizeCourseCategory(target.category || '')
    }

    chapters.value = target.chapters || []
  } catch (e) {
    toast.error('加载失败', e?.message || '未知错误')
  } finally {
    loading.value = false
  }
}

function validateBeforeSave() {
  const title = String(form.value.title || '').trim()
  const desc = String(form.value.description || '').trim()
  const category = String(form.value.category || '').trim()
  const price = Number(form.value.price)

  if (!title) {
    toast.warning('请填写课程标题')
    return false
  }
  if (!desc || desc.length < 10) {
    toast.warning('课程描述至少 10 个字', '你现在的描述太短，会导致后端 400')
    return false
  }
  if (!category) {
    toast.warning('请选择分类')
    return false
  }
  if (Number.isNaN(price) || price < 0) {
    toast.warning('价格必须是 ≥0 的数字')
    return false
  }

  return true
}

const handleSaveBaseInfo = async () => {
  if (!validateBeforeSave()) return

  saving.value = true
  try {
    const payload = {
      title: String(form.value.title || '').trim(),
      description: String(form.value.description || '').trim(),
      price: Number(form.value.price),
      category: String(form.value.category || '').trim()
    }

    if (isEdit.value) {
      await updateCourse(courseId.value, payload)
      toast.success('成功', '课程信息已更新')
      await loadData()
      return
    }

    const res = await createCourse(payload)
    if (res && res.id) {
      toast.success('创建成功', '现在可以添加章节与资源了')
      courseId.value = res.id
      // 兼容你现有路由写法：query.id
      router.replace(`/teacher/course-edit?id=${res.id}`)
      await loadData()
    } else {
      toast.success('创建成功')
    }
  } catch (e) {
    // 如果后端是 Validation failed，这里能提示更清楚
    const msg = e?.message || '创建/保存失败'
    toast.error('保存失败', msg)
  } finally {
    saving.value = false
  }
}

const openAddChapter = () => {
  if (!isEdit.value) return
  chapterForm.value.title = ''
  chapterForm.value.sortOrder = 1
  showChapterModal.value = true
}

const submitChapter = async () => {
  if (!chapterForm.value.title) {
    toast.warning('请输入章节标题')
    return
  }
  try {
    await addChapter(courseId.value, {
      title: chapterForm.value.title,
      sortOrder: Number(chapterForm.value.sortOrder) || 1
    })
    toast.success('章节添加成功')
    showChapterModal.value = false
    await loadData()
  } catch (e) {
    toast.error('添加章节失败', e?.message || '未知错误')
  }
}

const openAddResource = (chapterId) => {
  currentChapterId.value = chapterId
  resourceForm.value = { name: '', type: 'VIDEO', url: '' }
  showResourceModal.value = true
}

const submitResource = async () => {
  if (!currentChapterId.value) return
  if (!resourceForm.value.name || !resourceForm.value.url) {
    toast.warning('请填写资源名称和链接')
    return
  }
  try {
    await addResource(currentChapterId.value, {
      name: resourceForm.value.name,
      type: resourceForm.value.type,
      url: resourceForm.value.url
    })
    toast.success('资源添加成功')
    showResourceModal.value = false
    await loadData()
  } catch (e) {
    toast.error('添加资源失败', e?.message || '未知错误')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.edit-layout {
  display: grid;
  grid-template-columns: 1fr 1.3fr;
  gap: 20px;
  align-items: start;
}

.card {
  background: rgba(255, 255, 255, 0.92);
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 10px 28px rgba(17, 24, 39, 0.10);
  border: 1px solid rgba(17, 24, 39, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.card-title {
  font-size: 18px;
  font-weight: 800;
  color: #111827;
  margin: 0;
}

.form-group {
  margin-bottom: 14px;
}

.form-row {
  display: flex;
  gap: 12px;
}

.col {
  flex: 1;
}

label {
  display: block;
  font-size: 13px;
  font-weight: 700;
  color: rgba(17, 24, 39, 0.70);
  margin-bottom: 6px;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid rgba(17, 24, 39, 0.12);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.90);
  outline: none;
}

.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid rgba(17, 24, 39, 0.12);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.90);
  outline: none;
  resize: vertical;
}

.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid rgba(17, 24, 39, 0.12);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.90);
  outline: none;
}

.help-text {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(17, 24, 39, 0.55);
}

.full-width {
  width: 100%;
  margin-top: 6px;
}

.btn-primary {
  padding: 10px 16px;
  border: none;
  border-radius: 10px;
  background: #4f46e5;
  color: white;
  font-weight: 700;
  cursor: pointer;
}

.btn-secondary {
  padding: 10px 16px;
  border-radius: 10px;
  border: 1px solid rgba(17, 24, 39, 0.14);
  background: rgba(255, 255, 255, 0.85);
  cursor: pointer;
  font-weight: 700;
}

.chapter-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chapter-item {
  padding: 14px;
  border-radius: 12px;
  border: 1px solid rgba(17, 24, 39, 0.08);
  background: rgba(249, 250, 251, 0.7);
}

.chapter-info {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.chapter-title {
  font-weight: 800;
  color: #111827;
}

.chapter-meta {
  font-size: 12px;
  color: rgba(17, 24, 39, 0.55);
}

.chapter-actions {
  margin-top: 10px;
}

.resource-list {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.resource-item {
  padding: 10px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(17, 24, 39, 0.06);
}

.resource-name {
  font-weight: 700;
  color: #111827;
}

.resource-meta {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(17, 24, 39, 0.55);
  word-break: break-all;
}

.empty-text {
  padding: 16px;
  color: rgba(17, 24, 39, 0.55);
  background: rgba(249, 250, 251, 0.75);
  border-radius: 12px;
}
</style>
