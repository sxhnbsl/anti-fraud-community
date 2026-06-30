<template>
  <div class="profile-audit">
    <div class="page-header">
      <div class="page-title">
        <span class="title-icon">
          <el-icon><Avatar /></el-icon>
        </span>
        <h2>用户资料审核</h2>
      </div>
      <div class="page-action">
        <el-tag type="warning" effect="light">
          <el-icon><Warning /></el-icon>
          待审核: {{ pendingChanges.length }} 条
        </el-tag>
        <el-button type="primary" @click="loadChanges">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <el-card class="content-card">
      <template v-if="pendingChanges.length > 0">
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
              <el-button size="small" type="success" @click="handleAudit(row, 'APPROVE')" :icon="Check">
                通过
              </el-button>
              <el-button size="small" type="danger" @click="handleAudit(row, 'REJECT')" :icon="Close">
                拒绝
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
      <el-empty v-else-if="!loading" description="暂无待审核的资料变更" />
    </el-card>

    <el-dialog v-model="auditDialogVisible" :title="auditDialogTitle" width="500px" class="audit-dialog">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="变更ID">
          <el-input v-model="auditForm.id" disabled />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="auditForm.userId" disabled />
        </el-form-item>
        <el-form-item label="变更类型">
          <el-input v-model="auditForm.changeType" disabled />
        </el-form-item>
        <el-form-item label="变更前">
          <div v-if="auditForm.changeType === '头像'">
            <el-image 
              v-if="auditForm.oldValue" 
              :src="auditForm.oldValue" 
              :preview-src-list="[auditForm.oldValue]" 
              style="width: 100px; height: 100px; border-radius: 8px"
            />
            <span v-else>无</span>
          </div>
          <el-input v-else v-model="auditForm.oldValue" disabled />
        </el-form-item>
        <el-form-item label="变更后">
          <div v-if="auditForm.changeType === '头像'">
            <el-image 
              v-if="auditForm.newValue" 
              :src="auditForm.newValue" 
              :preview-src-list="[auditForm.newValue]" 
              style="width: 100px; height: 100px; border-radius: 8px"
            />
            <span v-else>无</span>
          </div>
          <el-input v-else v-model="auditForm.newValue" disabled />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.action">
            <el-radio label="APPROVE">通过</el-radio>
            <el-radio label="REJECT">拒绝</el-radio>
          </el-radio-group>
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
import { ElMessage } from 'element-plus'
import { Avatar, Warning, Refresh, Check, Close } from '@element-plus/icons-vue'
import { getPendingProfileChanges, auditProfileChange } from '@/api/user'

const loading = ref(false)
const pendingChanges = ref([])
const auditDialogVisible = ref(false)
const auditForm = ref({
  id: null,
  userId: '',
  changeType: '',
  oldValue: '',
  newValue: '',
  action: 'APPROVE'
})

const auditDialogTitle = computed(() => {
  return auditForm.value.action === 'APPROVE' ? '通过审核' : '拒绝审核'
})

const loadChanges = async () => {
  loading.value = true
  try {
    const res = await getPendingProfileChanges()
    // 转换变更类型为中文
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

const handleAudit = (row, action) => {
  auditForm.value = {
    id: row.id,
    userId: row.userId,
    changeType: row.changeType === 'nickname' ? '昵称' : '头像',
    oldValue: row.oldValue,
    newValue: row.newValue,
    action: action
  }
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  try {
    await auditProfileChange(auditForm.value.id, {
      action: auditForm.value.action
    })
    ElMessage.success(auditForm.value.action === 'APPROVE' ? '审核通过' : '已拒绝')
    auditDialogVisible.value = false
    loadChanges()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

onMounted(() => {
  loadChanges()
})
</script>

<style scoped>
.profile-audit {
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
  background: linear-gradient(135deg, #9c27b0 0%, #d1c4e9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(156, 39, 176, 0.3);
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
</style>
