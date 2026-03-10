<template>
  <PageContainer title="用户管理" desc="查看系统所有用户，支持冻结与解冻操作">
    <template #extra>
      <button class="btn-refresh" @click="loadData">刷新列表</button>
    </template>

    <div class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>姓名</th>
          <th>邮箱</th>
          <th>角色</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="user in userList" :key="user.id">
          <td>{{ user.id }}</td>
          <td class="font-medium">{{ user.name }}</td>
          <td>{{ user.email }}</td>
          <td>
              <span :class="['role-badge', user.role.toLowerCase()]">
                {{ formatRole(user.role) }}
              </span>
          </td>
          <td>
            <span :class="['status-dot', user.status === 'ACTIVE' ? 'active' : 'frozen']"></span>
            {{ user.status === 'ACTIVE' ? '正常' : '已冻结' }}
          </td>
          <td>
            <button
                v-if="user.status === 'ACTIVE'"
                class="btn-action danger"
                @click="handleFreeze(user)"
            >
              冻结
            </button>
            <button
                v-else
                class="btn-action primary"
                @click="handleUnfreeze(user)"
            >
              解冻
            </button>
          </td>
        </tr>
        <tr v-if="userList.length === 0">
          <td colspan="6" class="empty-text">暂无数据</td>
        </tr>
        </tbody>
      </table>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PageContainer from '../../components/common/PageContainer.vue'
import { fetchUsers, freezeUser, unfreezeUser } from '../../api/admin'
import toast from '../../utils/toast'

const userList = ref([])

const loadData = async () => {
  try {
    const res = await fetchUsers()
    userList.value = res || []
  } catch (error) {
  }
}

const formatRole = (role) => {
  const map = {
    'ADMIN': '管理员',
    'TEACHER': '教师',
    'STUDENT': '学生'
  }
  return map[role] || role
}

const handleFreeze = async (user) => {
  if (!confirm(`确定要冻结用户 "${user.name}" 吗？该用户将无法登录。`)) return
  try {
    await freezeUser(user.id)
    toast.success('操作成功', `用户 ${user.name} 已冻结`)
    await loadData() // 刷新列表
  } catch (e) {}
}

const handleUnfreeze = async (user) => {
  try {
    await unfreezeUser(user.id)
    toast.success('操作成功', `用户 ${user.name} 已恢复正常`)
    await loadData()
  } catch (e) {}
}

onMounted(() => {
  loadData()
})
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

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table th {
  text-align: left;
  padding: 12px 16px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  color: #6b7280;
  font-weight: 600;
}

.data-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #f3f4f6;
  color: #374151;
}

.font-medium {
  font-weight: 500;
  color: #111827;
}

/* 角色徽章 */
.role-badge {
  padding: 2px 8px;
  border-radius: 99px;
  font-size: 12px;
  font-weight: 500;
}
.role-badge.admin { background: #fee2e2; color: #991b1b; }
.role-badge.teacher { background: #fef3c7; color: #92400e; }
.role-badge.student { background: #dbeafe; color: #1e40af; }

/* 状态点 */
.status-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}
.status-dot.active { background: #10b981; }
.status-dot.frozen { background: #ef4444; }

/* 操作按钮 */
.btn-action {
  padding: 4px 10px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 12px;
  margin-right: 6px;
}
.btn-action.danger { background: #fee2e2; color: #991b1b; }
.btn-action.danger:hover { background: #fecaca; }

.btn-action.primary { background: #e0e7ff; color: #4338ca; }
.btn-action.primary:hover { background: #c7d2fe; }

.empty-text {
  text-align: center;
  color: #9ca3af;
  padding: 20px;
}
</style>