<template>
  <div>
    <h2 class="page-title">订阅与用量</h2>
    <div class="page-card" v-if="data">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="套餐">{{ data.subscription.planCode }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ data.subscription.status }}</el-descriptions-item>
        <el-descriptions-item label="周期">{{ data.subscription.billingCycle }}</el-descriptions-item>
        <el-descriptions-item label="到期">{{ data.subscription.endAt }}</el-descriptions-item>
        <el-descriptions-item label="品牌上限">{{ data.subscription.brandLimit }}</el-descriptions-item>
        <el-descriptions-item label="Prompt 上限">{{ data.subscription.promptLimit }}</el-descriptions-item>
        <el-descriptions-item label="引擎上限">{{ data.subscription.engineLimit }}</el-descriptions-item>
        <el-descriptions-item label="月响应额度">{{ data.subscription.monthlyResponseLimit }}</el-descriptions-item>
        <el-descriptions-item label="本月已用">{{ data.usage.responseUsed }}</el-descriptions-item>
        <el-descriptions-item label="历史保留天">{{ data.subscription.historyDays }}</el-descriptions-item>
      </el-descriptions>
      <el-progress
        style="margin-top:20px"
        :percentage="Math.min(100, Math.round((data.usage.responseUsed / data.subscription.monthlyResponseLimit) * 100))"
        :stroke-width="16"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'
const data = ref(null)
onMounted(async () => {
  data.value = (await http.get('/subscriptions/current')).data
})
</script>
