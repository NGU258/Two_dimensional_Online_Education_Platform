<template>
  <div class="home" ref="homeRef">
    <!-- 全屏视频背景 -->
    <div class="video-container">
      <video 
        ref="videoRef" 
        class="background-video" 
        autoplay 
        loop 
        playsinline
  
      >
        <source src="/videos/index/start.mp4" type="video/mp4">
        您的浏览器不支持视频播放
      </video>

      <!-- 边缘渐变层 -->
      <div class="video-gradient"></div>
      <!-- 视频覆盖层 -->
      <div class="video-overlay" :class="{ 'show-overlay': showOverlay }">
        <div class="video-text" :class="{ 'show-text': showText }">
          <h1 class="video-title">踏入你的专属次元</h1>
          <p class="video-subtitle">让学习成为一场舒适的探索之旅</p>
        </div>
        <el-button type="primary" size="large" @click="startExploring" class="start-explore-btn">开始学习</el-button>
      </div>
    </div>

    <!-- 轮播图 -->
    <div class="banner">
      <el-carousel ref="bannerCarousel" height="400px" indicator-position="outside">
        <el-carousel-item v-for="(item, index) in bannerList" :key="index">
          <div class="carousel-item">
            <img :src="item.image" :alt="item.title" />
            <div class="carousel-content">
              <h2>{{ item.title }}</h2>
              <p>{{ item.description }}</p>
              <el-button type="primary" size="large" @click="item.action">立即查看</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 分类导航 -->
    <div class="category-nav">
      <div class="category-container">
        <div class="category-item active" @click="goToCategory(0)">
          <div class="category-icon">🏠</div>
          <span class="category-name">首页</span>
        </div>
        <div 
          v-for="category in categories" 
          :key="category.id" 
          class="category-item"
          @click="goToCategory(category.id)"
        >
          <div class="category-icon">{{ category.icon }}</div>
          <span class="category-name">{{ category.name }}</span>
        </div>
      </div>
    </div>

    <!-- 新游预告 -->
    <div class="courses-section">
      <div class="section-header">
        <h2>新游预告</h2>
        <div class="section-nav">
          <span class="nav-item active">乱彩世界</span>
          <span class="nav-item">幻想少女公会</span>
          <span class="nav-item">三国：百将传</span>
          <span class="nav-item">鬼畜</span>
          <span class="nav-item">舞蹈</span>
          <span class="nav-item">娱乐</span>
          <span class="nav-item">异环</span>
          <span class="nav-item">空之回声</span>
          <span class="nav-item">音乐</span>
          <span class="nav-item">影视</span>
          <span class="nav-item">知识</span>
        </div>
      </div>
      <div v-if="userStore.isLoggedIn" class="carousel-container">
        <div class="carousel-controls left">
          <el-button type="primary" circle @click="prevNewGamesSlide" class="carousel-button">
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
        </div>
        <el-carousel ref="newGamesCarouselRef" v-model="newGamesCarouselCurrent" :interval="5000" :loop="true" :show-indicators="false" :arrow="'never'" class="course-carousel">
          <el-carousel-item v-for="(courseGroup, index) in newGamesCourseGroups" :key="index">
            <div class="course-group">
              <div v-for="course in courseGroup" :key="course.id" class="course-item">
                <el-card :body-style="{ padding: '0' }" class="anime-course-card">
                  <div class="course-image">
                    <div class="image-overlay">
                      <div class="anime-border"></div>
                      <img :src="course.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20course%20cover%20illustration&image_size=landscape_16_9'" :alt="course.title" />
                      <div class="play-icon" @click="() => { goToCourseDetail(course.id); playSound('点击音效1'); }">
                        <el-icon class="play-symbol"><VideoPlay /></el-icon>
                      </div>
                    </div>
                  </div>
                  <div class="course-info">
                    <h3 class="course-title">{{ course.title }}</h3>
                    <p class="course-description">{{ course.description || '暂无描述' }}</p>
                    <div class="course-meta">
                      <span class="course-category">{{ getCategoryName(course.categoryId) }}</span>
                      <span class="course-level">{{ course.level }}</span>
                      <span class="course-rating">
                        <el-rate v-model="course.rating" disabled :max="5" show-score />
                      </span>
                    </div>
                    <el-button type="primary" @click="() => { goToCourseDetail(course.id); playSound('点击音效1'); }" class="view-button">
                      <span class="button-text">查看详情</span>
                      <el-icon class="button-icon"><ArrowRight /></el-icon>
                    </el-button>
                  </div>
                </el-card>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
        <div class="carousel-controls right">
          <el-button type="primary" circle @click="nextNewGamesSlide" class="carousel-button">
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
      <div v-else class="login-prompt">
        <div class="prompt-content">
          <el-icon class="prompt-icon"><View /></el-icon>
          <h3>需要登录才能查看内容</h3>
          <p>请先登录您的账号，然后才能浏览和观看视频内容</p>
          <div class="prompt-buttons">
            <el-button type="primary" @click="() => { router.push('/login'); playSound('点击音效1'); }">
              立即登录
            </el-button>
            <el-button @click="() => { router.push('/register'); playSound('点击音效1'); }">
              注册账号
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐课程 -->
    <div class="courses-section">
      <div class="section-header">
        <h2>推荐课程</h2>
        <router-link to="/courses" class="more-link">查看更多</router-link>
      </div>
      <div v-if="userStore.isLoggedIn" class="carousel-container">
        <div class="carousel-controls left">
          <el-button type="primary" circle @click="prevSlide" class="carousel-button">
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
        </div>
        <el-carousel ref="carouselRef" v-model="carouselCurrent" :interval="5000" :loop="true" :show-indicators="false" :arrow="'never'" class="course-carousel">
          <el-carousel-item v-for="(courseGroup, index) in courseGroups" :key="index">
            <div class="course-group">
              <div v-for="course in courseGroup" :key="course.id" class="course-item">
                <el-card :body-style="{ padding: '0' }" class="anime-course-card">
                  <div class="course-image">
                    <div class="image-overlay">
                      <div class="anime-border"></div>
                      <img :src="course.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20course%20cover%20illustration&image_size=landscape_16_9'" :alt="course.title" />
                      <div class="play-icon" @click="() => { goToCourseDetail(course.id); playSound('点击音效1'); }">
                        <el-icon class="play-symbol"><VideoPlay /></el-icon>
                      </div>
                    </div>
                  </div>
                  <div class="course-info">
                    <h3 class="course-title">{{ course.title }}</h3>
                    <p class="course-description">{{ course.description || '暂无描述' }}</p>
                    <div class="course-meta">
                      <span class="course-category">{{ getCategoryName(course.categoryId) }}</span>
                      <span class="course-level">{{ course.level }}</span>
                      <span class="course-rating">
                        <el-rate v-model="course.rating" disabled :max="5" show-score />
                      </span>
                    </div>
                    <el-button type="primary" @click="() => { goToCourseDetail(course.id); playSound('点击音效1'); }" class="view-button">
                      <span class="button-text">查看详情</span>
                      <el-icon class="button-icon"><ArrowRight /></el-icon>
                    </el-button>
                  </div>
                </el-card>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
        <div class="carousel-controls right">
          <el-button type="primary" circle @click="nextSlide" class="carousel-button">
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
      <div v-else class="login-prompt">
        <div class="prompt-content">
          <el-icon class="prompt-icon"><View /></el-icon>
          <h3>需要登录才能查看内容</h3>
          <p>请先登录您的账号，然后才能浏览和观看视频内容</p>
          <div class="prompt-buttons">
            <el-button type="primary" @click="() => { router.push('/login'); playSound('点击音效1'); }">
              立即登录
            </el-button>
            <el-button @click="() => { router.push('/register'); playSound('点击音效1'); }">
              注册账号
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, onUnmounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { useCourseStore } from '../store/course'
import { View, Message, VideoPlay, ArrowRight, ArrowLeft, ArrowRight as ArrowRightIcon } from '@element-plus/icons-vue'

