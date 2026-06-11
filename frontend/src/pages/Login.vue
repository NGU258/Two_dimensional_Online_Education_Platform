<template>
  <div class="login-container" style="width: 100vw; max-width: none; margin: 0; padding: 0;">
    <!-- 左侧背景和品牌信息 -->
    <div class="login-bg">
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
    
    <!-- 右侧登录表单 -->
    <div class="login-form-container" ref="formContainerRef">
      <div class="login-form">

        
        <template v-if="!isForgot">
          <div class="login-form-content">
          <div class="form-toggle">
          <el-button 
            type="primary" 
            :plain="loginType !== 'phone'" 
            @click="switchLoginType('phone'); playSound('点击音效1')"
            class="toggle-btn"
          >
            手机号登录
          </el-button>
          <el-button 
            type="primary" 
            :plain="loginType !== 'username'" 
            @click="switchLoginType('username'); playSound('点击音效3')"
            class="toggle-btn"
          >
            账号登录
          </el-button>
          <el-button 
            type="primary" 
            :plain="loginType !== 'email'" 
            @click="switchLoginType('email'); playSound('点击音效2')"
            class="toggle-btn"
          >
            邮箱登录
          </el-button>
        </div>
        

        
        <el-form :model="form" @submit.prevent="handleSubmit" class="login-form-el" label-width="80px" autocomplete="off">
          <!-- 手机号登录表单 -->
          <template v-if="loginType === 'phone'">
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
                  <el-button type="info" class="send-code-btn" :disabled="countdown > 0" @click="sendCode(); playSound('验证码提示音')" style="width: 100%; height: 38px;">{{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}</el-button>
                </el-col>
              </el-row>
            </el-form-item>
          </template>
          
          <!-- 邮箱登录表单 -->
          <template v-else-if="loginType === 'email'">
            <el-form-item label="邮箱" required>
              <el-row :gutter="10" style="width: 100%;">
                <el-col :span="24">
                  <el-input v-model="form.email" type="email" placeholder="请输入邮箱" class="custom-input" style="width: 100%;" autocomplete="email" name="email" clearable @focus="playClickSound">
                  </el-input>
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
          </template>
          
          <!-- 账号登录表单 -->
          <template v-else-if="loginType === 'username'">
            <el-form-item label="账号" required>
              <el-row :gutter="10" style="width: 100%;">
                <el-col :span="24">
                  <el-input v-model="form.username" placeholder="请输入用户名或手机号" class="custom-input" style="width: 100%;" autocomplete="username" name="username" clearable @focus="playClickSound">
                  </el-input>
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
          </template>
          

          
          <el-form-item class="button-group" style="display: flex; justify-content: center;">
            <el-button 
              type="primary" 
              class="login-btn"
              :loading="loading"
              style="width: 200px;"
              @click="handleLogin()"
            >
              登录
            </el-button>
          </el-form-item>
          
          <!-- 记住我和忘记密码部分 -->
          <el-form-item class="form-extra" v-if="loginType === 'email' || loginType === 'username'">
            <el-checkbox v-model="rememberMe">记住我&nbsp;&nbsp;</el-checkbox>
            <el-link type="primary" :underline="'never'" class="forgot-password" @click="openForgotPassword(); playSound('点击音效1')">忘记密码？</el-link>
          </el-form-item>
        </el-form>
          </div>
        </template>
        
        <template v-else>
          <div class="forgot-form-content">
          <div class="form-header">
            <h2>密码找回</h2>
          </div>
          
          <div class="form-toggle forgot-toggle">
            <el-button 
              type="primary" 
              :plain="resetType !== 'phone'" 
              @click="resetType = 'phone'; playSound('点击音效1')"
              class="toggle-btn"
            >
              手机找回
            </el-button>
            <el-button 
              type="primary" 
              :plain="resetType !== 'email'" 
              @click="resetType = 'email'; playSound('点击音效2')"
              class="toggle-btn"
            >
              邮箱找回
            </el-button>
          </div>
          
          <el-form
            :model="resetForm"
            @submit.prevent="handleResetPassword"
            class="login-form-el"
            label-width="80px"
            autocomplete="off"
          >
            <template v-if="resetType === 'phone'">
              <el-form-item label="手机号" required>
                <el-row :gutter="10" style="width: 100%;">
                  <el-col :span="24">
                    <el-input v-model="resetForm.phone" placeholder="请输入手机号" class="custom-input" style="width: 100%;" autocomplete="tel" name="reset-phone" clearable @focus="playClickSound">
                    </el-input>
                  </el-col>
                </el-row>
              </el-form-item>
              
              <el-form-item label="验证码" required>
                <el-row :gutter="10" style="width: 100%;">
                  <el-col :span="16">
                    <el-input v-model="resetForm.code" placeholder="请输入验证码" class="custom-input" style="width: 100%;" autocomplete="one-time-code" name="reset-code-phone" clearable @focus="playClickSound">
                    </el-input>
                  </el-col>
                  <el-col :span="8">
                    <el-button 
                      type="info"
                      class="send-code-btn"
                      :disabled="resetCountdown > 0"
                      @click="sendResetCode(); playSound('验证码提示音')"
                      style="width: 100%; height: 38px;"
                    >
                      {{ resetCountdown > 0 ? `${resetCountdown}秒后重发` : '发送验证码' }}
                    </el-button>
                  </el-col>
                </el-row>
              </el-form-item>
            </template>
            
            <template v-else>
              <el-form-item label="邮箱" required>
                <el-row :gutter="10" style="width: 100%;">
                  <el-col :span="24">
                    <el-input v-model="resetForm.email" type="email" placeholder="请输入邮箱" class="custom-input" style="width: 100%;" autocomplete="email" name="reset-email" clearable @focus="playClickSound">
                    </el-input>
                  </el-col>
                </el-row>
              </el-form-item>
              
              <el-form-item label="验证码" required>
                <el-row :gutter="10" style="width: 100%;">
                  <el-col :span="16">
                    <el-input v-model="resetForm.code" placeholder="请输入验证码" class="custom-input" style="width: 100%;" autocomplete="one-time-code" name="reset-code-email" clearable @focus="playClickSound">
                    </el-input>
                  </el-col>
                  <el-col :span="8">
                    <el-button 
                      type="info"
                      class="send-code-btn"
                      :disabled="resetCountdown > 0"
                      @click="sendResetCode(); playSound('验证码提示音')"
                      style="width: 100%; height: 38px;"
                    >
                      {{ resetCountdown > 0 ? `${resetCountdown}秒后重发` : '发送验证码' }}
                    </el-button>
                  </el-col>
                </el-row>
              </el-form-item>
            </template>
            
            <el-form-item label="新密码" required>
              <el-row :gutter="10" style="width: 100%;">
                <el-col :span="24">
                  <el-input 
                    v-model="resetForm.newPassword" 
                    type="password" 
                    placeholder="请输入新密码" 
                    show-password 
                    class="custom-input" 
                    style="width: 100%;"
                    autocomplete="new-password"
                    name="reset-new-password"
                    clearable
                    @focus="playClickSound"
                  >
                  </el-input>
                </el-col>
              </el-row>
            </el-form-item>
            
            <el-form-item label="确认密码" required>
              <el-row :gutter="10" style="width: 100%;">
                <el-col :span="24">
                  <el-input 
                    v-model="resetForm.confirmPassword" 
                    type="password" 
                    placeholder="请再次输入新密码" 
                    show-password 
                    class="custom-input" 
                    style="width: 100%;"
                    autocomplete="new-password"
                    name="reset-confirm-password"
                    clearable
                    @focus="playClickSound"
                  >
                  </el-input>
                </el-col>
              </el-row>
            </el-form-item>
            
            <el-form-item class="reset-actions-item">
              <div class="reset-actions">
                <el-button
                  type="primary"
                  class="login-btn reset-action-btn"
                  :loading="false"
                  @click="backToLogin(); playSound('点击音效3')"
                >
                  返回
                </el-button>

                <el-button
                  type="primary"
                  class="login-btn reset-action-btn"
                  :loading="resetLoading"
                  @click="playSound('注册音'); handleResetPassword()"
                >
                  确认
                </el-button>
              </div>
            </el-form-item>
          </el-form>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useAudioManager } from '../utils/audioManager'

