<template>
  <div>
    <div class="head">
      <h2 class="page-title">AI 引擎配置</h2>
      <el-button type="primary" @click="open">新增引擎</el-button>
    </div>
    <div class="page-card">
      <el-table :data="list">
        <el-table-column prop="code" label="Code" width="120" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="region" label="区域" width="100" />
        <el-table-column prop="adapterType" label="适配器" width="100" />
        <el-table-column prop="enabled" label="启用" width="80" />
        <el-table-column prop="endpoint" label="Endpoint" show-overflow-tooltip />
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button link type="primary" @click="edit(row)">编辑</el-button></template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="visible" title="引擎" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="Code"><el-input v-model="form.code" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="区域"><el-input v-model="form.region" /></el-form-item>
        <el-form-item label="适配器"><el-input v-model="form.adapterType" placeholder="MOCK/HTTP" /></el-form-item>
        <el-form-item label="Endpoint"><el-input v-model="form.endpoint" /></el-form-item>
        <el-form-item label="API Key"><el-input v-model="form.apiKey" type="password" show-password /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.enabled" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const list = ref([])
const visible = ref(false)
const form = reactive({ id: null, code: '', name: '', region: 'CN', adapterType: 'MOCK', endpoint: '', apiKey: '', enabled: true })

async function load() { list.value = (await http.get('/admin/engines')).data }
function open() {
  Object.assign(form, { id: null, code: '', name: '', region: 'CN', adapterType: 'MOCK', endpoint: '', apiKey: '', enabled: true })
  visible.value = true
}
function edit(row) { Object.assign(form, row); visible.value = true }
async function save() {
  await http.post('/admin/engines', form)
  ElMessage.success('已保存')
  visible.value = false
  load()
}
onMounted(load)
</script>

<style scoped>
.head { display:flex; justify-content:space-between; align-items:center; }
</style>
