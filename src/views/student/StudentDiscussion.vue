<template>
  <PageContainer title="讨论区" desc="按课程查看帖子、发帖与回复。">
    <template #extra>
      <div class="filters">
        <select class="select" v-model="courseId" @change="loadPosts">
          <option value="">请选择课程</option>
          <option v-for="c in courses" :key="c.id" :value="c.id">{{ c.title }}</option>
        </select>

        <button class="btn-refresh" @click="loadPosts" :disabled="!courseId">刷新</button>
        <button class="btn-refresh" :disabled="!courseId" @click="openNew">发帖</button>
      </div>
    </template>

    <div class="card" style="padding: 12px">
      <div v-if="!courseId" class="muted">请先选择课程</div>

      <template v-else>
        <div v-for="p in posts" :key="p.id" class="post">
          <div class="top">
            <div class="left">
              <div class="title">
                <span v-if="p.pinned" class="pin">置顶</span>
                {{ p.title }}
              </div>
              <div class="muted" style="font-size: 12px; margin-top: 4px">
                {{ p.authorName || '匿名' }} · {{ fmt(p.createdAt) }}
              </div>
            </div>
            <div class="right">
              <!-- 修复：按钮文字根据状态变化 -->
              <button class="btn" @click="toggleReplies(p)">
                {{ p._showReplies ? '收起回复' : `回复(${p._replyCount ?? 0})` }}
              </button>
            </div>
          </div>

          <div class="content">{{ p.content }}</div>

          <div v-if="p._showReplies" class="replies">
            <div class="reply" v-for="r in p._replies" :key="r.id">
              <div class="muted" style="font-size: 12px">
                {{ r.authorName }} · {{ fmt(r.createdAt) }}
              </div>
              <div style="margin-top: 4px">{{ r.content }}</div>
            </div>

            <div class="replyBox">
              <input class="input" v-model="p._replyText" placeholder="输入回复内容..." @keyup.enter="doReply(p)"/>
              <button class="btn primary" @click="doReply(p)">发送</button>
            </div>
          </div>
        </div>

        <div v-if="!posts.length" class="muted">暂无帖子</div>
      </template>
    </div>

    <Modal v-model:visible="dialogVisible" title="发布帖子">
      <div class="field">
        <div class="label">标题</div>
        <input class="input" v-model="form.title" placeholder="请输入标题" />
      </div>
      <div class="field">
        <div class="label">内容</div>
        <textarea class="textarea" v-model="form.content" placeholder="请输入内容"></textarea>
      </div>

      <template #footer>
        <button class="btn" @click="dialogVisible = false">取消</button>
        <button class="btn primary" @click="submit">发布</button>
      </template>
    </Modal>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PageContainer from '../../components/common/PageContainer.vue'
import Modal from '../../components/common/Modal.vue'
import toast from '../../utils/toast'
import { fetchMyCourses, fetchDiscussion, postDiscussion, fetchReplies, replyPost } from '../../api/student'

const courses = ref([])
const courseId = ref('')
const posts = ref([])

const dialogVisible = ref(false)
const form = ref({ title: '', content: '' })

function fmt(x) {
  if (!x) return '-'
  try {
    const d = new Date(x)
    return d.toLocaleString()
  } catch {
    return String(x)
  }
}

async function loadCourses() {
  try {
    courses.value = await fetchMyCourses()
    //默认第一个
    if (!courseId.value && courses.value.length) {
      courseId.value = courses.value[0].id
      loadPosts()
    }
  } catch (e) {
    toast.error('加载课程失败', e.message)
  }
}

async function loadPosts() {
  if (!courseId.value) {
    posts.value = []
    return
  }
  try {
    const list = await fetchDiscussion({ courseId: courseId.value })
    posts.value = (list || []).map(p => ({
      ...p,
      _showReplies: false,/*回复区是否展开显示*/
      _replies: [],/*缓存这条帖子的回复列表*/
      _replyText: '',
      // 如果后端没返回回复数，这里默认0
      _replyCount: p.replyCount || 0
    }))
  } catch (e) {
    toast.error('加载帖子失败', String(e?.message || e))
  }
}

function openNew() {
  form.value = { title: '', content: '' }
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.title) return toast.warning('请填写标题')
  if (!form.value.content) return toast.warning('请填写内容')
  try {
    await postDiscussion({ courseId: courseId.value, ...form.value })
    toast.success('发布成功')
    dialogVisible.value = false
    await loadPosts()
  } catch (e) {
    toast.error('发布失败', String(e?.message || e))
  }
}

//只加载回复数据
async function loadRepliesData(p) {
  try {
    const list = await fetchReplies(courseId.value, p.id)
    p._replies = list || []
    p._replyCount = p._replies.length
  } catch (e) {
    toast.error('加载回复失败', String(e?.message || e))
  }
}

// 切换显示状态
async function toggleReplies(p) {
  if (!p._showReplies) {
    // 展开时加载
    await loadRepliesData(p)
    p._showReplies = true
  } else {
    // 收起
    p._showReplies = false
  }
}

async function doReply(p) {
  const txt = (p._replyText || '').trim()
  if (!txt) return toast.warning('请输入回复内容')
  try {
    await replyPost(courseId.value, p.id, txt)
    p._replyText = ''
    toast.success('回复成功')

    // 回复成功后，强制刷新回复列表，并确保是展开状态
    await loadRepliesData(p)
    p._showReplies = true

  } catch (e) {
    toast.error('回复失败', String(e?.message || e))
  }
}

onMounted(async () => {
  await loadCourses()
})
</script>

<style scoped>
.filters{
  display:flex;
  gap:10px;
  align-items:center;
}
.post{
  padding: 12px;
  border: 1px solid #eef0f6;
  border-radius: 12px;
  margin-bottom: 12px;
  background:#fff;
}
.top{
  display:flex;
  justify-content:space-between;
  gap: 10px;
}
.title{
  font-weight: 900;
}
.pin{
  display:inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  background: #eef3ff;
  color: var(--primary);
  font-size: 12px;
  margin-right: 6px;
}
.content{
  margin-top: 10px;
  white-space: pre-wrap;
}
.replies{
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #e8eaf3;
}
.reply{
  padding: 10px;
  border-radius: 10px;
  background:#f7f8fd;
  margin-bottom: 8px;
}
.replyBox{
  display:flex;
  gap:10px;
  align-items:center;
}
.field{
  margin-top: 8px;
}
.label{
  font-size: 13px;
  color:black;
  margin-bottom: 6px;
}
.textarea{
  width: 100%;
  min-height: 110px;
  resize: vertical;
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid #e6e8f0;
  outline: none;
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