const { playSound, playBackgroundMusic, stopBackgroundMusic, playClickSound } = useAudioManager()

const axiosInstance = axios.create()

const convertRole = (role) => {
  if (role === 0 || role === '0' || role === 'student') {
    return 'student'
  } else if (role === 1 || role === '1' || role === 'teacher') {
    return 'teacher'
  } else if (role === 2 || role === '2' || role === 'admin') {
    return 'admin'
  }
  return 'student'
}
axiosInstance.defaults.headers.common['Content-Type'] = 'application/json'

const route = useRoute()

const router = useRouter()
const userStore = useUserStore()
const formContainerRef = ref(null)

const loginType = ref('phone')
const loading = ref(false)
const rememberMe = ref(false)
const countdown = ref(0)
const form = ref({
  password: '',
  email: '',
  phone: '',
  code: '',
  username: ''
})

const isForgot = ref(false)
const resetType = ref('phone')
const resetLoading = ref(false)
const resetCountdown = ref(0)
const resetForm = ref({
  phone: '',
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

// 监听路由变化，确保在登录页面时显示登录表单而不是找回密码表单
watch(
  () => route.path,
  (newPath) => {
    if (newPath === '/login') {
      isForgot.value = false
      resetLoading.value = false
      resetCountdown.value = 0
      clearInterval(window.resetCodeTimer)
      resetForm.value = {
        phone: '',
        email: '',
        code: '',
        newPassword: '',
        confirmPassword: ''
      }
    }
  },
  { immediate: true }
)

const musicPlayed = ref(false)

onMounted(() => {
  console.log('Attempting to scroll to top...')
  window.scrollTo({ top: 0, behavior: 'smooth' })
  document.documentElement.scrollTop = 0
  document.body.scrollTop = 0
  
  if (formContainerRef.value) {
    formContainerRef.value.scrollTop = 0
  }
  
  isForgot.value = false
  resetLoading.value = false
  resetCountdown.value = 0
  clearInterval(window.resetCodeTimer)
  resetForm.value = {
    phone: '',
    email: '',
    code: '',
    newPassword: '',
    confirmPassword: ''
  }
  
  const tryAutoPlay = async () => {
    try {
      await playBackgroundMusic('/music/界面纯音/miss.mp3')
      musicPlayed.value = true
      console.log('背景音乐自动播放成功')
    } catch (err) {
      console.log('背景音乐自动播放失败，等待用户交互:', err)
    }
  }
  
  tryAutoPlay()
  
  const handleUserInteraction = async () => {
    if (!musicPlayed.value) {
      await playBackgroundMusic('/music/界面纯音/miss.mp3')
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
  stopBackgroundMusic('/music/界面纯音/miss.mp3')
})

// 监听手机号变化，重置倒计时
watch(() => form.value.phone, () => {
  countdown.value = 0
  // 清除可能存在的定时器
  clearInterval(window.codeTimer)
})

watch(() => resetForm.value.phone, () => {
  resetCountdown.value = 0
  clearInterval(window.resetCodeTimer)
})

watch(() => resetForm.value.email, () => {
  resetCountdown.value = 0
  clearInterval(window.resetCodeTimer)
})

// 切换登录类型并清空表单数据
const switchLoginType = (type) => {
  loginType.value = type
  // 清空表单数据
  switch (type) {
    case 'phone':
      form.value.email = ''
      form.value.password = ''
      form.value.username = ''
      break
    case 'username':
      form.value.email = ''
      form.value.password = ''
      form.value.phone = ''
      form.value.code = ''
      break
    case 'email':
      form.value.phone = ''
      form.value.code = ''
      form.value.username = ''
      form.value.password = ''
      break
  }
}

const handleLogin = async () => {
  playSound('注册音')
  await handleSubmit()
}

const openForgotPassword = () => {
  isForgot.value = true
  resetLoading.value = false
  resetCountdown.value = 0
  clearInterval(window.resetCodeTimer)
  
  resetForm.value = {
    phone: '',
    email: '',
    code: '',
    newPassword: '',
    confirmPassword: ''
  }

  // 默认根据当前登录方式选择重置方式
  if (loginType.value === 'email') {
    resetType.value = 'email'
  } else {
    resetType.value = 'phone'
  }
}

const backToLogin = () => {
  isForgot.value = false
  resetLoading.value = false
  resetCountdown.value = 0
  clearInterval(window.resetCodeTimer)

  resetForm.value = {
    phone: '',
    email: '',
    code: '',
    newPassword: '',
    confirmPassword: ''
  }
}

const handleSubmit = async () => {
  if (loginType.value === 'phone') {
    // 手机号登录逻辑
    if (!form.value.phone || !form.value.code) {
      ElMessage.warning({
        message: '请填写手机号和验证码',
        duration: 1500
      })
      return
    }
    
    // 手机号格式验证，支持带+86前缀
    const phoneRegex = /^(\+86)?1[3-9]\d{9}$/
    if (!phoneRegex.test(form.value.phone)) {
      ElMessage.warning({
        message: '请输入正确的手机号',
        duration: 1500
      })
      return
    }
    
    // 去除+86前缀
    let phone = form.value.phone
    if (phone.startsWith('+86')) {
      phone = phone.substring(3)
    }
    
    loading.value = true
    try {
      const loginResponse = await axios.post('/api/user/login/phone', { phone: phone, code: form.value.code })
      loading.value = false
      if (loginResponse.data.code === 200) {
        const user = loginResponse.data.user
        user.role = convertRole(user.role)
        userStore.token = loginResponse.data.token
        userStore.user = user
        localStorage.setItem('token', loginResponse.data.token)
        localStorage.setItem('user', JSON.stringify(user))
        
        ElMessage.success({
          message: '登录成功',
          duration: 1500
        })
        router.push('/')
      } else {
        // 登录失败，可能是用户未注册
        // 生成随机密码
        const randomPassword = Math.random().toString(36).substring(2, 12)
        // 生成用户名
        const username = '我的刀盾_' + phone 
        // 生成随机邮箱（使用手机号）
        const email = phone + '@qq.com'
        // 自动注册
        const registerResponse = await axios.post('/api/user/register/phone', { phone: phone, code: form.value.code, password: randomPassword, username, email })
        if (registerResponse.data.code === 200) {
          const loginResponse = await axios.post('/api/user/login', { username: phone, password: randomPassword })
          if (loginResponse.data.code === 200) {
            const user = loginResponse.data.user
            user.role = convertRole(user.role)
            userStore.token = loginResponse.data.token
            userStore.user = user
            localStorage.setItem('token', loginResponse.data.token)
            localStorage.setItem('user', JSON.stringify(user))
            
            ElMessage.success({
              message: '登录成功',
              duration: 1500
            })
            router.push('/')
          } else {
            ElMessage.error({
              message: loginResponse.data.message || '登录失败，请稍后重试',
              duration: 1500
            })
          }
        } else {
          ElMessage.error({
            message: registerResponse.data.message || '注册失败，请稍后重试',
            duration: 1500
          })
        }
      }
    } catch (error) {
      loading.value = false
      console.error('Phone login error:', error)
      if (error.response) {
        ElMessage.error({
          message: error.response.data.message || '操作失败，请稍后重试',
          duration: 1500
        })
      } else {
        ElMessage.error({
          message: '网络错误，请稍后重试',
          duration: 1500
        })
      }
    }
  } else if (loginType.value === 'email') {
    // 邮箱登录逻辑
    if (!form.value.email || !form.value.password) {
      ElMessage.warning({
        message: '请填写邮箱和密码',
        duration: 1500
      })
      return
    }
    
    // 邮箱格式验证
    const emailRegex = /^[^@]+@[^@]+\.[^@]+$/
    if (!emailRegex.test(form.value.email)) {
      ElMessage.warning({
        message: '请输入正确的邮箱地址',
        duration: 1500
      })
      return
    }
    
    loading.value = true
    try {
      const response = await axios.post('/api/user/login', { username: form.value.email, password: form.value.password })
      loading.value = false
      if (response.data.code === 200) {
        const user = response.data.user
        user.role = convertRole(user.role)
        userStore.token = response.data.token
        userStore.user = user
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('user', JSON.stringify(user))
        
        ElMessage.success({
          message: '登录成功',
          duration: 1500
        })
        router.push('/')
      } else {
        ElMessage.error({
          message: response.data.message || '登录失败，请检查邮箱和密码',
          duration: 1500
        })
      }
    } catch (error) {
      loading.value = false
      console.error('Email login error:', error)
      if (error.response) {
        ElMessage.error({
          message: error.response.data.message || '操作失败，请稍后重试',
          duration: 1500
        })
      } else {
        ElMessage.error({
          message: '网络错误，请稍后重试',
          duration: 1500
        })
      }
    }
  } else if (loginType.value === 'username') {
    // 账号登录逻辑
    if (!form.value.username || !form.value.password) {
      ElMessage.warning({
        message: '请填写用户名和密码',
        duration: 1500
      })
      return
    }
    loading.value = true
    try {
      const response = await axios.post('/api/user/login', { username: form.value.username, password: form.value.password })
      loading.value = false
      if (response.data.code === 200) {
        const user = response.data.user
        user.role = convertRole(user.role)
        userStore.token = response.data.token
        userStore.user = user
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('user', JSON.stringify(user))
        
        ElMessage.success({
          message: '登录成功',
          duration: 1500
        })
        router.push('/')
      } else {
        ElMessage.error({
          message: response.data.message || '登录失败，请检查用户名和密码',
          duration: 1500
        })
      }
    } catch (error) {
      loading.value = false
      console.error('Username login error:', error)
      if (error.response) {
        ElMessage.error({
          message: error.response.data.message || '操作失败，请稍后重试',
          duration: 1500
        })
      } else {
        ElMessage.error({
          message: '网络错误，请稍后重试',
          duration: 1500
        })
      }
    }
  }
}

const sendCode = async () => {
  if (!form.value.phone) {
    ElMessage.warning({
      message: '请输入手机号',
      duration: 1500
    })
    return
  }
  
  // 手机号格式验证，支持带+86前缀
  const phoneRegex = /^(\+86)?1[3-9]\d{9}$/
  if (!phoneRegex.test(form.value.phone)) {
    ElMessage.warning({
      message: '请输入正确的手机号',
      duration: 1500
    })
    return
  }
  
  // 去除+86前缀
  let phone = form.value.phone
  if (phone.startsWith('+86')) {
    phone = phone.substring(3)
  }
  
  try {
    // 发送验证码到后端
    const response = await fetch('/api/user/send-code', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ phone: phone })
    })
    
    const data = await response.json()
    if (data.code === 200) {
      ElMessage.success({
        message: '验证码已发送',
        duration: 1500
      })
      // 2.5秒后自动填充验证码
      if (data.data) {
        setTimeout(() => {
          form.value.code = data.data
        }, 2500)
      }
      // 开始倒计时
      startCountdown()
    } else {
      ElMessage.error({
        message: data.message || '发送验证码失败',
        duration: 1500
      })
    }
  } catch (error) {
    console.error('发送验证码错误:', error)
    ElMessage.error({
      message: '网络错误，请稍后重试',
      duration: 1500
    })
  }
}

const startCountdown = () => {
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
}

const sendEmailCode = async () => {
  if (!form.value.email) {
    ElMessage.warning({
      message: '请输入邮箱',
      duration: 1500
    })
    return
  }
  
  // 邮箱格式验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(form.value.email)) {
    ElMessage.warning({
      message: '请输入正确的邮箱地址',
      duration: 1500
    })
    return
  }
  
  try {
    // 发送邮箱验证码到后端
    const response = await fetch('/api/user/send-email-code', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email: form.value.email })
    })
    
    const data = await response.json()
    if (data.code === 200) {
      ElMessage.success({
        message: '邮箱验证码已发送',
        duration: 1500
      })
      // 开始倒计时
      startCountdown()
    } else {
      ElMessage.error({
        message: data.message || '发送邮箱验证码失败',
        duration: 1500
      })
    }
  } catch (error) {
    console.error('发送邮箱验证码错误:', error)
    ElMessage.error({
      message: '网络错误，请稍后重试',
      duration: 1500
    })
  }
}




