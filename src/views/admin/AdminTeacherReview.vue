<template>
  <PageContainer title="教师审核" desc="审核教师认证申请。">
    <template #extra>
      <button class="btn-refresh" @click="load">刷新</button>
    </template>

    <div class="card">
      <table class="table">
        <thead>
        <tr>
          <th>姓名</th>
          <th style="width: 240px">邮箱</th>
          <th style="width: 160px">状态</th>
          <th style="width: 240px">操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="t in list" :key="t.id">
          <td style="font-weight: 900">{{ t.name }}</td>
          <td class="mono">{{ t.email }}</td>
          <td>
            <span class="badge warning">待审核</span>
          </td>
          <td>
            <div class="op">
              <button class="btn-action primary" @click="review(t, true)">通过</button>
              <button class="btn-action danger" @click="review(t, false)">驳回</button>
            </div>
          </td>
        </tr>

        <tr v-if="!list.length">
          <td colspan="4" class="muted" style="padding: 18px">
            {{ errorMsg ? errorMsg : '暂无待审核教师' }}
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
import toast from '../../utils/toast'
import { fetchTeacherApplications, reviewTeacher } from '../../api/admin'

const list = ref([])
const errorMsg = ref('')

async function load() {
  errorMsg.value = ''
  try {
    list.value = await fetchTeacherApplications()
  } catch (e) {
    console.error(e)
    if (e.message && e.message.includes('404')) {
      errorMsg.value = '后端缺少获取待审核教师的接口 (GET /admin/teachers/pending)'
    } else {
      errorMsg.value = '加载失败: ' + e.message
    }
  }
}

async function review(t, approved) {
  if (!confirm(`确认${approved ? '通过' : '驳回'}教师「${t.name}」吗？`)) return
  try {
    await reviewTeacher(t.id, approved)
    toast.success(approved ? '已通过' : '已驳回')
    load()
  } catch (e) {
    toast.error('操作失败', String(e?.message || e))
  }
}

onMounted(load)
</script>

<style scoped>
.op{
  display:flex;
  gap: 10px;
  flex-wrap: wrap;
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
.btn-action.danger {
  background: #fee2e2;
  color: #991b1b;
  border:none;
  cursor: pointer;
}
.btn-action.danger:hover { background: #fecaca; }

.btn-action.primary {
  background: #e0e7ff;
  color: #4338ca;
  border:none;
  cursor: pointer;
}
.btn-action.primary:hover { background: #c7d2fe; }
</style>