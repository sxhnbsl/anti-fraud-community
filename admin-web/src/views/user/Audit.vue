<template>
  <div class="user-audit">
    <div class="page-header">
      <div class="page-title">
        <span class="title-icon">
          <el-icon><Tickets /></el-icon>
        </span>
        <h2>用户审核</h2>
      </div>
      <div class="page-action">
        <el-tag type="warning" effect="light">
          <el-icon><Warning /></el-icon>
          待审核: {{ totalPending }} 条
        </el-tag>
        <el-button type="primary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <el-card class="content-card">
      <el-tabs v-model="activeTab" class="audit-tabs">
        <!-- 注册审核标签页 -->
        <el-tab-pane label="注册审核" name="register">
          <el-table :data="pendingUsers" style="width: 100%" v-loading="loading" stripe>
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
            <el-table-column prop="createdAt" label="注册时间" min-width="180" align="center" />
            <el-table-column label="操作" min-width="280" align="center">
              <template #default="{ row }">
                <el-button size="small" type="success" @click="handleAudit(row, 'APPROVE')" :icon="Check">
                  通过
                </el-button>
                <el-button size="small" type="danger" @click="handleAudit(row, 'REJECT')" :icon="Close">
                  拒绝
                </el-button>
                <el-button size="small" @click="handleView(row)" :icon="View">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="pendingUsers.length === 0 && !loading" description="暂无待审核用户" />
        </el-tab-pane>

        <!-- 资料审核标签页 -->
        <el-tab-pane label="资料审核" name="profile">
          <el-table :data="pendingChanges" style="width: 100%" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="80" align="center" />
            <el-table-column prop="userId" label="用户ID" width="100" align="center" />
            <el-table-column prop="changeType" label="变更类型" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="row.changeType === 'nickname' ? 'primary' : 'info'" effect="light">
                  {{ row.changeType === 'nickname' ? '昵称' : '头像' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="oldValue" label="变更前" min-width="150">
              <template #default="{ row }">
                <div v-if="row.changeType === 'avatar'">
                  <el-image 
                    v-if="row.oldValue" 
                    :src="row.oldValue" 
                    :preview-src-list="[row.oldValue]" 
                    style="width: 60px; height: 60px; border-radius: 8px"
                  />
                  <span v-else>无</span>
                </div>
                <span v-else>{{ row.oldValue || '无' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="newValue" label="变更后" min-width="150">
              <template #default="{ row }">
                <div v-if="row.changeType === 'avatar'">
                  <el-image 
                    v-if="row.newValue" 
                    :src="row.newValue" 
                    :preview-src-list="[row.newValue]" 
                    style="width: 60px; height: 60px; border-radius: 8px"
                  />
                  <span v-else>无</span>
                </div>
                <span v-else>{{ row.newValue || '无' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="提交时间" width="180" align="center" />
            <el-table-column label="操作" width="200" align="center" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="success" @click="handleProfileAudit(row, 'APPROVE')" :icon="Check">
                  通过
                </el-button>
                <el-button size="small" type="danger" @click="handleProfileAudit(row, 'REJECT')" :icon="Close">
                  拒绝
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="pendingChanges.length === 0 && !loading" description="暂无待审核的资料变更" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="用户详情" width="600px" class="detail-dialog">
      <el-descriptions :column="1" border class="detail-descriptions">
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
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

    <el-dialog v-model="auditDialogVisible" :title="auditDialogTitle" width="500px" class="audit-dialog">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="昵称">
          <el-input v-model="auditForm.nickname" disabled />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="APPROVE">通过</el-radio>
            <el-radio label="REJECT">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="auditForm.remark" type="textarea" :rows="3" placeholder="请输入审核备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Tickets, Warning, Refresh, Check, Close, View } from '@element-plus/icons-vue'
import { getPendingUsers, auditUser, getPendingProfileChanges, auditProfileChange } from '@/api/user'

const loading = ref(false)
const activeTab = ref('register')
const userList = ref([])
const pendingChanges = ref([])
const detailDialogVisible = ref(false)
const auditDialogVisible = ref(false)
const currentUser = ref({})
const auditForm = ref({
  id: null,
  nickname: '',
  status: 'APPROVE',
  remark: ''
})

const pendingUsers = computed(() => {
  return userList.value.filter(user => user.accountStatus === 'PENDING')
})

const totalPending = computed(() => {
  return pendingUsers.value.length + pendingChanges.value.length
})

const auditDialogTitle = computed(() => {
  return auditForm.value.status === 'APPROVE' ? '通过审核' : '拒绝审核'
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

const refreshData = async () => {
  if (activeTab.value === 'register') {
    await loadUsers()
  } else {
    await loadChanges()
  }
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getPendingUsers()
    userList.value = res.data || []
    ElMessage.success('数据刷新成功')
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const loadChanges = async () => {
  loading.value = true
  try {
    const res = await getPendingProfileChanges()
    pendingChanges.value = (res.data || []).map(item => ({
      ...item,
      changeType: item.changeType === 'nickname' ? 'nickname' : 'avatar'
    }))
    ElMessage.success('数据刷新成功')
  } catch (error) {
    console.error('加载变更记录失败:', error)
    ElMessage.error('加载变更记录失败')
  } finally {
    loading.value = false
  }
}

const handleView = (row) => {
  currentUser.value = row
  detailDialogVisible.value = true
}

const handleAudit = (row, status) => {
  auditForm.value = {
    id: row.id,
    nickname: row.nickname,
    status: status,
    remark: ''
  }
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  try {
    await auditUser(auditForm.value.id, {
      action: auditForm.value.status,
      remark: auditForm.value.remark
    })
    ElMessage.success(auditForm.value.status === 'APPROVE' ? '审核通过' : '已拒绝')
    auditDialogVisible.value = false
    loadUsers()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

const handleProfileAudit = async (row, action) => {
  try {
    await ElMessageBox.confirm(
      `确定要${action === 'APPROVE' ? '通过' : '拒绝'}该资料变更吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await auditProfileChange(row.id, {
      action: action
    })
    ElMessage.success(action === 'APPROVE' ? '审核通过' : '已拒绝')
    loadChanges()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
      ElMessage.error('审核失败')
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-audit {
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
  background: linear-gradient(135deg, #E6A23C 0%, #f0c78a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3);
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

.audit-tabs {
  margin-top: 0;
}

.audit-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.audit-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  padding: 0 24px;
}

.audit-tabs :deep(.el-tabs__item.is-active) {
  color: #E6A23C;
  font-weight: 600;
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
  background: linear-gradient(180deg, #f8f9fa 0%, #f5f7fa 100%) !important;
}

:deep(.el-table td) {
  padding: 14px 0;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: #fafbfc !important;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background: #f0f7ff !important;
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