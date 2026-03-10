<template>
  <PageContainer title="讨论区管理" desc="默认显示全部帖子，也可以按课程筛选；支持置顶与删除。">
    <template #extra>
      <div class="row">
        <select v-model="courseId" class="select" @change="loadPosts">
          <option value="">全部课程</option>
          <option v-for="c in courses" :key="c.id" :value="String(c.id)">
            {{ c.title }}
          </option>
        </select>
        <button class="btn primary" @click="loadPosts" :disabled="loading">刷新</button>
      </div>
    </template>

    <div class="card">
      <div v-if="loading" class="tip">加载中...</div>
      <div v-else-if="posts.length === 0" class="tip">暂无帖子</div>

      <div v-else class="post-list">
        <div v-for="p in posts" :key="p.id" class="post-item" :class="{ pinned: p.pinned }">
          <div class="post-header">
            <div class="meta">
              <span class="title">{{ p.title }}</span>
              <span class="badge" v-if="p.pinned">置顶</span>
              <span class="course" v-if="p._courseTitle">· {{ p._courseTitle }}</span>
              <span class="author">· {{ p.authorName || '匿名' }}</span>
            </div>

            <div class="actions">
              <button class="btn small" @click="togglePin(p)" :disabled="loading">
                {{ p.pinned ? '取消置顶' : '置顶' }}
              </button>
              <button class="btn small danger" @click="removePost(p)" :disabled="loading">
                删除
              </button>
            </div>
          </div>

          <div class="content">{{ p.content }}</div>
          <div class="date">{{ formatTime(p.createdAt) }}</div>
        </div>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PageContainer from '@/components/common/PageContainer.vue'
import toast from '@/utils/toast'
import { fetchTeacherCourses, fetchCoursePosts, pinPost, deletePost } from '@/api/teacher'

const courses = ref([])
const courseId = ref('') // '' = 全部课程
const posts = ref([])
const loading = ref(false)

function formatTime(t) {
  if (!t) return '-'
  return new Date(t).toLocaleString()
}

async function loadCourses() {
  const res = await fetchTeacherCourses()
  courses.value = res || []
}

async function loadPosts() {
  loading.value = true
  posts.value = []
  try {
    // 全部课程：并发拉取每门课帖子然后合并
    if (!courseId.value) {
      const tasks = (courses.value || []).map(async (c) => {
        const list = await fetchCoursePosts(c.id)
        return (list || []).map(p => ({ ...p, _courseTitle: c.title }))
      })
      const merged = (await Promise.all(tasks)).flat()

      // 置顶优先，其次按时间倒序
      merged.sort((a, b) => {
        const ap = a.pinned ? 1 : 0
        const bp = b.pinned ? 1 : 0
        if (ap !== bp) return bp - ap
        return new Date(b.createdAt || 0) - new Date(a.createdAt || 0)
      })

      posts.value = merged
      return
    }

    // 单课程
    const cid = Number(courseId.value)
    const list = await fetchCoursePosts(cid)
    const title = courses.value.find(x => x.id === cid)?.title || ''
    posts.value = (list || []).map(p => ({ ...p, _courseTitle: title }))
  } catch (e) {
    toast.error('加载帖子失败', e?.message || '未知错误')
  } finally {
    loading.value = false
  }
}

async function togglePin(p) {
  try {
    loading.value = true
    await pinPost(p.id, !p.pinned)
    p.pinned = !p.pinned
    toast.success('操作成功', p.pinned ? '已置顶' : '已取消置顶')
    await loadPosts()
  } catch (e) {
    toast.error('操作失败', e?.message || '未知错误')
  } finally {
    loading.value = false
  }
}

async function removePost(p) {
  if (!confirm('确定删除该帖子？')) return
  try {
    loading.value = true
    await deletePost(p.id)
    toast.success('删除成功', '帖子已删除')
    await loadPosts()
  } catch (e) {
    toast.error('删除失败', e?.message || '未知错误')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadCourses()
  await loadPosts()
})
</script>

<style scoped>
.row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.select {
  height: 36px;
  border-radius: 10px;
  border: 1px solid rgba(17, 24, 39, 0.14);
  padding: 0 10px;
  background: rgba(255, 255, 255, 0.85);
}

.card {
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(17, 24, 39, 0.10);
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 12px 30px rgba(17, 24, 39, 0.08);
}

.tip {
  color: rgba(17, 24, 39, 0.65);
  padding: 14px 0;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.post-item {
  border-bottom: 1px solid rgba(17, 24, 39, 0.08);
  padding-bottom: 14px;
}

.post-item:last-child {
  border-bottom: none;
}

.post-item.pinned {
  background: rgba(79, 70, 229, 0.06);
  border-radius: 12px;
  padding: 12px;
}

.post-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.title {
  font-weight: 900;
  color: #111827;
}

.badge {
  background: rgba(79, 70, 229, 0.12);
  color: #4338ca;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid rgba(79, 70, 229, 0.20);
}

.course {
  color: rgba(17, 24, 39, 0.70);
  font-size: 12px;
}

.author {
  color: rgba(17, 24, 39, 0.55);
  font-size: 12px;
}

.actions {
  display: flex;
  align-items: center;
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

.small {
  padding: 6px 10px;
  font-size: 12px;
}

.danger {
  background: rgba(220, 38, 38, 0.10);
  border-color: rgba(220, 38, 38, 0.25);
  color: #991b1b;
}

.content {
  color: rgba(17, 24, 39, 0.78);
  line-height: 1.6;
}

.date {
  color: rgba(17, 24, 39, 0.45);
  font-size: 12px;
  margin-top: 6px;
}
</style>
