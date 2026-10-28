<template>
  <div>
    <h2 class="page-title">工作台</h2>
    <el-row :gutter="16">
      <el-col :span="6"><div class="page-card stat"><div class="muted">提及率</div><div class="num">{{ overview.mentionRate || 0 }}%</div></div></el-col>
      <el-col :span="6"><div class="page-card stat"><div class="muted">成功采样</div><div class="num">{{ overview.totalRuns || 0 }}</div></div></el-col>
      <el-col :span="6"><div class="page-card stat"><div class="muted">平均位次</div><div class="num">{{ overview.avgRank || '-' }}</div></div></el-col>
      <el-col :span="6"><div class="page-card stat"><div class="muted">情感均值</div><div class="num">{{ overview.avgSentiment || 0 }}</div></div></el-col>
    </el-row>
    <div class="page-card" style="margin-top:16px">
      <h3>各引擎提及率</h3>
      <el-table :data="overview.engines || []" size="small">
        <el-table-column prop="engineCode" label="引擎" />
        <el-table-column prop="total" label="采样数" />
        <el-table-column prop="mentioned" label="被提及" />
        <el-table-column prop="mentionRate" label="提及率%">
          <template #default="{ row }">{{ row.mentionRate }}%</template>
        </el-table-column>
      </el-table>
    </div>
    <div class="page-card" style="margin-top:16px">
      <h3>订阅用量</h3>
      <p class="muted" v-if="sub">套餐 {{ sub.subscription?.planCode }} · 状态 {{ sub.subscription?.status }} · 本月已用 {{ sub.usage?.responseUsed || 0 }} / {{ sub.subscription?.monthlyResponseLimit }}</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'

const overview = ref({})
const sub = ref(null)

onMounted(async () => {
  overview.value = (await http.get('/reports/overview')).data
  sub.value = (await http.get('/subscriptions/current')).data
})
</script>

<style scoped>
.stat .num { font-size: 28px; font-weight: 700; margin-top: 8px; color: #0f766e; }
h3 { margin: 0 0 12px; font-size: 16px; }
</style>
