<template>
  <PageContainer title="系统设置" desc="系统基础参数配置。">
    <template #extra>
      <button class="btn-refresh" @click="load">刷新</button>
    </template>

    <div class="card" style="padding: 16px">
      <div v-if="loading" style="padding: 20px; text-align: center; color: #666;">加载中...</div>

      <div v-else class="row">
        <div class="col field">
          <div class="label">系统名称</div>
          <input class="input" v-model="form.siteName" />
        </div>
        <div class="col field">
          <div class="label">允许学生注册</div>
          <select class="select" v-model="form.allowStudentRegister">
            <option :value="true">是</option>
            <option :value="false">否</option>
          </select>
        </div>
        <div class="col field">
          <div class="label">教师认证必须审核</div>
          <select class="select" v-model="form.teacherVerifyRequired">
            <option :value="true">是</option>
            <option :value="false">否</option>
          </select>
        </div>
      </div>

      <div class="hr"></div>

      <div class="actions">
        <button class="btn-refreshs" @click="save" :disabled="loading">保存</button>
        <button class="btn-refreshs" @click="reset" :disabled="loading">重置</button>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PageContainer from '../../components/common/PageContainer.vue'
import toast from '../../utils/toast'
import { fetchSettings, updateSettings } from '../../api/admin'

const loading = ref(false)
const form = ref({
  siteName: '',
  allowStudentRegister: true,
  teacherVerifyRequired: true
})
const origin = ref(null)

async function load() {
  loading.value = true
  try {
    const res = await fetchSettings()
    origin.value = { ...res }
    form.value = { ...res }/*两个独立的对象，互不影响*/
  } catch (e) {
    console.warn('加载设置失败:', e.message)
    toast.error('加载失败', '无法连接到设置接口')
  } finally {
    loading.value = false
  }
}

function reset() {
  if (!origin.value) return
  form.value = { ...origin.value }
  toast.info('已重置')
}

async function save() {
  try {
    await updateSettings(form.value)
    toast.success('保存成功')
    load()
  } catch (e) {
    toast.error('保存失败', String(e?.message || e))
  }
}

onMounted(load)
</script>

<style scoped>
.field{
  margin-bottom: 12px;
}
.label{
  font-size: 15px;
  color:black;
  margin-bottom: 6px;
}
.actions{
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
.btn-refreshs {
  padding: 8px 16px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 10px;
}
.btn-refreshs:hover {
  background: #f9fafb;
}
</style>