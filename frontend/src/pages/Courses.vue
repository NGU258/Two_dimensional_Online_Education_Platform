<template>
  <div class="courses">
    <!-- 背景音乐 -->
    <audio ref="musicRef" class="background-music" loop>
      <source :src="getResourceUrl('/music/视频_纯音乐.mp3')" type="audio/mpeg"></audio>
    <div class="main-content-layout">
      <!-- 左侧筛选条件 -->
      <div class="filter-sidebar">
        <el-card class="filter-card" v-show="filterCardVisible">
          <div class="filter-card-header">
            <span class="video-count">{{ total }}</span>
            <h2 class="filter-card-title">
              <a href="#" @click.prevent="scrollToTop" class="home-link">课程列表</a>
            </h2>
            <span class="current-video">{{ currentVideoIndex }}</span>
          </div>
          
          <div class="filter-item">
            <h3 class="filter-title">分类筛选</h3>
            <el-select v-model="categoryId" placeholder="选择分类" @change="() => { searchCourses(); playSound('点击音效1'); }" class="full-width">
              <el-option label="全部课程" value="0" />
              <el-option 
                v-for="category in categories" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id" 
              />
            </el-select>
          </div>
          
          <div class="filter-item">
            <h3 class="filter-title">排序方式</h3>
            <el-select v-model="sortBy" placeholder="排序方式" @change="() => { searchCourses(); playSound('点击音效1'); }" class="full-width">
              <el-option label="最新上架" value="createTime" />
              <el-option label="热门程度" value="popularity" />
              <el-option label="评分最高" value="rating" />
            </el-select>
          </div>
          
          <div class="filter-item">
            <h3 class="filter-title">搜索课程</h3>
            <el-input v-model="keyword" placeholder="搜索课程" clearable class="full-width" />
          </div>
          
          <div class="filter-footer">
            <el-button type="info" plain @click="() => { goBack(); playSound('点击音效1'); }" class="filter-button">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <el-button type="info" plain @click="() => { searchCourses(); playSound('点击音效1'); }" class="filter-button">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
          </div>
        </el-card>
      </div>
      
      <!-- 右侧视频卡片 -->
      <div class="courses-content">
        <div class="piano-keys-layout">
          <div class="piano-keys-container">
            <div 
              v-for="(course, index) in courses" 
              :key="course.id" 
              class="piano-key-item"
              :class="{ 'right-key': index % 2 === 1 }"
              :ref="el => { if (el) courseRefs[index] = el }"
            >
              <el-card :body-style="{ padding: '0' }" class="anime-course-card">
                <div class="course-image">
                  <div class="image-overlay">
                    <div class="anime-border"></div>
                    <img :src="course.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20course%20cover%20illustration&image_size=landscape_16_9'" :alt="course.title" />
                    <div class="play-icon" @click="() => { viewCourseDetail(course.id); playSound('点击音效1'); }">
                      <el-icon class="play-symbol"><VideoPlay /></el-icon>
                    </div>
                  </div>
                </div>
                <div class="course-info">
                  <h3 class="course-title">{{ course.title }}</h3>
                  <p class="course-description">{{ course.description }}</p>
                  <div class="course-meta">
                    <span class="course-category">{{ getCategoryName(course.categoryId) }}</span>
                    <span class="course-level">{{ course.level }}</span>
                    <span class="course-rating">
                      <el-rate v-model="course.rating" disabled :max="5" show-score />
                    </span>
                  </div>
                  <el-button type="primary" @click="() => { viewCourseDetail(course.id); playSound('点击音效1'); }" class="view-button">
                    <span class="button-text">查看详情</span>
                    <el-icon class="button-icon"><ArrowRight /></el-icon>
                  </el-button>
                </div>
              </el-card>
            </div>
          </div>
        </div>

        <div v-if="courses.length === 0" class="empty-state">
          <div v-if="userStore.isLoggedIn" class="custom-empty">
            <div class="empty-content">
              <div class="empty-icon">📦</div>
              <div class="empty-description">暂无课程数据</div>
            </div>
          </div>
          <div v-else class="login-prompt">
            <div class="custom-empty">
              <div class="empty-content">
                <div class="empty-icon">🔐</div>
                <div class="empty-description login-text">请先登录</div>
              </div>
            </div>
            <el-button type="primary" @click="goToLogin" class="login-button">
              去登录
            </el-button>
          </div>
        </div>


      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, VideoPlay, ArrowRight, ArrowLeft } from '@element-plus/icons-vue'
