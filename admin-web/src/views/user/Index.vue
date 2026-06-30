<template>
  <div class="user-manage">
    <div class="page-header">
      <div class="page-title">
        <span class="title-icon">
          <el-icon><User /></el-icon>
        </span>
        <h2>用户管理</h2>
      </div>
      <div class="page-action">
        <el-button type="primary" @click="loadUsers">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <el-card class="content-card">
      <el-table :data="activeUsers" style="width: 100%" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" min-width="150">
          <template #default="{ row }">
            <div class="user-cell">
              <div class="user-avatar-small">
                <el-icon><User /></el-icon>
              </div>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" min-width="150" />
        <el-table-column prop="phone" label="手机号" min-width="150" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column prop="role" label="角色" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="(['ROLE_ADMIN', 'ADMIN', 'admin'].includes(row.role)) ? 'danger' : 'primary'" effect="light">
              {{ (['ROLE_ADMIN', 'ADMIN', 'admin'].includes(row.role)) ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="accountStatus" label="账号状态" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.accountStatus)" effect="light">
              {{ getStatusText(row.accountStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleResetPassword(row)" :icon="Lock">
              重置密码
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="用户详情" width="600px" class="detail-dialog">
      <el-descriptions :column="1" border class="detail-descriptions">
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="(['ROLE_ADMIN', 'ADMIN', 'admin'].includes(currentUser.role)) ? 'danger' : 'primary'" effect="light">
            {{ (['ROLE_ADMIN', 'ADMIN', 'admin'].includes(currentUser.role)) ? '管理员' : '普通用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <el-tag :type="getStatusType(currentUser.accountStatus)" effect="light">
            {{ getStatusText(currentUser.accountStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createdAt }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Refresh, Delete, Lock } from '@element-plus/icons-vue'
import { getUserList, deleteUser, resetUserPassword } from '@/api/user'

const loading = ref(false)
const userList = ref([])
const detailDialogVisible = ref(false)
const currentUser = ref({})

const activeUsers = computed(() => {
  return userList.value.filter(user => user.accountStatus === 'ACTIVE')
})

const getStatusType = (status) => {
  const types = {
    PENDING: 'warning',
    ACTIVE: 'success',
    SUSPENDED: 'danger',
    REJECTED: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    PENDING: '待审核',
    ACTIVE: '已激活',
    SUSPENDED: '已停用',
    REJECTED: '已拒绝'
  }
  return texts[status] || status
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUserList()
    console.log('用户管理页面 - API返回数据:', res)
    console.log('用户列表:', res.data)
    userList.value = res.data || []
    console.log('用户状态:', userList.value.map(user => ({ username: user.username, accountStatus: user.accountStatus })))
    console.log('用户角色:', userList.value.map(user => ({ username: user.username, role: user.role })))
    console.log('激活用户数量:', activeUsers.value.length)
    ElMessage.success('数据刷新成功')
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      loadUsers()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

const handleResetPassword = (row) => {
  ElMessageBox.confirm(`确定要重置用户 ${row.nickname} 的密码为 123456 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await resetUserPassword(row.id, { password: '123456' })
      ElMessage.success('密码重置成功')
    } catch (error) {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-manage {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.page-title h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.page-action {
  display: flex;
  align-items: center;
  gap: 12px;
}

.content-card {
  border: none;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.content-card :deep(.el-card__body) {
  padding: 24px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar-small {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  flex-shrink: 0;
}

:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: linear-gradient(180deg, #f8f9fa 0%, #f5f7fa 100%);
  color: #606266;
  font-weight: 600;
  font-size: 14px;
}

:deep(.el-table th.el-table__cell) {
  padding: 16px 0;
}

:deep(.el-table td) {
  padding: 14px 0;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: #fafbfc;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background: #f0f7ff;
}

:deep(.el-tag) {
  border-radius: 20px;
  padding: 4px 14px;
  font-weight: 500;
  font-size: 12px;
  border: none;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #66b1ff 0%, #409EFF 100%);
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #67C23A 0%, #95d475 100%);
  border: none;
}

:deep(.el-button--success:hover) {
  background: linear-gradient(135deg, #95d475 0%, #67C23A 100%);
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #F56C6C 0%, #f78989 100%);
  border: none;
}

:deep(.el-button--danger:hover) {
  background: linear-gradient(135deg, #f78989 0%, #F56C6C 100%);
}

:deep(.el-loading-mask) {
  border-radius: 12px;
}

:deep(.el-dialog) {
  border-radius: 16px;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 24px;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  font-size: 18px;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #f0f0f0;
  padding: 16px 24px;
}

.detail-descriptions :deep(.el-descriptions__label) {
  background: #f8f9fa;
  font-weight: 600;
}

.detail-descriptions :deep(.el-descriptions__content) {
  color: #303133;
}
</style>