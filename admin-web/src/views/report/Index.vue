<template>
  <div class="report-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>举报管理</span>
          <el-select v-model="filterStatus" placeholder="举报状态" @change="loadReports">
            <el-option label="全部" value="" />
            <el-option label="待处理" value="PENDING" />

            <el-option label="已解决" value="RESOLVED" />
          </el-select>
        </div>
      </template>

      <el-table :data="reportList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="reportedType" label="举报类型" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="getReportedTypeType(row.reportedType)">
              {{ getReportedTypeText(row.reportedType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reporterName" label="举报人" width="120" />
        <el-table-column prop="reason" label="举报原因" min-width="300">
          <template #default="{ row }">
            <div class="reason-cell">{{ row.reason }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button size="small" type="success" @click="quickHandle(row)" v-if="row.status === 'PENDING' || row.status === 'pending' || row.status === 'PROCESSING' || row.status === 'processing'">
              处理
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="handleDialogVisible" title="处理举报" width="600px">
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="举报类型">
          <el-input v-model="handleForm.reportedTypeText" disabled />
        </el-form-item>
        <el-form-item label="举报人">
          <el-input v-model="handleForm.reporterName" disabled />
        </el-form-item>
        <el-form-item label="被举报对象ID">
          <el-input v-model="handleForm.reportedId" disabled />
        </el-form-item>
        <el-form-item label="举报原因">
          <el-input v-model="handleForm.reason" type="textarea" :rows="4" disabled />
        </el-form-item>
        <el-form-item label="举报描述">
          <el-input v-model="handleForm.description" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="handleForm.status" placeholder="选择处理状态">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已解决" value="RESOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果">
          <el-input v-model="handleForm.handleResult" type="textarea" :rows="3" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReportList, handleReport, deleteReport } from '@/api/report'

const reportList = ref([])
const filterStatus = ref('')
const handleDialogVisible = ref(false)
const handleForm = ref({
  id: null,
  reportedType: '',
  reportedTypeText: '',
  reporterName: '',
  reportedId: '',
  reason: '',
  description: '',
  status: '',
  handleResult: ''
})

const getReportedTypeType = (type) => {
  const types = {
    POST: 'info',
    COMMENT: 'warning',
    USER: 'danger'
  }
  return types[type] || 'info'
}

const getReportedTypeText = (type) => {
  const texts = {
    POST: '帖子',
    COMMENT: '评论',
    USER: '用户'
  }
  return texts[type] || type
}

const getStatusType = (status) => {
  const types = {
    PENDING: 'warning',
    pending: 'warning',
    PROCESSING: 'info',
    processing: 'info',
    RESOLVED: 'success',
    resolved: 'success'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    PENDING: '待处理',
    pending: '待处理',
    PROCESSING: '处理中',
    processing: '处理中',
    RESOLVED: '已解决',
    resolved: '已解决'
  }
  return texts[status] || status
}

const loadReports = async () => {
  try {
    const res = await getReportList({ status: filterStatus.value })
    reportList.value = res.data || []
  } catch (error) {
    console.error('加载举报列表失败:', error)
  }
}

const handleView = (row) => {
  handleForm.value = {
    id: row.id,
    reportedType: row.reportedType,
    reportedTypeText: getReportedTypeText(row.reportedType),
    reporterName: row.reporterName,
    reportedId: row.reportedId,
    reason: row.reason,
    description: row.description,
    status: row.status,
    handleResult: row.handleResult || ''
  }
  handleDialogVisible.value = true
}

const submitHandle = async () => {
  if (!handleForm.value.status) {
    ElMessage.warning('请选择处理状态')
    return
  }
  
  try {
    await handleReport(handleForm.value.id, {
      status: handleForm.value.status,
      handleResult: handleForm.value.handleResult
    })
    ElMessage.success('处理成功')
    handleDialogVisible.value = false
    loadReports()
  } catch (error) {
    console.error('处理失败:', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该举报吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteReport(row.id)
      ElMessage.success('删除成功')
      loadReports()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

const quickHandle = async (row) => {
  ElMessageBox.confirm('确定要将该举报标记为已解决吗？', '处理举报', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      await handleReport(row.id, {
        status: 'RESOLVED',
        handleResult: '已处理'
      })
      ElMessage.success('处理成功')
      loadReports()
    } catch (error) {
      console.error('处理失败:', error)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped>
.report-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.reason-cell {
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>