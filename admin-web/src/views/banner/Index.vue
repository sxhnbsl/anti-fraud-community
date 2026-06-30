<template>
  <div class="banner-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>轮播图管理</span>
          <el-button type="primary" @click="handleAdd" size="small">
            <el-icon><Plus /></el-icon>
            添加轮播图
          </el-button>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="banners" style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" min-width="120">
          <template #default="scope">
            <el-image 
              :src="scope.row.imageUrl" 
              fit="cover" 
              :preview-src-list="[scope.row.imageUrl]"
              style="width: 80px; height: 40px;" 
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="linkType" label="链接类型" min-width="120">
          <template #default="scope">
            {{ getLinkTypeText(scope.row.linkType) }}
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" min-width="80" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'active' ? 'success' : 'info'">
              {{ scope.row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="240">
          <template #default="scope">
            <div style="display: flex; gap: 8px; align-items: center">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button 
                :type="scope.row.status === 'active' ? 'warning' : 'success'" 
                size="small" 
                @click="handleToggleStatus(scope.row)"
              >
                <el-icon><Switch /></el-icon>
                {{ scope.row.status === 'active' ? '禁用' : '启用' }}
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑轮播图' : '添加轮播图'"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="轮播图标题">
          <el-input v-model="form.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/api/upload"
            :show-file-list="false"
            :on-success="handleImageUploadSuccess"
            :before-upload="beforeUpload"
            :data="{ type: 'image' }"
          >
            <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" placeholder="请输入跳转链接" />
        </el-form-item>
        <el-form-item label="链接类型">
          <el-select v-model="form.linkType" placeholder="请选择链接类型">
            <el-option label="无链接" value="none" />
            <el-option label="知识" value="knowledge" />
            <el-option label="案例" value="case" />
            <el-option label="帖子" value="post" />
            <el-option label="网页" value="webview" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联内容ID" v-if="['knowledge', 'case', 'post'].includes(form.linkType)">
          <el-input v-model="form.linkId" type="number" placeholder="请输入关联内容ID" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model="form.sortOrder" type="number" placeholder="请输入排序值" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" active-value="active" inactive-value="inactive" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.avatar-uploader {
  width: 200px;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #999;
  width: 200px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-uploader-icon:hover {
  border-color: #409EFF;
}

.avatar {
  width: 200px;
  height: 100px;
  display: block;
  border-radius: 6px;
}
</style>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Switch } from '@element-plus/icons-vue'
import axios from 'axios'

const loading = ref(false)
const banners = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  title: '',
  imageUrl: '',
  linkUrl: '',
  linkType: 'none',
  linkId: null,
  sortOrder: 100,
  status: 'active'
})

// 加载轮播图列表
const loadBanners = async () => {
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/banners')
    if (response.data.code === 200) {
      banners.value = response.data.data
    } else {
      ElMessage.error('加载轮播图失败')
    }
  } catch (error) {
    console.error('加载轮播图失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 图片上传前的验证
const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isJPG) {
    ElMessage.error('只能上传JPG/PNG图片!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB!')
    return false
  }
  return true
}

// 图片上传成功回调
const handleImageUploadSuccess = (response) => {
  if (response.success) {
    form.value.imageUrl = response.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败: ' + response.message)
  }
}

// 添加轮播图
const handleAdd = () => {
  isEdit.value = false
  form.value = {
    title: '',
    imageUrl: '',
    linkUrl: '',
    linkType: 'none',
    linkId: null,
    sortOrder: 100,
    status: 'active'
  }
  dialogVisible.value = true
}

// 编辑轮播图
const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

// 保存轮播图
const handleSave = async () => {
  try {
    let response
    if (isEdit.value) {
      response = await axios.put(`http://localhost:8080/api/banners/${form.value.id}`, form.value)
    } else {
      response = await axios.post('http://localhost:8080/api/banners', form.value)
    }
    
    if (response.data.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadBanners()
    } else {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    }
  } catch (error) {
    console.error('保存轮播图失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 切换状态
const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 'active' ? 'inactive' : 'active'
    const response = await axios.put(`http://localhost:8080/api/banners/${row.id}/status`, { status: newStatus })
    
    if (response.data.code === 200) {
      ElMessage.success('状态更新成功')
      loadBanners()
    } else {
      ElMessage.error('状态更新失败')
    }
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 删除轮播图
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个轮播图吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await axios.delete(`http://localhost:8080/api/banners/${id}`)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      loadBanners()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    if (error.message !== 'cancel') {
      console.error('删除轮播图失败:', error)
      ElMessage.error('网络错误，请稍后重试')
    }
  }
}

// 获取链接类型文本
const getLinkTypeText = (linkType) => {
  const linkTypeMap = {
    none: '无链接',
    knowledge: '知识',
    case: '案例',
    post: '帖子',
    webview: '网页'
  }
  return linkTypeMap[linkType] || linkType
}

// 页面加载时加载轮播图
onMounted(() => {
  loadBanners()
})
</script>

<style scoped>
.banner-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>