import { useCourseStore } from '../store/course'
import { useUserStore } from '../store/user'
import { useAudioManager } from '../utils/audioManager'

const { getResourceUrl } = useAudioManager()

// 音效播放函数
const playSound = (soundName) => {
  try {
    const audio = new Audio(getResourceUrl(`/audio/${encodeURIComponent(soundName)}.mp3`))
    audio.volume = 0.3
    audio.play().catch(err => {
      console.log('音频播放失败:', err)
    })
  } catch (error) {
    console.log('音效播放错误:', error)
  }
}

// 筛选卡片显示状态
const filterCardVisible = ref(true)
// 记录上一次滚动位置
const lastScrollTop = ref(0)
// 当前视频索引（初始显示2个卡片，所以从2开始）
const currentVideoIndex = ref(2)
// 音乐元素引用
const musicRef = ref(null)

const router = useRouter()
const route = useRoute()
const courseStore = useCourseStore()
const userStore = useUserStore()

const courses = ref([])
const categories = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(1000) // 设置为一个很大的值，一次性加载所有课程
const categoryId = ref('0')
const keyword = ref('')
const sortBy = ref('createTime')
const courseRefs = ref([]) // 课程卡片的ref数组

const getCategoryName = (id) => {
  const category = categories.value.find(c => c.id === id)
  return category ? category.name : '未分类'
}

const loadCourses = async () => {
  // 检查用户是否已登录
  if (!userStore.isLoggedIn) {
    // 用户未登录，显示空数据
    courses.value = []
    total.value = 0
    currentVideoIndex.value = 0
    return
  }
  
  try {
    const response = await courseStore.getCourses({
      page: currentPage.value,
      pageSize: pageSize.value,
      categoryId: categoryId.value,
      keyword: keyword.value,
      sortBy: sortBy.value
    })
    courses.value = response.courses
    total.value = response.total
    // 根据课程总数重新设置currentVideoIndex
    if (courses.value.length === 1) {
      currentVideoIndex.value = 1
    } else {
      currentVideoIndex.value = 2
    }
    
    // 确保筛选卡片始终显示
    filterCardVisible.value = true
    
    // 课程列表更新后，重新初始化IntersectionObserver
    nextTick(() => {
      initIntersectionObserver()
    })
  } catch (error) {
    console.error('获取课程失败:', error)
  }
}

