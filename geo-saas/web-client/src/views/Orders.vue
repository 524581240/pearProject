<template>
  <div>
    <h2 class="page-title">我的订单</h2>
    <div class="page-card">
      <el-table :data="orders">
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="planCode" label="套餐" width="120" />
        <el-table-column prop="billingCycle" label="周期" width="100" />
        <el-table-column prop="amount" label="金额" width="100" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="payChannel" label="渠道" width="100" />
        <el-table-column prop="paidAt" label="支付时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button v-if="row.status==='PENDING'" link type="primary" @click="pay(row)">去支付</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const orders = ref([])
async function load() {
  orders.value = (await http.get('/orders')).data
}
async function pay(row) {
  await http.post(`/orders/${row.id}/pay`, { channel: 'MOCK' })
  ElMessage.success('支付成功')
  load()
}
onMounted(load)
</script>
