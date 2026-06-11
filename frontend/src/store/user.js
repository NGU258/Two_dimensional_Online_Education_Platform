import { defineStore } from 'pinia'
import axios from 'axios'

function convertRole(role) {
  if (role === 0 || role === '0' || role === 'student') {
    return 'student'
  } else if (role === 1 || role === '1' || role === 'teacher') {
    return 'teacher'
  } else if (role === 2 || role === '2' || role === 'admin') {
    return 'admin'
  }
  return 'student'
}

export const useUserStore = defineStore('user', {
  state: () => {
    const storedUser = localStorage.getItem('user')
    let user = null
    if (storedUser) {
      try {
        user = JSON.parse(storedUser)
        user.role = convertRole(user.role)
      } catch (e) {
        user = null
      }
    }
    return {
      user,
      token: localStorage.getItem('token') || null
    }
  },
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    async login(username, password) {
      try {
        const response = await axios.post('/api/user/login', { username, password })
        if (response.data.code === 200) {
          this.token = response.data.token
          localStorage.setItem('token', response.data.token)
          await this.getUserInfo()
          return { success: true }
        } else {
          return { success: false, message: response.data.message || '登录失败' }
        }
      } catch (error) {
        console.error('Login error:', error)
        return { success: false, message: error.response?.data?.message || '网络错误，请稍后重试' }
      }
    },
    
    async register(username, password, email) {
      try {
        const response = await axios.post('/api/user/register', { username, password, email })
        if (response.data.code === 200) {
          // 注册成功后自动登录
          if (response.data.token && response.data.user) {
            this.token = response.data.token
            this.user = response.data.user
            localStorage.setItem('token', response.data.token)
            localStorage.setItem('user', JSON.stringify(response.data.user))
          }
          return { success: true }
        } else {
          return { success: false, message: response.data.message || '注册失败' }
        }
      } catch (error) {
        console.error('Register error:', error)
        return { success: false, message: error.response?.data?.message || '网络错误，请稍后重试' }
      }
    },
    async getUserInfo() {
      try {
        const response = await axios.get('/api/user/info')
        if (response.data.code === 200) {
          const user = response.data.data
          user.role = convertRole(user.role)
          this.user = user
          localStorage.setItem('user', JSON.stringify(user))
          return { success: true }
        } else {
          return { success: false, message: response.data.message || '获取用户信息失败' }
        }
      } catch (error) {
        console.error('Get user info error:', error)
        return { success: false, message: error.response?.data?.message || '网络错误，请稍后重试' }
      }
    },
    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
    async loginByPhone(phone, code) {
      try {
        const response = await axios.post('/api/user/login/phone', { phone, code })
        if (response.data.code === 200) {
          this.token = response.data.token
          const user = response.data.user
          user.role = convertRole(user.role)
          this.user = user
          localStorage.setItem('token', response.data.token)
          localStorage.setItem('user', JSON.stringify(user))
          return { success: true }
        } else {
          return { success: false, message: response.data.message || '登录失败' }
        }
      } catch (error) {
        console.error('Phone login error:', error)
        return { success: false, message: error.response?.data?.message || '网络错误，请稍后重试' }
      }
    },
    async registerByPhone(phone, code, password, username, email, role = 'student') {
      try {
        console.log('Sending register request with params:', { phone, code, password, username, email, role })
        const response = await axios.post('/api/user/register/phone', { phone, code, password, username, email, role })
        console.log('Register response:', response)
        console.log('Register response data:', response.data)
        
        if (response.data && typeof response.data === 'object') {
          if (response.data.code === 200) {
            if (response.data.token && response.data.user) {
              const user = response.data.user
              user.role = convertRole(user.role)
              this.token = response.data.token
              this.user = user
              localStorage.setItem('token', response.data.token)
              localStorage.setItem('user', JSON.stringify(user))
            }
            return { success: true }
          } else {
            // 确保获取正确的错误信息
            let errorMessage = '注册失败'
            if (response.data.message) {
              errorMessage = response.data.message
            } else if (response.data.error) {
              errorMessage = response.data.error
            }
            console.log('Register error message:', errorMessage)
            return { success: false, message: errorMessage }
          }
        } else if (response.data) {
          // 如果data是字符串，直接使用
          console.log('Register error message (string):', response.data)
          return { success: false, message: response.data }
        } else {
          console.error('Invalid response data:', response.data)
          return { success: false, message: '服务器返回数据格式错误' }
        }
      } catch (error) {
        console.error('Phone register error:', error)
        if (error.response) {
          console.log('Error response status:', error.response.status)
          console.log('Error response data:', error.response.data)
          
          // 尝试从error.response.data获取message
          if (error.response.data) {
            if (typeof error.response.data === 'object' && error.response.data.message) {
              console.log('Error message from response.data.message:', error.response.data.message)
              return { success: false, message: error.response.data.message }
            } else if (typeof error.response.data === 'string') {
              console.log('Error message from response.data (string):', error.response.data)
              return { success: false, message: error.response.data }
            } else {
              // 尝试将对象转换为字符串
              try {
                const errorString = JSON.stringify(error.response.data)
                console.log('Error message from stringified response.data:', errorString)
                return { success: false, message: errorString }
              } catch (e) {
                console.error('Failed to stringify error response:', e)
              }
            }
          }
        }
        return { success: false, message: '网络错误，请稍后重试' }
      }
    },
    updateUserAvatar(avatar) {
      if (this.user) {
        this.user.avatar = avatar
        localStorage.setItem('user', JSON.stringify(this.user))
        // 尝试异步更新到后端
        this.updateUserInfo({ avatar })
      }
    },
    async updateUserInfo(userInfo) {
      try {
        const response = await axios.put('/api/user/update', userInfo)
        if (response.data.code === 200) {
          // 更新本地用户信息
          if (this.user) {
            this.user = { ...this.user, ...userInfo }
            localStorage.setItem('user', JSON.stringify(this.user))
          }
          return true
        }
        return false
      } catch (error) {
        console.error('Update user info error:', error)
        return false
      }
    },
    
    // 收藏相关方法
    getCollections() {
      const collections = localStorage.getItem('collections')
      return collections ? JSON.parse(collections) : []
    },

    addCollection(course) {
      console.log('添加收藏:', course)
      const collections = this.getCollections()
      // 检查是否已经收藏
      const exists = collections.some(item => item.id === course.id)
      if (!exists) {
        collections.push(course)
        localStorage.setItem('collections', JSON.stringify(collections))
        console.log('收藏成功，当前收藏列表:', collections)
        return true
      }
      console.log('课程已经收藏过了')
      return false
    },

    removeCollection(courseId) {
      console.log('取消收藏，课程ID:', courseId)
      const collections = this.getCollections()
      const newCollections = collections.filter(item => item.id !== courseId)
      if (newCollections.length !== collections.length) {
        localStorage.setItem('collections', JSON.stringify(newCollections))
        console.log('取消收藏成功，当前收藏列表:', newCollections)
        return true
      }
      console.log('课程不在收藏列表中')
      return false
    },

    isCollected(courseId) {
      console.log('检查收藏状态，课程ID:', courseId)
      const collections = this.getCollections()
      const result = collections.some(item => item.id === courseId)
      console.log('收藏状态:', result)
      return result
    }
  }
})