const startResetCountdown = () => {
  resetCountdown.value = 60
  clearInterval(window.resetCodeTimer)
  window.resetCodeTimer = setInterval(() => {
    resetCountdown.value--
    if (resetCountdown.value <= 0) {
      clearInterval(window.resetCodeTimer)
    }
  }, 1000)
}

const sendResetCode = async () => {
  if (resetType.value === 'phone') {
    if (!resetForm.value.phone) {
      ElMessage.warning({
        message: '请输入手机号',
        duration: 1500
      })
      return
    }

    const phoneRegex = /^(\+86)?1[3-9]\d{9}$/
    if (!phoneRegex.test(resetForm.value.phone)) {
      ElMessage.warning({
        message: '请输入正确的手机号',
        duration: 1500
      })
      return
    }

    let phone = resetForm.value.phone
    if (phone.startsWith('+86')) {
      phone = phone.substring(3)
    }

    try {
      const response = await axiosInstance.post('/api/user/send-code', { phone: phone })
      if (response.data.code === 200) {
        ElMessage.success({
          message: '验证码已发送',
          duration: 1500
        })
        if (response.data.data) {
          setTimeout(() => {
            resetForm.value.code = response.data.data
          }, 2500)
        }
        startResetCountdown()
      } else {
        ElMessage.error({
          message: response.data.message || '发送验证码失败',
          duration: 1500
        })
      }
    } catch (error) {
      console.error('Send reset code (phone) error:', error)
      ElMessage.error({
        message: '网络错误，请稍后重试',
        duration: 1500
      })
    }
  } else {
    if (!resetForm.value.email) {
      ElMessage.warning({
        message: '请输入邮箱',
        duration: 1500
      })
      return
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(resetForm.value.email)) {
      ElMessage.warning({
        message: '请输入正确的邮箱地址',
        duration: 1500
      })
      return
    }

    try {
      const email = resetForm.value.email.trim().toLowerCase()
      const response = await axiosInstance.post('/api/user/send-email-code', { email: email })
      if (response.data.code === 200) {
        ElMessage.success({
          message: '邮箱验证码已发送',
          duration: 1500
        })
        if (response.data.data) {
          setTimeout(() => {
            resetForm.value.code = response.data.data
          }, 2500)
        }
        startResetCountdown()
      } else {
        ElMessage.error({
          message: response.data.message || '发送邮箱验证码失败',
          duration: 1500
        })
      }
    } catch (error) {
      console.error('Send reset code (email) error:', error)
      ElMessage.error({
        message: '网络错误，请稍后重试',
        duration: 1500
      })
    }
  }
}