// 音效播放函数
const playSound = (soundName) => {
  try {
    const audio = new Audio(`/audio/${encodeURIComponent(soundName)}.mp3`)
    // 增大刀盾音效的音量，覆盖主页视频音量
    audio.volume = 1.0
    audio.play().catch(err => {
      console.log('音频播放失败:', err)
    })
  } catch (error) {
    console.log('音效播放错误:', error)
  }
}

const router = useRouter()
const userStore = useUserStore()
const courseStore = useCourseStore()

// 滚动效果
const homeRef = ref(null)
// 视频引用和显示状态
const videoRef = ref(null)
const showOverlay = ref(true)
const showText = ref(true)

// 轮播控制
const carouselCurrent = ref(0)
const carouselRef = ref(null)

// 新游预告轮播控制
const newGamesCarouselCurrent = ref(0)
const newGamesCarouselRef = ref(null)

// 主轮播图控制
const bannerCarousel = ref(null)

// 轮播方法
const prevSlide = () => {
  if (carouselRef.value) {
    carouselRef.value.prev()
    playSound('点击音效1')
  }
}

const nextSlide = () => {
  if (carouselRef.value) {
    carouselRef.value.next()
    playSound('点击音效1')
  }
}

// 新游预告轮播方法
const prevNewGamesSlide = () => {
  if (newGamesCarouselRef.value) {
    newGamesCarouselRef.value.prev()
    playSound('点击音效1')
  }
}

