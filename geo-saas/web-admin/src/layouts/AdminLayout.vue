<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="brand">GEO Admin</div>
      <el-menu :default-active="$route.path" router background-color="#111827" text-color="#d1d5db" active-text-color="#93c5fd">
        <el-menu-item index="/dashboard">运营总览</el-menu-item>
        <el-menu-item index="/tenants">租户管理</el-menu-item>
        <el-menu-item index="/plans">套餐管理</el-menu-item>
        <el-menu-item index="/orders">订单流水</el-menu-item>
        <el-menu-item index="/engines">AI 引擎</el-menu-item>
        <el-menu-item index="/jobs">任务监控</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div>平台管理后台</div>
        <el-button text type="danger" @click="logout">退出</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function logout() {
  localStorage.removeItem('geo_admin_token')
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.aside { background: #111827; }
.brand { padding: 20px 16px; font-weight: 700; color: #93c5fd; }
.header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #e5e7eb; }
</style>
