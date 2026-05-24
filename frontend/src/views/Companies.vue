<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElDialog } from 'element-plus'
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
    const url = editingId.value ? `/api/companies/${editingId.value}` : '/api/companies'
    const method = editingId.value ? 'PUT' : 'POST'
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${localStorage.getItem('token')}` },
      body: JSON.stringify(form.value)
    }).then(r => r.json())
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
      <h2>公司管理</h2>
      <button @click="handleAdd" style="padding: 10px 20px; background: #1890ff; color: #fff; border: none; border-radius: 4px; cursor: pointer;">
        添加公司
      </button>
    </div>

    <div style="background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); overflow: auto;">
      <table style="width: 100%; border-collapse: collapse;">
        <thead>
          <tr style="background: #f5f7fa;">
            <th style="padding: 12px; text-align: left; font-weight: normal;">公司名称</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">行业</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">规模</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">描述</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in data" :key="row.id" style="border-bottom: 1px solid #ebeef5;">
            <td style="padding: 12px;">{{ row.name }}</td>
            <td style="padding: 12px;">{{ row.industry }}</td>
            <td style="padding: 12px;">{{ row.scale }}</td>
            <td style="padding: 12px;">{{ row.description }}</td>
            <td style="padding: 12px;">
              <button @click="handleEdit(row)" style="margin-right: 8px; padding: 4px 12px; background: #67c23a; color: #fff; border: none; border-radius: 4px; cursor: pointer;">编辑</button>
              <button @click="handleDelete(row.id)" style="padding: 4px 12px; background: #f56c6c; color: #fff; border: none; border-radius: 4px; cursor: pointer;">删除</button>
            </td>
          </tr>
          <tr v-if="data.length === 0">
            <td colspan="5" style="padding: 40px; text-align: center; color: #909399;">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <ElDialog :visible="dialogVisible" :title="editingId ? '编辑公司' : '添加公司'" @close="dialogVisible = false">
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">公司名称</label>
        <input v-model="form.name" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">行业</label>
        <input v-model="form.industry" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">规模</label>
        <input v-model="form.scale" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">描述</label>
        <textarea v-model="form.description" rows="3" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
      </div>
      <template #footer>
        <button @click="dialogVisible = false" style="margin-right: 8px; padding: 8px 16px; border: 1px solid #dcdfe6; border-radius: 4px; cursor: pointer;">取消</button>
        <button @click="handleSubmit" style="padding: 8px 16px; background: #1890ff; color: #fff; border: none; border-radius: 4px; cursor: pointer;">确定</button>
      </template>
    </ElDialog>
  </div>
</template>
