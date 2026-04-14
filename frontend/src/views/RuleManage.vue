<template>
  <div class="rules-container">
    <el-card>
      <template #header>
        <div class="rules-header">
          <h3>规则管理</h3>
          <el-button type="primary" @click="showAddDialog">新增规则</el-button>
        </div>
      </template>
      
      <el-table :data="ruleList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="ruleName" label="规则名称" />
        <el-table-column prop="ruleType" label="规则类型" width="120">
          <template #default="scope">
            <el-tag>{{ scope.row.ruleType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskType" label="风险类型" width="150" />
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="scope">
            <el-tag :type="getRiskType(scope.row.riskLevel)">{{ scope.row.riskLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.enabled ? 'success' : 'info'">
              {{ scope.row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="editRule(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRule(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增/编辑规则对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="ruleForm" label-width="100px">
        <el-form-item label="规则名称">
          <el-input v-model="ruleForm.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="规则类型">
          <el-select v-model="ruleForm.ruleType" placeholder="请选择规则类型">
            <el-option label="KEYWORD" value="KEYWORD" />
            <el-option label="REGEX" value="REGEX" />
            <el-option label="MISSING_CHECK" value="MISSING_CHECK" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险类型">
          <el-input v-model="ruleForm.riskType" placeholder="如：信息收集目的不明确" />
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="ruleForm.riskLevel" placeholder="请选择风险等级">
            <el-option label="HIGH" value="HIGH" />
            <el-option label="MEDIUM" value="MEDIUM" />
            <el-option label="LOW" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="匹配模式">
          <el-input v-model="ruleForm.pattern" type="textarea" :rows="3" placeholder="关键词或正则表达式" />
        </el-form-item>
        <el-form-item label="法律依据">
          <el-input v-model="ruleForm.lawReference" placeholder="如：个人信息保护法 第 17 条" />
        </el-form-item>
        <el-form-item label="修改建议">
          <el-input v-model="ruleForm.suggestion" type="textarea" :rows="3" placeholder="建议内容" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="ruleForm.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRule">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 模拟规则数据
const ruleList = ref([
  {
    id: 1,
    ruleName: '收集目的描述模糊',
    ruleType: 'KEYWORD',
    riskType: '信息收集目的不明确',
    riskLevel: 'MEDIUM',
    pattern: '可能，用于相关服务',
    lawReference: '个人信息保护法 第 17 条',
    suggestion: '建议明确说明收集目的、范围和使用场景。',
    enabled: true
  },
  {
    id: 2,
    ruleName: '共享规则缺失必要信息',
    ruleType: 'KEYWORD',
    riskType: '共享规则不明确',
    riskLevel: 'HIGH',
    pattern: '与第三方共享',
    lawReference: '个人信息保护法 第 23 条',
    suggestion: '建议明确说明共享的第三方类型、共享目的和信息类型。',
    enabled: true
  },
  {
    id: 3,
    ruleName: '用户权利描述不完整',
    ruleType: 'MISSING_CHECK',
    riskType: '用户权利说明不足',
    riskLevel: 'MEDIUM',
    pattern: '查询，更正，删除，撤回',
    lawReference: '个人信息保护法 第 44 条',
    suggestion: '建议补充说明删除、撤回同意等权利及行使方式。',
    enabled: true
  }
])

const dialogVisible = ref(false)
const dialogTitle = ref('新增规则')
const ruleForm = reactive({
  id: null,
  ruleName: '',
  ruleType: 'KEYWORD',
  riskType: '',
  riskLevel: 'MEDIUM',
  pattern: '',
  lawReference: '',
  suggestion: '',
  enabled: true
})

const getRiskType = (level) => {
  if (level === 'HIGH') return 'danger'
  if (level === 'MEDIUM') return 'warning'
  return 'success'
}

const showAddDialog = () => {
  dialogTitle.value = '新增规则'
  Object.assign(ruleForm, {
    id: null,
    ruleName: '',
    ruleType: 'KEYWORD',
    riskType: '',
    riskLevel: 'MEDIUM',
    pattern: '',
    lawReference: '',
    suggestion: '',
    enabled: true
  })
  dialogVisible.value = true
}

const editRule = (row) => {
  dialogTitle.value = '编辑规则'
  Object.assign(ruleForm, row)
  dialogVisible.value = true
}

const saveRule = () => {
  if (!ruleForm.ruleName || !ruleForm.ruleType || !ruleForm.riskLevel) {
    ElMessage.warning('请填写必要字段')
    return
  }
  if (ruleForm.id) {
    // 编辑
    const index = ruleList.value.findIndex(item => item.id === ruleForm.id)
    if (index !== -1) {
      ruleList.value[index] = { ...ruleForm }
    }
    ElMessage.success('更新成功')
  } else {
    // 新增 - 安全处理空数组情况
    const ids = ruleList.value.map(item => item.id)
    const newId = ids.length > 0 ? Math.max(...ids) + 1 : 1
    ruleList.value.unshift({ ...ruleForm, id: newId })
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
}

const deleteRule = (row) => {
  ElMessageBox.confirm('确定要删除这条规则吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ruleList.value = ruleList.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  }).catch(() => {
    // 用户取消删除操作
  })
}
</script>

<style scoped>
.rules-container {
  max-width: 1200px;
  margin: 0 auto;
}

.rules-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
