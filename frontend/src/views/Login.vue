<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
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
    if (res.code === 200) {
      authStore.login(res.data.token, res.data.userId, res.data.username)
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error('登录失败：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="height: 100vh; display: flex; align-items: center; justify-content: center; background: #f0f2f5;">
    <div style="background: #fff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); width: 400px;">
      <h2 style="text-align: center; margin-bottom: 30px; font-size: 24px; font-weight: bold;">求职助手登录</h2>
      <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500;">用户名</label>
        <input
          v-model="form.username"
          type="text"
          placeholder="请输入用户名"
          style="width: 100%; padding: 10px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px;"
        />
      </div>
      <div style="margin-bottom: 24px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500;">密码</label>
        <input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          @keyup.enter="handleSubmit"
          style="width: 100%; padding: 10px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px;"
        />
      </div>
      <button
        @click="handleSubmit"
        :disabled="loading"
        style="width: 100%; padding: 12px; background: loading ? '#a0cfff' : '#1890ff'; color: '#fff'; border: 'none'; border-radius: '4px'; font-size: '16px'; cursor: loading ? 'not-allowed' : 'pointer';"
      >
        {{ loading ? '登录中...' : '登录' }}
      </button>
      <p style="text-align: center; margin-top: 16px;">
        还没有账号？<a href="/register" style="color: #1890ff;">立即注册</a>
      </p>
    </div>
  </div>
</template>
