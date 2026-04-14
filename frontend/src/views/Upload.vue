<template>
  <div class="upload-container">
    <el-card>
      <template #header>
        <h3>上传隐私协议</h3>
      </template>
      
      <el-tabs v-model="activeTab">
        <!-- 文件上传 -->
        <el-tab-pane label="文件上传" name="file">
          <el-upload
            drag
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            accept=".txt,.pdf,.doc,.docx"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 txt/pdf/doc/docx 格式文件
              </div>
            </template>
          </el-upload>
        </el-tab-pane>
        
        <!-- 文本输入 -->
        <el-tab-pane label="文本输入" name="text">
          <el-input
            v-model="textContent"
            type="textarea"
            :rows="15"
            placeholder="请粘贴隐私协议文本内容..."
          />
        </el-tab-pane>
      </el-tabs>
      
      <div class="form-actions" style="margin-top: 20px">
        <el-input v-model="documentTitle" placeholder="请输入文档标题" style="width: 300px; margin-right: 10px" />
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          提交分析
        </el-button>
      </div>
    </el-card>
    
    <!-- 分析进度 -->
    <el-dialog v-model="progressVisible" title="分析进行中" :close-on-click-modal="false" :show-close="false">
      <el-progress :percentage="progress" :status="progressStatus" />
      <p style="text-align: center; margin-top: 10px">{{ progressText }}</p>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { uploadDocument, submitDocumentText } from '../api/document'
import { startAnalyze } from '../api/analyze'

const router = useRouter()
const activeTab = ref('file')
const selectedFile = ref(null)
const textContent = ref('')
const documentTitle = ref('')
const submitting = ref(false)
const progressVisible = ref(false)
const progress = ref(0)
const progressStatus = ref('')
const progressText = ref('正在分析...')
let progressInterval = null

const handleFileChange = (file) => {
  selectedFile.value = file.raw
  documentTitle.value = file.name
}

const handleSubmit = async () => {
  if (!documentTitle.value) {
    ElMessage.warning('请输入文档标题')
    return
  }
  
  if (activeTab.value === 'file' && !selectedFile.value) {
    ElMessage.warning('请选择文件')
    return
  }
  
  if (activeTab.value === 'text' && !textContent.value.trim()) {
    ElMessage.warning('请输入文本内容')
    return
  }
  
  submitting.value = true
  
  try {
    let documentId
    if (activeTab.value === 'file') {
      const res = await uploadDocument(selectedFile.value)
      if (res.code === 200) {
        documentId = res.data.id
      } else {
        throw new Error(res.message)
      }
    } else {
      const res = await submitDocumentText({
        title: documentTitle.value,
        text: textContent.value
      })
      if (res.code === 200) {
        documentId = res.data.id
      } else {
        throw new Error(res.message)
      }
    }
    
    // 开始分析
    progressVisible.value = true
    simulateProgress()
    
    const res = await startAnalyze({ documentId })
    if (res.code === 200) {
      progress.value = 100
      progressStatus.value = 'success'
      progressText.value = '分析完成！'
      setTimeout(() => {
        progressVisible.value = false
        ElMessage.success('分析完成')
        router.push(`/home/result/${res.data.taskId}`)
      }, 500)
    } else {
      throw new Error(res.message)
    }
  } catch (error) {
    progressStatus.value = 'exception'
    progressText.value = '分析失败'
    ElMessage.error('提交失败：' + error.message)
  } finally {
    submitting.value = false
  }
}

const simulateProgress = () => {
  progress.value = 0
  clearProgressInterval()
  progressInterval = setInterval(() => {
    progress.value += 10
    if (progress.value >= 90) {
      clearProgressInterval()
    }
  }, 300)
}

const clearProgressInterval = () => {
  if (progressInterval) {
    clearInterval(progressInterval)
    progressInterval = null
  }
}

// 组件卸载时清理定时器，防止内存泄漏
onUnmounted(() => {
  clearProgressInterval()
})
</script>

<style scoped>
.upload-container {
  max-width: 900px;
  margin: 0 auto;
}

.form-actions {
  display: flex;
  justify-content: center;
}
</style>
