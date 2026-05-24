import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue')
    },
    {
      path: '/',
      name: 'Layout',
      component: () => import('@/components/Layout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/dashboard' },
        { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
        { path: 'companies', name: 'Companies', component: () => import('@/views/Companies.vue') },
        { path: 'applications', name: 'Applications', component: () => import('@/views/Applications.vue') },
        { path: 'interview-records', name: 'InterviewRecords', component: () => import('@/views/InterviewRecords.vue') },
        { path: 'interview-schedules', name: 'InterviewSchedules', component: () => import('@/views/InterviewSchedules.vue') },
        { path: 'ai-chat', name: 'AIChat', component: () => import('@/views/AIChat.vue') }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
