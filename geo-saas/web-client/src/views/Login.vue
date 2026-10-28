<template>
  <div class="auth">
    <div class="box page-card">
      <h2>GEO 监测客户端登录</h2>
      <p class="muted">演示账号 demo / demo123</p>
      <el-form :model="form" @submit.prevent>
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-button type="primary" style="width:100%" :loading="loading" @click="submit">登录</el-button>
      </el-form>
      <div class="muted" style="margin-top:12px">没有账号？<router-link to="/register">注册</router-link></div>
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
const form = reactive({ username: 'demo', password: 'demo123' })

async function submit() {
  loading.value = true
  try {
    const res = await http.post('/auth/login', form)
    localStorage.setItem('geo_token', res.data.token)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth { min-height: 100vh; display: grid; place-items: center; background: linear-gradient(160deg, #ecfeff, #f8fafc); }
.box { width: 420px; }
h2 { margin: 0 0 8px; }
</style>
