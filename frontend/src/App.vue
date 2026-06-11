<template>
  <div class="app">
    <!-- 导航栏 -->
    <header class="navbar" v-if="!isLearningCave && !$route.meta.hideNav && !$route.meta.hideNavbar">
      <div class="navbar-container">
        <div class="navbar-logo" ref="logoRef" title="点我返回主页喵~" @click="() => { playSound('我的刀盾'); goToHome($event); }">
          <img src="/png/top.png" alt="小喵仙入梦学堂" class="logo-image">
          <h1 class="brand-title">小喵仙入梦学堂</h1>
        </div>
        
        <!-- 主题切换 -->
        <el-dropdown @command="handleThemeChange" placement="top-start">
          <span class="theme-toggle" @click="playSound('点击音效1')">
            <el-icon><MoonNight /></el-icon>
            <span>主题</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="dark">黑色主题</el-dropdown-item>
              <el-dropdown-item command="light">白色主题</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <div class="navbar-search">
          <div style="display: flex; width: 100%; max-width: 600px; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); border: 1px solid #9c27b0; border-radius: 20px; overflow: hidden; box-shadow: 0 0 10px rgba(156, 39, 176, 0.3); transition: all 0.3s ease;">
            <input 
              v-model="searchQuery" 
              placeholder="搜索课程、用户" 
              style="background: transparent; border: none; color: #00f5ff; flex: 1; padding: 10px 16px; outline: none; font-family: '楷体', 'KaiTi', 'Sitka Text Semibold', serif; text-shadow: 0 0 5px rgba(0, 245, 255, 0.5);"
            >
            <button 
              @click="() => { playSound('点击音效3'); handleSearch(); }"
              @mouseenter="playSearchHoverSound"
              style="width: 40px; background: linear-gradient(135deg, #9c27b0 0%, #673ab7 100%); border: none; display: flex; align-items: center; justify-content: center; cursor: pointer; box-shadow: 0 0 10px rgba(156, 39, 176, 0.5); transition: all 0.3s ease;"
            >
              <el-icon style="color: #00f5ff; font-size: 16px;"><Search /></el-icon>
            </button>
          </div>
        </div>
        <div class="navbar-user">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown>
              <span class="user-avatar" @click="playSound('点击音效1')">
                <img :src="userAvatar" :alt="userStore.user?.username || '用户'" />
                <span class="username">{{ userStore.user?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="userRole !== 'admin'" @click="() => { goToProfile(); playSound('点击音效1'); }">个人中心</el-dropdown-item>
                  <el-dropdown-item v-if="userRole === 'student'" @click="() => { goToLearningCave(); playSound('点击音效1'); }">学习洞府</el-dropdown-item>
                  <el-dropdown-item v-if="userRole === 'teacher'" @click="() => { goToCourseManagement(); playSound('点击音效1'); }">课程管理</el-dropdown-item>
                  <el-dropdown-item v-if="userRole === 'admin'" @click="() => { goToSystemManagement(); playSound('点击音效1'); }">系统管理</el-dropdown-item>
                  <el-dropdown-item @click="() => { logout(); playSound('点击音效2'); }">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <div class="auth-buttons" style="display: flex; gap: 16px; z-index: 9999; position: relative;">
              <router-link to="/login" class="auth-button login-button" style="position: relative; z-index: 9999;" @click="playSound('点击音效1')">登录</router-link>
              <router-link to="/register" class="auth-button register-button" style="position: relative; z-index: 9999;" @click="playSound('点击音效2')">注册</router-link>
            </div>
          </template>
        </div>
      </div>
    </header>

    <!-- 主要内容包装器 -->
    <div class="main-content-wrapper" :class="{ 'learning-cave': isLearningCave }">
      <!-- 主要内容 -->
      <main class="main-content" :class="{ 
        'login-page': $route.name === 'Login', 
        'home-page': $route.name === 'Home',
        'hide-nav-page': $route.meta.hideNav 
      }">
        <router-view />
      </main>
    </div>

    <!-- 页脚 -->
    <footer class="footer" v-if="!isLearningCave && !$route.meta.hideFooter">
      <div class="footer-container">
        <div class="footer-info">
          <h3>小喵仙入梦学堂</h3>
          <p>一个专注于沉浸式学习体验的二次元化在线教育平台<br>希望通过二次元动漫元素、音乐、游戏化机制等<br>为学习爱好者提供更加舒畅的学习体验~</p>
        </div>
        <div class="footer-contact">
          <h4>联系我们</h4>
          <p>邮箱：lyq6660928@163.com</p>
          <p>电话：15873948636</p>
          <p>站长：罗永庆</p>
        </div>
        <div class="footer-wechat">
          <h4>站长微信</h4>
          <img src="/images/me/myWechat.png" alt="站长微信" class="wechat-qr" @click="scrollToTop">
        </div>
      </div>
      <div class="footer-copyright">
        <p>© 2026 二次元化在线教育平台 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ArrowDown, Search, MoonNight, Sunny } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { onMounted, ref, watch, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from './store/user'
import { useAudioManager } from './utils/audioManager'

const { playSound, getResourceUrl } = useAudioManager()

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const searchQuery = ref('')
const logoRef = ref(null)
const currentTheme = ref('dark') // 默认黑色主题
const isLearningCave = ref(false) // 用于判断是否在学习洞府页面

// 监听用户头像变化，添加时间戳防止缓存
const userAvatar = computed(() => {
  const avatar = userStore.user?.avatar
  return avatar ? getResourceUrl(avatar) : getResourceUrl('/png/head1.png')
})

const userRole = computed(() => {
  const role = userStore.user?.role || 'student'
  console.log('User role computed:', role)
  return String(role)
})

// 监听用户信息变化，确保头像及时更新
watch(
  () => userStore.user,
  (newUser) => {
    console.log('User info changed:', newUser)
    if (newUser) {
      console.log('User role type:', typeof newUser.role, 'User role value:', newUser.role)
    }
  },
  { deep: true }
)

// 监听路由变化，判断是否在学习洞府页面
watch(
  () => route.path,
  (newPath) => {
    isLearningCave.value = newPath === '/help-center'
  },
  { immediate: true }
)

const goToProfile = () => {
  // 关闭所有打开的对话框
  try {
    // 尝试使用正确的方法关闭对话框
    if (ElMessageBox.close) {
      ElMessageBox.close();
    }
  } catch (error) {
    console.log('关闭对话框失败:', error);
  }
  router.push('/user/profile')
}

const goToLearningCave = () => {
  try {
    if (ElMessageBox.close) {
      ElMessageBox.close();
    }
  } catch (error) {
    console.log('关闭对话框失败:', error);
  }
  router.push('/help-center')
}

const goToCourseManagement = () => {
  try {
    if (ElMessageBox.close) {
      ElMessageBox.close();
    }
  } catch (error) {
    console.log('关闭对话框失败:', error);
  }
  router.push('/course-management')
}

const goToSystemManagement = () => {
  try {
    if (ElMessageBox.close) {
      ElMessageBox.close();
    }
  } catch (error) {
    console.log('关闭对话框失败:', error);
  }
  router.push('/system-management')
}

const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '退出登录', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    customClass: 'custom-logout-dialog',
    cancelButtonClass: 'custom-cancel-button'
  }).then(() => {
    playSound('点击音效2')
    // 先清除用户状态并跳转，然后显示成功提示
    userStore.logout()
    // 关闭所有打开的对话框
    try {
      if (ElMessageBox.close) {
        ElMessageBox.close();
      }
    } catch (error) {
      console.log('关闭对话框失败:', error);
    }
    router.push('/login')
    // 使用 setTimeout 确保页面跳转后再显示提示
    setTimeout(() => {
      ElMessage.success({
        message: '已成功退出登录',
        duration: 1500
      })
    }, 300)
  }).catch(() => {
    playSound('点击音效1')
    // 用户取消退出
  })
  
  // 添加鼠标悬停事件监听器到确认按钮和取消按钮
  setTimeout(() => {
    // 确认按钮
    const confirmButton = document.querySelector('.custom-logout-dialog .el-button--primary');
    if (confirmButton) {
      // 播放语音的函数
      const playLogoutVoice = () => {
        const voice = new Audio(getResourceUrl('/music/语音包/唉~这里已经没什么值得留恋的了.mp3'));
        voice.volume = 0.5;
        voice.play().catch(err => {
          console.log('语音播放失败:', err);
        });
      };
      
      // 添加鼠标悬停事件
      confirmButton.addEventListener('mouseenter', playLogoutVoice);
    }
    
    // 取消按钮
    const cancelButton = document.querySelector('.custom-logout-dialog .custom-cancel-button');
    if (cancelButton) {
      // 播放语音的函数
      const playCancelVoice = () => {
        const voice = new Audio(getResourceUrl('/music/语音包/大鱼我们好像在哪见过.mp3'));
        voice.volume = 0.5;
        voice.play().catch(err => {
          console.log('语音播放失败:', err);
        });
      };
      
      // 添加鼠标悬停事件
      cancelButton.addEventListener('mouseenter', playCancelVoice);
    }
    
    // 备用选择器，以防上面的选择器找不到元素
    setTimeout(() => {
      const cancelButtonFallback = document.querySelector('.custom-logout-dialog .el-button--default');
      if (cancelButtonFallback) {
        // 播放语音的函数
        const playCancelVoice = () => {
          const voice = new Audio(getResourceUrl('/music/语音包/大鱼我们好像在哪见过.mp3'));
          voice.volume = 0.5;
          voice.play().catch(err => {
            console.log('语音播放失败:', err);
          });
        };
        
        // 添加鼠标悬停事件
        cancelButtonFallback.addEventListener('mouseenter', playCancelVoice);
      }
    }, 200);
  }, 100);
}

