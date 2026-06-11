<template>
  <div class="register-container" style="width: 100vw; max-width: none; margin: 0; padding: 0;">
    <!-- 左侧背景和品牌信息 -->
    <div class="register-bg">
      <div class="bg-content">
        <h1 class="brand-name">
          <span v-for="(char, index) in '小喵仙入梦学堂'" :key="index" class="bounce-char" :style="{ animationDelay: `${index * 0.1}s` }">{{ char }}</span>
        </h1>
        <p class="brand-slogan">专注于沉浸式学习，力求以二次元动漫元素、音乐、游戏化机制等<br>为学习爱好者提供更加舒畅的学习体验~</p>
        <div class="anime-character">
          <!-- 这里可以放置二次元角色图片 -->
        </div>
      </div>
    </div>
    
    <!-- 右侧注册表单 -->
    <div class="register-form-container" ref="formContainerRef">
      <div class="register-form">
        

        
        <el-form :model="form" @submit.prevent="handleSubmit" class="register-form-el" label-width="80px">
          <el-form-item label="用户名" required>
            <el-row :gutter="10" style="width: 100%;">
              <el-col :span="24">
                <el-input v-model="form.username" placeholder="请输入用户名" class="custom-input" style="width: 100%;" autocomplete="username" name="username" clearable @focus="playClickSound">
                </el-input>
              </el-col>
            </el-row>
          </el-form-item>
          
          <el-form-item label="邮箱" required>
            <el-row :gutter="10" style="width: 100%;">
              <el-col :span="24">
                <el-input v-model="form.email" type="email" placeholder="请输入邮箱" class="custom-input" style="width: 100%;" autocomplete="email" name="email" clearable @focus="playClickSound">
                </el-input>
              </el-col>
            </el-row>
          </el-form-item>
          
          <el-form-item label="手机号" required>
            <el-row :gutter="10" style="width: 100%;">
              <el-col :span="24">
                <el-input v-model="form.phone" placeholder="请输入手机号" class="custom-input" style="width: 100%;" autocomplete="tel" name="phone" clearable @focus="playClickSound">
                </el-input>
              </el-col>
            </el-row>
          </el-form-item>
          
          <el-form-item label="验证码" required>
            <el-row :gutter="10" style="width: 100%;">
              <el-col :span="16">
                <el-input v-model="form.code" placeholder="请输入验证码" class="custom-input" style="width: 100%;" autocomplete="one-time-code" name="code" clearable @focus="playClickSound">
                </el-input>
              </el-col>
              <el-col :span="8">
                <el-button 
                  type="info" 
                  class="send-code-btn" 
                  @click="sendCode(); playSound('验证码提示音')" 
                  :disabled="countdown > 0"
                  style="width: 100%; height: 38px;"
                >
                  {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          
          <el-form-item label="密码" required>
            <el-row :gutter="10" style="width: 100%;">
              <el-col :span="24">
                <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password class="custom-input" style="width: 100%;" autocomplete="new-password" name="password" clearable @focus="playClickSound">
                </el-input>
              </el-col>
            </el-row>
          </el-form-item>
          
          <el-form-item label="确认密码" required>
          <el-row :gutter="10" style="width: 100%;">
            <el-col :span="24">
              <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" show-password class="custom-input" style="width: 100%;" autocomplete="new-password" name="confirm-password" clearable @focus="playClickSound">
              </el-input>
            </el-col>
          </el-row>
        </el-form-item>
        
        <el-form-item label="当前身份" required>
          <el-row :gutter="10" style="width: 100%;">
            <el-col :span="24">
              <el-radio-group v-model="form.role" class="role-radio-group">
                <el-radio 
                  v-for="role in roles" 
                  :key="role.value" 
                  :label="role.value"
                  @click="playClickSound"
                >
                  {{ role.label }}
                </el-radio>
              </el-radio-group>
            </el-col>
          </el-row>
        </el-form-item>
        
        <el-form-item class="button-group" style="display: flex; justify-content: center;">
          <el-button 
            type="primary" 
            native-type="submit" 
            class="register-btn"
            :loading="loading"
            style="width: 200px;"
            @click="playSound('注册音')"
          >
            注册
          </el-button>
        </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useAudioManager } from '../utils/audioManager'