const handleResetPassword = async () => {
  if (!resetForm.value.code || !resetForm.value.newPassword || !resetForm.value.confirmPassword) {
    ElMessage.warning({
      message: '请填写验证码与新密码',
      duration: 1500
    })
    return
  }

  if (resetForm.value.newPassword !== resetForm.value.confirmPassword) {
    ElMessage.warning({
      message: '两次输入的密码不一致',
      duration: 1500
    })
    return
  }

  if (resetForm.value.newPassword.length < 6) {
    ElMessage.warning({
      message: '新密码至少 6 位',
      duration: 1500
    })
    return
  }

  if (resetType.value === 'phone') {
    if (!resetForm.value.phone) {
      ElMessage.warning({
        message: '请输入手机号',
        duration: 1500
      })
      return
    }
    const phoneRegex = /^(\+86)?1[3-9]\d{9}$/
    if (!phoneRegex.test(resetForm.value.phone)) {
      ElMessage.warning({
        message: '请输入正确的手机号',
        duration: 1500
      })
      return
    }
  } else {
    if (!resetForm.value.email) {
      ElMessage.warning({
        message: '请输入邮箱',
        duration: 1500
      })
      return
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(resetForm.value.email)) {
      ElMessage.warning({
        message: '请输入正确的邮箱地址',
        duration: 1500
      })
      return
    }
  }

  resetLoading.value = true
  try {
    if (resetType.value === 'phone') {
      let phone = resetForm.value.phone
      if (phone.startsWith('+86')) {
        phone = phone.substring(3)
      }
      const response = await axiosInstance.post('/api/user/reset-password/phone', {
        phone: phone,
        code: resetForm.value.code,
        newPassword: resetForm.value.newPassword
      })

      if (response.data.code === 200) {
        ElMessage.success({
          message: '密码重置成功，请使用“账号登录/邮箱登录”进行登录',
          duration: 2000
        })
        backToLogin()
        switchLoginType('username')
      } else {
        ElMessage.error({
          message: response.data.message || '重置失败，请稍后重试',
          duration: 1500
        })
      }
    } else {
      const email = resetForm.value.email.trim().toLowerCase()
      const response = await axiosInstance.post('/api/user/reset-password/email', {
        email: email,
        code: resetForm.value.code,
        newPassword: resetForm.value.newPassword
      })

      if (response.data.code === 200) {
        ElMessage.success({
          message: '密码重置成功，快去登录吧喵',
          duration: 2000
        })
        backToLogin()
        switchLoginType('email')
        form.value.email = email
      } else {
        ElMessage.error({
          message: response.data.message || '重置失败，请稍后重试',
          duration: 1500
        })
      }
    }
  } catch (error) {
    console.error('Reset password error:', error)
    ElMessage.error({
      message: error.response?.data?.message || '网络错误，请稍后重试',
      duration: 1500
    })
  } finally {
    resetLoading.value = false
  }
}