// 播放搜索按钮悬停音效的函数
const playSearchHoverSound = () => {
  try {
    const voice = new window.Audio(getResourceUrl('/music/纳西塔/不知道干什么的话，要不要我带你去转转呀？.wav'));
    voice.volume = 0.5;
    voice.play().catch(err => {
      console.log('语音播放失败:', err);
    });
  } catch (error) {
    console.log('播放音效错误:', error);
  }
};

const handleSearch = () => {
  // 播放点击音效
  playSound('点击音效1');
  const keyword = searchQuery.value.trim();
  // 关闭所有打开的对话框
  try {
    if (ElMessageBox.close) {
      ElMessageBox.close();
    }
  } catch (error) {
    console.log('关闭对话框失败:', error);
  }
  // 跳转到搜索结果页
  if (keyword) {
    router.push(`/courses?keyword=${encodeURIComponent(keyword)}`)
  } else {
    router.push('/courses')
  }
}

const goToHome = (event) => {
  // 检查event是否存在
  if (event) {
    event.preventDefault();
    event.stopPropagation();
  }
  console.log('goToHome函数被调用');
  // 播放点击音效
  playSound('点击音效1');
  // 关闭所有打开的对话框
  try {
    if (ElMessageBox.close) {
      ElMessageBox.close();
    }
  } catch (error) {
    console.log('关闭对话框失败:', error);
  }
  // 使用Vue Router跳转到首页
  router.push('/');
}