const { playSound, playBackgroundMusic, stopBackgroundMusic, playClickSound } = useAudioManager()

const router = useRouter()
const userStore = useUserStore()
const formContainerRef = ref(null)
const musicPlayed = ref(false)

const loading = ref(false)
const countdown = ref(0)
const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  code: '',
  email: '',
  role: 'student'
})

const roles = [
  { label: '学生', value: 'student' },
  { label: '教师', value: 'teacher' },
  { label: '管理员', value: 'admin' }
]

// 监听手机号变化，重置倒计时
watch(() => form.value.phone, () => {
  countdown.value = 0
  // 清除可能存在的定时器
  clearInterval(window.codeTimer)
})

const handleSubmit = async () => {
  // 注册逻辑
  if (!form.value.username || !form.value.email || !form.value.phone || !form.value.code || !form.value.password || !form.value.confirmPassword) {
    ElMessage.warning('请填写所有必填字段')
    return
  }
  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(form.value.email)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  
  // 手机号格式验证，支持带+86前缀
  const phoneRegex = /^(\+86)?1[3-9]\d{9}$/
  if (!phoneRegex.test(form.value.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  
  // 去除+86前缀
  let phone = form.value.phone
  if (phone.startsWith('+86')) {
    phone = phone.substring(3)
  }
  
  loading.value = true
  try {
    const result = await userStore.registerByPhone(
      phone,
      form.value.code,
      form.value.password,
      form.value.username,
      form.value.email,
      form.value.role
    )
    
    if (result.success) {
      // 注册成功后直接跳转到首页
      ElMessage.success('注册成功')
      router.push('/')
    } else {
      // 注册失败
      console.log('Register error message:', result.message)
      ElMessage.error(result.message || '注册失败，请稍后重试')
    }
  } catch (error) {
    loading.value = false
    console.error('Register catch error:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const sendCode = async () => {
  if (!form.value.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  
  // 验证手机号格式，支持带+86前缀
  const phoneRegex = /^(\+86)?1[3-9]\d{9}$/
  if (!phoneRegex.test(form.value.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  
  // 去除+86前缀
  let phone = form.value.phone
  if (phone.startsWith('+86')) {
    phone = phone.substring(3)
  }
  
  try {
    // 调用后端API发送验证码
    const response = await axios.post('/api/user/send-code', { phone: phone })
    if (response.data.code === 200) {
      ElMessage.success('验证码已发送')
      // 2.5秒后自动填充验证码
      if (response.data.data) {
        setTimeout(() => {
          form.value.code = response.data.data
        }, 2500)
      }
      // 开始倒计时
      countdown.value = 60
      // 清除可能存在的定时器
      clearInterval(window.codeTimer)
      // 存储新的定时器
      window.codeTimer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(window.codeTimer)
        }
      }, 1000)
    } else {
      ElMessage.error('发送验证码失败: ' + response.data.message)
    }
  } catch (error) {
    ElMessage.error('发送验证码失败，请稍后重试')
    console.error('发送验证码错误:', error)
  }
}



// 组件挂载时滚动到页面顶部
onMounted(() => {
  console.log('Attempting to scroll to top...')
  window.scrollTo({ top: 0, behavior: 'smooth' })
  document.documentElement.scrollTop = 0
  document.body.scrollTop = 0
  
  if (formContainerRef.value) {
    formContainerRef.value.scrollTop = 0
  }
  
  const tryAutoPlay = async () => {
    try {
      await playBackgroundMusic('/music/界面纯音/UnicornPhantom.mp3')
      musicPlayed.value = true
      console.log('注册页面背景音乐自动播放成功')
    } catch (err) {
      console.log('注册页面背景音乐自动播放失败，等待用户交互:', err)
    }
  }
  
  tryAutoPlay()
  
  const handleUserInteraction = async () => {
    if (!musicPlayed.value) {
      await playBackgroundMusic('/music/界面纯音/UnicornPhantom.mp3')
      musicPlayed.value = true
    }
  }
  
  document.addEventListener('click', handleUserInteraction, { once: true })
  document.addEventListener('touchstart', handleUserInteraction, { once: true })
  document.addEventListener('keydown', handleUserInteraction, { once: true })
  
  setTimeout(() => {
    if (!musicPlayed.value) {
      playClickSound()
    }
  }, 1000)
})

onUnmounted(() => {
  clearInterval(window.codeTimer)
  stopBackgroundMusic('/music/界面纯音/UnicornPhantom.mp3')
})
</script>

<style scoped>
.register-container {
  display: flex;
  min-height: 80vh;
  width: 100%;
  overflow: hidden;
  position: relative;
}

/* 左侧背景 */
.register-bg {
  flex: 1.8;
  background-image: url('/images/12.jpg');
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  min-width: 0;
  overflow: hidden;
  filter: brightness(1.2);
}

.register-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

/* 黑色主题 */
html.dark-theme .register-bg {
  background-color: black;
}

html.dark-theme .register-bg::before {
  background: linear-gradient(to right, 
    rgba(0, 0, 0, 0.95),   /* 左边：深黑 */
    rgba(0, 0, 0, 0.1) 66%, /* 中间：近乎透明/亮色 */
    rgba(0, 0, 0, 0.65)),linear-gradient(to top, 
    rgba(0, 0, 0, 0.9),   
    rgba(0, 0, 0, 0) 50%, 
    rgba(0, 0, 0, 0.2) );
}

/* 白色主题 */
html.light-theme .register-bg {
  background-color: #f0f0f0;
}

html.light-theme .register-bg::before {
  background: linear-gradient(to right, 
    rgba(255, 255, 255, 0.85),   /* 左边：白色 */
    rgba(255, 255, 255, 0.1) 66%, /* 中间：近乎透明 */
    rgba(255, 255, 255, 0.65)),linear-gradient(to top, 
    rgba(255, 255, 255, 0.87),   /* 左边：白色 */
    rgba(255, 255, 255, 0) 15%, /* 中间：近乎透明 */
    rgba(255, 255, 255, 0) );
}

.bg-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #40c4ff;
  max-width: 800px; 
  padding: 0 40px;
}

.brand-name {
  font-size: 36px;
  font-weight: bold;
  margin-top:20px;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  display: inline-block;
}

.bounce-char {
  display: inline-block;
  animation: bounce 2s ease-in-out infinite, colorChange 3s ease-in-out infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

@keyframes colorChange {
  0% {
    color: #ff6b9d;
  }
  50% {
    color: #5f27cd;
  }
  100% {
    color: #ff6b9d;
  }
}

.brand-slogan {
  font-size: 18px;
  color:pink;
  line-height: 1.8;
  margin-top: 400px;
  margin-bottom: 40px;
  opacity: 0.8;
  text-align: center;
  max-width: 600px;
  text-shadow: 0 0 10px rgba(255, 107, 157, 0.8);
  font-weight: 500;
}

/* 右侧注册表单 */
.register-form-container {
  flex: 1.4;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 65px 40px 40px 30px;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  position: relative;
  border-left: none;
}

/* 黑色主题 */
html.dark-theme .register-form-container {
  background-color: black;
  box-shadow: -5px 0 15px rgba(255, 107, 157, 0.3);
}

/* 白色主题 */
html.light-theme .register-form-container {
  background-color: white;
  box-shadow: -5px 0 15px rgba(0, 0, 0, 0.1);
}

/* 中间分隔线 */
.register-form-container::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: linear-gradient(to bottom, transparent, #ff6b9d, #5f27cd, transparent);
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.5);
  animation: lineGlow 3s ease-in-out infinite;
}

@keyframes lineGlow {
  0%, 100% {
    opacity: 0.6;
    box-shadow: 0 0 8px rgba(255, 107, 157, 0.5);
  }
  50% {
    opacity: 1;
    box-shadow: 0 0 15px rgba(255, 107, 157, 0.7);
  }
}

/* 装饰元素 */
.register-form-container::before {
  content: '';
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  background: linear-gradient(45deg, #ff6b9d, #5f27cd, #ff6b9d);
  border-radius: 10px;
  z-index: -1;
  animation: borderGlow 3s ease-in-out infinite;
}

@keyframes borderGlow {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 0.8;
  }
}

.register-form-container::-webkit-scrollbar {
  display: none;
}

.register-form {
  width: 100%;
  max-width: 350px;
  /* margin-top: 140px; */
}

.form-header {
  margin-bottom: 30px;
  text-align: center;
}

.form-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #40c4ff;
  margin-bottom: 8px;
}

.form-header p {
  font-size: 14px;
  color: #81d4fa;
}

/* 表单标题样式 */
.form-header {
  margin-bottom: 30px;
  text-align: center;
}

.form-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #40c4ff;
  margin-bottom: 8px;
  text-shadow: 0 0 10px rgba(64, 196, 255, 0.8);
}

.form-header p {
  font-size: 14px;
  color: #81d4fa;
  text-shadow: 0 0 5px rgba(129, 212, 250, 0.5);
}

.register-form-el {
  margin-bottom: 20px;
}

.register-form-el :deep(.el-form-item__label) {
  font-weight: bold;
}

.register-form-el :deep(.el-input__wrapper) {
  border-radius: 15px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

/* 黑色主题 */
html.dark-theme .register-form-el :deep(.el-form-item__label) {
  color: #ff6b9d;
  text-shadow: 0 0 2px rgba(255, 107, 157, 0.8);
}

html.dark-theme .register-form-el :deep(.el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.8);
  border-color: #e80ec7ff;
  --el-input-text-color: #e80ec7ff;
  --el-input-placeholder-color: rgba(11, 215, 222, 1);
  box-shadow: 0 0 8px rgba(232, 14, 199, 0.2);
}

/* 白色主题 */
html.light-theme .register-form-el :deep(.el-form-item__label) {
  color: #333;
  text-shadow: none;
}

html.light-theme .register-form-el :deep(.el-input__wrapper) {
  background-color: white;
  border-color: #ccc;
  --el-input-text-color: #333;
  --el-input-placeholder-color: #999;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}

.register-form-el :deep(.el-input__placeholder) {
  font-weight: bold;
}

.register-form-el :deep(.el-input__wrapper::before) {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  transform: rotate(45deg);
  animation: shine 3s ease-in-out infinite;
  z-index: 1;
  pointer-events: none;
}

.register-form-el :deep(.el-input__inner) {
  position: relative;
  z-index: 2;
  font-weight: 500;
  font-size: 14px !important;
  background-color: transparent !important;
  border: none !important;
  outline: none !important;
}

/* 黑色主题 */
html.dark-theme .register-form-el :deep(.el-input__placeholder) {
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.8), 0 0 20px rgba(255, 107, 157, 0.5);
}

html.dark-theme .register-form-el :deep(.el-input__wrapper::before) {
  background: linear-gradient(45deg, transparent, rgba(255, 107, 157, 0.2), transparent);
}

html.dark-theme .register-form-el :deep(.el-input__inner) {
  color: #e80ec7ff !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1), 0 0 20px rgba(232, 14, 199, 0.8), 0 0 30px rgba(95, 39, 205, 0.6) !important;
  caret-color: #e80ec7ff !important;
}

/* 白色主题 */
html.light-theme .register-form-el :deep(.el-input__placeholder) {
  text-shadow: none;
}

html.light-theme .register-form-el :deep(.el-input__wrapper::before) {
  background: linear-gradient(45deg, transparent, rgba(0, 0, 0, 0.1), transparent);
}

html.light-theme .register-form-el :deep(.el-input__inner) {
  color: #333 !important;
  text-shadow: none !important;
  caret-color: #333 !important;
}

/* 优化自动填充样式 */
.register-form-el :deep(.el-input__inner:-webkit-autofill) {
  -webkit-background-clip: text;
  transition: background-color 5000s ease-in-out 0s !important;
  font-weight: 500 !important;
  font-size: 14px !important;
}

.register-form-el :deep(.el-input__inner:-webkit-autofill:hover),
.register-form-el :deep(.el-input__inner:-webkit-autofill:focus) {
  -webkit-background-clip: text;
  transition: background-color 5000s ease-in-out 0s !important;
  font-weight: 500 !important;
  font-size: 14px !important;
}

/* 黑色主题 */
html.dark-theme .register-form-el :deep(.el-input__inner:-webkit-autofill) {
  -webkit-text-fill-color: #ffffff !important;
  background-color: transparent !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1), 0 0 20px rgba(232, 14, 199, 0.8), 0 0 30px rgba(95, 39, 205, 0.6) !important;
  caret-color: #e80ec7ff !important;
}