</script>

<style scoped>
.login-container {
  display: flex;
  min-height: 80vh;
  width: 100%;
  overflow: hidden;
  position: relative;
}

/* 左侧背景 */
.login-bg {
  flex: 1.8;
  background-image: url('/images/6.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  min-width: 0;
  overflow: hidden;
}

/* 黑色主题 */
html.dark-theme .login-bg {
  background-color: black;
}

/* 白色主题 */
html.light-theme .login-bg {
  background-color: #f0f0f0;
}

.login-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  animation: glowShift 8s ease-in-out infinite;
}

/* 黑色主题 */
html.dark-theme .login-bg::before {
  background: 
    linear-gradient(to right, 
      rgba(0, 0, 0, 0.7),   /* 左边：深黑 */
      rgba(0, 0, 0, 0) 60%, /* 中间：近乎透明/亮色 */
      rgba(0, 0, 0, 0) ),
    linear-gradient(to bottom, 
      rgba(0, 0, 0, 0.6) 4%,   
      rgba(0, 0, 0, 0) , 
      rgba(0, 0, 0, 0) ),
    /* 二次元风格发光效果 */
    radial-gradient(circle at 50% 50%, rgba(255, 107, 157, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 20% 80%, rgba(64, 196, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(95, 39, 205, 0.1) 0%, transparent 50%);
}

/* 白色主题 */
html.light-theme .login-bg::before {
  background: 
    linear-gradient(to right, 
      rgba(255, 255, 255, 0.9),   /* 左边：白色 */
      rgba(255, 255, 255, 0.1) 50%, /* 中间：近乎透明 */
      rgba(255, 255, 255, 0.9) ),
    linear-gradient(to bottom, 
      rgba(255, 255, 255, 0.6) 4%,   
      rgba(255, 255, 255, 0) , 
      rgba(255, 255, 255, 0) ),
    /* 二次元风格发光效果 */
    radial-gradient(circle at 50% 50%, rgba(255, 107, 157, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 20% 80%, rgba(64, 196, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(95, 39, 205, 0.1) 0%, transparent 50%);
}

@keyframes glowShift {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.bg-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #40c4ff;
  max-width: 800px; 
  padding: 0 40px;
  margin-top:10px; /* 调整内容的垂直位置，使其显示在图片上方 */
}

/* 二次元风格装饰元素 */
.login-bg::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    /* 顶部装饰线 */
    linear-gradient(to right, transparent, rgba(255, 107, 157, 0), transparent) top / 100% 2px no-repeat,
    /* 底部装饰线 */
    linear-gradient(to right, transparent, rgba(64, 196, 255, 0), transparent) bottom / 100% 2px no-repeat,
    /* 左侧装饰线 */
    linear-gradient(to bottom, transparent, rgba(95, 39, 205, 0), transparent) left / 2px 100% no-repeat,
    /* 右侧装饰线 */
    linear-gradient(to bottom, transparent, rgba(255, 107, 157, 0.3), transparent) right / 2px 100% no-repeat;
  pointer-events: none;
  animation: borderGlow 4s ease-in-out infinite;
}

@keyframes borderGlow {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 1;
  }
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
  text-shadow: 0 0 2px rgba(255, 192, 203, 0.8);
  line-height: 1.8;
  margin-top: 400px;
  margin-bottom: 40px;
  opacity: 0.8;
  text-align: center;
  max-width: 600px;
}

/* 右侧登录表单 */
.login-form-container {
  flex: 1.4;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 60px 40px 10px 40px;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  position: relative;
  border-left: none;
}

/* 黑色主题 */
html.dark-theme .login-form-container {
  background-color: black;
  box-shadow: -5px 0 15px rgba(255, 107, 157, 0.3);
}

/* 白色主题 */
html.light-theme .login-form-container {
  background-color: white;
  box-shadow: -5px 0 15px rgba(0, 0, 0, 0.1);
}

/* 登录表单内容容器 - 可独立调整位置 */
.login-form-content {
  width: 100%;
  padding: 20px 0;
  /* 可以在这里调整登录表单的位置 */
  margin-top: 10px;
}

/* 忘记密码表单内容容器 - 可独立调整位置 */
.forgot-form-content {
  width: 100%;
  padding: 20px 0;
  /* 可以在这里调整忘记密码表单的位置 */
  margin-top: -30px;
}

/* 中间分隔线 */
.login-form-container::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: linear-gradient(to bottom, transparent, #ff6b9d, #5f27cd, transparent);
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.8);
  animation: lineGlow 3s ease-in-out infinite;
}

@keyframes lineGlow {
  0%, 100% {
    opacity: 0.6;
    box-shadow: 0 0 10px rgba(255, 107, 157, 0.8);
  }
  50% {
    opacity: 1;
    box-shadow: 0 0 20px rgba(255, 107, 157, 1);
  }
}

/* 装饰元素 */
.login-form-container::before {
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

.login-form-container::-webkit-scrollbar {
  display: none;
}

.login-form {
  width: 100%;
  max-width: 350px;
  min-height: 400px; /* 设置固定的最小高度，确保布局稳定 */
  display: flex;
  flex-direction: column;
  margin-top: 10px; /* 调整表单的垂直位置 */
}

.form-header {
  margin-bottom: 25px;
  text-align: center;
}

.form-header h2 {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 10px;
  letter-spacing: 2px;
  position: relative;
  
  /* 渐变文字 + 霓虹发光 */
  color: transparent;
  background: linear-gradient(90deg, #ff6b9d, #5f27cd, #48dbfb, #40c4ff, #ff6b9d);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  background-clip: text;
  text-shadow: none;
  
  animation: titleGradientShift 4s ease infinite, titleGlow 2s ease-in-out infinite, titleFloat 3s ease-in-out infinite;
}

.form-header p {
  font-size: 14px;
  color: #81d4fa;
}

.form-header h2::after {
  content: '';
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  bottom: -10px;
  width: 140px;
  height: 3px;
  border-radius: 999px;
  background: linear-gradient(90deg, #ff6b9d, #5f27cd, #48dbfb);
  box-shadow: 0 0 18px rgba(232, 14, 199, 0.55), 0 0 22px rgba(64, 196, 255, 0.35);
  opacity: 0.95;
  animation: underlinePulse 2s ease-in-out infinite;
  pointer-events: none;
}

@keyframes titleGradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes titleGlow {
  0%, 100% {
    filter: drop-shadow(0 0 6px rgba(232, 14, 199, 0.35)) drop-shadow(0 0 14px rgba(95, 39, 205, 0.25));
    opacity: 0.95;
  }
  50% {
    filter: drop-shadow(0 0 10px rgba(255, 107, 157, 0.6)) drop-shadow(0 0 22px rgba(64, 196, 255, 0.35));
    opacity: 1;
  }
}

@keyframes titleFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-2px);
  }
}

@keyframes underlinePulse {
  0%, 100% {
    transform: translateX(-50%) scaleX(0.95);
    opacity: 0.75;
  }
  50% {
    transform: translateX(-50%) scaleX(1);
    opacity: 1;
  }
}

.form-toggle {
  display: flex;
  margin-bottom: 30px;
  border-bottom: 1px solid #333;
}

.toggle-btn {
  flex: 1;
  border-radius: 15px 15px 0 0;
  border-bottom: 3px solid transparent;
  --el-button-text-color: #e80ec7ff;
  transition: all 0.3s ease;
  font-weight: bold;
  position: relative;
  overflow: hidden;
  padding: 12px 0;
  font-size: 16px;
}

.toggle-btn::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(232, 14, 199, 0.2), transparent);
  transform: rotate(45deg);
  animation: shine 3s ease-in-out infinite;
  z-index: 1;
  pointer-events: none;
}

