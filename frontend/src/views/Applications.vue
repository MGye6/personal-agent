<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElDialog, ElButton, ElInput, ElSelect, ElOption, ElTable, ElTableColumn } from 'element-plus'
import { applicationAPI } from '@/api'

const data = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const form = ref({ companyId: '', companyName: '', position: '', applicationDate: '', status: 'APPLIED', note: '' })
const statusOptions = ['APPLIED', 'SCREENING', 'INTERVIEWING', 'OFFER', 'REJECTED', 'WITHDRAWN']
const statusLabels = {
  APPLIED: '已投递',
  SCREENING: '筛选中',
  INTERVIEWING: '面试中',
  OFFER: '已发offer',
  REJECTED: '已拒绝',
  WITHDRAWN: '已撤回'
}
const statusColors = {
  APPLIED: '#909399',
  SCREENING: '#e6a23c',
  INTERVIEWING: '#409eff',
  OFFER: '#67c23a',
  REJECTED: '#f56c6c',
  WITHDRAWN: '#909399'
}

const loadData = async () => {
  try {
    const res = await applicationAPI.getAll()
    if (res.code === 200) {
      data.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleAdd = () => {
  editingId.value = null
  form.value = { companyId: '', companyName: '', position: '', applicationDate: '', status: 'APPLIED', note: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editingId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  if (!confirm('确定要删除吗？')) return
  try {
    const res = await applicationAPI.delete(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleSubmit = async () => {
  if (!form.value.companyName || !form.value.position) {
    ElMessage.warning('请填写公司名称和职位')
    return
  }
  try {
    const res = editingId.value
      ? await applicationAPI.update(editingId.value, form.value)
      : await applicationAPI.create(form.value)
    if (res.code === 200) {
      ElMessage.success(editingId.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(loadData)
</script>

<template>
  <div style="padding: 20px;">
    <div style="display: flex; justify-content: space-between; margin-bottom: 20px;">
      <h2 style="margin: 0;">投递记录</h2>
      <ElButton type="primary" @click="handleAdd">添加投递</ElButton>
    </div>

    <ElTable :data="data" style="width: 100%;">
      <ElTableColumn prop="companyName" label="公司" />
      <ElTableColumn prop="position" label="职位" />
      <ElTableColumn prop="applicationDate" label="投递日期" />
      <ElTableColumn label="状态">
        <template #default="{ row }">
          <span :style="{ padding: '2px 8px', borderRadius: '4px', background: statusColors[row.status], color: '#fff', fontSize: '12px' }">
            {{ statusLabels[row.status] || row.status }}
          </span>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="note" label="备注" />
      <ElTableColumn label="操作">
        <template #default="{ row }">
          <ElButton type="success" size="small" @click="handleEdit(row)" style="margin-right: 8px;">编辑</ElButton>
          <ElButton type="danger" size="small" @click="handleDelete(row.id)">删除</ElButton>
        </template>
      </ElTableColumn>
    </ElTable>

    <ElDialog :model-value="dialogVisible" :title="editingId ? '编辑投递' : '添加投递'" @close="dialogVisible = false">
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">公司名称</label>
        <ElInput v-model="form.companyName" placeholder="请输入公司名称" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">职位</label>
        <ElInput v-model="form.position" placeholder="请输入职位" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">投递日期</label>
        <ElInput v-model="form.applicationDate" type="date" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">状态</label>
        <ElSelect v-model="form.status" style="width: 100%;">
          <ElOption v-for="opt in statusOptions" :key="opt" :label="statusLabels[opt]" :value="opt" />
        </ElSelect>
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">备注</label>
        <ElInput v-model="form.note" type="textarea" :rows="3" placeholder="请输入备注" />
      </div>
      <template #footer>
        <ElButton @click="dialogVisible = false" style="margin-right: 8px;">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>