const loadCategories = async () => {
  try {
    categories.value = await courseStore.getCategories()
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const searchCourses = async () => {
  currentPage.value = 1
  await loadCourses()
}

const goToLogin = () => {
  playSound('点击音效1')
  router.push('/login')
}

const scrollToTop = () => {
  const coursesContent = document.querySelector('.courses-content')
  if (coursesContent) {
    coursesContent.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadCourses()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadCourses()
}

const viewCourseDetail = (courseId) => {
  // 跳转到课程详情页
  router.push(`/course/${courseId}?from=courses`)
}

const goBack = () => {
  router.push('/')
}

// 滚动监听函数
const handleScroll = () => {
  // 获取courses-content元素
  const coursesContent = document.querySelector('.courses-content')
  if (!coursesContent) return
  
  // 使用courses-content元素的滚动属性，因为滚动条在这个元素上
  const scrollTop = coursesContent.scrollTop
  const scrollHeight = coursesContent.scrollHeight
  const clientHeight = coursesContent.clientHeight
  
  // 始终显示筛选卡片
  filterCardVisible.value = true
  
  // 更新上一次滚动位置
  lastScrollTop.value = scrollTop
}

// 页面加载完成后初始化筛选卡片状态
const initFilterCardState = () => {
  // 页面加载时默认显示筛选卡片
  filterCardVisible.value = true
}

// 自动滚动轮播功能
let autoScrollTimeout = null
let animationId = null
let scrollCount = 0
let userScrollTimeout = null
let isUserScrolling = false

const smoothScroll = (element, to, duration, callback) => {
  const start = element.scrollTop
  const change = to - start
  const increment = 20
  let currentTime = 0
  
  const animateScroll = () => {
    currentTime += increment
    const val = Math.easeInOutQuad(currentTime, start, change, duration)
    element.scrollTop = val
    
    if (currentTime < duration) {
      animationId = requestAnimationFrame(animateScroll)
    } else {
      if (callback) callback()
    }
  }
  
  animateScroll()
}

// 缓动函数
Math.easeInOutQuad = (t, b, c, d) => {
  t /= d / 2
  if (t < 1) return c / 2 * t * t + b
  t--
  return -c / 2 * (t * (t - 2) - 1) + b
}

const scrollNext = () => {
  // 如果用户正在滚动，不执行自动滚动
  if (isUserScrolling) return
  
  // 如果用户未登录或课程列表为空，不执行自动滚动
  if (!userStore.isLoggedIn || courses.value.length === 0) {
    return
  }
  
  const coursesContent = document.querySelector('.courses-content')
  if (!coursesContent) return
  
  // 检查是否已经显示到最后一个课程
  if (currentVideoIndex.value >= courses.value.length) {
    // 已经显示到最后一个课程，从第一个课程开始继续滚动
    currentVideoIndex.value = 1
    // 重置滚动次数
    scrollCount = 0
    // 停顿2秒后继续滚动
    autoScrollTimeout = setTimeout(scrollNext, 2000)
    return
  }
  
  // 增加滚动次数
  scrollCount++
  
  // 在滚动之前更新当前视频数量+1
  currentVideoIndex.value = currentVideoIndex.value + 1
  
  const scrollTop = coursesContent.scrollTop
  const scrollHeight = coursesContent.scrollHeight
  const clientHeight = coursesContent.clientHeight
  
  // 检查是否已经到达滚动容器的底部
  if (scrollTop + clientHeight >= scrollHeight - 10) {
    // 已经到达底部，从第一个课程开始继续滚动
    currentVideoIndex.value = 1
    // 重置滚动次数
    scrollCount = 0
    // 先滚动一小段距离，然后再回到顶部，创造连续滚动的效果
    const bufferScroll = scrollTop + 100
    smoothScroll(coursesContent, bufferScroll, 500, () => {
      // 快速跳转到顶部
      coursesContent.scrollTop = 0
      // 停顿2秒后继续滚动
      autoScrollTimeout = setTimeout(scrollNext, 2000)
    })
    return
  }
  
  // 计算下一个滚动位置，每次滚动一个卡片的高度
  let nextScrollTop = scrollTop + 313
  
  // 滚动到第3次及以后时，每次减去2的误差值
  if (scrollCount >= 3) {
    nextScrollTop -= 3
  }
  
  // 平滑向下滚动，滚动完成后停顿2秒再继续
  smoothScroll(coursesContent, nextScrollTop, 1000, () => {
    // 停顿2秒后继续滚动
    autoScrollTimeout = setTimeout(scrollNext, 2000)
  })
}

const startAutoScroll = () => {
  // 清除之前的定时器和动画
  if (autoScrollTimeout) {
    clearTimeout(autoScrollTimeout)
  }
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  
  // 开始滚动
  scrollNext()
}

const stopAutoScroll = () => {
  if (autoScrollTimeout) {
    clearTimeout(autoScrollTimeout)
    autoScrollTimeout = null
  }
  if (animationId) {
    cancelAnimationFrame(animationId)
    animationId = null
  }
}

// 处理用户滚动事件
const handleUserScroll = (event) => {
  // 标记用户正在滚动
  isUserScrolling = true
  
  // 停止自动滚动
  stopAutoScroll()
  
  // 清除之前的用户滚动定时器
  if (userScrollTimeout) {
    clearTimeout(userScrollTimeout)
    userScrollTimeout = null
  }
  
  // 设置新的定时器，10秒后恢复自动滚动
  userScrollTimeout = setTimeout(() => {
    isUserScrolling = false
    // 恢复自动滚动
    startAutoScroll()
  }, 10000)
}

// 监听路由变化，更新搜索关键词
watch(
  () => route.query.keyword,
  (newKeyword) => {
    if (newKeyword) {
      keyword.value = newKeyword
      searchCourses()
    }
  },
  { immediate: true }
)

onMounted(async () => {
  // 从URL参数中获取搜索关键词
  const urlKeyword = route.query.keyword
  if (urlKeyword) {
    keyword.value = urlKeyword
  }
  
  // 并行加载数据，提高性能
  const [categoriesResult, coursesResult] = await Promise.all([
    loadCategories(),
    loadCourses()
  ])
  
  // 初始化筛选卡片状态
  initFilterCardState()
  
  // 使用 nextTick 确保 DOM 完全渲染后再操作
  nextTick(() => {
    // 为 courses-content 元素添加滚动事件监听器，因为滚动条在这个元素上
    const coursesContent = document.querySelector('.courses-content')
    if (coursesContent) {
      // 从其他页面导航到这里时，滚动到顶部
      coursesContent.scrollTop = 0
      
      coursesContent.addEventListener('scroll', handleScroll)
      // 为 courses-content 元素添加鼠标滚轮事件监听器，检测用户滚动
      coursesContent.addEventListener('wheel', handleUserScroll)
      
      // 2秒后开始自动滚动轮播
      setTimeout(startAutoScroll, 2000)
    }
    
    // 初始化音乐播放
    if (musicRef.value) {
      if (userStore.isLoggedIn) {
        // 用户已登录，尝试播放音乐
        // 设置音量为30%
        musicRef.value.volume = 0.35
        // 尝试播放音乐
        musicRef.value.play().catch(err => {
          console.log('音乐自动播放失败:', err)
          // 尝试模拟用户交互后播放
          const handleUserInteraction = () => {
            if (musicRef.value) {
              musicRef.value.play().catch(err => {
                console.log('音乐播放失败:', err)
              })
            }
          }
          // 添加用户交互事件监听器
          document.body.addEventListener('click', handleUserInteraction, { once: true })
          document.body.addEventListener('touchstart', handleUserInteraction, { once: true })
          document.body.addEventListener('keydown', handleUserInteraction, { once: true })
        })
      } else {
        // 用户未登录，确保音乐静音并暂停
        musicRef.value.muted = true
        musicRef.value.pause()
      }
    }
    
    // 初始化IntersectionObserver，监测卡片的可见性
    initIntersectionObserver()
  })
})

// 初始化IntersectionObserver
const initIntersectionObserver = () => {
  // 等DOM完全渲染后再初始化
  nextTick(() => {
    // 配置IntersectionObserver
    const observerOptions = {
      root: document.querySelector('.courses-content'),
      rootMargin: '0px',
      threshold: 0.8 // 当卡片80%可见时触发
    }
    
    // 创建IntersectionObserver实例
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          // 找到当前可见卡片的索引
          const cardIndex = courseRefs.value.indexOf(entry.target)
          if (cardIndex !== -1) {
            // 更新当前课程数量为可见卡片的索引+1
            currentVideoIndex.value = cardIndex + 1
          }
        }
      })
    }, observerOptions)
    
    // 观察所有课程卡片
    courseRefs.value.forEach(card => {
      if (card) {
        observer.observe(card)
      }
    })
  })
}