html.dark-theme .register-form-el :deep(.el-input__inner:-webkit-autofill:hover),
html.dark-theme .register-form-el :deep(.el-input__inner:-webkit-autofill:focus) {
  -webkit-text-fill-color: #ffffff !important;
  background-color: transparent !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1), 0 0 20px rgba(232, 14, 199, 0.8), 0 0 30px rgba(95, 39, 205, 0.6) !important;
  caret-color: #e80ec7ff !important;
}

/* 白色主题 */
html.light-theme .register-form-el :deep(.el-input__inner:-webkit-autofill) {
  -webkit-text-fill-color: #333 !important;
  background-color: white !important;
  text-shadow: none !important;
  caret-color: #333 !important;
}

html.light-theme .register-form-el :deep(.el-input__inner:-webkit-autofill:hover),
html.light-theme .register-form-el :deep(.el-input__inner:-webkit-autofill:focus) {
  -webkit-text-fill-color: #333 !important;
  background-color: white !important;
  text-shadow: none !important;
  caret-color: #333 !important;
}

/* 优化自动填充下拉菜单样式 */
input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
input:-webkit-autofill:active {
  -webkit-text-fill-color: #ffffff !important;
  -webkit-background-clip: text !important;
  background-color: transparent !important;
  background-image: none !important;
  transition: background-color 5000s ease-in-out 0s !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1), 0 0 20px rgba(232, 14, 199, 0.8), 0 0 30px rgba(95, 39, 205, 0.6) !important;
  caret-color: #e80ec7ff !important;
  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  font-weight: 500 !important;
  font-size: 14px !important;
}

