<template>
  <div class="case-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>案例管理</span>
          <el-button type="primary" @click="handleAdd">添加案例</el-button>
        </div>
      </template>

      <el-table :data="caseList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="category" label="分类" min-width="120" />
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
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="案例标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="电信诈骗" value="电信诈骗" />
            <el-option label="网络诈骗" value="网络诈骗" />
            <el-option label="金融诈骗" value="金融诈骗" />
            <el-option label="情感诈骗" value="情感诈骗" />
            <el-option label="兼职诈骗" value="兼职诈骗" />
            <el-option label="投资诈骗" value="投资诈骗" />
            <el-option label="购物诈骗" value="购物诈骗" />
            <el-option label="冒充诈骗" value="冒充诈骗" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="案例内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" />
        </el-form-item>
        <el-form-item label="诈骗手法分析" prop="fraudAnalysis">
          <el-input v-model="form.fraudAnalysis" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="防范建议" prop="preventionAdvice">
          <el-input v-model="form.preventionAdvice" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-upload
            class="avatar-uploader"
            :action="''"
            :http-request="(file) => uploadImage(file)"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeImageUpload"
          >
            <img v-if="form.coverImage" :src="form.coverImage" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div v-if="form.coverImage" class="cover-image-actions">
            <el-button size="small" type="danger" @click="removeCoverImage">删除封面图</el-button>
          </div>
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
import { getCaseList, createCase, updateCase, deleteCase } from '@/api/case'
import { uploadFile } from '@/api/upload'

const caseList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加案例')
const formRef = ref(null)
const form = ref({
  id: null,
  title: '',
  category: '',
  content: '',
  fraudAnalysis: '',
  preventionAdvice: '',
  coverImage: '',
  images: [],
  videoUrl: ''
})

const rules = {
  title: [{ required: true, message: '请输入案例标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入案例内容', trigger: 'blur' }],
  fraudAnalysis: [{ required: true, message: '请输入诈骗手法分析', trigger: 'blur' }],
  preventionAdvice: [{ required: true, message: '请输入防范建议', trigger: 'blur' }]
}

const loadCases = async () => {
  try {
    const res = await getCaseList()
    caseList.value = res.data || []
  } catch (error) {
    console.error('加载案例列表失败:', error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加案例'
  form.value = {
    id: null,
    title: '',
    category: '',
    content: '',
    fraudAnalysis: '',
    preventionAdvice: '',
    coverImage: '',
    images: [],
    videoUrl: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑案例'
  form.value = {
    id: row.id,
    title: row.title,
    category: row.category,
    content: row.content,
    fraudAnalysis: row.fraudAnalysis || row.victimInfo || '',
    preventionAdvice: row.preventionAdvice || row.warningTips || '',
    coverImage: row.coverImage,
    images: row.images ? (typeof row.images === 'string' ? JSON.parse(row.images) : row.images) : [],
    videoUrl: row.videoUrl || ''
  }
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
      await updateCase(form.value.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await createCase(submitData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadCases()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该案例吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCase(row.id)
      ElMessage.success('删除成功')
      loadCases()
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

const removeCoverImage = () => {
  form.value.coverImage = ''
  ElMessage.success('封面图删除成功')
}

onMounted(() => {
  loadCases()
})
</script>

<style scoped>
.case-manage {
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
</style>