onUnmounted(() => {
  // 移除 courses-content 元素的滚动事件监听器
  const coursesContent = document.querySelector('.courses-content')
  if (coursesContent) {
    coursesContent.removeEventListener('scroll', handleScroll)
    // 移除 courses-content 元素的鼠标滚轮事件监听器
    coursesContent.removeEventListener('wheel', handleUserScroll)
  }
  
  // 清理自动滚动轮播功能
  stopAutoScroll()
  
  // 清理用户滚动定时器
  if (userScrollTimeout) {
    clearTimeout(userScrollTimeout)
    userScrollTimeout = null
  }
})
</script>

<style scoped>
.courses {
  position: fixed;
  top: 60px;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('/images/bg/cat.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: transparent;
  overflow: hidden;
}

/* 黑色主题 */
html.dark-theme .courses {
  background-color: transparent;
}

/* 白色主题 */
html.light-theme .courses {
  background-color: transparent;
}

/* 背景音乐样式 */
.background-music {
  position: absolute;
  top: -9999px;
  left: -9999px;
  opacity: 0;
  pointer-events: none;
}

/* 隐藏App.vue中的footer */
.courses ~ .footer {
  display: none !important;
}

/* 隐藏App.vue中的navbar */
.courses ~ .navbar {
  display: none !important;
}

/* 调整courses的top位置，移除导航栏的高度 */
.courses {
  top: 0 !important;
}

.main-content-layout {
  position: relative;
  height: 100%;
  padding: 20px;
}

.filter-sidebar {
  position: absolute;
  left: 110px;
  top: 133px;
  width: 250px;
  flex-shrink: 0;
  z-index: 100;
}

.courses-content {
  margin-left: 290px;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 20px;
  padding-bottom: 20px;
}

/* 自定义滚动条样式 */
.courses-content::-webkit-scrollbar {
  width: 8px;
}

.courses-content::-webkit-scrollbar-track {
  background: rgba(26, 26, 46, 0.5);
  border-radius: 10px;
}

.courses-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd);
  border-radius: 10px;
  transition: all 0.3s ease;
}