// 滚动到顶部
const scrollToTop = () => {
  // 尝试滚动整个页面
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
  
  // 同时尝试滚动 main-content 元素（如果它是滚动容器）
  const mainContent = document.querySelector('.main-content')
  if (mainContent) {
    mainContent.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }
  
  // 尝试使用 document.documentElement 滚动
  document.documentElement.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
  
  // 尝试使用 document.body 滚动
  document.body.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

const handleThemeChange = (theme) => {
  playSound('点击音效1')
  currentTheme.value = theme
  if (theme === 'dark') {
    document.documentElement.classList.remove('light-theme')
    document.documentElement.classList.add('dark-theme')
  } else {
    document.documentElement.classList.remove('dark-theme')
    document.documentElement.classList.add('light-theme')
  }
  // 保存主题到本地存储
  localStorage.setItem('theme', theme)
}

const handleLogoClick = () => {
  console.log('Logo clicked!');
  playSound('我的刀盾');
  // 关闭所有打开的对话框
  try {
    if (ElMessageBox.close) {
      ElMessageBox.close();
    }
  } catch (error) {
    console.log('关闭对话框失败:', error);
  }
  // 确保导航到首页
  router.push('/');
}

// 组件挂载后修改占位符颜色和添加logo点击事件
onMounted(async () => {
  const savedTheme = localStorage.getItem('theme') || 'dark'
  currentTheme.value = savedTheme
  if (savedTheme === 'dark') {
    document.documentElement.classList.add('dark-theme')
  } else {
    document.documentElement.classList.add('light-theme')
  }
  
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    try {
      const user = JSON.parse(storedUser)
      console.log('=== App.vue onMounted ===')
      console.log('LocalStorage user:', user)
      console.log('User role:', user.role, 'Type:', typeof user.role)
    } catch (e) {
      console.log('Failed to parse localStorage user:', e)
    }
  }
  
  if (userStore.isLoggedIn) {
    await userStore.getUserInfo()
  }

  // 等待DOM更新
  setTimeout(() => {
    const searchInput = document.querySelector('.navbar-search .el-input__inner')
    if (searchInput) {
      // 创建样式元素
      const style = document.createElement('style')
      style.textContent = `
        .navbar-search .el-input__inner::placeholder {
          color: rgba(0, 245, 255, 0.7) !important;
          text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
        }
        .navbar-search .el-input__inner::-webkit-input-placeholder {
          color: rgba(0, 245, 255, 0.7) !important;
          text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
        }
        .navbar-search .el-input__inner::-moz-placeholder {
          color: rgba(0, 245, 255, 0.7) !important;
          text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
          opacity: 1 !important;
        }
        .navbar-search .el-input__inner:-ms-input-placeholder {
          color: rgba(0, 245, 255, 0.7) !important;
          text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
        }
      `
      document.head.appendChild(style)
    }
  }, 100)

  // 添加logo点击事件
  if (logoRef.value) {
    logoRef.value.addEventListener('click', () => {
      console.log('Logo clicked!');
      playSound('我的刀盾');
      // 关闭所有打开的对话框
      try {
        if (ElMessageBox.close) {
          ElMessageBox.close();
        }
      } catch (error) {
        console.log('关闭对话框失败:', error);
      }
      router.push('/');
    });
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
}

/* 黑色主题 */
html.dark-theme, 
html.dark-theme body {
  background-color: black;
  color: #40c4ff;
  text-shadow: 0 0 2px rgba(64, 196, 255, 0.8);
}

html.dark-theme .app {
  background-color: black;
}

/* 白色主题 */
html.light-theme, 
html.light-theme body {
  background-color: #f0f0f0;
  color: #333;
  text-shadow: none;
}

html.light-theme .app {
  background-color: #f0f0f0;
}

html, body {
  height: 100%;
  overflow-x: hidden;
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
}

.app {
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 主内容包装器，用于推挤页脚 */
.main-content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 隐藏滚动条 */
::-webkit-scrollbar {
  display: none;
}

::-moz-scrollbar {
  display: none;
}

::-ms-scrollbar {
  display: none;
}



/* 导航栏样式 */
html.dark-theme .navbar {
  background-color: black;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  border-bottom: 1px solid #333;
}

html.light-theme .navbar {
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid #e0e0e0;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 60px;
  gap: 16px;
}

.navbar-logo {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 16px;
  transition: all 0.3s ease;
  z-index: 99999;
  position: relative;
  width: fit-content;
  user-select: none;
  pointer-events: auto;
}

.navbar-logo:active {
  transform: scale(0.95);
}

.navbar-logo:hover {
  transform: translateY(-2px) scale(1.02);
}

.logo-image {
  width: 40px;
  height: 40px;
  object-fit: contain;
  filter: brightness(0.9) contrast(1.1) saturate(1.1) drop-shadow(0 0 5px rgba(64, 196, 255, 0.5));
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.logo-image:hover {
  filter: brightness(2.5) contrast(1.5) saturate(1.5) drop-shadow(0 0 10px rgba(64, 196, 255, 0.8));
  transform: rotate(10deg) scale(1.1);
}

/* 白色主题 - logo图片白色背景 */
html.light-theme .logo-image {
  background-color: white;
  border-radius: 50%;
  padding: 2px;
  filter: brightness(1) contrast(1) saturate(1) drop-shadow(0 0 5px rgba(0, 0, 0, 0.2));
}

html.light-theme .logo-image:hover {
  filter: brightness(1.1) contrast(1.1) saturate(1.1) drop-shadow(0 0 8px rgba(255, 107, 157, 0.5));
}

.brand-title {
  font-size: 28px;
  font-weight: bold;
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
  margin: 0;
  background: linear-gradient(45deg, #ff6b9d, #ff8fab, #feca57, #48dbfb, #54a0ff, #5f27cd, #7e47c2, #ff6b9d);
  background-size: 800% 800%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  -moz-background-clip: text;
  -moz-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
  text-shadow: 0 0 10px rgba(255, 107, 157, 1), 0 0 20px rgba(255, 107, 157, 0.8), 0 0 30px rgba(95, 39, 205, 0.6), 0 0 40px rgba(126, 71, 194, 0.4);
  animation: glow 2s ease-in-out infinite alternate, gradientShift 4s ease infinite, float 3s ease-in-out infinite;
  position: relative;
  z-index: 1;
  display: inline-block;
  pointer-events: none;
  transition: all 0.3s ease;
  letter-spacing: 2px;
  text-transform: uppercase;
  text-shadow: 0 0 10px rgba(255, 107, 157, 1),
               0 0 20px rgba(255, 107, 157, 0.8),
               0 0 30px rgba(95, 39, 205, 0.6),
               0 0 40px rgba(126, 71, 194, 0.4),
               0 0 50px rgba(255, 138, 171, 0.2);
}

@keyframes glow {
  from {
    text-shadow: 0 0 10px rgba(255, 107, 157, 1), 0 0 20px rgba(255, 107, 157, 0.8), 0 0 30px rgba(95, 39, 205, 0.6), 0 0 40px rgba(126, 71, 194, 0.4);
  }
  to {
    text-shadow: 0 0 15px rgba(255, 107, 157, 1), 0 0 30px rgba(255, 107, 157, 1), 0 0 45px rgba(95, 39, 205, 0.8), 0 0 60px rgba(126, 71, 194, 0.6), 0 0 80px rgba(255, 138, 171, 0.4);
  }
}

@keyframes gradientShift {
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

@keyframes float {
  0%, 100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(10px);
  }
}

/* 白色主题 - 品牌标题红色 */
html.light-theme .brand-title {
  background: none;
  -webkit-background-clip: unset;
  -webkit-text-fill-color: #ff0000;
  -moz-background-clip: unset;
  -moz-text-fill-color: #ff0000;
  background-clip: unset;
  text-fill-color: #ff0000;
  color: #ff0000;
  text-shadow: none;
  animation: float 3s ease-in-out infinite;
}

.navbar-menu {
  display: flex;
  gap: 20px;
  flex-shrink: 0;
}

.menu-item {
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.3s ease;
  padding: 8px 0;
  position: relative;
}

/* 黑色主题 */
html.dark-theme .menu-item {
  color: #40c4ff;
}

html.dark-theme .menu-item:hover {
  color: #81d4fa;
}

html.dark-theme .menu-item::after {
  background-color: #40c4ff;
}

/* 白色主题 */
html.light-theme .menu-item {
  color: #333;
}

html.light-theme .menu-item:hover {
  color: #000;
}

html.light-theme .menu-item::after {
  background-color: #ff6b9d;
}

.menu-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  transition: width 0.3s ease;
}

.menu-item:hover::after {
  width: 100%;
}

.navbar-search {
  flex: 1;
  max-width: 400px;
}

/* 黑色主题 */
html.dark-theme .navbar-search .el-input {
  --el-input-bg-color: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  --el-input-border-color: #9c27b0;
  --el-input-hover-border-color: #ba68c8;
  --el-input-text-color: #00f5ff;
}

html.dark-theme .navbar-search :deep(.el-input__wrapper) {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%) !important;
  border: 1px solid #9c27b0 !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
}

html.dark-theme .navbar-search :deep(.el-input__wrapper:hover) {
  border-color: #ba68c8 !important;
  box-shadow: 0 0 15px rgba(186, 104, 200, 0.5) !important;
}

html.dark-theme .navbar-search :deep(.el-input__wrapper .el-input__append) {
  background-color: transparent !important;
  border-left: none !important;
  background: linear-gradient(135deg, #9c27b0 0%, #673ab7 100%) !important;
  border-radius: 0 20px 20px 0 !important;
}

/* 白色主题 */
html.light-theme .navbar-search .el-input {
  --el-input-bg-color: white;
  --el-input-border-color: #ccc;
  --el-input-hover-border-color: #999;
  --el-input-text-color: #333;
}

html.light-theme .navbar-search :deep(.el-input__wrapper) {
  background: white !important;
  border: 1px solid #ccc !important;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1) !important;
}

html.light-theme .navbar-search :deep(.el-input__wrapper:hover) {
  border-color: #999 !important;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.2) !important;
}

html.light-theme .navbar-search :deep(.el-input__wrapper .el-input__inner) {
  background: white !important;
  color: #333 !important;
}

html.light-theme .navbar-search :deep(.el-input__wrapper .el-input__append) {
  background: white !important;
  border-left: 1px solid #ccc !important;
}

/* 为输入框添加渐变背景 */
.navbar-search :deep(.el-input__wrapper) {
  transition: all 0.3s ease !important;
  border-radius: 20px !important;
  overflow: hidden !important;
}

/* 黑色主题 */
html.dark-theme .el-input__wrapper .el-input__append .el-button {
  background: black !important;
  border: 1px solid #9c27b0 !important;
  color: #9c27b0 !important;
}

html.dark-theme .el-input__wrapper .el-input__append .el-button:hover {
  background: #1a1a1a !important;
  border-color: #ba68c8 !important;
  color: #ba68c8 !important;
}

html.dark-theme .el-input__wrapper .el-input__append {
  background: black !important;
  border-left: 1px solid #9c27b0 !important;
}

/* 白色主题 */
html.light-theme .el-input__wrapper .el-input__append .el-button {
  background: white !important;
  border: 1px solid #ff6b9d !important;
  color: #ff6b9d !important;
}

html.light-theme .el-input__wrapper .el-input__append .el-button:hover {
  background: #f8f8f8 !important;
  border-color: #ff8fab !important;
  color: #ff8fab !important;
}

html.light-theme .el-input__wrapper .el-input__append {
  background: white !important;
  border-left: 1px solid #ff6b9d !important;
}

/* 自定义搜索按钮样式 */
.el-input__wrapper .el-input__append .el-button {
  border-radius: 0 20px 20px 0 !important;
  margin: 0 !important;
  height: 100% !important;
  padding: 0 16px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
  min-width: 40px !important;
}

/* 确保输入框 append 部分没有白色背景 */
.el-input__wrapper .el-input__append {
  border-radius: 0 20px 20px 0 !important;
}

/* 确保输入框整体样式正确 */
.el-input__wrapper {
  border-radius: 20px !important;
  overflow: hidden !important;
}

/* 调整输入框文本颜色 */
.navbar-search :deep(.el-input__inner) {
  color: #00f5ff !important;
  text-shadow: 0 0 5px rgba(0, 245, 255, 0.5) !important;
}

/* 调整输入框占位符颜色 */
.navbar-search :deep(.el-input__wrapper .el-input__inner) {
  color: #00f5ff !important;
  text-shadow: 0 0 5px rgba(0, 245, 255, 0.5) !important;
}

/* 为所有浏览器设置占位符颜色 */
.navbar-search :deep(.el-input__wrapper .el-input__inner::placeholder) {
  color: rgba(0, 245, 255, 0.7) !important;
  text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
  font-weight: 500 !important;
}

/* 直接为input元素设置占位符颜色 */
.navbar-search input::placeholder {
  color: rgba(0, 245, 255, 0.7) !important;
  text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
  font-weight: 500 !important;
}

/* 为不同浏览器设置input占位符颜色 */
.navbar-search input::-webkit-input-placeholder {
  color: rgba(0, 245, 255, 0.7) !important;
  text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
  font-weight: 500 !important;
}

.navbar-search input::-moz-placeholder {
  color: rgba(0, 245, 255, 0.7) !important;
  text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
  opacity: 1 !important;
  font-weight: 500 !important;
}

.navbar-search input:-ms-input-placeholder {
  color: rgba(0, 245, 255, 0.7) !important;
  text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
  font-weight: 500 !important;
}

/* 针对不同浏览器的兼容性 */
.navbar-search :deep(.el-input__wrapper .el-input__inner::-webkit-input-placeholder) {
  color: rgba(0, 245, 255, 0.7) !important;
  text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
  font-weight: 500 !important;
}

.navbar-search :deep(.el-input__wrapper .el-input__inner::-moz-placeholder) {
  color: rgba(0, 245, 255, 0.7) !important;
  text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
  opacity: 1 !important;
  font-weight: 500 !important;
}

.navbar-search :deep(.el-input__wrapper .el-input__inner:-ms-input-placeholder) {
  color: rgba(0, 245, 255, 0.7) !important;
  text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
  font-weight: 500 !important;
}

/* 直接针对Element Plus的占位符样式 */
.navbar-search :deep(.el-input__wrapper .el-input__placeholder) {
  color: rgba(0, 245, 255, 0.7) !important;
  text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
  font-weight: 500 !important;
  font-size: 14px !important;
}

/* 增强特异性的占位符样式 */
.app .navbar .navbar-container .navbar-search :deep(.el-input__placeholder) {
  color: rgba(0, 245, 255, 0.7) !important;
  text-shadow: 0 0 3px rgba(0, 245, 255, 0.3) !important;
  font-weight: 500 !important;
}

.navbar-search .el-input__wrapper {
  border-radius: 20px;
  overflow: hidden;
}

/* 移除输入框内部的边框和背景 */
.navbar-search :deep(.el-input__wrapper .el-input__append) {
  background-color: transparent !important;
  border-left: none !important;
}

/* 自定义搜索按钮样式 */
.app .navbar .navbar-container .navbar-search :deep(.el-input__wrapper .el-input__append .el-button) {
  background: linear-gradient(135deg, #9c27b0 0%, #673ab7 100%) !important;
  border: none !important;
  color: #00f5ff !important;
  border-radius: 0 !important;
  margin: 0 !important;
  height: 100% !important;
  padding: 0 16px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.5) !important;
  transition: all 0.3s ease !important;
  background-color: transparent !important;
}

.app .navbar .navbar-container .navbar-search :deep(.el-input__wrapper .el-input__append .el-button:hover) {
  background: linear-gradient(135deg, #ba68c8 0%, #9575cd 100%) !important;
  box-shadow: 0 0 15px rgba(186, 104, 200, 0.7) !important;
  background-color: transparent !important;
}

.navbar-search :deep(.el-input__wrapper .el-input__append .el-button .el-icon) {
  color: #00f5ff !important;
  font-size: 16px !important;
  text-shadow: 0 0 5px rgba(0, 245, 255, 0.5) !important;
}

.navbar-user {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

/* 主题切换按钮样式 */
.theme-toggle {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 25px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #ff6b9d 0%, #5f27cd 100%);
  border: 1px solid #ff6b9d;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.5), 0 0 30px rgba(95, 39, 205, 0.3);
  position: relative;
  overflow: hidden;
  font-weight: bold;
  font-size: 14px;
  color: #fff;
  text-shadow: 0 0 10px rgba(255, 107, 157, 0.8);
  flex-shrink: 0;
  margin: 0 4px;
}

.theme-toggle::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transform: rotate(45deg);
  animation: shine 2s ease-in-out infinite;
  z-index: 1;
  pointer-events: none;
}

.theme-toggle:hover {
  background: linear-gradient(135deg, #ff8fab 0%, #7e47c2 100%);
  border-color: #ff8fab;
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.8), 0 0 40px rgba(95, 39, 205, 0.5);
  transform: translateY(-2px) scale(1.05);
}

.theme-toggle .el-icon {
  z-index: 2;
  position: relative;
  color: #fff;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
}

.theme-toggle span {
  z-index: 2;
  position: relative;
  color: #fff;
  text-shadow: 0 0 10px rgba(255, 107, 157, 0.8);
}

/* 白色主题下的主题切换按钮 */
html.light-theme .theme-toggle {
  background: linear-gradient(135deg, #ff6b9d 0%, #5f27cd 100%);
  border: 1px solid #ff6b9d;
  color: #fff;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.5), 0 0 30px rgba(95, 39, 205, 0.3);
  text-shadow: 0 0 10px rgba(255, 107, 157, 0.8);
}

html.light-theme .theme-toggle:hover {
  background: linear-gradient(135deg, #ff8fab 0%, #7e47c2 100%);
  border-color: #ff8fab;
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.8), 0 0 40px rgba(95, 39, 205, 0.5);
  transform: translateY(-2px) scale(1.05);
}

/* 主题切换下拉菜单样式 */
.el-dropdown-menu {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%) !important;
  border: 1px solid #ff6b9d !important;
  border-radius: 10px !important;
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.5), 0 0 40px rgba(95, 39, 205, 0.3) !important;
  animation: dropdownFadeIn 0.3s ease-out;
  z-index: 99999 !important;
  position: relative !important;
  min-width: 120px !important;
  overflow: visible !important;
}

html.light-theme .el-dropdown-menu {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%) !important;
  border: 1px solid #dcdfe6 !important;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1) !important;
}

.el-dropdown-menu__item {
  color: #ff6b9d !important;
  padding: 12px 16px !important;
  transition: all 0.3s ease !important;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.5);
}

.el-dropdown-menu__item:hover {
  background: rgba(255, 107, 157, 0.1) !important;
  color: #ff8fab !important;
  transform: translateX(8px) !important;
  text-shadow: 0 0 10px rgba(255, 107, 157, 0.8);
}

html.light-theme .el-dropdown-menu__item {
  color: #303133 !important;
  text-shadow: none;
  background: #ffffff !important;
}

html.light-theme .el-dropdown-menu__item:hover {
  background: #ecf5ff !important;
  color: #409eff !important;
  transform: translateX(8px) !important;
  text-shadow: none;
}

/* 白色主题下的下拉菜单容器样式 */
html.light-theme .el-popper {
  background: #ffffff !important;
  border: 1px solid #dcdfe6 !important;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1) !important;
}

/* 白色主题下的下拉菜单内容区域样式 */
html.light-theme .el-dropdown-menu__wrap {
  background: #ffffff !important;
  border: none !important;
}

/* 白色主题下的下拉菜单项边框 */
html.light-theme .el-dropdown-menu__item:not(:last-child) {
  border-bottom: 1px solid #e4e7ed !important;
}

/* 白色主题下的用户头像 */
html.light-theme .user-avatar {
  color: #333;
}

html.light-theme .user-avatar:hover {
  background: rgba(0, 0, 0, 0.1);
}

html.light-theme .username {
  color: #333;
  text-shadow: none;
}

/* 白色主题下的登录/注册按钮 */
html.light-theme .login-button {
  color: #ff6b9d;
  border: 1px solid #ff6b9d;
  background-color: transparent;
}

html.light-theme .login-button:hover {
  background-color: rgba(255, 107, 157, 0.1);
  color: #ff8fab;
  border-color: #ff8fab;
}

html.light-theme .register-button {
  color: white;
  border: none;
  background: linear-gradient(45deg, #ff6b9d, #5f27cd);
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.3);
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: all 0.3s ease;
  background: transparent;
  border: none;
  box-shadow: none;
  position: relative;
  overflow: hidden;
}

.user-avatar:hover {
  background: rgba(64, 196, 255, 0.1);
  box-shadow: none;
  transform: translateY(-1px);
}

.user-avatar img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #40c4ff;
  box-shadow: 0 0 8px rgba(64, 196, 255, 0.6);
  transition: all 0.3s ease;
}

.user-avatar:hover img {
  box-shadow: 0 0 12px rgba(64, 196, 255, 0.8);
  transform: scale(1.05);
}

.username {
  font-size: 14px;
  color: #40c4ff;
  text-shadow: 0 0 5px rgba(64, 196, 255, 0.5);
  font-weight: 500;
}

/* 修改下拉框箭头颜色为红色 */
.user-avatar .el-icon {
  color: #ff6b9d !important;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.5) !important;
  transition: all 0.3s ease !important;
}

.user-avatar:hover .el-icon {
  color: #ff8fab !important;
  text-shadow: 0 0 8px rgba(255, 107, 157, 0.8) !important;
}

/* 自定义下拉菜单样式 */
.el-dropdown-menu {
  background: black !important;
  border: none !important;
  border-radius: 10px !important;
  box-shadow: 0 0 15px rgba(156, 39, 176, 0.5) !important;
  padding: 8px 0 !important;
  overflow: hidden !important;
  margin: 0 !important;
  min-width: 120px !important;
}

.el-dropdown-menu__item {
  color: #ff6b9d !important;
  text-shadow: 0 0 3px rgba(255, 107, 157, 0.3) !important;
  font-size: 14px !important;
  padding: 10px 16px !important;
  transition: all 0.3s ease !important;
  border-radius: 0 !important;
  background: black !important;
  margin: 0 !important;
  width: 100% !important;
  box-sizing: border-box !important;
}

.el-dropdown-menu__item:hover {
  background: rgba(255, 107, 157, 0.1) !important;
  color: #ff8fab !important;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.7) !important;
  transform: translateX(5px);
}

.el-dropdown-menu__item:not(:last-child) {
  border-bottom: 1px solid rgba(156, 39, 176, 0.3) !important;
}

/* 移除下拉菜单的箭头和多余样式 */
.el-dropdown-menu::before {
  display: none !important;
}

.el-dropdown-menu::after {
  display: none !important;
}

/* 确保下拉菜单容器没有白色背景 */
.el-popper {
  background: black !important;
  border: none !important;
  box-shadow: 0 0 15px rgba(156, 39, 176, 0.5) !important;
  border-radius: 10px !important;
  overflow: hidden !important;
  padding: 0 !important;
  margin: 0 !important;
}

.el-popper__arrow {
  display: none !important;
}

/* 确保下拉菜单的父容器也没有白色背景 */
.el-dropdown {
  position: relative !important;
}

/* 确保下拉菜单的内容区域没有白色背景 */
.el-dropdown-menu__wrap {
  background: black !important;
  border: none !important;
  overflow: hidden !important;
}

/* 为下拉菜单添加动画效果 */
.el-dropdown-menu {
  animation: dropdownFadeIn 0.3s ease-out;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.auth-button {
  text-decoration: none;
  font-size: 14px;
  font-weight: bold;
  padding: 10px 20px;
  border-radius: 25px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.3);
}

.login-button {
  color: #ff6b9d;
  border: 1px solid #ff6b9d;
  background-color: transparent;
}

.login-button::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 107, 157, 0.2), transparent);
  transform: rotate(45deg);
  animation: shine 3s ease-in-out infinite;
  z-index: 1;
  pointer-events: none;
}

