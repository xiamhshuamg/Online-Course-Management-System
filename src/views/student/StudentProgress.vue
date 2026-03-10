<template>
  <PageContainer title="学习进度" desc="按课程查看完成率与最近学习时间。">
    <div class="card">
      <table class="table">
        <thead>
        <tr>
          <th>课程</th>
          <th style="width: 120px">已完成</th>
          <th style="width: 120px">总章节</th>
          <th style="width: 220px">完成率</th>
          <th style="width: 220px">最近学习</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="row in list" :key="row.courseId">
          <td style="font-weight: 800">{{ row.courseTitle }}</td>
          <!-- 后端现在返回 chapterCompleted 和 chapterTotal -->
          <td>{{ row.chapterCompleted }} 章</td>
          <td>{{ row.chapterTotal }} 章</td>
          <td><ProgressBar :value="row.progress" /></td>
          <td class="mono">{{ formatTime(row.lastStudyTime) }}</td>
        </tr>

        <tr v-if="!list.length">
          <td colspan="5" class="muted" style="padding: 18px">
            {{ loading ? '加载中...' : '暂无学习记录' }}
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PageContainer from '../../components/common/PageContainer.vue'
import ProgressBar from '../../components/common/ProgressBar.vue'
import toast from '../../utils/toast'
import { fetchProgress } from '../../api/student'

const list = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    list.value = await fetchProgress()
  } catch (e) {
    toast.error('加载失败', String(e?.message || e))
  } finally {
    loading.value = false
  }
}

const formatTime = (t) => {
  return t ? new Date(t).toLocaleString() : '-'
}

onMounted(load)
</script>

<style scoped>
.mono {
  font-family: monospace;
  color: #6b7280;
}
.muted {
  text-align: center;
  color: #9ca3af;
}
</style>