<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElButton, ElInput, ElCard, ElRow, ElCol, ElTabs, ElTabPane } from 'element-plus'
import { resumeAPI } from '@/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const resume = ref({
  title: '我的简历',
  name: '',
  phone: '',
  email: '',
  location: '',
  education: '',
  workExperience: '',
  skills: '',
  projects: '',
  awards: '',
  selfIntroduction: ''
})
const loading = ref(false)
const previewMode = ref(false)
const activeTab = ref('basic')

const loadResume = async () => {
  loading.value = true
  try {
    const res = await resumeAPI.getMyResume()
    if (res.code === 200 && res.data) {
      resume.value = { ...res.data }
    }
  } catch (error) {
    ElMessage.error('加载简历失败')
  } finally {
    loading.value = false
  }
}

const saveResume = async () => {
  loading.value = true
  try {
    const res = resume.value.id 
      ? await resumeAPI.update(resume.value.id, resume.value)
      : await resumeAPI.create(resume.value)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      if (!resume.value.id) {
        resume.value.id = res.data.id
      }
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}

const exportPdf = async () => {
  if (!resume.value.id) {
    ElMessage.warning('请先保存简历')
    return
  }
  try {
    const url = `/api/resumes/${resume.value.id}/export/pdf`
    const response = await fetch(url, {
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      }
    })
    if (!response.ok) {
      throw new Error('导出失败')
    }
    const blob = await response.blob()
    const downloadUrl = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    const filename = resume.value.name ? `${resume.value.name}_简历.pdf` : '简历.pdf'
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(downloadUrl)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('PDF导出失败:', error)
    ElMessage.error('导出失败')
  }
}

onMounted(loadResume)
</script>

<template>
  <div style="padding: 20px; max-width: 1200px; margin: 0 auto;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2 style="margin: 0;">简历管理</h2>
      <div>
        <ElButton type="info" @click="previewMode = !previewMode" style="margin-right: 8px;">
          {{ previewMode ? '返回编辑' : '预览' }}
        </ElButton>
        <ElButton type="primary" @click="saveResume" :loading="loading" style="margin-right: 8px;">
          保存简历
        </ElButton>
        <ElButton type="success" @click="exportPdf">
          导出 PDF
        </ElButton>
      </div>
    </div>

    <ElCard v-if="!previewMode" style="border-radius: 12px;">
      <template #header>
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <span style="font-size: 16px; font-weight: 600;">编辑简历</span>
        </div>
      </template>

      <ElTabs v-model="activeTab">
        <ElTabPane label="基本信息" name="basic">
          <ElRow :gutter="20">
            <ElCol :span="12">
              <div style="margin-bottom: 16px;">
                <label style="display: block; margin-bottom: 4px; font-weight: 500;">简历标题</label>
                <ElInput v-model="resume.title" placeholder="请输入简历标题" />
              </div>
            </ElCol>
            <ElCol :span="12">
              <div style="margin-bottom: 16px;">
                <label style="display: block; margin-bottom: 4px; font-weight: 500;">姓名</label>
                <ElInput v-model="resume.name" placeholder="请输入姓名" />
              </div>
            </ElCol>
          </ElRow>

          <ElRow :gutter="20">
            <ElCol :span="12">
              <div style="margin-bottom: 16px;">
                <label style="display: block; margin-bottom: 4px; font-weight: 500;">电话</label>
                <ElInput v-model="resume.phone" placeholder="请输入电话" />
              </div>
            </ElCol>
            <ElCol :span="12">
              <div style="margin-bottom: 16px;">
                <label style="display: block; margin-bottom: 4px; font-weight: 500;">邮箱</label>
                <ElInput v-model="resume.email" placeholder="请输入邮箱" />
              </div>
            </ElCol>
          </ElRow>

          <div style="margin-bottom: 16px;">
            <label style="display: block; margin-bottom: 4px; font-weight: 500;">所在城市</label>
            <ElInput v-model="resume.location" placeholder="请输入所在城市" />
          </div>
        </ElTabPane>

        <ElTabPane label="自我介绍" name="intro">
          <div style="margin-bottom: 16px;">
            <ElInput
              v-model="resume.selfIntroduction"
              type="textarea"
              :rows="10"
              placeholder="请输入自我介绍，例如：
- 个人概况
- 求职意向
- 优势特长"
            />
          </div>
        </ElTabPane>

        <ElTabPane label="教育经历" name="education">
          <div style="margin-bottom: 16px;">
            <ElInput
              v-model="resume.education"
              type="textarea"
              :rows="10"
              placeholder="请输入教育经历，例如：
- 学校名称 | 专业 | 学历 | 时间段
- 主修课程
- GPA/排名"
            />
          </div>
        </ElTabPane>

        <ElTabPane label="工作经历" name="work">
          <div style="margin-bottom: 16px;">
            <ElInput
              v-model="resume.workExperience"
              type="textarea"
              :rows="10"
              placeholder="请输入工作经历，例如：