const nextNewGamesSlide = () => {
  if (newGamesCarouselRef.value) {
    newGamesCarouselRef.value.next()
    playSound('点击音效1')
  }
}

// 用户交互事件处理函数
const handleUserInteraction = (event) => {
  if (videoRef.value) {
    // 只有当滚动位置在200px以内时，才解除静音
    if (window.pageYOffset <= 200) {
      // 尝试解除静音并播放视频
      videoRef.value.muted = false
      videoRef.value.play().catch(err => {
        console.log('视频播放失败:', err)
      })
    }
  }
}

const handleScroll = () => {
  console.log('滚动事件触发')
  
  if (homeRef.value) {
    if (window.pageYOffset > 50) {
      homeRef.value.classList.add('scrolled')
    } else {
      homeRef.value.classList.remove('scrolled')
    }
  }
  
  // 尝试多种方法获取滚动位置
  const scrollTop1 = window.pageYOffset
  const scrollTop2 = document.documentElement.scrollTop
  const scrollTop3 = document.body.scrollTop
  
  // 使用最大的值作为滚动位置
  const scrollTop = Math.max(scrollTop1, scrollTop2, scrollTop3)
  
  // 调试信息
  console.log('滚动位置 - pageYOffset:', scrollTop1)
  console.log('滚动位置 - documentElement.scrollTop:', scrollTop2)
  console.log('滚动位置 - body.scrollTop:', scrollTop3)
  console.log('最终滚动位置:', scrollTop)
  
  // 当滚动超过200px时设置暂停标志，回到200px以内时清除暂停标志
  if (scrollTop > 700) {
    if (!shouldPauseVideo.value) {
      console.log('滚动超过200px，设置视频暂停标志')
      shouldPauseVideo.value = true
    }
  } else {
    if (shouldPauseVideo.value) {
      console.log('滚动在200px以内，清除视频暂停标志')
      shouldPauseVideo.value = false
    }
  }
  
  // 根据暂停标志控制视频播放状态
  if (videoRef.value) {
    console.log('视频元素存在，当前播放状态:', !videoRef.value.paused, '暂停标志:', shouldPauseVideo.value)
    if (shouldPauseVideo.value && !videoRef.value.paused) {
      console.log('执行视频暂停')
      videoRef.value.pause()
      console.log('视频已暂停，当前状态:', videoRef.value.paused)
    } else if (!shouldPauseVideo.value && videoRef.value.paused) {
      console.log('执行视频播放')
      videoRef.value.play().catch(err => {
        console.log('视频播放失败:', err)
      })
      console.log('视频已恢复播放，当前状态:', !videoRef.value.paused)
    }
  } else {
    console.log('视频元素不存在')
  }
}

// 视频时间更新处理函数
const handleTimeUpdate = () => {
  if (!videoRef.value) return
  
  try {
    const video = videoRef.value
    const duration = video.duration
    const currentTime = video.currentTime
    
    // 检查 duration 是否为 NaN
    if (isNaN(duration)) return
    
    // 视频开始时（前1.8秒）显示黑色覆盖层和文字
    if (currentTime < 1.55) {
      showOverlay.value = true
      showText.value = true
    }
    // 视频结束前1.8秒显示覆盖层和按钮
    else if (duration - currentTime < 2.4) {
      showOverlay.value = true
      showText.value = false
    }
    // 其他时间隐藏
    else {
      showOverlay.value = false
      showText.value = false
    }
  } catch (error) {
    console.log('视频时间更新处理错误:', error)
  }
}

