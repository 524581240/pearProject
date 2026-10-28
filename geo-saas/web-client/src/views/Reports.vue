<template>
  <div>
    <h2 class="page-title">可见度报表</h2>
    <el-row :gutter="16">
      <el-col :span="6"><div class="page-card"><div class="muted">提及率</div><div class="num">{{ overview.mentionRate }}%</div></div></el-col>
      <el-col :span="6"><div class="page-card"><div class="muted">采样次数</div><div class="num">{{ overview.totalRuns }}</div></div></el-col>
      <el-col :span="6"><div class="page-card"><div class="muted">平均位次</div><div class="num">{{ overview.avgRank }}</div></div></el-col>
      <el-col :span="6"><div class="page-card"><div class="muted">情感均值</div><div class="num">{{ overview.avgSentiment }}</div></div></el-col>
    </el-row>
    <div class="page-card" style="margin-top:16px">
      <h3>提及率趋势</h3>
      <div ref="chartRef" style="height:320px"></div>
    </div>
    <div class="page-card" style="margin-top:16px">
      <h3>最近采样</h3>
      <el-table :data="overview.recent || []">
        <el-table-column prop="createdAt" label="时间" width="180" />
        <el-table-column prop="engineCode" label="引擎" width="110" />
        <el-table-column prop="mentioned" label="提及" width="80" />
        <el-table-column prop="positionRank" label="位次" width="80" />
        <el-table-column prop="rawAnswer" label="摘要" show-overflow-tooltip />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import http from '../api/http'

const overview = ref({})
const chartRef = ref()

onMounted(async () => {
  overview.value = (await http.get('/reports/overview')).data
  const trends = (await http.get('/reports/trends')).data
  await nextTick()
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['提及率%', '采样数'] },
    xAxis: { type: 'category', data: trends.days || [] },
    yAxis: [{ type: 'value', name: '提及率%' }, { type: 'value', name: '采样数' }],
    series: [
      { name: '提及率%', type: 'line', data: trends.mentionRates || [], smooth: true },
      { name: '采样数', type: 'bar', yAxisIndex: 1, data: trends.totals || [] }
    ]
  })
})
</script>

<style scoped>
.num { font-size: 28px; font-weight: 700; margin-top: 8px; color: #0f766e; }
h3 { margin: 0 0 12px; }
</style>