.toggle-btn:hover {
  --el-button-text-color: #f06292;
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(232, 14, 199, 0.4);
}

.toggle-btn.is-plain {
  border-bottom-color: transparent;
  --el-button-bg-color: transparent;
  --el-button-border-color: #e80ec7ff;
  --el-button-text-color: #81d4fa;
  text-shadow: 0 0 3px rgba(64, 196, 255, 0.3);
  opacity: 0.7;
}

.toggle-btn:not(.is-plain) {
  border-bottom-color: #e80ec7ff;
  --el-button-bg-color: linear-gradient(135deg, rgba(232, 14, 199, 0.2), rgba(95, 39, 205, 0.2));
  --el-button-border-color: #e80ec7ff;
  box-shadow: 0 0 20px rgba(232, 14, 199, 0.7);
  text-shadow: 0 0 8px rgba(232, 14, 199, 0.8);
  --el-button-text-color: #ffffff;
  transform: translateY(-2px);
}

.toggle-btn span {
  position: relative;
  z-index: 2;
}

.login-form-el {
  margin-bottom: 20px;
}

.login-form-el :deep(.el-form-item__label) {
  font-weight: bold;
}

.login-form-el :deep(.el-input__wrapper) {
  border-radius: 15px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

/* 黑色主题 */
html.dark-theme .login-form-el :deep(.el-form-item__label) {
  color: #e80ec7ff;
  text-shadow: 0 0 2px rgba(232, 14, 199, 0.8);
}

html.dark-theme .login-form-el :deep(.el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.8);
  border-color: #e80ec7ff;
  --el-input-text-color: #e80ec7ff;
  --el-input-placeholder-color: rgba(11, 215, 222, 1);
  box-shadow: 0 0 8px rgba(232, 14, 199, 0.2);
}

