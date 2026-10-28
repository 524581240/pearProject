<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="brand">GEO Monitor</div>
      <el-menu :default-active="$route.path" router background-color="#0f172a" text-color="#cbd5e1" active-text-color="#5eead4">
        <el-menu-item index="/dashboard">工作台</el-menu-item>
        <el-menu-item index="/brands">品牌管理</el-menu-item>
        <el-menu-item index="/prompts">Prompt 库</el-menu-item>
        <el-menu-item index="/verify">即时验证</el-menu-item>
        <el-menu-item index="/jobs">监测任务</el-menu-item>
        <el-menu-item index="/reports">可见度报表</el-menu-item>
        <el-menu-item index="/plans">套餐购买</el-menu-item>
        <el-menu-item index="/orders">我的订单</el-menu-item>
        <el-menu-item index="/subscription">订阅与用量</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div>{{ me?.tenantName || '租户' }} · {{ me?.displayName || me?.username }}</div>
        <el-button text type="danger" @click="logout">退出</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api/http'

const router = useRouter()
const me = ref(null)

onMounted(async () => {
  const res = await http.get('/auth/me')
  me.value = res.data
})

function logout() {
  localStorage.removeItem('geo_token')
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.aside { background: #0f172a; color: #fff; }
.brand { padding: 20px 16px; font-weight: 700; letter-spacing: 0.5px; color: #5eead4; }
.header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #e5e7eb; }
</style>