.login-button:hover {
  background-color: rgba(255, 107, 157, 0.1);
  color: #ff8fab;
  border-color: #ff8fab;
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6);
  transform: translateY(-2px) scale(1.05);
}

.register-button {
  color: white;
  border: none;
  background: linear-gradient(45deg, #ff6b9d, #5f27cd);
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.3);
}

.register-button::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transform: rotate(45deg);
  animation: shine 3s ease-in-out infinite;
  z-index: 1;
  pointer-events: none;
}

.register-button:hover {
  background: linear-gradient(45deg, #ff8fab, #7e47c2);
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.8);
  transform: translateY(-2px) scale(1.05);
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

.auth-button:active {
  transform: translateY(0) scale(0.98);
}

@keyframes shine {
  0% {
    transform: translateX(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) rotate(45deg);
  }
}

/* 主要内容样式 */
html.dark-theme .main-content {
  background-color: black;
}

html.light-theme .main-content {
  background-color: #f0f0f0;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 80px 20px 20px;
}

/* 学习洞府页面样式 */
.learning-cave .main-content {
  padding: 20px;
}

/* 隐藏导航栏时的页面样式 */
.hide-nav-page.main-content {
  padding-top: 0 !important;
  padding-left: 0 !important;
  padding-right: 0 !important;
  padding-bottom: 0 !important;
  margin: 0 !important;
  max-width: 100% !important;
  width: 100% !important;
}

/* 为登录页面添加特殊样式 */
.login-page .main-content {
  max-width: none !important;
  padding: 0 !important;
  margin: 0 !important;
  width: 100% !important;
  min-height: 100vh !important;
}

/* 为主页添加特殊样式 */
.home-page .main-content {
  max-width: none !important;
  padding: 0 !important;
  margin: 0 !important;
  width: 100% !important;
  min-height: 100vh !important;
}

/* 为登录页面隐藏页脚 */
.login-page .footer {
  display: none !important;
}

/* 页脚样式 */
html.dark-theme .footer {
  background-color: black;
  border-top: 1px solid #333;
}

html.light-theme .footer {
  background-color: white;
  border-top: 1px solid #e0e0e0;
}

.footer {
  padding: 40px 0;
  margin-top: 0;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 60px;
}

.footer-info {
  flex: 1.5;
  min-width: 250px;
}

.footer-contact {
  flex: 1;
  min-width: 200px;
}

.footer-wechat {
  flex: 1;
  min-width: 180px;
}

/* 黑色主题下的页脚文字 */
html.dark-theme .footer-info h3 {
  color: #ff6b9d;
  text-shadow: 0 0 2px rgba(255, 107, 157, 0.8);
}

html.dark-theme .footer-info p {
  color: #cf1729ff;
  text-shadow: 0 0 1px rgba(0, 245, 255, 0.8);
}

html.dark-theme .footer-contact h4,
html.dark-theme .footer-wechat h4 {
  color: #ff6b9d;
  text-shadow: 0 0 2px rgba(255, 107, 157, 0.8);
}

html.dark-theme .footer-contact p {
  color: #00ff00;
  text-shadow: 0 0 1px rgba(0, 255, 0, 0.8);
}

/* 白色主题下的页脚文字 */
html.light-theme .footer-info h3 {
  color: #333;
  text-shadow: none;
}

html.light-theme .footer-info p {
  color: #666;
  text-shadow: none;
}

html.light-theme .footer-contact h4,
html.light-theme .footer-wechat h4 {
  color: #333;
  text-shadow: none;
}

html.light-theme .footer-contact p {
  color: #666;
  text-shadow: none;
}

/* 页脚通用样式 */
.footer-info h3 {
  font-size: 18px;
  margin-bottom: 20px;
  font-weight: bold;
  text-align: center;
}

.footer-info p {
  font-size: 14px;
  line-height: 1.6;
  text-align: center;
  max-width: 400px;
  margin: 0 auto;
}

.footer-contact h4,
.footer-wechat h4 {
  font-size: 16px;
  margin-bottom: 20px;
  font-weight: bold;
  text-align: center;
}

.footer-contact p {
  font-size: 14px;
  margin-bottom: 12px;
  line-height: 1.6;
  text-align: center;
}

/* 微信二维码样式 */
.footer-wechat {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.wechat-qr {
  width: 140px;
  height: 140px;
  border-radius: 12px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 2px solid rgba(255, 107, 157, 0.3);
}

.wechat-qr:hover {
  transform: scale(1.05);
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6);
  border-color: rgba(255, 107, 157, 0.8);
}

html.dark-theme .footer-copyright {
  border-top: 1px solid #333;
  color: #81d4fa;
  text-shadow: 0 0 1px rgba(129, 212, 250, 0.8);
}

html.light-theme .footer-copyright {
  border-top: 1px solid #e0e0e0;
  color: #666;
  text-shadow: none;
}

.footer-copyright {
  text-align: center;
  margin-top: 40px;
  padding-top: 20px;
  font-size: 12px;
}

/* 自定义ElMessage样式 */
.el-message {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 2px solid #ff6b9d !important;
  border-radius: 10px !important;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.8) !important;
  color: white !important;
  font-family: '楷体', 'KaiTi', 'Sitka Text Semibold', serif !important;
  font-weight: bold !important;
  font-size: 14px !important;
  padding: 8px 16px !important;
  min-width: unset !important;
  max-width: 90vw !important;
  width: fit-content !important;
  z-index: 99999 !important;
  position: fixed !important;
  top: 50px !important;
  right: 20px !important;
  margin: 0 !important;
  word-wrap: break-word !important;
  white-space: normal !important;
  line-height: 1.3 !important;
  min-height: unset !important;
  height: auto !important;
}