/* 白色主题 */
html.light-theme .login-form-el :deep(.el-form-item__label) {
  color: #333;
  text-shadow: none;
}

html.light-theme .login-form-el :deep(.el-input__wrapper) {
  background-color: white;
  border-color: #ccc;
  --el-input-text-color: #333;
  --el-input-placeholder-color: #999;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}

.login-form-el :deep(.el-input__placeholder) {
  font-weight: bold;
}

.login-form-el :deep(.el-input__wrapper::before) {
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

.login-form-el :deep(.el-input__inner) {
  position: relative;
  z-index: 2;
  font-weight: bold;
  font-size: 16px !important;
  background-color: transparent !important;
  border: none !important;
  outline: none !important;
}

/* 优化自动填充样式 */
.login-form-el :deep(.el-input__inner:-webkit-autofill) {
  -webkit-background-clip: text;
  transition: background-color 5000s ease-in-out 0s !important;
  font-weight: 500 !important;
  font-size: 14px !important;
}

.login-form-el :deep(.el-input__inner:-webkit-autofill:hover),
.login-form-el :deep(.el-input__inner:-webkit-autofill:focus) {
  -webkit-background-clip: text;
}

/* 黑色主题 */
html.dark-theme .login-form-el :deep(.el-input__placeholder) {
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.8), 0 0 20px rgba(255, 107, 157, 0.5);
}

html.dark-theme .login-form-el :deep(.el-input__wrapper::before) {
  background: linear-gradient(45deg, transparent, rgba(255, 107, 157, 0.2), transparent);
}

html.dark-theme .login-form-el :deep(.el-input__inner) {
  color: #e80ec7ff !important;
  text-shadow: 0 0 15px rgba(232, 14, 199, 1), 0 0 30px rgba(232, 14, 199, 0.8), 0 0 45px rgba(95, 39, 205, 0.6) !important;
  caret-color: #e80ec7ff !important;
}

html.dark-theme .login-form-el :deep(.el-input__inner:-webkit-autofill) {
  -webkit-text-fill-color: #ffffff !important;
  background-color: rgba(0, 0, 0, 0.8) !important;
  text-shadow: 0 0 10px rgba(232, 14, 199, 1), 0 0 20px rgba(232, 14, 199, 0.8), 0 0 30px rgba(95, 39, 205, 0.6) !important;
  caret-color: #e80ec7ff !important;
}

html.dark-theme .login-form-el :deep(.el-input__inner:-webkit-autofill:hover),
html.dark-theme .login-form-el :deep(.el-input__inner:-webkit-autofill:focus) {
  -webkit-text-fill-color: #ffffff !important;
}

/* 白色主题 */
html.light-theme .login-form-el :deep(.el-input__placeholder) {
  text-shadow: none;
}

html.light-theme .login-form-el :deep(.el-input__wrapper::before) {
  background: linear-gradient(45deg, transparent, rgba(0, 0, 0, 0.1), transparent);
}

html.light-theme .login-form-el :deep(.el-input__inner) {
  color: #333 !important;
  text-shadow: none !important;
  caret-color: #333 !important;
}

html.light-theme .login-form-el :deep(.el-input__inner:-webkit-autofill) {
  -webkit-text-fill-color: #333 !important;
  background-color: white !important;
  text-shadow: none !important;
  caret-color: #333 !important;
}

html.light-theme .login-form-el :deep(.el-input__inner:-webkit-autofill:hover),
html.light-theme .login-form-el :deep(.el-input__inner:-webkit-autofill:focus) {
  -webkit-text-fill-color: #333 !important;
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

/* 确保输入框占位符样式 */
.login-form-el :deep(.el-input__placeholder) {
  color: rgba(11, 215, 222, 1) !important;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.8), 0 0 20px rgba(232, 14, 199, 0.5) !important;
  font-weight: 500 !important;
  font-size: 14px !important;
}

.login-form-el :deep(.el-input__wrapper:hover) {
  border-color: #e80ec7ff;
  box-shadow: 0 0 10px rgba(232, 14, 199, 0.3);
  transform: translateY(-1px) scale(1.01);
  background-color: rgba(0, 0, 0, 0.8) !important;
}

.login-form-el :deep(.el-input__wrapper.is-focus) {
  border-color: #e80ec7ff;
  box-shadow: 0 0 0 2px rgba(232, 14, 199, 0.4) inset, 0 0 10px rgba(232, 14, 199, 0.3);
  background-color: rgba(0, 0, 0, 0.8) !important;
  transform: translateY(-1px) scale(1.01);
}

.custom-input {
  width: 100%;
}

.form-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding: 0 5px;
}

.form-extra :deep(.el-checkbox__label) {
  color: #e80ec7ff;
  text-shadow: 0 0 2px rgba(232, 14, 199, 0.8);
  transition: all 0.3s ease;
  font-weight: bold;
  font-size: 14px;
  margin-right: 10px;
}

.form-extra :deep(.el-checkbox__label:hover) {
  color: #f06292;
  text-shadow: 0 0 3px rgba(240, 98, 146, 0.9);
}

.forgot-password {
  font-size: 14px;
  color: #e80ec7ff;
  text-shadow: 0 0 2px rgba(232, 14, 199, 0.8);
  transition: all 0.3s ease;
  font-weight: bold;
}

