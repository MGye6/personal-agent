<script setup>import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Layout, Menu, Avatar, Dropdown } from 'element-plus';
import { useAuthStore } from '@/stores/auth';
const { Header, Sider, Content } = Layout;
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const collapsed = ref(false);
const menuItems = [
 { key: '/dashboard', title: '仪表盘' },
 { key: '/companies', title: '公司管理' },
 { key: '/applications', title: '投递记录' },
 { key: '/interview-records', title: '面试记录' },
 { key: '/interview-schedules', title: '面试日程' },
 { key: '/ai-chat', title: 'AI助手' }
];
const userMenu = [
 { command: 'logout', label: '退出登录' }
];
const handleMenuSelect = (key) => {
 router.push(key);
};
const handleUserMenu = (command) => {
 if (command === 'logout') {
 authStore.logout();
 router.push('/login');
 }
};
</script>

<template>
  <Layout style="height: 100vh;">
    <Sider collapsible v-model:collapsed="collapsed">
      <div style="height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 16px; font-weight: bold;">
        {{ collapsed ? '求职' : '求职助手' }}
      </div>
      <Menu
        :default-active="route.path"
        mode="inline"
        @select="handleMenuSelect"
        style="border-right: 0;"
      >
        <Menu.Item v-for="item in menuItems" :key="item.key">
          <span>{{ item.title }}</span>
        </Menu.Item>
      </Menu>
    </Sider>
    <Layout>
      <Header style="background: #fff; padding: 0 20px; display: flex; align-items: center; justify-content: flex-end;">
        <Dropdown :menu-items="userMenu" @command="handleUserMenu">
          <div style="display: flex; align-items: center; cursor: pointer;">
            <Avatar size="small" style="margin-right: 8px;">{{ authStore.username?.[0]?.toUpperCase() || 'U' }}</Avatar>
            <span>{{ authStore.username }}</span>
          </div>
        </Dropdown>
      </Header>
      <Content style="margin: 16px; overflow: auto;">
        <router-view />
      </Content>
    </Layout>
  </Layout>
</template>