.el-message__icon {
  color: white !important;
  font-size: 16px !important;
  margin-right: 8px !important;
}

.el-message__content {
  font-size: 14px !important;
  font-weight: bold !important;
  color: white !important;
  white-space: normal !important;
  word-break: break-word !important;
  text-align: center !important;
  width: 100% !important;
  display: block !important;
  line-height: 1.3 !important;
  margin: 0 !important;
  padding: 0 !important;
}

/* 成功提示 */
.el-message--success {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 2px solid #ff6b9d !important;
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.8) !important;
}

/* 警告提示 */
.el-message--warning {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 2px solid #ff6b9d !important;
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.8) !important;
  color: white !important;
}

/* 错误提示 */
.el-message--error {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 2px solid #ff6b9d !important;
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.8) !important;
  color: white !important;
}

/* 信息提示 */
.el-message--info {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 2px solid #ff6b9d !important;
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.8) !important;
  color: white !important;
}

/* 自定义ElMessageBox样式 */
.el-message-box {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.95), rgba(22, 33, 62, 0.95)) !important;
  border: 1px solid #9c27b0 !important;
  border-radius: 15px !important;
  box-shadow: 0 0 20px rgba(156, 39, 176, 0.6) !important;
  font-family: '楷体', 'KaiTi', 'Sitka Text Semibold', serif !important;
}

