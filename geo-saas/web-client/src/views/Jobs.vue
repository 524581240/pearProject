<template>
  <div>
    <div class="head">
      <h2 class="page-title">监测任务</h2>
      <el-button type="primary" @click="visible=true">创建任务</el-button>
    </div>
    <div class="page-card">
      <el-table :data="jobs">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="frequency" label="频率" width="100" />
        <el-table-column prop="engineCodes" label="引擎" />
        <el-table-column prop="promptIds" label="Prompt IDs" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="lastRunAt" label="上次运行" width="180" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button link type="primary" :loading="runningId===row.id" @click="run(row)">立即运行</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="page-card" style="margin-top:16px">
      <h3>最近批次</h3>
      <el-table :data="batches">
        <el-table-column prop="id" label="批次" width="80" />
        <el-table-column prop="jobId" label="任务" width="80" />
        <el-table-column prop="triggerType" label="触发" width="100" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="进度">
          <template #default="{ row }">{{ row.successItems }}/{{ row.totalItems }}（失败 {{ row.failedItems }}）</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/batches/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="visible" title="创建监测任务" width="640px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="品牌">
          <el-select v-model="form.brandId" style="width:100%" @change="loadPrompts">
            <el-option v-for="b in brands" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="频率">
          <el-select v-model="form.frequency" style="width:100%">
            <el-option label="每天" value="DAILY" />
            <el-option label="每周" value="WEEKLY" />
            <el-option label="仅手动" value="MANUAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="Prompt">
          <el-select v-model="promptIds" multiple style="width:100%">
            <el-option v-for="p in prompts" :key="p.id" :label="p.content" :value="String(p.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="引擎">
          <el-checkbox-group v-model="engineCodes">
            <el-checkbox v-for="e in engines" :key="e.code" :label="e.code">{{ e.name }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="create">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const jobs = ref([])
const batches = ref([])
const brands = ref([])
const prompts = ref([])
const engines = ref([])
const visible = ref(false)
const runningId = ref(null)
const promptIds = ref([])
const engineCodes = ref([])
const form = reactive({ name: '', brandId: null, frequency: 'DAILY' })

async function load() {
  jobs.value = (await http.get('/jobs')).data
  batches.value = (await http.get('/batches')).data
}
async function loadPrompts() {
  prompts.value = (await http.get('/prompts', { params: { brandId: form.brandId } })).data
}
async function create() {
  await http.post('/jobs', {
    ...form,
    promptIds: promptIds.value.join(','),
    engineCodes: engineCodes.value.join(','),
    repeatCount: 1
  })
  ElMessage.success('任务已创建')
  visible.value = false
  load()
}
async function run(row) {
  runningId.value = row.id
  try {
    const res = await http.post(`/jobs/${row.id}/run`)
    ElMessage.success('运行完成 #' + res.data.id)
    load()
  } finally {
    runningId.value = null
  }
}
onMounted(async () => {
  brands.value = (await http.get('/brands')).data
  engines.value = (await http.get('/engines')).data
  engineCodes.value = engines.value.slice(0, 3).map(e => e.code)
  await load()
})
</script>

<style scoped>
.head { display:flex; justify-content:space-between; align-items:center; }
h3 { margin:0 0 12px; }
</style>
