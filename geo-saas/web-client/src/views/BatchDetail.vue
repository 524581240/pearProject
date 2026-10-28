<template>
  <div>
    <h2 class="page-title">批次详情 #{{ id }}</h2>
    <div class="page-card" v-if="detail.batch">
      <p>状态：{{ detail.batch.status }} · 触发：{{ detail.batch.triggerType }} · 成功 {{ detail.batch.successItems }}/{{ detail.batch.totalItems }}</p>
      <el-table :data="detail.items || []">
        <el-table-column prop="engineCode" label="引擎" width="110" />
        <el-table-column prop="promptId" label="Prompt" width="90" />
        <el-table-column prop="promptVersion" label="版本" width="70" />
        <el-table-column label="提及" width="80">
          <template #default="{ row }"><el-tag :type="row.mentioned ? 'success' : 'info'">{{ row.mentioned ? '是' : '否' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="positionRank" label="位次" width="70" />
        <el-table-column prop="sentimentScore" label="情感" width="80" />
        <el-table-column prop="citationJson" label="引用" show-overflow-tooltip />
        <el-table-column prop="latencyMs" label="耗时ms" width="90" />
        <el-table-column label="原文">
          <template #default="{ row }">
            <el-button link type="primary" @click="show(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-drawer v-model="visible" title="原始回答" size="50%">
      <pre class="raw">{{ current?.rawAnswer }}</pre>
      <p class="muted">错误：{{ current?.errorMessage || '-' }}</p>
      <p class="muted">竞品提及：{{ current?.competitorMentionsJson }}</p>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import http from '../api/http'

const route = useRoute()
const id = route.params.id
const detail = ref({})
const visible = ref(false)
const current = ref(null)

function show(row) {
  current.value = row
  visible.value = true
}
onMounted(async () => {
  detail.value = (await http.get(`/batches/${id}`)).data
})
</script>

<style scoped>
.raw { white-space: pre-wrap; background:#0f172a; color:#e2e8f0; padding:16px; border-radius:8px; }
</style>