- 公司名称 | 职位 | 时间段
- 工作内容描述
- 取得成果"
            />
          </div>
        </ElTabPane>

        <ElTabPane label="项目经历" name="projects">
          <div style="margin-bottom: 16px;">
            <ElInput
              v-model="resume.projects"
              type="textarea"
              :rows="10"
              placeholder="请输入项目经历，例如：
- 项目名称 | 角色 | 时间段
- 项目描述
- 技术栈
- 个人职责与成果"
            />
          </div>
        </ElTabPane>

        <ElTabPane label="技能" name="skills">
          <div style="margin-bottom: 16px;">
            <ElInput
              v-model="resume.skills"
              type="textarea"
              :rows="10"
              placeholder="请输入技能，例如：
- 编程语言：Java, Python, JavaScript
- 框架：Spring Boot, Vue.js
- 数据库：MySQL, Redis
- 工具：Git, Docker"
            />
          </div>
        </ElTabPane>

        <ElTabPane label="获奖情况" name="awards">
          <div style="margin-bottom: 16px;">
            <ElInput
              v-model="resume.awards"
              type="textarea"
              :rows="10"
              placeholder="请输入获奖情况，例如：
- 奖项名称 | 颁发机构 | 时间
- 荣誉称号"
            />
          </div>
        </ElTabPane>
      </ElTabs>
    </ElCard>

    <ElCard v-else style="border-radius: 12px;">
      <template #header>
        <div style="display: flex; align-items: center;">
          <span style="font-size: 16px; font-weight: 600;">简历预览</span>
        </div>
      </template>

      <div style="padding: 20px;">
        <div style="text-align: center; margin-bottom: 30px;">
          <h1 style="font-size: 28px; margin: 0 0 10px 0;">{{ resume.name || '个人简历' }}</h1>
          <p style="color: #606266; margin: 0;">{{ resume.title }}</p>
        </div>

        <div v-if="resume.name || resume.phone || resume.email || resume.location" style="background: #f5f7fa; padding: 15px; border-radius: 8px; margin-bottom: 20px;">
          <div style="display: flex; flex-wrap: wrap; gap: 20px;">
            <span v-if="resume.name">👤 {{ resume.name }}</span>
            <span v-if="resume.phone">📞 {{ resume.phone }}</span>
            <span v-if="resume.email">📧 {{ resume.email }}</span>
            <span v-if="resume.location">📍 {{ resume.location }}</span>
          </div>
        </div>

        <div v-if="resume.selfIntroduction" style="margin-bottom: 20px;">
          <h3 style="font-size: 16px; color: #303133; margin: 0 0 10px 0; padding-bottom: 5px; border-bottom: 2px solid #409eff;">自我介绍</h3>
          <div style="white-space: pre-wrap; line-height: 1.8;">{{ resume.selfIntroduction }}</div>
        </div>

        <div v-if="resume.education" style="margin-bottom: 20px;">
          <h3 style="font-size: 16px; color: #303133; margin: 0 0 10px 0; padding-bottom: 5px; border-bottom: 2px solid #409eff;">教育经历</h3>
          <div style="white-space: pre-wrap; line-height: 1.8;">{{ resume.education }}</div>
        </div>

        <div v-if="resume.workExperience" style="margin-bottom: 20px;">
          <h3 style="font-size: 16px; color: #303133; margin: 0 0 10px 0; padding-bottom: 5px; border-bottom: 2px solid #409eff;">工作经历</h3>
          <div style="white-space: pre-wrap; line-height: 1.8;">{{ resume.workExperience }}</div>
        </div>

        <div v-if="resume.projects" style="margin-bottom: 20px;">
          <h3 style="font-size: 16px; color: #303133; margin: 0 0 10px 0; padding-bottom: 5px; border-bottom: 2px solid #409eff;">项目经历</h3>
          <div style="white-space: pre-wrap; line-height: 1.8;">{{ resume.projects }}</div>
        </div>

        <div v-if="resume.skills" style="margin-bottom: 20px;">
          <h3 style="font-size: 16px; color: #303133; margin: 0 0 10px 0; padding-bottom: 5px; border-bottom: 2px solid #409eff;">技能</h3>
          <div style="white-space: pre-wrap; line-height: 1.8;">{{ resume.skills }}</div>
        </div>

        <div v-if="resume.awards">
          <h3 style="font-size: 16px; color: #303133; margin: 0 0 10px 0; padding-bottom: 5px; border-bottom: 2px solid #409eff;">获奖情况</h3>
          <div style="white-space: pre-wrap; line-height: 1.8;">{{ resume.awards }}</div>
        </div>
      </div>
    </ElCard>
  </div>
</template>