/* 优化自动填充下拉菜单项的样式 */
::-webkit-contacts-auto-fill-button,
::-webkit-credentials-auto-fill-button {
  visibility: hidden;
  display: none !important;
  pointer-events: none;
  position: absolute;
  right: 0;
}

/* 优化自动填充下拉菜单样式 */
input::-webkit-contacts-auto-fill-section {
  background-color: transparent !important;
  color: #e80ec7ff !important;
  font-family: inherit !important;
  font-size: 14px !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1), 0 0 20px rgba(232, 14, 199, 0.8) !important;
}

/* 优化自动填充下拉菜单项样式 */
input::-webkit-contacts-auto-fill-strong-password-section {
  background-color: transparent !important;
  color: #e80ec7ff !important;
  font-family: inherit !important;
  font-size: 14px !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1), 0 0 20px rgba(232, 14, 199, 0.8) !important;
}

/* 优化自动填充下拉菜单项悬停样式 */
input::-webkit-contacts-auto-fill-section .item {
  background-color: transparent !important;
  color: #e80ec7ff !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1), 0 0 20px rgba(232, 14, 199, 0.8) !important;
  transition: all 0.3s ease !important;
}

input::-webkit-contacts-auto-fill-section .item:hover {
  background-color: rgba(232, 14, 199, 0.2) !important;
  color: #e80ec7ff !important;
  text-shadow: 0 0 15px rgba(232, 14, 199, 1), 0 0 25px rgba(232, 14, 199, 0.9) !important;
  transform: translateX(5px) !important;
}

