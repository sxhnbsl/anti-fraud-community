<template>
  <div class="comment-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评论管理</span>
          <el-select v-model="filterStatus" placeholder="审核状态" @change="loadComments">
            <el-option label="全部" value="" />
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </div>
      </template>

      <el-table :data="commentList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="评论内容" min-width="300">
          <template #default="{ row }">
            <div class="content-cell">{{ row.content }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="postTitle" label="关联帖子" width="200">
          <template #default="{ row }">
            <div class="post-title">{{ row.postTitle || '未知帖子' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞数" width="80" />
        <el-table-column prop="auditStatus" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusType(row.auditStatus)">
              {{ getAuditStatusText(row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="auditDialogVisible" title="评论审核" width="600px">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="评论内容">
          <el-input v-model="auditForm.content" type="textarea" :rows="5" disabled />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="auditForm.authorName" disabled />
        </el-form-item>
        <el-form-item label="关联帖子">
          <el-input v-model="auditForm.postTitle" disabled />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="APPROVED">通过</el-radio>
            <el-radio label="REJECTED">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="auditForm.remark" type="textarea" :rows="3" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCommentList, auditComment, deleteComment } from '@/api/comment'

const commentList = ref([])
const filterStatus = ref('')
const auditDialogVisible = ref(false)
const auditForm = ref({
  id: null,
  content: '',
  authorName: '',
  postTitle: '',
  status: '',
  remark: ''
})

const getAuditStatusType = (status) => {
  const types = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger'
  }
  return types[status] || 'info'
}

const getAuditStatusText = (status) => {
  const texts = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已拒绝'
  }
  return texts[status] || status
}

const loadComments = async () => {
  try {
    const res = await getCommentList({ auditStatus: filterStatus.value })
    commentList.value = res.data || []
  } catch (error) {
    console.error('加载评论列表失败:', error)
  }
}

const handleView = (row) => {
  auditForm.value = {
    id: row.id,
    content: row.content,
    authorName: row.authorName,
    postTitle: row.postTitle || '未知帖子',
    status: '',
    remark: ''
  }
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  if (!auditForm.value.status) {
    ElMessage.warning('请选择审核结果')
    return
  }
  
  try {
    await auditComment(auditForm.value.id, {
      auditStatus: auditForm.value.status,
      auditRemark: auditForm.value.remark
    })
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    loadComments()
  } catch (error) {
    console.error('审核失败:', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteComment(row.id)
      ElMessage.success('删除成功')
      loadComments()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-cell {
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.post-title {
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>