.courses-content::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #ff8fab, #7e47c2);
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.5);
}

.filter-card {
  border-radius: 15px;
  padding: 16px;
  --el-card-bg-color: transparent;
}

/* 黑色主题 */
html.dark-theme .filter-card {
  background: linear-gradient(135deg, rgba(26, 26, 26, 0.5) 0%, rgba(22, 33, 62, 0.7) 100%);
  border: 1px solid rgba(156, 39, 176, 0.7);
  box-shadow: 0 0 15px rgba(156, 39, 176, 0.3);
  --el-card-border-color: rgba(156, 39, 176, 0.7);
}

/* 白色主题 */
html.light-theme .filter-card {
  background: linear-gradient(135deg, rgba(240, 240, 240, 0.7) 0%, rgba(224, 224, 224, 0.7) 100%);
  border: 1px solid rgba(204, 204, 204, 0.7);
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
  --el-card-border-color: rgba(204, 204, 204, 0.7);
}

/* 白色主题下的下拉框样式 */
html.light-theme .el-select {
  --el-select-bg-color: #ffffff;
  --el-select-border-color: #dcdfe6;
  --el-select-hover-border-color: #409eff;
  --el-select-input-color: #303133;
}

html.light-theme .el-select-dropdown {
  background-color: #ffffff !important;
  border: 1px solid #dcdfe6 !important;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1) !important;
}

html.light-theme .el-select-dropdown__item {
  color: #303133 !important;
}

html.light-theme .el-select-dropdown__item:hover {
  background-color: #ecf5ff !important;
  color: #409eff !important;
}

html.light-theme .el-select-dropdown__item.selected {
  background-color: #ecf5ff !important;
  color: #409eff !important;
}

.filter-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.video-count {
  font-size: 14px;
  font-weight: bold;
  color: #40c4ff;
  text-shadow: 0 0 5px rgba(64, 196, 255, 0.5);
}

.filter-card-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  color: #ff4757;
  text-shadow: 0 0 5px rgba(255, 71, 87, 0.5);
}

.home-link {
  color: inherit;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.home-link:hover {
  color: #00f5ff;
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.8);
}

.current-video {
  font-size: 14px;
  font-weight: bold;
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.5);
}

