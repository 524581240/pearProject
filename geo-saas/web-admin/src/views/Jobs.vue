<template>
  <div>
    <h2 class="page-title">任务监控</h2>
    <div class="page-card">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="tenantId" label="租户" width="90" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="frequency" label="频率" width="100" />
        <el-table-column prop="engineCodes" label="引擎" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="lastRunAt" label="上次运行" width="180" />
        <el-table-column prop="nextRunAt" label="下次运行" width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'
const list = ref([])
onMounted(async () => { list.value = (await http.get('/admin/jobs')).data })
</script>
