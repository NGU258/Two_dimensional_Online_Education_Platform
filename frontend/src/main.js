import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import axios from 'axios'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { ElMessage } from 'element-plus'
import './assets/element-plus-override.css'

// 配置Element Plus主题
const app = createApp(App)
const pinia = createPinia()

// 全局配置Element Plus样式
app.config.globalProperties.$ELEMENT = {
  size: 'default',
  zIndex: 3000
}

// 路由动态导入错误处理 - 当缓存失效时自动刷新
router.onError((error) => {
  const pattern = /Loading chunk (\d)+ failed/g
  const isChunkLoadFailed = error.message.match(pattern)
  
  if (isChunkLoadFailed) {
    console.warn('检测到模块加载失败，可能是缓存问题，正在尝试刷新...')
    
    // 清除浏览器缓存并重试
    if ('serviceWorker' in navigator) {
      navigator.serviceWorker.getRegistrations().then(registrations => {
        registrations.forEach(registration => {
          registration.unregister()
        })
      })
    }
    
    // 强制刷新页面，清除缓存
    window.location.reload(true)
  } else {
    console.error('路由导航错误:', error)
    ElMessage.error('页面加载失败，请稍后重试')
  }
})

// 配置axios
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 全局样式覆盖
const style = document.createElement('style')
style.textContent = `
  /* 全局样式覆盖 */
  :root {
    --el-select-bg-color: #2a2a2a !important;
    --el-input-bg-color: #2a2a2a !important;
    --el-input-border-color: #333 !important;
    --el-input-text-color: #40c4ff !important;
    --el-input-placeholder-color: rgba(11, 215, 222, 1) !important;
  }
  
  /* 性别下拉框样式 */
  .el-select .el-input__wrapper {
    background-color: #2a2a2a !important;
    --el-input-bg-color: #2a2a2a !important;
    --el-input-border-color: #333 !important;
    --el-input-text-color: #40c4ff !important;
    --el-input-placeholder-color: rgba(11, 215, 222, 1) !important;
    border-radius: 15px !important;
    box-shadow: 0 0 8px rgba(64, 196, 255, 0.2) !important;
    overflow: hidden !important;
    border: 1px solid #333 !important;
  }
  
  .el-select .el-input__wrapper:hover {
    --el-input-border-color: #40c4ff !important;
    --el-input-hover-border-color: #40c4ff !important;
    box-shadow: 0 0 10px rgba(64, 196, 255, 0.3) !important;
    border: 1px solid #40c4ff !important;
  }
  
  .el-select .el-input__wrapper.is-focus {
    --el-input-border-color: #40c4ff !important;
    --el-input-focus-border-color: #40c4ff !important;
    box-shadow: 0 0 0 2px rgba(64, 196, 255, 0.4) inset, 0 0 10px rgba(64, 196, 255, 0.3) !important;
    border: 1px solid #40c4ff !important;
  }
  
  .el-select .el-input__inner {
    background-color: #2a2a2a !important;
    color: #40c4ff !important;
    text-shadow: 0 0 15px rgba(64, 196, 255, 1), 0 0 30px rgba(64, 196, 255, 0.8), 0 0 45px rgba(0, 150, 255, 0.6) !important;
    font-weight: bold !important;
    font-size: 16px !important;
    border: none !important;
    outline: none !important;
    caret-color: #40c4ff !important;
  }
  
  .el-select .el-input__inner::placeholder {
    color: rgba(11, 215, 222, 1) !important;
  }
  
  /* el-empty 组件样式覆盖 */
  .el-empty {
    --el-empty-text-color: #00f5ff !important;
  }
  
  .el-empty__description {
    color: #00f5ff !important;
    font-size: 14px !important;
    font-weight: normal !important;
    text-shadow: 0 0 5px rgba(0, 245, 255, 0.5) !important;
  }
`
document.head.appendChild(style)

app.use(router)
app.use(pinia)
app.use(ElementPlus, {
  locale: zhCn
})
app.mount('#app')