<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElDialog, ElButton, ElSelect, ElOption, ElTable, ElTableColumn, ElInput } from 'element-plus'
import { interviewScheduleAPI, applicationAPI } from '@/api'

const data = ref([])
const applications = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const form = ref({ jobApplicationId: '', companyName: '', interviewType: 'TECHNICAL', date: '', startTime: '', endTime: '', location: '', status: 'SCHEDULED', note: '' })
const typeOptions = ['PHONE', 'VIDEO', 'ONSITE', 'HR', 'TECHNICAL', 'GROUP', 'FINAL']
const typeLabels = {
  PHONE: '电话面试',
  VIDEO: '视频面试',
  ONSITE: '现场面试',
  HR: 'HR面试',
  TECHNICAL: '技术面试',
  GROUP: '群面',
  FINAL: '终面'
}
const statusOptions = ['SCHEDULED', 'COMPLETED', 'CANCELLED', 'POSTPONED']
const statusLabels = {
  SCHEDULED: '已安排',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  POSTPONED: '已延期'
}
const statusColors = {
  SCHEDULED: '#409eff',
  COMPLETED: '#67c23a',
  CANCELLED: '#f56c6c',
  POSTPONED: '#e6a23c'
}

const loadData = async () => {
  try {
    const [res, appRes] = await Promise.all([
      interviewScheduleAPI.getAll(),
      applicationAPI.getAll()
    ])
    if (res.code === 200) {
      data.value = res.data || []
    }
    if (appRes.code === 200) {
      applications.value = appRes.data || []
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleAdd = () => {
  editingId.value = null
  form.value = { jobApplicationId: '', companyName: '', interviewType: 'TECHNICAL', date: '', startTime: '', endTime: '', location: '', status: 'SCHEDULED', note: '' }
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
    const res = await interviewScheduleAPI.delete(id)
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
  if (!form.value.jobApplicationId) {
    ElMessage.warning('请选择投递记录')
    return
  }
  try {
    const res = editingId.value
      ? await interviewScheduleAPI.update(editingId.value, form.value)
      : await interviewScheduleAPI.create(form.value)
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
      <h2 style="margin: 0;">面试日程</h2>
      <ElButton type="primary" @click="handleAdd">添加日程</ElButton>
    </div>

    <ElTable :data="data" style="width: 100%;">
      <ElTableColumn prop="companyName" label="公司" />
      <ElTableColumn label="面试类型">
        <template #default="{ row }">
          {{ typeLabels[row.interviewType] || row.interviewType }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="date" label="日期" />
      <ElTableColumn label="时间">
        <template #default="{ row }">
          {{ row.startTime }} - {{ row.endTime }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="location" label="地点" />
      <ElTableColumn label="状态">
        <template #default="{ row }">
          <span :style="{ padding: '2px 8px', borderRadius: '4px', background: statusColors[row.status], color: '#fff', fontSize: '12px' }">
            {{ statusLabels[row.status] || row.status }}
          </span>
        </template>
      </ElTableColumn>
      <ElTableColumn label="操作">
        <template #default="{ row }">
          <ElButton type="success" size="small" @click="handleEdit(row)" style="margin-right: 8px;">编辑</ElButton>
          <ElButton type="danger" size="small" @click="handleDelete(row.id)">删除</ElButton>
        </template>
      </ElTableColumn>
    </ElTable>

    <ElDialog :model-value="dialogVisible" :title="editingId ? '编辑面试日程' : '添加面试日程'" @close="dialogVisible = false">
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">投递记录</label>
        <ElSelect v-model="form.jobApplicationId" style="width: 100%;" placeholder="请选择投递记录">
          <ElOption v-for="app in applications" :key="app.id" :label="`${app.companyName} - ${app.position}`" :value="app.id" />
        </ElSelect>
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">面试类型</label>
        <ElSelect v-model="form.interviewType" style="width: 100%;">
          <ElOption v-for="opt in typeOptions" :key="opt" :label="typeLabels[opt]" :value="opt" />
        </ElSelect>
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">日期</label>
        <ElInput v-model="form.date" type="date" />
      </div>
      <div style="display: flex; gap: 12px; margin-bottom: 16px;">
        <div style="flex: 1;">
          <label style="display: block; margin-bottom: 4px; font-weight: 500;">开始时间</label>
          <ElInput v-model="form.startTime" type="time" />
        </div>
        <div style="flex: 1;">
          <label style="display: block; margin-bottom: 4px; font-weight: 500;">结束时间</label>
          <ElInput v-model="form.endTime" type="time" />
        </div>
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">地点</label>
        <ElInput v-model="form.location" placeholder="请输入地点" />
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
