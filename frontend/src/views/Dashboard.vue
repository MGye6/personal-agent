<script setup>import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { dashboardAPI, applicationAPI, interviewRecordAPI } from '@/api';
const stats = ref(null);
const recentApplications = ref([]);
const recentInterviews = ref([]);
const statusLabels = {
 APPLIED: '已投递',
 SCREENING: '筛选中',
 INTERVIEWING: '面试中',
 OFFER: '已发offer',
 REJECTED: '已拒绝',
 WITHDRAWN: '已撤回'
};
const resultLabels = {
 PENDING: '待定',
 PASSED: '通过',
 FAILED: '未通过',
 CANCELLED: '已取消'
};
const loadData = async () => {
 try {
 const [statsRes, appRes, interviewRes] = await Promise.all([
 dashboardAPI.getStats(),
 applicationAPI.getAll(),
 interviewRecordAPI.getAll()
 ]);
 if (statsRes.code === 200)
 stats.value = statsRes.data;
 if (appRes.code === 200)
 recentApplications.value = appRes.data?.slice(0, 5) || [];
 if (interviewRes.code === 200)
 recentInterviews.value = interviewRes.data?.slice(0, 5) || [];
 }
 catch (error) {
 ElMessage.error('加载数据失败');
 }
};
onMounted(loadData);
</script>

<template>
  <div style="padding: 20px;">
    <h2>仪表盘</h2>
    
    <div v-if="stats" style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin: 20px 0;">
      <div style="background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);">
        <div style="color: #909399; font-size: 14px; margin-bottom: 8px;">公司总数</div>
        <div style="font-size: 32px; font-weight: bold; color: #303133;">{{ stats.totalCompanies }}</div>
      </div>
      <div style="background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);">
        <div style="color: #909399; font-size: 14px; margin-bottom: 8px;">投递记录</div>
        <div style="font-size: 32px; font-weight: bold; color: #303133;">{{ stats.totalApplications }}</div>
      </div>
      <div style="background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);">
        <div style="color: #909399; font-size: 14px; margin-bottom: 8px;">面试记录</div>
        <div style="font-size: 32px; font-weight: bold; color: #303133;">{{ stats.totalInterviews }}</div>
      </div>
      <div style="background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);">
        <div style="color: #909399; font-size: 14px; margin-bottom: 8px;">待定面试</div>
        <div style="font-size: 32px; font-weight: bold; color: #303133;">{{ stats.upcomingInterviews }}</div>
      </div>
    </div>

    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
      <div style="background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);">
        <h3 style="padding: 16px; border-bottom: 1px solid #ebeef5; margin: 0;">最近投递记录</h3>
        <table style="width: 100%; border-collapse: collapse;">
          <thead>
            <tr style="background: #f5f7fa;">
              <th style="padding: 12px; text-align: left; font-weight: normal;">公司</th>
              <th style="padding: 12px; text-align: left; font-weight: normal;">职位</th>
              <th style="padding: 12px; text-align: left; font-weight: normal;">状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="app in recentApplications" :key="app.id" style="border-bottom: 1px solid #ebeef5;">
              <td style="padding: 12px;">{{ app.companyName }}</td>
              <td style="padding: 12px;">{{ app.position }}</td>
              <td style="padding: 12px;">
                <span style="padding: 2px 8px; border-radius: 4px; background: #409eff; color: #fff; font-size: 12px;">
                  {{ statusLabels[app.status] || app.status }}
                </span>
              </td>
            </tr>
            <tr v-if="recentApplications.length === 0">
              <td colspan="3" style="padding: 40px; text-align: center; color: #909399;">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div style="background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);">
        <h3 style="padding: 16px; border-bottom: 1px solid #ebeef5; margin: 0;">最近面试记录</h3>
        <table style="width: 100%; border-collapse: collapse;">
          <thead>
            <tr style="background: #f5f7fa;">
              <th style="padding: 12px; text-align: left; font-weight: normal;">公司</th>
              <th style="padding: 12px; text-align: left; font-weight: normal;">类型</th>
              <th style="padding: 12px; text-align: left; font-weight: normal;">结果</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="interview in recentInterviews" :key="interview.id" style="border-bottom: 1px solid #ebeef5;">
              <td style="padding: 12px;">{{ interview.companyName }}</td>
              <td style="padding: 12px;">{{ interview.interviewType }}</td>
              <td style="padding: 12px;">
                <span :style="{ padding: '2px 8px', borderRadius: '4px', background: interview.result === 'PASSED' ? '#67c23a' : interview.result === 'FAILED' ? '#f56c6c' : '#909399', color: '#fff', fontSize: '12px' }">
                  {{ resultLabels[interview.result] || interview.result }}
                </span>
              </td>
            </tr>
            <tr v-if="recentInterviews.length === 0">
              <td colspan="3" style="padding: 40px; text-align: center; color: #909399;">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