.filter-footer {
  margin-top: 20px;
  padding-top: 15px;
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

/* 黑色主题 */
html.dark-theme .filter-footer {
  border-top: 1px solid rgba(156, 39, 176, 0.3);
}

/* 白色主题 */
html.light-theme .filter-footer {
  border-top: 1px solid #e0e0e0;
}

.filter-button {
  flex: 1;
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 1px solid #ff6b9d !important;
  color: white !important;
  border-radius: 20px !important;
  padding: 8px 16px !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
  position: relative;
  overflow: hidden;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 8px !important;
  font-size: 14px !important;
}

.filter-button::before {
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

.filter-button:hover {
  background: linear-gradient(135deg, #ff8fab, #7e47c2) !important;
  border-color: #ff8fab !important;
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6) !important;
  transform: translateY(-2px);
}

.filter-button:active {
  transform: translateY(0) scale(0.98);
}

.filter-item {
  margin-bottom: 20px;
}

.filter-item:last-child {
  margin-bottom: 0;
}

.filter-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

/* 黑色主题 */
html.dark-theme .filter-title {
  color: #ff6b9d;
  text-shadow: 0 0 3px rgba(255, 107, 157, 0.5);
}

/* 白色主题 */
html.light-theme .filter-title {
  color: #333;
  text-shadow: none;
}

.full-width {
  width: 100%;
}

/* 黑色主题 */
html.dark-theme .filter-card :deep(.el-select),
html.dark-theme .filter-card :deep(.el-input) {
  --el-select-bg-color: rgba(26, 26, 46, 0.8);
  --el-select-border-color: #9c27b0;
  --el-select-text-color: #00f5ff;
  --el-input-bg-color: rgba(26, 26, 46, 0.8);
  --el-input-border-color: #9c27b0;
  --el-input-text-color: #00f5ff;
}

html.dark-theme .filter-card :deep(.el-select:hover),
html.dark-theme .filter-card :deep(.el-input__wrapper.is-focus) {
  --el-select-border-color: #ba68c8;
  --el-input-border-color: #ba68c8;
  box-shadow: 0 0 10px rgba(186, 104, 200, 0.5);
}

/* 白色主题 */
html.light-theme .filter-card :deep(.el-select),
html.light-theme .filter-card :deep(.el-input) {
  --el-select-bg-color: white;
  --el-select-border-color: #ccc;
  --el-select-text-color: #333;
  --el-input-bg-color: white;
  --el-input-border-color: #ccc;
  --el-input-text-color: #333;
}

html.light-theme .filter-card :deep(.el-select:hover),
html.light-theme .filter-card :deep(.el-input__wrapper.is-focus) {
  --el-select-border-color: #999;
  --el-input-border-color: #999;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}

.piano-keys-layout {
  width: 100%;
  display: flex;
  justify-content: center;
}

.piano-keys-container {
  display: flex;
  flex-direction: column;
  gap: 0;
  width: 60%;
  max-width: 1200px;
  margin-top: 42px; /* 调整这个值来改变顶部位置 */
}

.piano-key-item {
  width: 58%;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.piano-key-item:nth-child(odd) {
  align-self: flex-start;
  margin-left: 0;
}

.piano-key-item:nth-child(even) {
  align-self: flex-end;
  margin-right: -16%;
}

@media (max-width: 768px) {
  .main-content-layout {
    flex-direction: column;
  }
  
  .filter-sidebar {
    width: 100%;
  }
  
  .piano-keys-container {
    width: 85%;
  }
  
  .piano-key-item {
    width: 70%;
  }
  
  .piano-key-item:nth-child(odd),
  .piano-key-item:nth-child(even) {
    align-self: center;
    margin-left: 0;
    margin-right: 0;
  }
}

.anime-course-card {
  border-radius: 15px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 黑色主题 */
html.dark-theme .anime-course-card {
  background: linear-gradient(135deg, rgba(26, 26, 26, 0.95) 0%, rgba(22, 33, 62, 0.95) 100%);
  border: 1px solid #9c27b0;
  box-shadow: 0 4px 20px rgba(156, 39, 176, 0.3);
}

html.dark-theme .piano-course-item:nth-child(odd) .anime-course-card {
  border-color: #ff6b9d;
  box-shadow: 0 4px 20px rgba(255, 107, 157, 0.3);
}

html.dark-theme .piano-course-item:nth-child(even) .anime-course-card {
  border-color: #40c4ff;
  box-shadow: 0 4px 20px rgba(64, 196, 255, 0.3);
}

/* 白色主题 */
html.light-theme .anime-course-card {
  background: #ffffff;
  border: 1px solid #ccc;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

html.light-theme .piano-course-item:nth-child(odd) .anime-course-card {
  border-color: #ff6b9d;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.2);
}

html.light-theme .piano-course-item:nth-child(even) .anime-course-card {
  border-color: #40c4ff;
  box-shadow: 0 2px 8px rgba(64, 196, 255, 0.2);
}

.anime-course-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #ff6b9d, #5f27cd, #40c4ff, #ff6b9d);
  background-size: 200% 100%;
  animation: gradientShift 3s ease infinite;
}

.anime-course-card:hover {
  transform: translateY(-8px) scale(1.03);
  border-width: 2px;
}

/* 黑色主题 */
html.dark-theme .anime-course-card:hover {
  box-shadow: 0 12px 30px rgba(156, 39, 176, 0.5);
}

html.dark-theme .piano-course-item:nth-child(odd) .anime-course-card:hover {
  box-shadow: 0 12px 30px rgba(255, 107, 157, 0.5);
}

html.dark-theme .piano-course-item:nth-child(even) .anime-course-card:hover {
  box-shadow: 0 12px 30px rgba(64, 196, 255, 0.5);
}

/* 白色主题 */
html.light-theme .anime-course-card:hover {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

html.light-theme .piano-course-item:nth-child(odd) .anime-course-card:hover {
  box-shadow: 0 8px 20px rgba(255, 107, 157, 0.3);
}

html.light-theme .piano-course-item:nth-child(even) .anime-course-card:hover {
  box-shadow: 0 8px 20px rgba(64, 196, 255, 0.3);
}

.course-image {
  height: 168px;
  overflow: hidden;
  position: relative;
}

.image-overlay {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: 12px 12px 0 0;
}

.anime-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2px solid transparent;
  border-radius: 12px 12px 0 0;
  background: linear-gradient(45deg, #ff6b9d, #5f27cd, #40c4ff, #ff6b9d) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  z-index: 10;
  pointer-events: none;
  transition: all 0.3s ease;
}

/* 白色主题 */
html.light-theme .anime-border {
  background: linear-gradient(45deg, #ccc, #999, #ccc) border-box;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
  filter: contrast(1.1) saturate(1.1);
}

.image-overlay:hover img {
  transform: scale(1.1);
}

.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background: rgba(156, 39, 176, 0.8);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  border: 2px solid #00f5ff;
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.5);
}

.play-icon:hover {
  background: rgba(186, 104, 200, 0.9);
  transform: translate(-50%, -50%) scale(1.1);
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.8);
}

.image-overlay:hover .play-icon {
  opacity: 1;
}

.play-symbol {
  font-size: 24px;
  color: #00f5ff;
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.8);
  animation: pulse 2s ease-in-out infinite;
}

.course-info {
  padding: 8px 10px;
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.course-title {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
}

/* 黑色主题 */
html.dark-theme .course-title {
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.5);
}

/* 白色主题 */
html.light-theme .course-title {
  color: #333;
  text-shadow: none;
}

.course-description {
  font-size: 10px;
  margin-bottom: 6px;
  height: 32px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 黑色主题 */
html.dark-theme .course-description {
  color: #81d4fa;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
}

/* 白色主题 */
html.light-theme .course-description {
  color: #666;
  text-shadow: none;
}

.course-meta {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  font-size: 9px;
}

/* 黑色主题 */
html.dark-theme .course-meta {
  color: #81d4fa;
}

/* 白色主题 */
html.light-theme .course-meta {
  color: #666;
}

.course-category {
  margin-right: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

/* 黑色主题 */
html.dark-theme .course-category {
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.2), rgba(103, 194, 58, 0.1));
  color: #67c23a;
  border: 1px solid rgba(103, 194, 58, 0.3);
  text-shadow: 0 0 3px rgba(103, 194, 58, 0.5);
}

html.dark-theme .course-category:hover {
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.3), rgba(103, 194, 58, 0.2));
  box-shadow: 0 0 10px rgba(103, 194, 58, 0.4);
}

/* 白色主题 */
html.light-theme .course-category {
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.1), rgba(103, 194, 58, 0.05));
  color: #529b2e;
  border: 1px solid rgba(103, 194, 58, 0.2);
  text-shadow: none;
}

