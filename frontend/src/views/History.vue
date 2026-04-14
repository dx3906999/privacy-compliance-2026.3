<template>
  <div class="history-container">
    <el-card>
      <template #header>
        <h3>历史分析记录</h3>
      </template>
      
      <el-table :data="historyList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="documentTitle" label="文档标题" />
        <el-table-column prop="analyzeTime" label="分析时间" width="180" />
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="scope">
            <el-tag :type="getRiskType(scope.row.riskLevel)">{{ scope.row.riskLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskScore" label="风险评分" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewResult(scope.row)">查看结果</el-button>
            <el-button size="small" type="danger" @click="deleteRecord(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 模拟历史数据
const historyList = ref([
  {
    id: 1,
    documentTitle: '某 APP 隐私政策',
    analyzeTime: '2026-03-13 13:40:00',
    riskLevel: 'HIGH',
    riskScore: 78.5,
    taskId: 1
  },
  {
    id: 2,
    documentTitle: '电商平台隐私声明',
    analyzeTime: '2026-03-12 10:20:00',
    riskLevel: 'MEDIUM',
    riskScore: 45.0,
    taskId: 2
  },
  {
    id: 3,
    documentTitle: '社交媒体隐私协议',
    analyzeTime: '2026-03-11 15:30:00',
    riskLevel: 'LOW',
    riskScore: 22.0,
    taskId: 3
  }
])

const getRiskType = (level) => {
  if (level === 'HIGH') return 'danger'
  if (level === 'MEDIUM') return 'warning'
  return 'success'
}

const viewResult = (row) => {
  router.push(`/home/result/${row.taskId}`)
}

const deleteRecord = (row) => {
  ElMessageBox.confirm('确定要删除这条分析记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    historyList.value = historyList.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  }).catch(() => {
    // 用户取消删除操作
  })
}
</script>

<style scoped>
.history-container {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
