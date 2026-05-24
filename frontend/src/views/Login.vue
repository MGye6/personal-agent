<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElButton, ElInput, ElCard } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { authAPI } from '@/api'

const router = useRouter()
const authStore = useAuthStore()
const form = ref({ username: '', password: '' })
const loading = ref(false)

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await authAPI.login(form.value)
    const token = res.token || res.data?.token
    const userId = res.userId || res.data?.userId
    const username = res.username || res.data?.username

    if (token) {
      authStore.login(token, userId, username)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error(res.message || '登录失败：无效的响应格式')
    }
  } catch (error) {
    ElMessage.error('登录失败：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
    <div style="width: 420px;">
      <div style="text-align: center; margin-bottom: 30px;">
        <div style="font-size: 48px; margin-bottom: 12px;">🎯</div>
        <h1 style="font-size: 28px; font-weight: bold; color: #ffffff; margin-bottom: 8px;">求职助手</h1>
        <p style="color: rgba(255, 255, 255, 0.8); font-size: 14px;">智能求职管理平台</p>
      </div>
      <ElCard style="border-radius: 12px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15); border: none;">
        <div style="padding: 0 20px;">
          <h2 style="text-align: center; margin-bottom: 30px; font-size: 22px; font-weight: 600; color: #2f3542;">用户登录</h2>
          <div style="margin-bottom: 20px;">
            <label style="display: block; margin-bottom: 8px; font-weight: 500; color: #303133;">用户名</label>
            <ElInput
              v-model="form.username"
              placeholder="请输入用户名"
              style="height: 44px;"
              size="large"
            />
          </div>
          <div style="margin-bottom: 28px;">
            <label style="display: block; margin-bottom: 8px; font-weight: 500; color: #303133;">密码</label>
            <ElInput
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              @keyup.enter="handleSubmit"
              style="height: 44px;"
              size="large"
            />
          </div>
          <ElButton
            type="primary"
            @click="handleSubmit"
            :loading="loading"
            style="width: 100%; height: 44px; font-size: 16px; border-radius: 8px;"
            size="large"
          >
            {{ loading ? '登录中...' : '登录' }}
          </ElButton>
          <p style="text-align: center; margin-top: 20px; color: #606266; font-size: 14px;">
            还没有账号？<a href="/register" style="color: #409eff; text-decoration: none;">立即注册</a>
          </p>
        </div>
      </ElCard>
    </div>
  </div>
</template>