html.light-theme .course-category:hover {
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.2), rgba(103, 194, 58, 0.1));
  box-shadow: 0 0 10px rgba(103, 194, 58, 0.3);
}

.course-level {
  margin-right: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

/* 黑色主题 */
html.dark-theme .course-level {
  background: linear-gradient(135deg, rgba(64, 196, 255, 0.2), rgba(64, 196, 255, 0.1));
  color: #40c4ff;
  border: 1px solid rgba(64, 196, 255, 0.3);
  text-shadow: 0 0 3px rgba(64, 196, 255, 0.5);
}

html.dark-theme .course-level:hover {
  background: linear-gradient(135deg, rgba(64, 196, 255, 0.3), rgba(64, 196, 255, 0.2));
  box-shadow: 0 0 10px rgba(64, 196, 255, 0.4);
}

/* 白色主题 */
html.light-theme .course-level {
  background: linear-gradient(135deg, rgba(64, 196, 255, 0.1), rgba(64, 196, 255, 0.05));
  color: #29b6f6;
  border: 1px solid rgba(64, 196, 255, 0.2);
  text-shadow: none;
}

html.light-theme .course-level:hover {
  background: linear-gradient(135deg, rgba(64, 196, 255, 0.2), rgba(64, 196, 255, 0.1));
  box-shadow: 0 0 10px rgba(64, 196, 255, 0.3);
}

.course-rating {
  flex: 1;
  text-align: right;
}

.course-rating :deep(.el-rate__text) {
  color: #feca57 !important;
  text-shadow: 0 0 3px rgba(254, 202, 87, 0.5);
}

.course-rating :deep(.el-rate__icon) {
  color: #feca57 !important;
  text-shadow: 0 0 5px rgba(254, 202, 87, 0.8);
}

.view-button {
  width: 100%;
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 1px solid #ff6b9d !important;
  color: white !important;
  border-radius: 20px !important;
  padding: 5px 10px !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
  position: relative;
  overflow: hidden;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 4px !important;
  margin-top: auto;
}

.view-button::before {
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

.view-button:hover {
  background: linear-gradient(135deg, #ff8fab, #7e47c2) !important;
  border-color: #ff8fab !important;
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6) !important;
  transform: translateY(-2px);
}

.view-button:active {
  transform: translateY(0) scale(0.98);
}

.button-text {
  z-index: 2;
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
  font-size: 10px;
}

.button-icon {
  z-index: 2;
  font-size: 10px;
  transition: transform 0.3s ease;
}

.view-button:hover .button-icon {
  transform: translateX(5px);
}

.empty-state {
  margin: 40px 0;
  text-align: center;
}

.login-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  position: absolute;
  top: 50%;
  left: 70%;
  transform: translate(-50%, -50%);
  z-index: 10;
}

.login-button {
  margin-top: 20px;
  width: 120px;
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 1px solid #ff6b9d !important;
  color: white !important;
  border-radius: 20px !important;
  padding: 8px 16px !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
}

.login-button:hover {
  background: linear-gradient(135deg, #ff8fab, #7e47c2) !important;
  border-color: #ff8fab !important;
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6) !important;
  transform: translateY(-2px);
}

/* 自定义空状态样式 */
.custom-empty {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.empty-icon {
  font-size: 48px;
  opacity: 0.8;
}

.empty-description {
  font-size: 14px;
  color: #22e06eff;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
}

.login-text {
  color: #00f5ff !important;
  text-shadow: 0 0 5px rgba(0, 245, 255, 0.5) !important;
  font-weight: normal !important;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.pagination :deep(.el-pagination__total),
.pagination :deep(.el-pagination__sizes),
.pagination :deep(.el-pagination__jump),
.pagination :deep(.el-pagination__text) {
  color: #81d4fa;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
}

.pagination :deep(.el-pagination__item),
.pagination :deep(.el-pagination__prev),
.pagination :deep(.el-pagination__next) {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.8), rgba(22, 33, 62, 0.8));
  border-color: #9c27b0;
  color: #40c4ff;
  text-shadow: 0 0 3px rgba(64, 196, 255, 0.5);
  transition: all 0.3s ease;
}

.pagination :deep(.el-pagination__item:hover),
.pagination :deep(.el-pagination__prev:hover),
.pagination :deep(.el-pagination__next:hover) {
  border-color: #ba68c8;
  color: #00f5ff;
  box-shadow: 0 0 10px rgba(186, 104, 200, 0.4);
}

.pagination :deep(.el-pagination__item.active) {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd);
  border-color: #ff6b9d;
  color: white;
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.5);
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.6);
}

/* 动画效果 */
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

@keyframes borderGlow {
  0%, 100% {
    box-shadow: 0 0 10px rgba(156, 39, 176, 0.3);
  }
  50% {
    box-shadow: 0 0 20px rgba(156, 39, 176, 0.6);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

@keyframes shine {
  0% {
    transform: translateX(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) rotate(45deg);
  }
}
</style>