<template>
  <div class="auth">
    <div class="box page-card">
      <h2>GEO 管理后台登录</h2>
      <p class="muted">管理员 admin / admin123</p>
      <el-form :model="form">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-button type="primary" style="width:100%" :loading="loading" @click="submit">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123' })

async function submit() {
  loading.value = true
  try {
    const res = await http.post('/auth/login', form)
    if (res.data.role !== 'PLATFORM_ADMIN') {
      ElMessage.error('非平台管理员账号')
      return
    }
    localStorage.setItem('geo_admin_token', res.data.token)
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth { min-height:100vh; display:grid; place-items:center; background:#eef2ff; }
.box { width:420px; }
</style>