/* 优化自动填充下拉菜单滚动条样式 */
input::-webkit-contacts-auto-fill-section::-webkit-scrollbar {
  width: 8px;
}

input::-webkit-contacts-auto-fill-section::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.5);
  border-radius: 4px;
}

input::-webkit-contacts-auto-fill-section::-webkit-scrollbar-thumb {
  background: rgba(232, 14, 199, 0.7);
  border-radius: 4px;
  box-shadow: 0 0 5px rgba(232, 14, 199, 0.5);
}

input::-webkit-contacts-auto-fill-section::-webkit-scrollbar-thumb:hover {
  background: rgba(232, 14, 199, 0.9);
  box-shadow: 0 0 8px rgba(232, 14, 199, 0.8);
}

/* 优化自动填充下拉菜单整体样式 */
input:-webkit-autofill-dropdown {
  background-color: transparent !important;
  border: 1px solid #e80ec7ff !important;
  border-radius: 8px !important;
  box-shadow: 0 0 20px rgba(232, 14, 199, 0.7) !important;
  backdrop-filter: blur(10px) !important;
}

input:-webkit-autofill-dropdown .option {
  background-color: transparent !important;
  color: #e80ec7ff !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1), 0 0 20px rgba(232, 14, 199, 0.8) !important;
  padding: 10px 15px !important;
  transition: all 0.3s ease !important;
  border-radius: 4px !important;
}

