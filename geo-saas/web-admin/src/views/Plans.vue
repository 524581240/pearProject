<template>
  <div>
    <div class="head">
      <h2 class="page-title">套餐管理</h2>
      <el-button type="primary" @click="open">新建/编辑</el-button>
    </div>
    <div class="page-card">
      <el-table :data="list">
        <el-table-column prop="code" label="编码" width="120" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="priceMonthly" label="月价" width="90" />
        <el-table-column prop="priceYearly" label="年价" width="90" />
        <el-table-column prop="promptLimit" label="Prompt" width="90" />
        <el-table-column prop="engineLimit" label="引擎" width="80" />
        <el-table-column prop="monthlyResponseLimit" label="月响应" width="100" />
        <el-table-column prop="enabled" label="启用" width="80" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button link type="primary" @click="edit(row)">编辑</el-button></template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="visible" title="套餐" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="编码"><el-input v-model="form.code" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
        <el-form-item label="月价"><el-input-number v-model="form.priceMonthly" :min="0" /></el-form-item>
        <el-form-item label="年价"><el-input-number v-model="form.priceYearly" :min="0" /></el-form-item>
        <el-form-item label="品牌上限"><el-input-number v-model="form.brandLimit" :min="1" /></el-form-item>
        <el-form-item label="Prompt上限"><el-input-number v-model="form.promptLimit" :min="1" /></el-form-item>
        <el-form-item label="引擎上限"><el-input-number v-model="form.engineLimit" :min="1" /></el-form-item>
        <el-form-item label="月响应"><el-input-number v-model="form.monthlyResponseLimit" :min="1" /></el-form-item>
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
const form = reactive({ id: null, code: '', name: '', description: '', priceMonthly: 0, priceYearly: 0, brandLimit: 1, promptLimit: 20, engineLimit: 3, monthlyResponseLimit: 1000, enabled: true, seatLimit: 3, historyDays: 30, sortOrder: 0 })

async function load() { list.value = (await http.get('/admin/plans')).data }
function open() {
  Object.assign(form, { id: null, code: '', name: '', description: '', priceMonthly: 99, priceYearly: 990, brandLimit: 1, promptLimit: 30, engineLimit: 4, monthlyResponseLimit: 1500, enabled: true, seatLimit: 3, historyDays: 60, sortOrder: 10 })
  visible.value = true
}
function edit(row) { Object.assign(form, row); visible.value = true }
async function save() {
  await http.post('/admin/plans', form)
  ElMessage.success('已保存')
  visible.value = false
  load()
}
onMounted(load)
</script>

<style scoped>
.head { display:flex; justify-content:space-between; align-items:center; }
</style>
