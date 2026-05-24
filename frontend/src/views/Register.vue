<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElButton, ElInput, ElCard } from 'element-plus'
import { authAPI } from '@/api'

const router = useRouter()
const form = ref({ username: '', password: '', email: '' })
const loading = ref(false)

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password || !form.value.email) {
    ElMessage.warning('请填写完整信息')
    return
  }
  loading.value = true
  try {
    const res = await authAPI.register(form.value)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (error) {
    ElMessage.error('注册失败：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="height: 100vh; display: flex; align-items: center; justify-content: center; background: #f0f2f5;">
    <ElCard style="width: 400px;">
      <h2 style="text-align: center; margin-bottom: 30px; font-size: 24px; font-weight: bold;">求职助手注册</h2>
      <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500;">用户名</label>
        <ElInput
          v-model="form.username"
          placeholder="请输入用户名"
        />
      </div>
      <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500;">邮箱</label>
        <ElInput
          v-model="form.email"
          type="email"
          placeholder="请输入邮箱"
        />
      </div>
      <div style="margin-bottom: 24px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500;">密码</label>
        <ElInput
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          @keyup.enter="handleSubmit"
        />
      </div>
      <ElButton
        type="primary"
        @click="handleSubmit"
        :loading="loading"
        style="width: 100%;"
      >
        {{ loading ? '注册中...' : '注册' }}
      </ElButton>
      <p style="text-align: center; margin-top: 16px;">
        已有账号？<a href="/login" style="color: #1890ff;">立即登录</a>
      </p>
    </ElCard>
  </div>
</template>