input:-webkit-autofill-dropdown .option:hover {
  background-color: rgba(232, 14, 199, 0.2) !important;
  color: #e80ec7ff !important;
  text-shadow: 0 0 15px rgba(232, 14, 199, 1), 0 0 25px rgba(232, 14, 199, 0.9) !important;
  transform: translateX(5px) !important;
  box-shadow: 0 0 10px rgba(232, 14, 199, 0.5) !important;
}

input:-webkit-autofill-dropdown .option.selected {
  background-color: rgba(232, 14, 199, 0.3) !important;
  color: #e80ec7ff !important;
  text-shadow: 0 0 15px rgba(232, 14, 199, 1), 0 0 25px rgba(232, 14, 199, 0.9) !important;
  box-shadow: 0 0 15px rgba(232, 14, 199, 0.7) !important;
}

/* 优化自动填充下拉菜单标题样式 */
input:-webkit-autofill-dropdown .section-title {
  color: #e80ec7ff !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1) !important;
  font-weight: bold !important;
  padding: 8px 15px !important;
  border-bottom: 1px solid rgba(232, 14, 199, 0.3) !important;
}

/* 针对Firefox的自动填充样式 */
input:-moz-autofill,
input:-moz-autofill:hover,
input:-moz-autofill:focus,
input:-moz-autofill:active {
  color: #ffffff !important;
  background-color: transparent !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1), 0 0 20px rgba(232, 14, 199, 0.8), 0 0 30px rgba(95, 39, 205, 0.6) !important;
  caret-color: #e80ec7ff !important;
  box-shadow: none !important;
  font-weight: 500 !important;
  font-size: 14px !important;
}

.register-form-el :deep(.el-input__wrapper:hover) {
  transform: translateY(-3px) scale(1.02);
}

.register-form-el :deep(.el-input__wrapper.is-focus) {
  transform: translateY(-2px) scale(1.01);
}

