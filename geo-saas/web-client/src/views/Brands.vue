<template>
  <div>
    <div class="head">
      <h2 class="page-title">品牌管理</h2>
      <el-button type="primary" @click="openCreate">新建品牌</el-button>
    </div>
    <div class="page-card">
      <el-table :data="list">
        <el-table-column prop="name" label="品牌" />
        <el-table-column prop="aliases" label="别名" />
        <el-table-column prop="domain" label="域名" />
        <el-table-column prop="industry" label="行业" />
        <el-table-column prop="locale" label="语言区" width="100" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button link type="primary" @click="manageComp(row)">竞品</el-button>
            <el-button link @click="edit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">归档</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="visible" :title="form.id ? '编辑品牌' : '新建品牌'" width="520px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="别名"><el-input v-model="form.aliases" placeholder="逗号分隔" /></el-form-item>
        <el-form-item label="域名"><el-input v-model="form.domain" /></el-form-item>
        <el-form-item label="行业"><el-input v-model="form.industry" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="compVisible" title="竞品管理" size="40%">
      <el-form inline @submit.prevent>
        <el-form-item><el-input v-model="compForm.name" placeholder="竞品名" /></el-form-item>
        <el-form-item><el-button type="primary" @click="addComp">添加</el-button></el-form-item>
      </el-form>
      <el-table :data="competitors">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="domain" label="域名" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="danger" @click="delComp(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../api/http'

const list = ref([])
const visible = ref(false)
const form = reactive({ id: null, name: '', aliases: '', domain: '', industry: '' })
const compVisible = ref(false)
const currentBrand = ref(null)
const competitors = ref([])
const compForm = reactive({ name: '' })

async function load() {
  list.value = (await http.get('/brands')).data
}
function openCreate() {
  Object.assign(form, { id: null, name: '', aliases: '', domain: '', industry: '' })
  visible.value = true
}
function edit(row) {
  Object.assign(form, row)
  visible.value = true
}
async function save() {
  if (form.id) await http.put(`/brands/${form.id}`, form)
  else await http.post('/brands', form)
  ElMessage.success('已保存')
  visible.value = false
  load()
}
async function remove(row) {
  await ElMessageBox.confirm('确认归档该品牌？')
  await http.delete(`/brands/${row.id}`)
  load()
}
async function manageComp(row) {
  currentBrand.value = row
  competitors.value = (await http.get(`/brands/${row.id}/competitors`)).data
  compVisible.value = true
}
async function addComp() {
  await http.post(`/brands/${currentBrand.value.id}/competitors`, { name: compForm.name })
  compForm.name = ''
  competitors.value = (await http.get(`/brands/${currentBrand.value.id}/competitors`)).data
}
async function delComp(row) {
  await http.delete(`/brands/competitors/${row.id}`)
  competitors.value = (await http.get(`/brands/${currentBrand.value.id}/competitors`)).data
}
onMounted(load)
</script>

<style scoped>
.head { display: flex; justify-content: space-between; align-items: center; }
</style>
