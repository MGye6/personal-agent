<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElDialog, ElButton, ElInput, ElTable, ElTableColumn, ElTextarea } from 'element-plus'
import { companyAPI } from '@/api'

const data = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const form = ref({ name: '', industry: '', scale: '', description: '' })

const loadData = async () => {
  try {
    const res = await companyAPI.getAll()
    if (res.code === 200) {
      data.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleAdd = () => {
  editingId.value = null
  form.value = { name: '', industry: '', scale: '', description: '' }
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
    const res = await companyAPI.delete(id)
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
  if (!form.value.name) {
    ElMessage.warning('请输入公司名称')
    return
  }
  try {
    const res = editingId.value 
      ? await companyAPI.update(editingId.value, form.value) 
      : await companyAPI.create(form.value)
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
      <h2 style="margin: 0;">公司管理</h2>
      <ElButton type="primary" @click="handleAdd">添加公司</ElButton>
    </div>

    <ElTable :data="data" style="width: 100%;">
      <ElTableColumn prop="name" label="公司名称" />
      <ElTableColumn prop="industry" label="行业" />
      <ElTableColumn prop="scale" label="规模" />
      <ElTableColumn prop="description" label="描述" />
      <ElTableColumn label="操作">
        <template #default="{ row }">
          <ElButton type="success" size="small" @click="handleEdit(row)" style="margin-right: 8px;">编辑</ElButton>
          <ElButton type="danger" size="small" @click="handleDelete(row.id)">删除</ElButton>
        </template>
      </ElTableColumn>
    </ElTable>

    <ElDialog :model-value="dialogVisible" :title="editingId ? '编辑公司' : '添加公司'" @close="dialogVisible = false">
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">公司名称</label>
        <ElInput v-model="form.name" placeholder="请输入公司名称" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">行业</label>
        <ElInput v-model="form.industry" placeholder="请输入行业" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">规模</label>
        <ElInput v-model="form.scale" placeholder="请输入规模" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">描述</label>
        <ElInput v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
      </div>
      <template #footer>
        <ElButton @click="dialogVisible = false" style="margin-right: 8px;">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>