/* 黑色主题 */
html.dark-theme .register-form-el :deep(.el-input__wrapper:hover) {
  border-color: #e80ec7ff;
  box-shadow: 0 0 15px rgba(232, 14, 199, 0.5);
  background-color: rgba(0, 0, 0, 0.8) !important;
}

html.dark-theme .register-form-el :deep(.el-input__wrapper.is-focus) {
  border-color: #e80ec7ff;
  box-shadow: 0 0 0 3px rgba(232, 14, 199, 0.6) inset, 0 0 20px rgba(232, 14, 199, 0.6);
  background-color: rgba(0, 0, 0, 0.8) !important;
}

/* 白色主题 */
html.light-theme .register-form-el :deep(.el-input__wrapper:hover) {
  border-color: #ff6b9d;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.3);
  background-color: white !important;
}

html.light-theme .register-form-el :deep(.el-input__wrapper.is-focus) {
  border-color: #ff6b9d;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.3) inset, 0 0 20px rgba(255, 107, 157, 0.3);
  background-color: white !important;
}

.custom-input {
  width: 100%;
}

.form-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.form-extra :deep(.el-checkbox__label) {
  color: #ff6b9d;
  text-shadow: 0 0 3px rgba(255, 107, 157, 0.3);
  transition: color 0.3s ease;
}

.form-extra :deep(.el-checkbox__label:hover) {
  color: #ff8fab;
}

.forgot-password {
  font-size: 14px;
  color: #ff6b9d;
  text-shadow: 0 0 3px rgba(255, 107, 157, 0.3);
  transition: all 0.3s ease;
}

.forgot-password:hover {
  color: #ff8fab;
  text-decoration: underline;
  transform: translateY(-1px);
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 20px;
  justify-content: center;
}

.login-btn {
  flex: 1;
  height: 45px;
  font-size: 16px;
  font-weight: bold;
  --el-button-bg-color: transparent;
  --el-button-border-color: #ff6b9d;
  --el-button-text-color: #ff6b9d;
  --el-button-hover-bg-color: rgba(255, 107, 157, 0.1);
  --el-button-hover-border-color: #ff8fab;
  --el-button-hover-text-color: #ff8fab;
  border-radius: 25px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.3);
}

.login-btn::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 107, 157, 0.2), transparent);
  transform: rotate(45deg);
  animation: shine 3s ease-in-out infinite;
}

.login-btn:hover {
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6);
  transform: translateY(-3px) scale(1.02);
  --el-button-text-color: #ff8fab;
  --el-button-border-color: #ff8fab;
}

.login-btn:active {
  transform: translateY(0) scale(0.98);
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.4);
}

.register-btn {
  height: 45px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 25px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  border: none;
  margin-top: 10px;
}

/* 黑色主题 */
html.dark-theme .register-btn {
  background: linear-gradient(45deg, #ff6b9d, #5f27cd);
  color: white;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.5);
}

/* 白色主题 */
html.light-theme .register-btn {
  background: linear-gradient(45deg, #ff6b9d, #5f27cd);
  color: white;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.3);
}

.register-btn::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transform: rotate(45deg);
  animation: shine 3s ease-in-out infinite;
}

@keyframes shine {
  0% {
    transform: translateX(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) rotate(45deg);
  }
}

.register-btn:hover {
  background: linear-gradient(45deg, #ff8fab, #7e47c2);
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.8);
  transform: translateY(-3px) scale(1.05);
  text-shadow: 0 0 15px rgba(255, 255, 255, 0.8);
}

.register-btn:active {
  transform: translateY(0) scale(0.98);
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.4);
}