// 视频是否应该被暂停的标志
const shouldPauseVideo = ref(false)

onMounted(() => {
  console.log('组件已挂载，添加滚动事件监听器')
  // 使用捕获阶段监听滚动事件，确保不会被阻止
  window.addEventListener('scroll', handleScroll, { capture: true, passive: true })
  handleScroll() // 初始化状态
  
  // 测试滚动事件是否能触发
  console.log('测试滚动事件监听器是否添加成功')
  
  // 添加视频事件监听
  if (videoRef.value) {
    console.log('视频元素存在，初始化视频')
    try {
      // 确保视频元素的属性正确设置
      videoRef.value.autoplay = true
      videoRef.value.loop = true
      videoRef.value.playsinline = true
      
      // 设置视频音量为30%
      videoRef.value.volume = 0.25
      
      // 初始时解除静音，让视频有声音
      videoRef.value.muted = false
      
      // 尝试自动播放视频
      const playVideo = () => {
        if (!videoRef.value) return
        
        videoRef.value.play().catch(err => {
          console.log('视频自动播放失败:', err)
        })
      }
      
      // 立即尝试播放
      playVideo()

      // 监听视频时间更新
      videoRef.value.addEventListener('timeupdate', handleTimeUpdate)
      
      // 监听视频结束事件，确保循环播放
      videoRef.value.addEventListener('ended', () => {
        if (videoRef.value) {
          videoRef.value.currentTime = 0
          playVideo()
        }
      })

      // 初始检查
      setTimeout(() => {
        handleTimeUpdate()
      }, 100)
    } catch (error) {
      console.log('视频初始化错误:', error)
    }
  }
  
  // 添加必要的交互事件监听器
  document.addEventListener('click', handleUserInteraction)
  
  // 直接在视频元素上添加点击事件监听器
  if (videoRef.value) {
    videoRef.value.addEventListener('click', handleUserInteraction)
  }
  
  // 非阻塞方式获取数据 - 使用 setTimeout 放到微任务队列末尾
  setTimeout(() => {
    // 获取课程列表
    courseStore.getCourses().catch(err => {
      console.log('获取课程列表失败:', err)
    })
    
    // 如果已登录，获取用户信息
    if (userStore.isLoggedIn) {
      userStore.getUserInfo().catch(err => {
        console.log('获取用户信息失败:', err)
      })
    }
  }, 0)
  
  // 设置轮播图默认显示第二张
  setTimeout(() => {
    if (bannerCarousel.value) {
      bannerCarousel.value.setActiveItem(1)
    }
  }, 100)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  // 移除视频事件监听
  if (videoRef.value) {
    videoRef.value.removeEventListener('timeupdate', handleTimeUpdate)
    videoRef.value.removeEventListener('ended', () => {
      if (videoRef.value) {
        videoRef.value.currentTime = 0
        videoRef.value.play()
      }
    })
  }
  
  // 移除用户交互事件监听器
  document.removeEventListener('click', handleUserInteraction)
  
  if (videoRef.value) {
    videoRef.value.removeEventListener('click', handleUserInteraction)
  }
})

const bannerList = ref([
  {
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20education%20banner%20with%20colorful%20characters%20and%20study%20theme&image_size=landscape_16_9',
    title: '二次元绘画基础课程',
    description: '从零开始学习二次元绘画技巧，成为绘画达人',
    action: () => router.push('/courses')
  },
  {
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20game%20design%20banner%20with%20characters%20and%20game%20elements&image_size=landscape_16_9',
    title: '游戏原画创作课程',
    description: '学习游戏原画创作技巧，设计属于你的游戏角色',
    action: () => router.push('/courses')
  },
  {
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20animation%20banner%20with%20moving%20characters&image_size=landscape_16_9',
    title: '动漫角色设计课程',
    description: '掌握动漫角色设计的核心要素，创作独特的角色形象',
    action: () => router.push('/courses')
  }
])

const categories = ref([
  { id: 1, name: '绘画', icon: '🎨' },
  { id: 2, name: '动画', icon: '📺' },
  { id: 3, name: '游戏', icon: '🎮' },
  { id: 4, name: '设计', icon: '🎯' },
  { id: 5, name: '编程', icon: '💻' },
  { id: 6, name: '音乐', icon: '🎵' },
  { id: 7, name: '写作', icon: '✍️' },
  { id: 8, name: '更多', icon: '📚' }
])

