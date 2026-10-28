<template>
  <div>
    <h2 class="page-title">套餐购买</h2>
    <el-row :gutter="16">
      <el-col :span="6" v-for="p in plans" :key="p.id">
        <div class="page-card plan">
          <h3>{{ p.name }}</h3>
          <p class="muted">{{ p.description }}</p>
          <div class="price">¥{{ p.priceMonthly }}<span>/月</span></div>
          <div class="muted">年付 ¥{{ p.priceYearly }}</div>
          <ul>
            <li>品牌 {{ p.brandLimit }}</li>
            <li>Prompt {{ p.promptLimit }}</li>
            <li>引擎 {{ p.engineLimit }}</li>
            <li>月响应 {{ p.monthlyResponseLimit }}</li>
            <li>席位 {{ p.seatLimit }}</li>
          </ul>
          <el-button type="primary" style="width:100%" :disabled="p.code==='trial'" @click="buy(p, 'MONTHLY')">月付购买</el-button>
          <el-button style="width:100%;margin-top:8px" :disabled="p.code==='trial'" @click="buy(p, 'YEARLY')">年付购买</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../api/http'

const plans = ref([])
const router = useRouter()

async function buy(plan, cycle) {
  await ElMessageBox.confirm(`确认以 ${cycle === 'YEARLY' ? '年付' : '月付'} 购买「${plan.name}」？将走 Mock 支付秒完成。`)
  const order = (await http.post('/orders', { planCode: plan.code, billingCycle: cycle })).data
  const pay = (await http.post(`/orders/${order.id}/pay`, { channel: 'MOCK' })).data
  ElMessage.success('支付成功：' + pay.orderNo)
  router.push('/subscription')
}

onMounted(async () => {
  plans.value = (await http.get('/plans')).data
})
</script>

<style scoped>
.plan h3 { margin: 0 0 8px; }
.price { font-size: 28px; font-weight: 700; margin: 12px 0 4px; color: #0f766e; }
.price span { font-size: 13px; color: #6b7280; }
ul { padding-left: 18px; color: #374151; line-height: 1.8; }
</style>
