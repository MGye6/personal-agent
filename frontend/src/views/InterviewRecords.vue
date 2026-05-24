<script setup>import { ref, onMounted } from 'vue';
import { ElMessage, ElDialog } from 'element-plus';
import { interviewRecordAPI, applicationAPI } from '@/api';
const data = ref([]);
const applications = ref([]);
const dialogVisible = ref(false);
const editingId = ref(null);
const form = ref({ jobApplicationId: '', companyName: '', interviewType: 'TECHNICAL', date: '', result: 'PENDING', feedback: '' });
const typeOptions = ['PHONE', 'VIDEO', 'ONSITE', 'HR', 'TECHNICAL', 'GROUP', 'FINAL'];
const typeLabels = {
 PHONE: '电话面试',
 VIDEO: '视频面试',
 ONSITE: '现场面试',
 HR: 'HR面试',
 TECHNICAL: '技术面试',
 GROUP: '群面',
 FINAL: '终面'
};
const resultOptions = ['PENDING', 'PASSED', 'FAILED', 'CANCELLED'];
const resultLabels = {
 PENDING: '待定',
 PASSED: '通过',
 FAILED: '未通过',
 CANCELLED: '已取消'
};
const resultColors = {
 PENDING: '#909399',
 PASSED: '#67c23a',
 FAILED: '#f56c6c',
 CANCELLED: '#909399'
};
const loadData = async () => {
 try {
 const [res, appRes] = await Promise.all([
 interviewRecordAPI.getAll(),
 applicationAPI.getAll()
 ]);
 if (res.code === 200) {
 data.value = res.data || [];
 }
 if (appRes.code === 200) {
 applications.value = appRes.data || [];
 }
 }
 catch (error) {
 ElMessage.error('加载数据失败');
 }
};
const handleAdd = () => {
 editingId.value = null;
 form.value = { jobApplicationId: '', companyName: '', interviewType: 'TECHNICAL', date: '', result: 'PENDING', feedback: '' };
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
 const res = await interviewRecordAPI.delete(id);
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
 if (!form.value.jobApplicationId) {
 ElMessage.warning('请选择投递记录');
 return;
 }
 try {
 const url = editingId.value ? `/api/interview-records/${editingId.value}` : '/api/interview-records';
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
const getCompanyName = (appId) => {
 const app = applications.value.find(a => a.id === appId);
 return app ? app.companyName : '';
};
onMounted(loadData);
</script>

<template>
  <div style="padding: 20px;">
    <div style="display: flex; justify-content: space-between; margin-bottom: 20px;">
      <h2>面试记录</h2>
      <button @click="handleAdd" style="padding: 10px 20px; background: #1890ff; color: #fff; border: none; border-radius: 4px; cursor: pointer;">
        添加记录
      </button>
    </div>

    <div style="background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); overflow: auto;">
      <table style="width: 100%; border-collapse: collapse;">
        <thead>
          <tr style="background: #f5f7fa;">
            <th style="padding: 12px; text-align: left; font-weight: normal;">公司</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">面试类型</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">日期</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">结果</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">反馈</th>
            <th style="padding: 12px; text-align: left; font-weight: normal;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in data" :key="row.id" style="border-bottom: 1px solid #ebeef5;">
            <td style="padding: 12px;">{{ row.companyName }}</td>
            <td style="padding: 12px;">{{ typeLabels[row.interviewType] || row.interviewType }}</td>
            <td style="padding: 12px;">{{ row.date }}</td>
            <td style="padding: 12px;">
              <span :style="{ padding: '2px 8px', borderRadius: '4px', background: resultColors[row.result], color: '#fff', fontSize: '12px' }">
                {{ resultLabels[row.result] || row.result }}
              </span>
            </td>
            <td style="padding: 12px;">{{ row.feedback }}</td>
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

    <ElDialog :visible="dialogVisible" :title="editingId ? '编辑面试记录' : '添加面试记录'" @close="dialogVisible = false">
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">投递记录</label>
        <select v-model="form.jobApplicationId" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;">
          <option value="">请选择投递记录</option>
          <option v-for="app in applications" :key="app.id" :value="app.id">{{ app.companyName }} - {{ app.position }}</option>
        </select>
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">面试类型</label>
        <select v-model="form.interviewType" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;">
          <option v-for="opt in typeOptions" :key="opt" :value="opt">{{ typeLabels[opt] }}</option>
        </select>
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">日期</label>
        <input v-model="form.date" type="date" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">结果</label>
        <select v-model="form.result" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;">
          <option v-for="opt in resultOptions" :key="opt" :value="opt">{{ resultLabels[opt] }}</option>
        </select>
      </div>
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 4px; font-weight: 500;">反馈</label>
        <textarea v-model="form.feedback" rows="3" style="width: 100%; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
      </div>
      <template #footer>
        <button @click="dialogVisible = false" style="margin-right: 8px; padding: 8px 16px; border: 1px solid #dcdfe6; border-radius: 4px; cursor: pointer;">取消</button>
        <button @click="handleSubmit" style="padding: 8px 16px; background: #1890ff; color: #fff; border: none; border-radius: 4px; cursor: pointer;">确定</button>
      </template>
    </ElDialog>
  </div>
</template>
