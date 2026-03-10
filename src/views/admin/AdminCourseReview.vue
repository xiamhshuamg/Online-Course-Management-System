<template>
  <PageContainer title="课程审核" desc="查看并处理教师提交的课程审核申请。">
    <template #extra>
      <button class="btn-refresh" @click="load">刷新列表</button>
    </template>

    <div class="card">
      <table class="table">
        <thead>
        <tr>
          <th>ID</th>
          <th>课程名称</th>
          <th style="width: 120px">教师</th>
          <th style="width: 120px">分类</th>
          <th style="width: 100px">价格</th>
          <th style="width: 200px">操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="c in list" :key="c.id">
          <td class="mono">{{ c.id }}</td>
          <td style="font-weight: 800">{{ c.title }}</td>
          <td>{{ c.teacherName }}</td>
          <td>{{ c.category }}</td>
          <td>{{ c.price }} 元</td>
          <td>
            <div class="op">
              <button class="btn-action primary" @click="openReview(c, true)">通过</button>
              <button class="btn-action danger" @click="openReview(c, false)">驳回</button>
            </div>
          </td>
        </tr>
        <tr v-if="!list.length">
          <td colspan="6" class="muted" style="padding: 24px; text-align: center;">
            暂无待审核课程
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <Modal v-model="modalVisible" :title="isApprove ? '通过审核' : '驳回审核'">
      <div v-if="activeItem">
        <p>正在处理课程：<strong>{{ activeItem.title }}</strong></p>
        <div class="field" style="margin-top: 16px;">
          <div class="label">审核备注 (可选)</div>
          <textarea class="input" v-model="remark" rows="4" placeholder="请输入审核意见..."></textarea>
        </div>
      </div>
      <template #footer>
        <button class="btn" @click="modalVisible = false">取消</button>
        <button :class="['btn', isApprove ? 'primary' : 'danger']" @click="submitDecision">
          确认{{ isApprove ? '通过' : '驳回' }}
        </button>
      </template>
    </Modal>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PageContainer from '../../components/common/PageContainer.vue'
import Modal from '../../components/common/Modal.vue'
import toast from '../../utils/toast'
import { fetchPendingCourses, reviewCourse } from '../../api/admin'

const list = ref([])
const modalVisible = ref(false)
const activeItem = ref(null)
const isApprove = ref(true)
const remark = ref('')

async function load() {
  try {
    list.value = await fetchPendingCourses()
  } catch (e) {
    toast.error('加载列表失败', e.message)
  }
}

function openReview(course, approved) {
  activeItem.value = course
  isApprove.value = approved
  remark.value = ''
  modalVisible.value = true
}

async function submitDecision() {
  if (!activeItem.value) return
  try {
    await reviewCourse(activeItem.value.id, isApprove.value, remark.value)
    toast.success('操作成功')
    modalVisible.value = false
    load() // 重新刷新列表
  } catch (e) {
    toast.error('审核提交失败', e.message)
  }
}

onMounted(load)
</script>

<style scoped>
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
.op {
  display: flex;
  gap: 8px;
}
.field {
  margin-top: 10px;
}
.label {
  font-size: 13px;
  color: var(--muted);
  margin-bottom: 6px;
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