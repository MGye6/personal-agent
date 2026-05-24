<script setup>import { ref, onMounted } from 'vue';
import { ElMessage, ElDialog } from 'element-plus';
import { applicationAPI } from '@/api';
const data = ref([]);
const dialogVisible = ref(false);
const editingId = ref(null);
const form = ref({ companyId: '', companyName: '', position: '', applicationDate: '', status: 'APPLIED', note: '' });
const statusOptions = ['APPLIED', 'SCREENING', 'INTERVIEWING', 'OFFER', 'REJECTED', 'WITHDRAWN'];
const statusLabels = {
 APPLIED: '已投递',
 SCREENING: '筛选中',
 INTERVIEWING: '面试中',
 OFFER: '已发offer',
 REJECTED: '已拒绝',
 WITHDRAWN: '已撤回'
};
const statusColors = {
 APPLIED: '#909399',
 SCREENING: '#e6a23c',
 INTERVIEWING: '#409eff',
 OFFER: '#67c23a',
 REJECTED: '#f56c6c',
 WITHDRAWN: '#909399'
};
const loadData = async () => {
 try {
 const res = await applicationAPI.getAll();
 if (res.code === 200) {
 data.value = res.data || [];
 }
 }
 catch (error) {
 ElMessage.error('加载数据失败');
 }
};
const handleAdd = () => {
 editingId.value = null;
 form.value = { companyId: '', companyName: '', position: '', applicationDate: '', status: 'APPLIED', note: '' };
 dialogVisible.value = true;
};
const handleEdit = (row) => {
 editingId.value = row.id;
 form.value = { ...row };
 dialogVisible.value = true;
};
const handleDelete = async (id) => {
 if (!confirm('确定要删除吗？'))
 return;
 try {
 const res = await applicationAPI.delete(id);
 if (res.code === 200) {
 ElMessage.success('删除成功');
 loadData();
 }
 else {
 ElMessage.error(res.message || '删除失败');
 }
 }
 catch (error) {
 ElMessage.error('删除失败');
 }
};
const handleSubmit = async () => {
 if (!form.value.companyName || !form.value.position) {
 ElMessage.warning('请填写公司名称和职位');
 return;
 }
 try {
 const url = editingId.value ? `/api/applications/${editingId.value}` : '/api/applications';
 const method = editingId.value ? 'PUT' : 'POST';
 const res = await fetch(url, {
 method,
 headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${localStorage.getItem('token')}` },
 body: JSON.stringify(form.value)
 }).then(r => r.json());
 if (res.code === 200) {
 ElMessage.success(editingId.value ? '更新成功' : '创建成功');
 dialogVisible.value = false;
 loadData();
 }
 else {
 ElMessage.error(res.message || '操作失败');
 }
 }
 catch (error) {
 ElMessage.error('操作失败');
 }
};
onMounted(loadData);
</script>

<template>
  <div style="padding: 20px;">
    <div style="display: flex; justify-content: space-between; margin-bottom: 20px;">
      <h2>投递记录</h2>
      <button @click="handleAdd" style="padding: 10px 20px; background: #1890ff; color: #fff; border: none; border-radius: 4px; cursor: pointer;">
        添加投递
      </button>
    </div>

    <div style="background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); overflow: auto;">
      <table style="width: 100%; border-collapse: collapse;">
        <thead>
          <tr style="background: #f5f7fa;">
            <th style="padding: 12px; text-align: left; font-weight: normal;">公司</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">职位</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">投递日期</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">状态</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">备注</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in data" :key="row.id" style="border-bottom: 1px solid #ebeef5;">
            <td style="padding: 12px;">{{ row.companyName }}</td>
            <td style="padding: 12px;">{{ row.position }}</td>
            <td style="padding: 12px;">{{ row.applicationDate }}</td>
            <td style="padding: 12px;">
              <span :style="{ padding: '2px 8px', borderRadius: '4px', background: statusColors[row.status], color: '#fff', fontSize: '12px' }">
                {{ statusLabels[row.status] || row.status }}
              </span>
            </td>
            <td style="padding: 12px;">{{ row.note }}</td>
            <td style="padding: 12px;">
              <button @click="handleEdit(row)" style="margin-right: 8px; padding: 4px 12px; background: #67c23a; color: #fff; border: none; border-radius: 4px; cursor: pointer;">编辑</button>
              <button @click="handleDelete(row.id)" style="padding: 4px 12px; background: #f56c6c; color: #fff; border: none; border-radius: 4px; cursor: pointer;">删除</button>
            </td>
          </tr>
          <tr v-if="data.length === 0">
            <td colspan="6" style="padding: 40px; text-align: center; color: #909399;">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <ElDialog :visible="dialogVisible" :title="editingId ? '编辑投递' : '添加投递'" @close="dialogVisible = false">
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">公司名称</label>
        <input v-model="form.companyName" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">职位</label>
        <input v-model="form.position" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">投递日期</label>
        <input v-model="form.applicationDate" type="date" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">状态</label>
        <select v-model="form.status" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;">
          <option v-for="opt in statusOptions" :key="opt" :value="opt">{{ statusLabels[opt] }}</option>
        </select>
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">备注</label>
        <textarea v-model="form.note" rows="3" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
      </div>
      <template #footer>
        <button @click="dialogVisible = false" style="margin-right: 8px; padding: 8px 16px; border: 1px solid #dcdfe6; border-radius: 4px; cursor: pointer;">取消</button>
        <button @click="handleSubmit" style="padding: 8px 16px; background: #1890ff; color: #fff; border: none; border-radius: 4px; cursor: pointer;">确定</button>
      </template>
    </ElDialog>
  </div>
</template>