.forgot-password:hover {
  color: #f06292;
  text-shadow: 0 0 3px rgba(240, 98, 146, 0.9);
  text-decoration: underline;
  transform: translateY(-1px);
}

.forgot-toggle {
  margin-bottom: 20px;
  width: 100%;
  /* 强制等分按钮布局，避免由于不同状态导致视觉高度/宽度不一致 */
  display: flex;
  align-items: stretch;
}

.forgot-toggle .toggle-btn {
  flex: 1 1 0;
  min-width: 0;
  height: 35px;
  white-space: nowrap;
}

.reset-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  width: 100%;
}

.reset-actions .reset-action-btn {
  min-width: 0;
  width: 110px;
  height: 35px;
  margin-top: 0;
}

.reset-actions-item :deep(.el-form-item__content) {
  display: flex;
  justify-content: center;
  width: 100% !important;
  margin-left: 0 !important; /* 消除 Element Plus 由于 label-width 产生的左侧偏移 */
}

.reset-actions-item :deep(.el-form-item__label) {
  width: 0 !important;
  padding: 0 !important;
  margin: 0 !important;
  display: none;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 20px;
  justify-content: center;
}

.register-btn {
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

.register-btn::before {
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

.register-btn:hover {
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6);
  transform: translateY(-3px) scale(1.02);
  --el-button-text-color: #ff8fab;
  --el-button-border-color: #ff8fab;
}

.register-btn:active {
  transform: translateY(0) scale(0.98);
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.4);
}

.login-btn {
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
html.dark-theme .login-btn {
  background: linear-gradient(45deg, #e80ec7ff, #5f27cd);
  color: white;
  text-shadow: 0 0 15px rgba(255, 255, 255, 0.8), 0 0 25px rgba(232, 14, 199, 0.6);
  box-shadow: 0 0 15px rgba(232, 14, 199, 0.5);
}

/* 白色主题 */
html.light-theme .login-btn {
  background: linear-gradient(45deg, #ff6b9d, #5f27cd);
  color: white;
  text-shadow: 0 0 15px rgba(255, 255, 255, 0.8), 0 0 25px rgba(255, 107, 157, 0.6);
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.3);
}

.login-btn::before {
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

.login-btn:hover {
  transform: translateY(-3px) scale(1.05);
  text-shadow: 0 0 15px rgba(255, 255, 255, 0.8);
}

.login-btn:active {
  transform: translateY(0) scale(0.98);
}

/* 黑色主题 */
html.dark-theme .login-btn:hover {
  background: linear-gradient(45deg, #ff8fab, #7e47c2);
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.8);
}

html.dark-theme .login-btn:active {
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.4);
}

/* 白色主题 */
html.light-theme .login-btn:hover {
  background: linear-gradient(45deg, #ff8fab, #7e47c2);
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.5);
}

html.light-theme .login-btn:active {
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.3);
}

.send-code-btn {
  width: 100% !important;
  height: 38px !important;
  font-weight: bold;
  border-radius: 15px !important;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  font-size: 14px;
  padding: 0 12px !important;
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
  transform: rotate(45deg);
  animation: shine 3s ease-in-out infinite;
}

.send-code-btn:hover {
  transform: translateY(-2px) scale(1.02);
}

.send-code-btn:active {
  transform: translateY(0) scale(0.98);
}

/* 黑色主题 */
html.dark-theme .send-code-btn {
  --el-button-bg-color: rgba(232, 14, 199, 0.1) !important;
  --el-button-border-color: #e80ec7ff !important;
  --el-button-text-color: #e80ec7ff !important;
  --el-button-hover-bg-color: rgba(232, 14, 199, 0.2) !important;
  --el-button-hover-border-color: #f06292 !important;
  --el-button-hover-text-color: #f06292 !important;
  box-shadow: 0 0 8px rgba(232, 14, 199, 0.2);
  border: 1px solid #e80ec7ff !important;
  background: rgba(232, 14, 199, 0.1) !important;
  color: #e80ec7ff !important;
}

html.dark-theme .send-code-btn::before {
  background: linear-gradient(45deg, transparent, rgba(255, 107, 157, 0.2), transparent);
}

html.dark-theme .send-code-btn:hover {
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.6);
  --el-button-text-color: #ff8fab;
  --el-button-border-color: #ff8fab;
}

html.dark-theme .send-code-btn:active {
  box-shadow: 0 0 8px rgba(255, 107, 157, 0.4);
}

/* 白色主题 */
html.light-theme .send-code-btn {
  --el-button-bg-color: rgba(255, 107, 157, 0.1) !important;
  --el-button-border-color: #ff6b9d !important;
  --el-button-text-color: #333 !important;
  --el-button-hover-bg-color: rgba(255, 107, 157, 0.2) !important;
  --el-button-hover-border-color: #ff8fab !important;
  --el-button-hover-text-color: #333 !important;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #ff6b9d !important;
  background: rgba(255, 107, 157, 0.1) !important;
  color: #333 !important;
}

html.light-theme .send-code-btn::before {
  background: linear-gradient(45deg, transparent, rgba(0, 0, 0, 0.1), transparent);
}

html.light-theme .send-code-btn:hover {
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.3);
  --el-button-text-color: #333;
  --el-button-border-color: #ff8fab;
}

html.light-theme .send-code-btn:active {
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
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

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .login-bg {
    width: 100%;
    height: 300px;
  }
  
  .login-form-container {
    width: 100%;
    flex: 1;
    padding: 30px 20px;
    border-left: none;
    border-top: 1px solid #ff6b9d;
    box-shadow: 0 -5px 15px rgba(255, 107, 157, 0.3);
  }
  
  .login-form {
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
  
  .register-btn,
  .login-btn {
    width: 100%;
  }
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

/* 响应式调整提示窗口大小 */
@media (max-width: 768px) {
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