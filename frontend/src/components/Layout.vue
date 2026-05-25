<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElContainer, ElAside, ElHeader, ElMain, ElMenu, ElMenuItem, ElAvatar, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const isCollapsed = ref(false)

const menuItems = [
  { key: '/dashboard', title: '仪表盘' },
  { key: '/companies', title: '公司管理' },
  { key: '/applications', title: '投递记录' },
  { key: '/interview-records', title: '面试记录' },
  { key: '/interview-schedules', title: '面试日程' },
  { key: '/resume', title: '简历管理' },
  { key: '/ai-chat', title: 'AI助手' }
]

const handleMenuSelect = (key) => {
  router.push(key)
}

const handleUserMenu = (command) => {
  if (command === 'logout') {
    authStore.logout()
    router.push('/login')
  }
}
</script>

<template>
  <ElContainer style="height: 100vh;">
    <ElAside :width="isCollapsed ? '64px' : '220px'" style="background-color: #2f3542;">
      <div style="height: 60px; display: flex; align-items: center; justify-content: center; color: #ffffff; font-size: 18px; font-weight: bold; border-bottom: 1px solid #3d4556;">
        {{ isCollapsed ? '助' : '求职助手' }}
      </div>
      <ElMenu
        :default-active="route.path"
        mode="vertical"
        @select="handleMenuSelect"
        :background-color="'#2f3542'"
        :text-color="'#b8c4d4'"
        :active-text-color="'#ffffff'"
        :unique-opened="true"
        style="border-right: none; height: calc(100% - 60px);"
      >
        <ElMenuItem v-for="item in menuItems" :key="item.key" :index="item.key">
          <span style="font-size: 14px;">{{ item.title }}</span>
        </ElMenuItem>
      </ElMenu>
    </ElAside>
    <ElContainer>
      <ElHeader style="background: #ffffff; padding: 0 24px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);">
        <div style="font-size: 18px; font-weight: 600; color: #2f3542;">
          {{ menuItems.find(m => m.key === route.path)?.title || '求职助手' }}
        </div>
        <ElDropdown @command="handleUserMenu">
          <div style="display: flex; align-items: center; cursor: pointer; padding: 8px 12px; border-radius: 20px; hover:background-color: #f5f7fa;">
            <ElAvatar size="small" style="margin-right: 10px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              {{ authStore.username?.[0]?.toUpperCase() || 'U' }}
            </ElAvatar>
            <span style="color: #303133; font-size: 14px;">{{ authStore.username }}</span>
          </div>
          <template #dropdown>
            <ElDropdownMenu>
              <ElDropdownItem command="logout">退出登录</ElDropdownItem>
            </ElDropdownMenu>
          </template>
        </ElDropdown>
      </ElHeader>
      <ElMain style="margin: 20px; overflow: auto; background-color: #f5f7fa; padding: 20px;">
        <router-view />
      </ElMain>
    </ElContainer>
  </ElContainer>
</template>
