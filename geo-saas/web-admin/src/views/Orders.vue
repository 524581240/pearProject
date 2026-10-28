<template>
  <div>
    <h2 class="page-title">订单流水</h2>
    <div class="page-card">
      <el-table :data="list">
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="tenantId" label="租户" width="90" />
        <el-table-column prop="planCode" label="套餐" width="120" />
        <el-table-column prop="amount" label="金额" width="100" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="payChannel" label="渠道" width="100" />
        <el-table-column prop="paidAt" label="支付时间" width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'
const list = ref([])
onMounted(async () => { list.value = (await http.get('/admin/orders')).data })
</script>
