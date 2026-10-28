<template>
  <div class="auth">
    <div class="box page-card">
      <h2>注册租户账号</h2>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" /></el-form-item>
        <el-form-item label="公司/团队名"><el-input v-model="form.companyName" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-button type="primary" style="width:100%" :loading="loading" @click="submit">注册并登录</el-button>
      </el-form>
      <div class="muted" style="margin-top:12px"><router-link to="/login">返回登录</router-link></div>
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
const form = reactive({ username: '', password: '', companyName: '', email: '' })

async function submit() {
  loading.value = true
  try {
    const res = await http.post('/auth/register', form)
    localStorage.setItem('geo_token', res.data.token)
    ElMessage.success('注册成功，已开通试用套餐')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth { min-height: 100vh; display: grid; place-items: center; }
.box { width: 460px; }
</style>