// 获取分类名称
const getCategoryName = (id) => {
  const category = categories.value.find(c => c.id === id)
  return category ? category.name : '未分类'
}

// 推荐课程 - 按评分排序的前6个课程
const recommendedCourses = computed(() => {
  return [...courseStore.courses]
    .sort((a, b) => (b.rating || 0) - (a.rating || 0))
    .slice(0, 6)
})

// 将推荐课程分成每组两个
const courseGroups = computed(() => {
  const groups = []
  for (let i = 0; i < recommendedCourses.value.length; i += 2) {
    groups.push(recommendedCourses.value.slice(i, i + 2))
  }
  return groups
})

// 新游预告课程分组
const newGamesCourseGroups = computed(() => {
  const groups = []
  for (let i = 0; i < courseStore.courses.length; i += 2) {
    groups.push(courseStore.courses.slice(i, i + 2))
  }
  return groups
})

const goToCourseDetail = (courseId) => {
  router.push(`/course/${courseId}?from=home`)
}

const goToCategory = (categoryId) => {
  if (categoryId === 0) {
    router.push('/')
  } else {
    router.push(`/courses?categoryId=${categoryId}`)
  }
}

const startExploring = () => {
  // 随机选择一个点击音效
  const soundOptions = ['点击音效1', '点击音效2', '点击音效3']
  const randomSound = soundOptions[Math.floor(Math.random() * soundOptions.length)]
  playSound(randomSound)
  router.push('/courses')
}
</script>

<style scoped>
.home {
  margin: 0;
  padding: 0;
  width: 100%;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
}

/* 黑色主题 */
html.dark-theme .home {
  background-color: black;
}

/* 白色主题 */
html.light-theme .home {
  background-color: #f0f0f0;
}

/* 视频容器样式 */
.video-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
  z-index: 0;
  border-radius: 0;
  box-shadow: none;
  max-width: none;
  border: none;
  outline: none;
  height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #0a0a0f 0%, #1a1a2e 50%, #0a0a0f 100%);
}

.background-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
  border-radius: 0;
  margin: 0;
  padding: 0;
  border: none;
  min-width: 100%;
  min-height: 100%;
}

.video-gradient {   
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    linear-gradient(to bottom, rgba(0,0,0,1), rgba(0,0,0,0) 5%, rgba(0,0,0,0)),
    linear-gradient(to right, rgba(0,0,0,0.8), rgba(0,0,0,0) 5%, rgba(0,0,0,0)),
    linear-gradient(to left, rgba(0,0,0,1), rgba(0,0,0,0) 15%, rgba(0,0,0,0));
  z-index: 2;
  pointer-events: none;
  border-radius: 0;
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: transparent;
  z-index: 3;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  text-align: center;
  color: white;
  padding: 0 20px 80px;
  transition: all 0.5s ease;
  opacity: 0;
  visibility: hidden;
  border-radius: 0;
}

.video-overlay.show-overlay {
  opacity: 1;
  visibility: visible;
}

/* 开始探索按钮样式 */
.start-explore-btn {
  position: relative;
  overflow: hidden;
  border-radius: 30px;
  padding: 15px 40px;
  font-size: 18px;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 2px;
  background: linear-gradient(45deg, #40c4ff, #5f27cd, #ff6b9d, #40c4ff);
  background-size: 400% 400%;
  border: none;
  color: white;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
  box-shadow: 0 0 20px rgba(64, 196, 255, 0.5), 0 0 40px rgba(95, 39, 205, 0.3);
  transition: all 0.3s ease;
  animation: buttonGlow 2s ease-in-out infinite, gradientShift 3s ease infinite, buttonPulse 2s ease-in-out infinite;
  cursor: pointer;
}

.start-explore-btn::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transform: rotate(45deg);
  animation: buttonShine 3s ease-in-out infinite;
  pointer-events: none;
}

.start-explore-btn:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 0 30px rgba(64, 196, 255, 0.8), 0 0 60px rgba(95, 39, 205, 0.6);
  text-shadow: 0 0 15px rgba(255, 255, 255, 1);
}

.start-explore-btn:active {
  transform: translateY(0) scale(0.98);
  box-shadow: 0 0 15px rgba(64, 196, 255, 0.6), 0 0 30px rgba(95, 39, 205, 0.4);
}

