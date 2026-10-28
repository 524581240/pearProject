<template>
  <div>
    <h2 class="page-title">多 AI 平台即时验证</h2>
    <p class="muted">选定品牌、Prompt 与引擎后立即采样，查看提及 / 位次 / 引用原文。</p>
    <div class="page-card">
      <el-form label-width="100px">
        <el-form-item label="品牌">
          <el-select v-model="form.brandId" style="width:320px" @change="loadPrompts">
            <el-option v-for="b in brands" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Prompt">
          <el-select v-model="selectedPrompts" multiple style="width:100%">
            <el-option v-for="p in prompts" :key="p.id" :label="p.content" :value="String(p.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="AI 引擎">
          <el-checkbox-group v-model="selectedEngines">
            <el-checkbox v-for="e in engines" :key="e.code" :label="e.code">{{ e.name }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="run">开始验证</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-if="batch" class="page-card" style="margin-top:16px">
      <h3>批次 #{{ batch.id }} · {{ batch.status }} · 成功 {{ batch.successItems }}/{{ batch.totalItems }}</h3>
      <el-button link type="primary" @click="$router.push(`/batches/${batch.id}`)">查看完整详情</el-button>
      <el-table :data="items" style="margin-top:12px">
        <el-table-column prop="engineCode" label="引擎" width="120" />
        <el-table-column prop="promptId" label="Prompt" width="90" />
        <el-table-column label="提及" width="80">
          <template #default="{ row }">
            <el-tag :type="row.mentioned ? 'success' : 'info'">{{ row.mentioned ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="positionRank" label="位次" width="80" />
        <el-table-column prop="sentimentScore" label="情感" width="80" />
        <el-table-column prop="rawAnswer" label="回答摘要" show-overflow-tooltip />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const brands = ref([])
const prompts = ref([])
const engines = ref([])
const selectedPrompts = ref([])
const selectedEngines = ref([])
const loading = ref(false)
const batch = ref(null)
const items = ref([])
const form = reactive({ brandId: null })

async function loadPrompts() {
  prompts.value = (await http.get('/prompts', { params: { brandId: form.brandId } })).data
  selectedPrompts.value = prompts.value.slice(0, 2).map(p => String(p.id))
}

async function run() {
  if (!form.brandId || !selectedPrompts.value.length || !selectedEngines.value.length) {
    ElMessage.warning('请选择品牌、Prompt 和引擎')
    return
  }
  loading.value = true
  try {
    const res = await http.post('/verify', {
      brandId: form.brandId,
      promptIds: selectedPrompts.value.join(','),
      engineCodes: selectedEngines.value.join(','),
      repeatCount: 1
    })
    batch.value = res.data
    const detail = (await http.get(`/batches/${res.data.id}`)).data
    items.value = detail.items
    ElMessage.success('验证完成')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  brands.value = (await http.get('/brands')).data
  engines.value = (await http.get('/engines')).data
  selectedEngines.value = engines.value.slice(0, 3).map(e => e.code)
  if (brands.value[0]) {
    form.brandId = brands.value[0].id
    await loadPrompts()
  }
})
</script>
