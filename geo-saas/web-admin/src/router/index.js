import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layouts/AdminLayout.vue'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: Layout,
    meta: { auth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'tenants', component: () => import('../views/Tenants.vue') },
      { path: 'plans', component: () => import('../views/Plans.vue') },
      { path: 'orders', component: () => import('../views/Orders.vue') },
      { path: 'engines', component: () => import('../views/Engines.vue') },
      { path: 'jobs', component: () => import('../views/Jobs.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.auth && !localStorage.getItem('geo_admin_token')) {
    return '/login'
  }
})

export default router