@keyframes buttonGlow {
  0%, 100% {
    box-shadow: 0 0 20px rgba(64, 196, 255, 0.5), 0 0 40px rgba(95, 39, 205, 0.3);
  }
  50% {
    box-shadow: 0 0 30px rgba(64, 196, 255, 0.8), 0 0 60px rgba(95, 39, 205, 0.6);
  }
}

@keyframes buttonShine {
  0% {
    transform: translateX(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) rotate(45deg);
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

@keyframes buttonPulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 20px rgba(64, 196, 255, 0.5), 0 0 40px rgba(95, 39, 205, 0.3);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 0 30px rgba(64, 196, 255, 0.8), 0 0 60px rgba(95, 39, 205, 0.6);
  }
}

.video-text {
  opacity: 0;
  visibility: hidden;
  transition: all 0.5s ease;
  margin-bottom: 12px;
  height: 0;
  overflow: hidden;
  text-align: center;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.video-text.show-text {
  opacity: 1;
  visibility: visible;
  height: auto;
  overflow: visible;
}

.video-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 12px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.8);
  color: #40c4ff;
  animation: fadeInUp 1s ease-out;
  max-width: 600px;
  line-height: 1.2;
  letter-spacing: 1px;
  font-family: 'Arial', sans-serif;
}

.video-subtitle {
  font-size: 18px;
  margin-bottom: 24px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.8);
  color: #81d4fa;
  animation: fadeInUp 1s ease-out 0.3s both;
  max-width: 400px;
  line-height: 1.3;
  font-family: 'Arial', sans-serif;
}



/* 滚动时的效果 */
.home.scrolled .video-overlay {
  opacity: 0.7;
}

.home.scrolled .video-title,
.home.scrolled .video-subtitle {
  transform: translateY(20px);
  opacity: 0;
  transition: all 0.5s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 开始探索按钮样式 */
.video-overlay .el-button {
  animation: fadeInUp 1s ease-out 0.6s both;
  background: rgba(64, 196, 255, 0.8);
  border: 2px solid rgba(129, 212, 250, 0.5);
  color: white;
  font-size: 16px;
  font-weight: bold;
  padding: 12px 32px;
  border-radius: 25px;
  box-shadow: 0 2px 8px rgba(64, 196, 255, 0.4);
  transition: all 0.3s ease;
  letter-spacing: 1px;
}

.video-overlay .el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(64, 196, 255, 0.6);
  background: rgba(64, 196, 255, 0.9);
}

/* 轮播图样式 */
.banner {
  margin-top: 100vh;
  margin-bottom: 30px;
}

.carousel-item {
  position: relative;
  height: 100%;
  overflow: hidden;
}

.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-content {
  position: absolute;
  top: 50%;
  left: 10%;
  transform: translateY(-50%);
  color: #40c4ff;
  max-width: 500px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.carousel-content h2 {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 16px;
}

.carousel-content p {
  font-size: 18px;
  margin-bottom: 24px;
  opacity: 0.9;
  color: #81d4fa;
}

/* 分类导航样式 */
.category-nav {
  padding: 20px 0;
  margin-bottom: 30px;
  border-top: 1px solid #333;
  border-bottom: 1px solid #333;
}

.category-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 20px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  min-width: 80px;
}

.category-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.category-name {
  font-size: 14px;
}

/* 黑色主题 */
html.dark-theme .category-nav {
  background-color: #1a1a1a;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  border-top: 1px solid #333;
  border-bottom: 1px solid #333;
}

html.dark-theme .category-item {
  background-color: #2a2a2a;
  border: 1px solid #333;
}

html.dark-theme .category-item:hover {
  background-color: rgba(64, 196, 255, 0.1);
  transform: translateY(-5px);
  border-color: #40c4ff;
}

html.dark-theme .category-item.active {
  background-color: rgba(64, 196, 255, 0.2);
  border-color: #40c4ff;
  box-shadow: 0 2px 8px rgba(64, 196, 255, 0.3);
}

html.dark-theme .category-name {
  color: #40c4ff;
}

/* 白色主题 */
html.light-theme .category-nav {
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-top: 1px solid #e0e0e0;
  border-bottom: 1px solid #e0e0e0;
}

html.light-theme .category-item {
  background-color: #f8f8f8;
  border: 1px solid #e0e0e0;
}