.el-message-box__title {
  color: #40c4ff !important;
  text-shadow: 0 0 5px rgba(64, 196, 255, 0.8) !important;
  font-family: '楷体', 'KaiTi', 'Sitka Text Semibold', serif !important;
  font-weight: bold !important;
}

.el-message-box__content {
  color: #81d4fa !important;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.5) !important;
  font-family: '楷体', 'KaiTi', 'Sitka Text Semibold', serif !important;
}

.el-message-box__content .el-icon-warning {
  color: #e6a23c !important;
  text-shadow: 0 0 5px rgba(230, 162, 60, 0.8) !important;
}

.el-message-box__btns {
  display: flex !important;
  justify-content: flex-end !important;
  gap: 10px !important;
}

.el-message-box__btn {
  border-radius: 20px !important;
  padding: 8px 20px !important;
  font-family: '楷体', 'KaiTi', 'Sitka Text Semibold', serif !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3) !important;
}

.el-button--primary {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 1px solid #ff6b9d !important;
  color: white !important;
  text-shadow: 0 0 3px rgba(0, 0, 0, 0.5) !important;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #ff8fab, #7e47c2) !important;
  border-color: #ff8fab !important;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.6) !important;
}

.el-button--default {
  background: white !important;
  border: 1px solid #9c27b0 !important;
  color: #9c27b0 !important;
  font-weight: bold !important;
}