.send-code-btn {
  width: 100% !important;
  height: 38px !important;
  font-weight: bold;
  --el-button-bg-color: rgba(255, 107, 157, 0.1) !important;
  --el-button-border-color: #ff6b9d !important;
  --el-button-text-color: #ff6b9d !important;
  --el-button-hover-bg-color: rgba(255, 107, 157, 0.2) !important;
  --el-button-hover-border-color: #ff8fab !important;
  --el-button-hover-text-color: #ff8fab !important;
  border-radius: 15px !important;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 8px rgba(255, 107, 157, 0.2);
  font-size: 14px;
  padding: 0 12px !important;
  border: 1px solid #ff6b9d !important;
  background: rgba(255, 107, 157, 0.1) !important;
  color: #ff6b9d !important;
  box-sizing: border-box !important;
  margin: 0 !important;
  line-height: 36px !important;
  vertical-align: middle !important;
}

.send-code-btn::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 107, 157, 0.2), transparent);
  transform: rotate(45deg);
  animation: shine 3s ease-in-out infinite;
}

.send-code-btn:hover {
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.6);
  transform: translateY(-2px) scale(1.02);
  --el-button-text-color: #ff8fab;
  --el-button-border-color: #ff8fab;
}

.send-code-btn:active {
  transform: translateY(0) scale(0.98);
  box-shadow: 0 0 8px rgba(255, 107, 157, 0.4);
}

.divider {
  text-align: center;
  margin: 25px 0;
  position: relative;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #ff6b9d, transparent);
}

.divider span {
  position: relative;
  background: linear-gradient(135deg, #1a0a2e, #2d1b4a);
  padding: 0 20px;
  font-size: 14px;
  color: #ff6b9d;
  text-shadow: 0 0 3px rgba(255, 107, 157, 0.3);
}

.social-login :deep(.el-button) {
  --el-button-bg-color: rgba(255, 107, 157, 0.1);
  --el-button-border-color: #ff6b9d;
  --el-button-text-color: #ff6b9d;
  --el-button-hover-bg-color: rgba(255, 107, 157, 0.2);
  --el-button-hover-border-color: #ff8fab;
  --el-button-hover-text-color: #ff8fab;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.social-login :deep(.el-button:hover) {
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.4);
  transform: translateY(-2px);
}

.social-login {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.social-btn {
  flex: 1;
}

/* 优化 ElMessage 样式，调整提示窗口大小 */
.el-message {
  min-width: unset !important;
  max-width: 90vw !important;
  padding: 8px 16px !important;
  font-size: 14px !important;
  line-height: 1.3 !important;
  width: fit-content !important;
  min-height: unset !important;
  height: auto !important;
}

.el-message__content {
  white-space: normal !important;
  word-break: break-word !important;
  text-align: center !important;
  width: 100% !important;
  display: block !important;
  line-height: 1.3 !important;
  margin: 0 !important;
  padding: 0 !important;
}

/* 全局样式覆盖，确保消息框样式生效 */
body .el-message {
  min-width: unset !important;
  max-width: 90vw !important;
  padding: 8px 16px !important;
  font-size: 14px !important;
  line-height: 1.3 !important;
  width: fit-content !important;
  min-height: unset !important;
  height: auto !important;
}

body .el-message__content {
  white-space: normal !important;
  word-break: break-word !important;
  text-align: center !important;
  line-height: 1.3 !important;
  margin: 0 !important;
  padding: 0 !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
  }
  
  .register-bg {
    width: 100%;
    height: 300px;
  }
  
  .register-form-container {
    width: 100%;
    flex: 1;
    padding: 30px 20px;
    border-left: none;
    border-top: 1px solid #ff6b9d;
    box-shadow: 0 -5px 15px rgba(255, 107, 157, 0.3);
  }
  
  .register-form {
    max-width: 100%;
  }
  
  .brand-name {
    font-size: 28px;
  }
  
  .brand-slogan {
    font-size: 16px;
  }
  
  .button-group {
    flex-direction: column;
  }
  
  .login-btn,
  .register-btn {
    width: 100%;
  }
  
  /* 响应式调整提示窗口大小 */
  .el-message {
    max-width: 90vw !important;
    padding: 8px 14px !important;
  }
  
  body .el-message {
    max-width: 90vw !important;
    padding: 8px 14px !important;
  }
}
</style>