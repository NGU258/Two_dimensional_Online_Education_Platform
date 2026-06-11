<template>
  <div class="system-management">
    <div class="back-button-container">
      <el-button type="primary" plain class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回首页
      </el-button>
    </div>

    <div class="tabs-container">
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="用户管理" name="users">
          <div class="tab-content">
            <div class="search-bar">
              <el-input v-model="searchQuery" placeholder="搜索用户名" class="search-input"></el-input>
              <el-button type="primary" @click="searchUser">搜索</el-button>
              <el-button @click="showAddUserDialog = true">添加用户</el-button>
            </div>

            <el-table :data="filteredUsers" border class="users-table">
              <el-table-column prop="id" label="ID" width="80"></el-table-column>
              <el-table-column prop="username" label="用户名"></el-table-column>
              <el-table-column prop="email" label="邮箱"></el-table-column>
              <el-table-column prop="role" label="角色">
                <template #default="scope">
                  <el-tag :type="getRoleTagType(scope.row.role)">
                    {{ getRoleLabel(scope.row.role) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态">
                <template #default="scope">
                  <el-switch 
                    :value="scope.row.status" 
                    @change="toggleUserStatus(scope.row)"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                  ></el-switch>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180">
                <template #default="scope">
                  <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane label="系统配置" name="settings">
          <div class="tab-content">
            <el-form :model="systemSettings" label-width="120px" class="settings-form">
              <el-form-item label="网站名称">
                <el-input v-model="systemSettings.siteName"></el-input>
              </el-form-item>
              <el-form-item label="网站描述">
                <el-textarea v-model="systemSettings.siteDescription"></el-textarea>
              </el-form-item>
              <el-form-item label="最大注册人数">
                <el-input-number v-model="systemSettings.maxUsers" :min="1" :max="10000"></el-input-number>
              </el-form-item>
              <el-form-item label="允许新用户注册">
                <el-switch v-model="systemSettings.allowRegistration"></el-switch>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveSettings">保存配置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <el-tab-pane label="系统日志" name="logs">
          <div class="tab-content">
            <el-table :data="logs" border class="logs-table">
              <el-table-column prop="id" label="ID" width="80"></el-table-column>
              <el-table-column prop="time" label="时间"></el-table-column>
              <el-table-column prop="type" label="类型">
                <template #default="scope">
                  <el-tag :type="getLogTypeTagType(scope.row.type)">
                    {{ scope.row.type }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="user" label="操作用户"></el-table-column>
              <el-table-column prop="action" label="操作描述"></el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog title="添加用户" :visible.sync="showAddUserDialog" width="500px">
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="用户名" required>
          <el-input v-model="userForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" required>
          <el-input v-model="userForm.email" type="email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="密码" required>
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="学生" value="student"></el-option>
            <el-option label="教师" value="teacher"></el-option>
            <el-option label="管理员" value="admin"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddUserDialog = false">取消</el-button>
        <el-button type="primary" @click="addUser">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const goBack = () => {
  router.push('/')
}

const activeTab = ref('users')
const searchQuery = ref('')
const showAddUserDialog = ref(false)

const userForm = ref({
  username: '',
  email: '',
  password: '',
  role: 'student'
})

const users = ref([
  { id: 1, username: 'admin', email: 'admin@example.com', role: 'admin', status: true },
  { id: 2, username: 'teacher1', email: 'teacher1@example.com', role: 'teacher', status: true },
  { id: 3, username: 'student1', email: 'student1@example.com', role: 'student', status: true },
  { id: 4, username: 'student2', email: 'student2@example.com', role: 'student', status: false },
  { id: 5, username: 'teacher2', email: 'teacher2@example.com', role: 'teacher', status: true }
])

const systemSettings = ref({
  siteName: '小喵仙入梦学堂',
  siteDescription: '专注于沉浸式学习，力求以二次元动漫元素提供更加舒畅的学习体验',
  maxUsers: 1000,
  allowRegistration: true
})

const logs = ref([
  { id: 1, time: '2024-01-15 10:30:25', type: '登录', user: 'admin', action: '管理员登录系统' },
  { id: 2, time: '2024-01-15 10:25:18', type: '创建', user: 'admin', action: '创建用户 student3' },
  { id: 3, time: '2024-01-15 09:45:32', type: '修改', user: 'admin', action: '修改系统配置' },
  { id: 4, time: '2024-01-15 08:15:47', type: '登录', user: 'teacher1', action: '教师登录系统' },
  { id: 5, time: '2024-01-14 16:30:00', type: '删除', user: 'admin', action: '删除用户 student_old' }
])

const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  return users.value.filter(user => 
    user.username.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const getRoleLabel = (role) => {
  const labels = {
    student: '学生',
    teacher: '教师',
    admin: '管理员'
  }
  return labels[role] || role
}

const getRoleTagType = (role) => {
  const types = {
    student: 'info',
    teacher: 'success',
    admin: 'danger'
  }
  return types[role] || 'info'
}

const getLogTypeTagType = (type) => {
  const types = {
    '登录': 'info',
    '创建': 'success',
    '修改': 'warning',
    '删除': 'danger'
  }
  return types[type] || 'info'
}

const searchUser = () => {
  ElMessage.info(`搜索用户: ${searchQuery.value}`)
}

const addUser = () => {
  if (!userForm.value.username || !userForm.value.email || !userForm.value.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  const newUser = {
    id: Date.now(),
    username: userForm.value.username,
    email: userForm.value.email,
    role: userForm.value.role,
    status: true
  }
  
  users.value.push(newUser)
  showAddUserDialog.value = false
  userForm.value = {
    username: '',
    email: '',
    password: '',
    role: 'student'
  }
  ElMessage.success('用户添加成功')
}

const editUser = (user) => {
  ElMessage.info(`编辑用户: ${user.username}`)
}

const deleteUser = (userId) => {
  users.value = users.value.filter(u => u.id !== userId)
  ElMessage.success('用户删除成功')
}

const toggleUserStatus = (user) => {
  user.status = !user.status
  ElMessage.success(`用户 ${user.username} 状态已更新`)
}

const saveSettings = () => {
  ElMessage.success('系统配置保存成功')
}
</script>

<style scoped>
.system-management {
  padding: 30px;
  min-height: 100vh;
  background: linear-gradient(135deg, #0d0d1a 0%, #1a0a2e 50%, #0a0a1a 100%);
  position: relative;
}

.system-management::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(ellipse at 20% 80%, rgba(255, 107, 157, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(95, 39, 205, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 50% 50%, rgba(64, 196, 255, 0.05) 0%, transparent 70%);
  pointer-events: none;
  z-index: 0;
}

.system-management > * {
  position: relative;
  z-index: 1;
}

.back-button-container {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 20px;
}

.back-btn {
  background: linear-gradient(45deg, #5f27cd, #ff6b9d);
  border: 2px solid #ff6b9d;
  color: #fff;
  border-radius: 20px;
  padding: 12px 24px;
  font-weight: bold;
  font-size: 14px;
  box-shadow: 
    0 0 15px rgba(255, 107, 157, 0.5),
    inset 0 0 10px rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.8);
}

.back-btn:hover {
  background: linear-gradient(45deg, #7e47c2, #ff8fab);
  box-shadow: 
    0 0 25px rgba(255, 107, 157, 0.8),
    inset 0 0 15px rgba(255, 255, 255, 0.15);
  transform: translateX(-3px) scale(1.02);
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 32px;
  font-weight: bold;
  color: #ff6b9d;
  text-shadow: 0 0 10px rgba(255, 107, 157, 0.8);
  margin-bottom: 8px;
}

.page-subtitle {
  color: #81d4fa;
  font-size: 16px;
}

.tabs-container {
  max-width: 1200px;
  margin: 0 auto;
}

.tab-content {
  padding: 25px;
}

.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
}

.search-input {
  flex: 1;
  max-width: 350px;
}

.users-table,
.logs-table {
  width: 100%;
}

.settings-form {
  max-width: 700px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.el-tabs__item {
  color: #40c4ff !important;
}

.el-tabs__item.is-active {
  color: #ff6b9d !important;
}

.el-tabs__header {
  border-bottom: 1px solid #9c27b0 !important;
}

.el-tabs__active-bar {
  background: linear-gradient(45deg, #ff6b9d, #5f27cd) !important;
}

.el-table {
  background: rgba(26, 26, 46, 0.95) !important;
  border: 1px solid #9c27b0 !important;
}

.el-table__header-wrapper,
.el-table__body-wrapper {
  border-color: #9c27b0 !important;
}

.el-table th {
  background: rgba(156, 39, 176, 0.2) !important;
  color: #ff6b9d !important;
  font-weight: bold !important;
}

.el-table td {
  color: #81d4fa !important;
  border-color: #9c27b0 !important;
}

.el-table tr:hover > td {
  background: rgba(255, 107, 157, 0.1) !important;
}

.el-form-item__label {
  color: #ff6b9d !important;
}

.el-input__inner,
.el-textarea__inner {
  background: rgba(0, 0, 0, 0.5);
  border: 1px solid #9c27b0;
  color: #40c4ff;
}

.el-select .el-input__inner {
  width: 100%;
}

.el-option {
  background: #1a1a2e;
  color: #40c4ff;
}

.el-option:hover {
  background: rgba(255, 107, 157, 0.2);
}

.el-tag {
  border-radius: 10px !important;
}

.el-tag--info {
  background: rgba(64, 196, 255, 0.2) !important;
  border-color: #40c4ff !important;
  color: #40c4ff !important;
}

.el-tag--success {
  background: rgba(103, 194, 58, 0.2) !important;
  border-color: #67c23a !important;
  color: #67c23a !important;
}

.el-tag--danger {
  background: rgba(255, 107, 157, 0.2) !important;
  border-color: #ff6b9d !important;
  color: #ff6b9d !important;
}

.el-tag--warning {
  background: rgba(230, 162, 60, 0.2) !important;
  border-color: #e6a23c !important;
  color: #e6a23c !important;
}

.el-button--primary {
  background: linear-gradient(45deg, #ff6b9d, #5f27cd) !important;
  border: 2px solid #ff6b9d !important;
  border-radius: 15px !important;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.5) !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
}

.el-button--primary:hover {
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.8) !important;
  transform: translateY(-2px) !important;
}

.el-button--danger {
  background: linear-gradient(45deg, #ff4757, #ff6b6b) !important;
  border: 2px solid #ff4757 !important;
  border-radius: 15px !important;
  box-shadow: 0 0 15px rgba(255, 71, 87, 0.5) !important;
  font-weight: bold !important;
}

.el-button--danger:hover {
  box-shadow: 0 0 25px rgba(255, 71, 87, 0.8) !important;
  transform: translateY(-2px) !important;
}

.el-button--default {
  background: rgba(95, 39, 205, 0.2) !important;
  border: 1px solid #9c27b0 !important;
  border-radius: 15px !important;
  color: #40c4ff !important;
}

.el-button--default:hover {
  background: rgba(95, 39, 205, 0.4) !important;
  box-shadow: 0 0 15px rgba(95, 39, 205, 0.5) !important;
}

.el-switch__core {
  background: rgba(156, 39, 176, 0.3) !important;
  border: 1px solid #9c27b0 !important;
}

.el-switch__core.is-checked {
  background: linear-gradient(45deg, #ff6b9d, #5f27cd) !important;
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.5) !important;
}

.el-switch__button {
  background: #fff !important;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5) !important;
}

.el-dialog {
  background: rgba(26, 10, 46, 0.95) !important;
  border: 1px solid #9c27b0 !important;
  border-radius: 20px !important;
  box-shadow: 0 0 30px rgba(156, 39, 176, 0.5) !important;
}

.el-dialog__header {
  border-bottom: 1px solid #9c27b0 !important;
}

.el-dialog__title {
  color: #ff6b9d !important;
  font-weight: bold !important;
}

.el-dialog__body {
  color: #40c4ff !important;
}

.el-dialog__footer {
  border-top: 1px solid #9c27b0 !important;
}

.el-pagination button {
  background: rgba(95, 39, 205, 0.2) !important;
  border: 1px solid #9c27b0 !important;
  color: #40c4ff !important;
}

.el-pagination button:hover {
  background: rgba(255, 107, 157, 0.3) !important;
}

.el-pager li {
  background: rgba(95, 39, 205, 0.2) !important;
  border: 1px solid #9c27b0 !important;
  color: #40c4ff !important;
}

.el-pager li.active {
  background: linear-gradient(45deg, #ff6b9d, #5f27cd) !important;
  border-color: #ff6b9d !important;
}

::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(156, 39, 176, 0.1);
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(45deg, #ff6b9d, #5f27cd);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(45deg, #ff8fab, #7e47c2);
}
</style>