.el-button--default:hover {
  background: #f5f5f5 !important;
  border-color: #ba68c8 !important;
  color: #ba68c8 !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
  transition: all 0.3s ease !important;
}

/* 自定义退出登录对话框样式 */
.custom-logout-dialog {
  border-radius: 20px !important;
  overflow: hidden !important;
}

/* 自定义按钮样式 - 与登录按钮一致 */
.custom-logout-dialog .el-button {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 1px solid #ff6b9d !important;
  color: white !important;
  text-shadow: 0 0 3px rgba(0, 0, 0, 0.5) !important;
  font-weight: bold !important;
  border-radius: 25px !important;
  padding: 10px 20px !important;
  font-family: '楷体', 'KaiTi', 'Sitka Text Semibold', serif !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.3) !important;
  position: relative !important;
  overflow: hidden !important;
}

.custom-logout-dialog .el-button::before {
  content: '' !important;
  position: absolute !important;
  top: -50% !important;
  left: -50% !important;
  width: 200% !important;
  height: 200% !important;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.2), transparent) !important;
  transform: rotate(45deg) !important;
  animation: shine 3s ease-in-out infinite !important;
  z-index: 1 !important;
  pointer-events: none !important;
}

.custom-logout-dialog .el-button:hover {
  background: linear-gradient(45deg, #ff8fab, #7e47c2) !important;
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.8) !important;
  transform: translateY(-2px) scale(1.05) !important;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5) !important;
}

.custom-logout-dialog .el-button:active {
  transform: translateY(0) scale(0.98) !important;
}

/* 确保按钮样式不被覆盖 */
.custom-logout-dialog .el-button--primary,
.custom-logout-dialog .el-button--default {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 1px solid #ff6b9d !important;
  color: white !important;
  border-radius: 25px !important;
  padding: 10px 20px !important;
}

/* 按钮间距 */
.custom-logout-dialog .el-message-box__btns {
  display: flex !important;
  justify-content: flex-end !important;
  gap: 16px !important;
}

/* 动画效果 */
@keyframes shine {
  0% {
    transform: translateX(-100%) rotate(45deg) !important;
  }
  100% {
    transform: translateX(100%) rotate(45deg) !important;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-container {
    padding: 0 10px;
  }

  .navbar-logo h1 {
    font-size: 18px;
  }

  .navbar-menu {
    gap: 20px;
  }

  .menu-item {
    font-size: 14px;
  }

  .main-content {
    padding: 10px;
  }

  .footer-container {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}
</style>