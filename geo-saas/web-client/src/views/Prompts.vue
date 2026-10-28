<template>
  <div>
    <div class="head">
      <h2 class="page-title">Prompt 库</h2>
      <div>
        <el-select v-model="brandId" placeholder="筛选品牌" clearable style="width:180px;margin-right:8px" @change="load">
          <el-option v-for="b in brands" :key="b.id" :label="b.name" :value="b.id" />
        </el-select>
        <el-button type="primary" @click="openCreate">新建 Prompt</el-button>
      </div>
    </div>
    <div class="page-card">
      <el-table :data="list">
        <el-table-column prop="brandId" label="品牌ID" width="90" />
        <el-table-column prop="groupName" label="分组" width="120" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="currentVersion" label="版本" width="80" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="edit(row)">编辑</el-button>
            <el-button link @click="showVersions(row)">版本</el-button>
            <el-button link type="danger" @click="archive(row)">归档</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="visible" :title="form.id ? '编辑 Prompt' : '新建 Prompt'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="品牌">
          <el-select v-model="form.brandId" style="width:100%">
            <el-option v-for="b in brands" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分组"><el-input v-model="form.groupName" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="标签"><el-input v-model="form.tags" placeholder="推荐,对比" /></el-form-item>
        <el-form-item v-if="form.id" label="变更说明"><el-input v-model="form.changeNote" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="verVisible" title="版本历史" size="40%">
      <el-timeline>
        <el-timeline-item v-for="v in versions" :key="v.id" :timestamp="'v' + v.versionNo">
          <div>{{ v.changeNote }}</div>
          <pre class="ver">{{ v.content }}</pre>
        </el-timeline-item>
      </el-timeline>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const brands = ref([])
const list = ref([])
const brandId = ref()
const visible = ref(false)
const form = reactive({ id: null, brandId: null, groupName: '默认', content: '', tags: '', changeNote: '' })
const verVisible = ref(false)
const versions = ref([])

async function load() {
  brands.value = (await http.get('/brands')).data
  const q = brandId.value ? `?brandId=${brandId.value}` : ''
  list.value = (await http.get('/prompts' + q)).data
}
function openCreate() {
  Object.assign(form, { id: null, brandId: brands.value[0]?.id, groupName: '默认', content: '', tags: '', changeNote: '' })
  visible.value = true
}
function edit(row) {
  Object.assign(form, { ...row, changeNote: '' })
  visible.value = true
}
async function save() {
  if (form.id) await http.put(`/prompts/${form.id}`, form)
  else await http.post('/prompts', form)
  ElMessage.success('已保存（内容变更会升版本）')
  visible.value = false
  load()
}
async function showVersions(row) {
  versions.value = (await http.get(`/prompts/${row.id}/versions`)).data
  verVisible.value = true
}
async function archive(row) {
  await http.delete(`/prompts/${row.id}`)
  load()
}
onMounted(load)
</script>

<style scoped>
.head { display:flex; justify-content:space-between; align-items:center; }
.ver { white-space: pre-wrap; background:#f8fafc; padding:8px; border-radius:6px; font-size:12px; }
</style>
