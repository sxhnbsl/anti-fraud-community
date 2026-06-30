<template>
  <div class="knowledge-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>知识库管理</span>
          <el-button type="primary" @click="handleAdd">添加知识</el-button>
        </div>
      </template>

      <el-table :data="knowledgeList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="category" label="分类" min-width="120" />
        <el-table-column prop="tags" label="标签" min-width="150" />
        <el-table-column prop="viewCount" label="浏览量" min-width="100" />
        <el-table-column prop="likeCount" label="点赞数" min-width="100" />
        <el-table-column prop="createdAt" label="创建时间" min-width="180" />
        <el-table-column label="操作" min-width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-upload
            class="avatar-uploader"
            :action="''"
            :http-request="(file) => uploadImage(file)"
            :show-file-list="true"
            :on-success="handleImageSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeImageUpload"
          >
            <div v-if="form.coverImage" class="cover-image-container">
              <img :src="form.coverImage" class="avatar" />
              <el-button size="small" type="danger" @click="removeCoverImage">删除</el-button>
            </div>
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="upload-demo"
            :action="''"
            :http-request="(file) => uploadImage(file, 'images')"
            :show-file-list="true"
            :on-success="handleImageSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeImageUpload"
            multiple
          >
            <el-button type="primary">上传图片</el-button>
          </el-upload>
          <div v-if="form.images && form.images.length > 0" class="image-list">
            <div v-for="(image, index) in form.images" :key="index" class="image-item">
              <img :src="image" class="image-thumb" />
              <el-button size="small" type="danger" @click="removeImage(index)">删除</el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="视频">
          <el-upload
            class="upload-demo"
            :action="''"
            :http-request="(file) => uploadVideo(file)"
            :show-file-list="true"
            :on-success="handleVideoSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeVideoUpload"
            :limit="1"
          >
            <el-button type="primary">上传视频</el-button>
          </el-upload>
          <div v-if="form.videoUrl" class="video-item">
            <video :src="form.videoUrl" controls class="video-thumb"></video>
            <el-button size="small" type="danger" @click="removeVideo">删除</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getKnowledgeList, createKnowledge, updateKnowledge, deleteKnowledge } from '@/api/knowledge'
import { uploadFile } from '@/api/upload'

const knowledgeList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加知识')
const formRef = ref(null)
const form = ref({
  id: null,
  title: '',
  category: '',
  tags: '',
  content: '',
  coverImage: '',
  images: [],
  videoUrl: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请输入分类', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const loadKnowledge = async () => {
  try {
    const res = await getKnowledgeList()
    knowledgeList.value = res.data || []
  } catch (error) {
    console.error('加载知识列表失败:', error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加知识'
  form.value = {
    id: null,
    title: '',
    category: '',
    tags: '',
    content: '',
    coverImage: '',
    images: [],
    videoUrl: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  console.log('编辑知识 - 原始数据:', row)
  console.log('编辑知识 - images字段:', row.images, typeof row.images)
  
  dialogTitle.value = '编辑知识'
  form.value = {
    id: row.id,
    title: row.title,
    category: row.category,
    tags: row.tags,
    content: row.content,
    coverImage: row.coverImage,
    images: row.images ? (typeof row.images === 'string' ? JSON.parse(row.images) : row.images) : [],
    videoUrl: row.videoUrl || ''
  }
  
  console.log('编辑知识 - 处理后的form:', form.value)
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    await formRef.value.validate()
    // 将images数组转换为JSON字符串
    const submitData = {
      ...form.value,
      images: Array.isArray(form.value.images) ? JSON.stringify(form.value.images) : form.value.images
    }
    if (form.value.id) {
      await updateKnowledge(form.value.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await createKnowledge(submitData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadKnowledge()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该知识吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteKnowledge(row.id)
      ElMessage.success('删除成功')
      loadKnowledge()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 图片上传相关
const uploadImage = async (file, target = 'cover') => {
  try {
    const response = await uploadFile(file.file, 'image')
    if (response.success) {
      if (target === 'cover') {
        form.value.coverImage = response.data.url
      } else {
        form.value.images.push(response.data.url)
      }
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败: ' + error.message)
  }
}

const handleImageSuccess = (response, file, fileList) => {
  // 自定义上传已处理
}

const handleUploadError = (error, file, fileList) => {
  ElMessage.error('上传失败')
}

const removeCoverImage = () => {
  form.value.coverImage = ''
  ElMessage.success('封面图已删除')
}

const beforeImageUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif' || file.type === 'image/webp'
  const isLt10M = file.size / 1024 / 1024 < 10
  
  if (!isJpgOrPng) {
    ElMessage.error('只能上传图片文件!')
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
  }
  
  return isJpgOrPng && isLt10M
}

const removeImage = (index) => {
  form.value.images.splice(index, 1)
  ElMessage.success('图片删除成功')
}

// 视频上传相关
const uploadVideo = async (file) => {
  try {
    const response = await uploadFile(file.file, 'video')
    if (response.success) {
      form.value.videoUrl = response.data.url
      ElMessage.success('视频上传成功')
    } else {
      ElMessage.error(response.message || '视频上传失败')
    }
  } catch (error) {
    ElMessage.error('视频上传失败: ' + error.message)
  }
}

const handleVideoSuccess = (response, file, fileList) => {
  // 自定义上传已处理
}

const beforeVideoUpload = (file) => {
  const isVideo = file.type.startsWith('video/')
  const isLt200M = file.size / 1024 / 1024 < 200
  
  if (!isVideo) {
    ElMessage.error('只能上传视频文件!')
  }
  if (!isLt200M) {
    ElMessage.error('视频大小不能超过 200MB!')
  }
  
  return isVideo && isLt200M
}

const removeVideo = () => {
  form.value.videoUrl = ''
  ElMessage.success('视频删除成功')
}

onMounted(() => {
  loadKnowledge()
})
</script>

<style scoped>
.knowledge-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.image-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.image-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.image-item .el-button {
  position: absolute;
  top: 5px;
  right: 5px;
  padding: 4px 8px;
  font-size: 12px;
  min-height: auto;
}

.video-item {
  margin-top: 10px;
  position: relative;
  width: 300px;
}

.video-thumb {
  width: 100%;
  border-radius: 4px;
}

.video-item .el-button {
  position: absolute;
  top: 5px;
  right: 5px;
  padding: 4px 8px;
  font-size: 12px;
  min-height: auto;
}

.cover-image-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.cover-image-container .el-button {
  margin-top: 8px;
  width: 120px;
}
</style>