html.light-theme .category-item:hover {
  background-color: rgba(255, 107, 157, 0.1);
  transform: translateY(-5px);
  border-color: #ff6b9d;
}

html.light-theme .category-item.active {
  background-color: rgba(255, 107, 157, 0.2);
  border-color: #ff6b9d;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.3);
}

html.light-theme .category-name {
  color: #333;
}

/* 课程区域样式 */
.courses-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  flex-wrap: wrap;
  gap: 10px;
}

.section-header h2 {
  font-size: 24px;
  font-weight: bold;
  flex-shrink: 0;
}

.section-nav {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 5px;
  flex: 1;
  min-width: 0;
}

.section-nav::-webkit-scrollbar {
  height: 4px;
}

.section-nav::-webkit-scrollbar-track {
  border-radius: 2px;
}

.section-nav::-webkit-scrollbar-thumb {
  border-radius: 2px;
}

.nav-item {
  font-size: 14px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.nav-item:hover {
  transition: all 0.3s ease;
}

.nav-item.active {
  font-weight: bold;
}

.more-link {
  text-decoration: none;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.more-link:hover {
  transition: all 0.3s ease;
}

.courses-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.course-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-5px);
  transition: all 0.3s ease;
}

.course-cover {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-cover img {
  transform: scale(1.05);
}

.course-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #333;
}

.course-info {
  padding: 16px;
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-description {
  font-size: 14px;
  margin-bottom: 12px;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 黑色主题 */
html.dark-theme .section-header {
  border-bottom: 1px solid #333;
}

html.dark-theme .section-header h2 {
  color: #40c4ff;
}

html.dark-theme .section-nav::-webkit-scrollbar-track {
  background: #1a1a1a;
}

html.dark-theme .section-nav::-webkit-scrollbar-thumb {
  background: #333;
}

html.dark-theme .nav-item {
  color: #81d4fa;
}

html.dark-theme .nav-item:hover {
  color: #40c4ff;
  background-color: rgba(64, 196, 255, 0.1);
}

html.dark-theme .nav-item.active {
  color: #40c4ff;
  background-color: rgba(64, 196, 255, 0.2);
}

html.dark-theme .more-link {
  color: #40c4ff;
}

html.dark-theme .more-link:hover {
  color: #81d4fa;
}

html.dark-theme .course-card {
  background-color: #1a1a1a;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  border: 1px solid #333;
}

html.dark-theme .course-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.7);
  border-color: #40c4ff;
}

html.dark-theme .course-duration {
  background-color: rgba(0, 0, 0, 0.8);
  color: #40c4ff;
  border: 1px solid #333;
}

html.dark-theme .course-title {
  color: #40c4ff;
}

html.dark-theme .course-description {
  color: #81d4fa;
}

html.dark-theme .course-stats {
  color: #81d4fa;
}

/* 白色主题 */
html.light-theme .section-header {
  border-bottom: 1px solid #e0e0e0;
}

html.light-theme .section-header h2 {
  color: #333;
}

html.light-theme .section-nav::-webkit-scrollbar-track {
  background: #f0f0f0;
}

html.light-theme .section-nav::-webkit-scrollbar-thumb {
  background: #ccc;
}

html.light-theme .nav-item {
  color: #666;
}

html.light-theme .nav-item:hover {
  color: #333;
  background-color: rgba(255, 107, 157, 0.1);
}

html.light-theme .nav-item.active {
  color: #333;
  background-color: rgba(255, 107, 157, 0.2);
}

html.light-theme .more-link {
  color: #ff6b9d;
}

html.light-theme .more-link:hover {
  color: #ff8fab;
}

html.light-theme .course-card {
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e0e0e0;
}

html.light-theme .course-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  border-color: #ff6b9d;
}

html.light-theme .course-duration {
  background-color: rgba(255, 255, 255, 0.9);
  color: #333;
  border: 1px solid #e0e0e0;
}

html.light-theme .course-title {
  color: #333;
}

html.light-theme .course-description {
  color: #666;
}

html.light-theme .course-stats {
  color: #999;
}

