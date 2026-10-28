import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layouts/MainLayout.vue'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/register', component: () => import('../views/Register.vue') },
  {
    path: '/',
    component: Layout,
    meta: { auth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'brands', component: () => import('../views/Brands.vue') },
      { path: 'prompts', component: () => import('../views/Prompts.vue') },
      { path: 'verify', component: () => import('../views/Verify.vue') },
      { path: 'jobs', component: () => import('../views/Jobs.vue') },
      { path: 'batches/:id', component: () => import('../views/BatchDetail.vue') },
      { path: 'reports', component: () => import('../views/Reports.vue') },
      { path: 'plans', component: () => import('../views/Plans.vue') },
      { path: 'orders', component: () => import('../views/Orders.vue') },
      { path: 'subscription', component: () => import('../views/Subscription.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.auth && !localStorage.getItem('geo_token')) {
    return '/login'
  }
})

export default router
