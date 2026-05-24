<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElCard, ElRow, ElCol, ElTable, ElTableColumn, ElTag } from 'element-plus'
import { dashboardAPI, applicationAPI, interviewRecordAPI } from '@/api'

const stats = ref(null)
const recentApplications = ref([])
const recentInterviews = ref([])

const statusLabels = {
  APPLIED: '已投递',
  SCREENING: '筛选中',
  INTERVIEWING: '面试中',
  OFFER: '已发offer',
  REJECTED: '已拒绝',
  WITHDRAWN: '已撤回'
}

const resultLabels = {
  PENDING: '待定',
  PASSED: '通过',
  FAILED: '未通过',
  CANCELLED: '已取消'
}

const statusColors = {
  APPLIED: '#409eff',
  SCREENING: '#67c23a',
  INTERVIEWING: '#e6a23c',
  OFFER: '#10b981',
  REJECTED: '#f56c6c',
  WITHDRAWN: '#909399'
}

const resultColors = {
  PENDING: '#909399',
  PASSED: '#67c23a',
  FAILED: '#f56c6c',
  CANCELLED: '#d9d9d9'
}

const loadData = async () => {
  try {
    const [statsRes, appRes, interviewRes] = await Promise.all([
      dashboardAPI.getStats(),
      applicationAPI.getAll(),
      interviewRecordAPI.getAll()
    ])
    // 兼容两种响应格式
    stats.value = statsRes.data || statsRes
    recentApplications.value = (appRes.data || appRes)?.slice(0, 5) || []
    recentInterviews.value = (interviewRes.data || interviewRes)?.slice(0, 5) || []
  }
  catch (error) {
    ElMessage.error('加载数据失败')
  }
}

onMounted(loadData)
</script>

<template>
  <div style="padding: 20px;">
    <div style="margin-bottom: 24px;">
      <h2 style="font-size: 24px; font-weight: 600; color: #2f3542; margin-bottom: 8px;">仪表盘</h2>
      <p style="color: #909399; font-size: 14px;">欢迎回来，查看您的求职进度概览</p>
    </div>
    
    <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 24px;">
      <ElCard style="border-radius: 12px; border: none; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);">
        <div style="display: flex; align-items: center;">
          <div style="width: 50px; height: 50px; border-radius: 12px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; margin-right: 16px;">
            <span style="color: #fff; font-size: 24px;">🏢</span>
          </div>
          <div>
            <div style="color: #909399; font-size: 13px; margin-bottom: 4px;">公司总数</div>
            <div style="font-size: 28px; font-weight: bold; color: #2f3542;">{{ stats?.totalCompanies || 0 }}</div>
          </div>
        </div>
      </ElCard>
      
      <ElCard style="border-radius: 12px; border: none; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);">
        <div style="display: flex; align-items: center;">
          <div style="width: 50px; height: 50px; border-radius: 12px; background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); display: flex; align-items: center; justify-content: center; margin-right: 16px;">
            <span style="color: #fff; font-size: 24px;">📝</span>
          </div>
          <div>
            <div style="color: #909399; font-size: 13px; margin-bottom: 4px;">投递记录</div>
            <div style="font-size: 28px; font-weight: bold; color: #2f3542;">{{ stats?.totalApplications || 0 }}</div>
          </div>
        </div>
      </ElCard>
      
      <ElCard style="border-radius: 12px; border: none; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);">
        <div style="display: flex; align-items: center;">
          <div style="width: 50px; height: 50px; border-radius: 12px; background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); display: flex; align-items: center; justify-content: center; margin-right: 16px;">
            <span style="color: #fff; font-size: 24px;">💼</span>
          </div>
          <div>
            <div style="color: #909399; font-size: 13px; margin-bottom: 4px;">面试记录</div>
            <div style="font-size: 28px; font-weight: bold; color: #2f3542;">{{ stats?.totalInterviews || 0 }}</div>
          </div>
        </div>
      </ElCard>
      
      <ElCard style="border-radius: 12px; border: none; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);">
        <div style="display: flex; align-items: center;">
          <div style="width: 50px; height: 50px; border-radius: 12px; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); display: flex; align-items: center; justify-content: center; margin-right: 16px;">
            <span style="color: #fff; font-size: 24px;">📅</span>
          </div>
          <div>
            <div style="color: #909399; font-size: 13px; margin-bottom: 4px;">待定面试</div>
            <div style="font-size: 28px; font-weight: bold; color: #2f3542;">{{ stats?.upcomingInterviews || 0 }}</div>
          </div>
        </div>
      </ElCard>
    </div>

    <ElRow :gutter="20">
      <ElCol :span="12">
        <ElCard style="border-radius: 12px; border: none; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);">
          <template #header>
            <div style="display: flex; align-items: center;">
              <span style="font-size: 16px; font-weight: 600; color: #2f3542;">最近投递记录</span>
            </div>
          </template>
          <ElTable :data="recentApplications" border="none" style="width: 100%;">
            <ElTableColumn prop="companyName" label="公司" header-align="left">
              <template #default="scope">
                <span style="color: #303133; font-weight: 500;">{{ scope.row.companyName }}</span>
              </template>
            </ElTableColumn>
            <ElTableColumn prop="position" label="职位" header-align="left">
              <template #default="scope">
                <span style="color: #606266;">{{ scope.row.position }}</span>
              </template>
            </ElTableColumn>
            <ElTableColumn prop="status" label="状态" header-align="left">
              <template #default="scope">
                <ElTag :style="{ backgroundColor: statusColors[scope.row.status] || '#909399', color: '#fff' }">
                  {{ statusLabels[scope.row.status] || scope.row.status }}
                </ElTag>
              </template>
            </ElTableColumn>
          </ElTable>
          <div v-if="recentApplications.length === 0" style="text-align: center; padding: 40px; color: #909399;">
            暂无投递记录
          </div>
        </ElCard>
      </ElCol>

      <ElCol :span="12">
        <ElCard style="border-radius: 12px; border: none; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);">
          <template #header>
            <div style="display: flex; align-items: center;">
              <span style="font-size: 16px; font-weight: 600; color: #2f3542;">最近面试记录</span>
            </div>
          </template>
          <ElTable :data="recentInterviews" border="none" style="width: 100%;">
            <ElTableColumn prop="companyName" label="公司" header-align="left">
              <template #default="scope">
                <span style="color: #303133; font-weight: 500;">{{ scope.row.companyName }}</span>
              </template>
            </ElTableColumn>
            <ElTableColumn prop="interviewType" label="类型" header-align="left">
              <template #default="scope">
                <span style="color: #606266;">{{ scope.row.interviewType }}</span>
              </template>
            </ElTableColumn>
            <ElTableColumn prop="result" label="结果" header-align="left">
              <template #default="scope">
                <ElTag :style="{ backgroundColor: resultColors[scope.row.result] || '#909399', color: '#fff' }">
                  {{ resultLabels[scope.row.result] || scope.row.result }}
                </ElTag>
              </template>
            </ElTableColumn>
          </ElTable>
          <div v-if="recentInterviews.length === 0" style="text-align: center; padding: 40px; color: #909399;">
            暂无面试记录
          </div>
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>