/* 课程卡片样式 */
.piano-keys-container {
  display: flex;
  flex-direction: column;
  gap: 0;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.piano-key-item {
  width: 30%;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  margin: 0 auto 20px;
}

.piano-key-item:nth-child(odd) {
  align-self: flex-start;
  margin-left: 15%;
}

.piano-key-item:nth-child(even) {
  align-self: flex-end;
  margin-right: 15%;
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

/* 白色主题 */
html.light-theme .anime-course-card {
  background: #ffffff;
  border: 1px solid #ccc;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

/* 白色主题 */
html.light-theme .anime-course-card:hover {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
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
  cursor: pointer;
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
  cursor: pointer;
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

/* 轮播容器样式 */
.carousel-container {
  position: relative;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
}

.carousel-controls {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
}

.carousel-controls.left {
  left: -5px;
}

.carousel-controls.right {
  right: -5px;
}

.carousel-button {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(45deg, #40c4ff, #5f27cd, #ff6b9d, #40c4ff) !important;
  background-size: 400% 400% !important;
  border: 1px solid #40c4ff !important;
  color: white !important;
  box-shadow: 0 0 15px rgba(64, 196, 255, 0.5) !important;
  animation: gradientShift 3s ease infinite !important;
  transition: all 0.3s ease !important;
}

.carousel-button:hover {
  transform: scale(1.1) !important;
  box-shadow: 0 0 20px rgba(64, 196, 255, 0.8) !important;
}

.course-carousel {
  width: 100%;
  margin: 0 20px;
  overflow: hidden;
}

.course-carousel :deep(.el-carousel) {
  overflow: hidden;
}

.course-carousel :deep(.el-carousel__container) {
  overflow: hidden;
}

.course-carousel :deep(.el-carousel__item) {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
  margin: 0;
  width: 100% !important;
}

.course-group {
  display: flex;
  gap: 20px;
  width: 100%;
  justify-content: center;
  padding: 0 20px;
}

.course-item {
  width: 48%;
  max-width: 500px;
  flex-shrink: 0;
}

/* 登录提示样式 */
.login-prompt {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  background: url('/images/1.jpg') no-repeat center center;
  background-size: cover;
  border-radius: 10px;
  border: 1px solid rgba(0, 255, 0, 0.3);
  box-shadow: 0 0 30px rgba(0, 255, 0, 0.2);
  margin: 20px 0;
}

.prompt-content {
  text-align: center;
  padding: 40px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 8px;
  border: 1px solid rgba(0, 255, 0, 0.4);
  box-shadow: 0 0 20px rgba(0, 255, 0, 0.3);
}

.prompt-icon {
  font-size: 64px;
  color: #00ff00;
  margin-bottom: 20px;
  animation: pulse 2s infinite;
}

.prompt-content h3 {
  color: #ff0000;
  margin-bottom: 15px;
  font-size: 24px;
  font-weight: 700;
}

.prompt-content p {
  color: #b0ffb0;
  margin-bottom: 30px;
  font-size: 16px;
}

.prompt-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.prompt-buttons .el-button {
  min-width: 120px;
  padding: 10px 20px;
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.prompt-buttons .el-button:hover {
  transform: scale(1.05);
  box-shadow: 0 0 15px rgba(0, 255, 0, 0.5);
}

.prompt-buttons .el-button:not(.el-button--primary) {
  background: transparent !important;
  border: 2px solid #00ff00 !important;
  color: #00ff00 !important;
  border-radius: 25px;
  transition: all 0.3s ease;
}

.prompt-buttons .el-button:not(.el-button--primary):hover {
  background: rgba(0, 255, 0, 0.2) !important;
  box-shadow: 0 0 20px rgba(0, 255, 0, 0.6);
  transform: scale(1.05);
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .video-title {
    font-size: 32px;
  }

  .video-subtitle {
    font-size: 18px;
  }

  .video-container {
    height: 70vh;
  }

  .carousel-content {
    left: 5%;
    max-width: 300px;
  }

  .carousel-content h2 {
    font-size: 24px;
  }

  .carousel-content p {
    font-size: 14px;
  }

  .category-container {
    gap: 10px;
  }

  .category-item {
    min-width: 60px;
    padding: 12px;
  }

  .category-icon {
    font-size: 24px;
  }

  .category-name {
    font-size: 12px;
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

  .carousel-controls.left {
    left: -30px;
  }

  .carousel-controls.right {
    right: -30px;
  }

  .carousel-button {
    width: 30px;
    height: 30px;
  }

  .course-group {
    flex-direction: column;
    align-items: center;
  }

  .course-item {
    width: 100%;
    max-width: 400px;
  }
}
</style>