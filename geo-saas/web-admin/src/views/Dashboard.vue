<template>
  <div>
    <h2 class="page-title">运营总览</h2>
    <el-row :gutter="16">
      <el-col :span="6"><div class="page-card"><div class="muted">租户数</div><div class="num">{{ data.tenantCount }}</div></div></el-col>
      <el-col :span="6"><div class="page-card"><div class="muted">订单数</div><div class="num">{{ data.orderCount }}</div></div></el-col>
      <el-col :span="6"><div class="page-card"><div class="muted">套餐数</div><div class="num">{{ data.planCount }}</div></div></el-col>
      <el-col :span="6"><div class="page-card"><div class="muted">监测任务</div><div class="num">{{ data.jobCount }}</div></div></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'
const data = ref({})
onMounted(async () => { data.value = (await http.get('/admin/dashboard')).data })
</script>

<style scoped>
.num { font-size:28px; font-weight:700; margin-top:8px; color:#1d4ed8; }
</style>
