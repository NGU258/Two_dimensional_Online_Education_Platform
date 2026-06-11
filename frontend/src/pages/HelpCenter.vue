<template>
  <div class="help-center">
    <!-- 雪花效果 -->
    <div class="snow-container">
      <div v-for="i in 100" :key="i" class="snowflake" :style="{
        left: Math.random() * 100 + '%',
        animationDelay: Math.random() * 5 + 's',
        animationDuration: (Math.random() * 5 + 10) + 's'
      }"></div>
    </div>
    
    <!-- 背景音乐 -->
    <audio ref="musicRef" class="background-music" loop @canplay="playMusic">
      <source :src="getResourceUrl('/music/日落.mp3')" type="audio/mpeg">
    </audio>
    
    <div class="header">
      <button @click="goBack" class="back-button">
        <el-icon style="color:purple; font-size: 20px;"><ArrowLeft /></el-icon>
      </button>
    </div>
    
    <div class="help-content">
      <!-- 侧边导航 -->
      <div class="sidebar">
        <div class="navigation">
          <h1 class="nav-title" @click="scrollToSection('tips')">01 | 学习小技巧</h1>
          <h2 class="nav-subtitle" @click="scrollToSection('tips', 'plan')">制定学习计划</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('tips', 'discussion')">主动参与讨论</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('tips', 'notes')">做笔记</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('tips', 'review')">定期复习</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('tips', 'fragments')">利用碎片时间</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('tips', 'multidimensional')">多维度学习</h2>
          
          <h1 class="nav-title" @click="scrollToSection('experience')">02 | 学习经验</h1>
          <h2 class="nav-subtitle" @click="scrollToSection('experience', 'step-by-step')">循序渐进</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('experience', 'practice')">实践出真知</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('experience', 'curiosity')">保持好奇心</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('experience', 'ask')">学会提问</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('experience', 'share')">分享知识</h2>
          
          <h1 class="nav-title" @click="scrollToSection('thoughts')">03 | 学习心得</h1>
          <h2 class="nav-subtitle" @click="scrollToSection('thoughts', 'persistence')">坚持是成功的关键</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('thoughts', 'method')">找到适合自己的学习方法</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('thoughts', 'enjoy')">享受学习的过程</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('thoughts', 'goals')">设定明确的目标</h2>
          <h2 class="nav-subtitle" @click="scrollToSection('thoughts', 'attitude')">保持积极的心态</h2>
        </div>
      </div>
      
      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 学习小技巧 -->
        <div v-if="activeMenuItem === 'tips'" class="section-content">
          <h1 class="section-title">学习小技巧</h1>
          <div id="plan" class="content-item">
            <h2 class="content-subtitle">制定学习计划</h2>
            <p class="content-text">合理安排学习时间，制定每日、每周的学习计划，确保学习进度。</p>
          </div>
          <div id="discussion" class="content-item">
            <h2 class="content-subtitle">主动参与讨论</h2>
            <p class="content-text">积极参与课程讨论，与其他学习者交流学习心得，加深对知识的理解。</p>
          </div>
          <div id="notes" class="content-item">
            <h2 class="content-subtitle">做笔记</h2>
            <p class="content-text">学习过程中及时做笔记，记录重点内容和自己的思考，便于复习。</p>
          </div>
          <div id="review" class="content-item">
            <h2 class="content-subtitle">定期复习</h2>
            <p class="content-text">定期复习已学内容，巩固记忆，避免遗忘。</p>
          </div>
          <div id="fragments" class="content-item">
            <h2 class="content-subtitle">利用碎片时间</h2>
            <p class="content-text">充分利用碎片时间进行学习，比如通勤时间、午休时间等。</p>
          </div>
          <div id="multidimensional" class="content-item">
            <h2 class="content-subtitle">多维度学习</h2>
            <p class="content-text">通过视频、音频、文字等多种形式学习，提高学习效果。</p>
          </div>
        </div>
        
        <!-- 学习经验 -->
        <div v-if="activeMenuItem === 'experience'" class="section-content">
          <h1 class="section-title">学习经验</h1>
          <div id="step-by-step" class="content-item">
            <h2 class="content-subtitle">循序渐进</h2>
            <p class="content-text">学习是一个循序渐进的过程，不要急于求成，打好基础是关键。</p>
          </div>
          <div id="practice" class="content-item">
            <h2 class="content-subtitle">实践出真知</h2>
            <p class="content-text">理论结合实践，通过实际操作加深对知识的理解和掌握。</p>
          </div>
          <div id="curiosity" class="content-item">
            <h2 class="content-subtitle">保持好奇心</h2>
            <p class="content-text">保持对新知识的好奇心，主动探索和学习，不断拓展自己的知识面。</p>
          </div>
          <div id="ask" class="content-item">
            <h2 class="content-subtitle">学会提问</h2>
            <p class="content-text">遇到问题时，学会主动提问，寻求帮助，不要自己钻牛角尖。</p>
          </div>
          <div id="share" class="content-item">
            <h2 class="content-subtitle">分享知识</h2>
            <p class="content-text">分享自己的学习心得和知识，不仅可以帮助他人，也可以加深自己的理解。</p>
          </div>
        </div>
        
        <!-- 学习心得 -->
        <div v-if="activeMenuItem === 'thoughts'" class="section-content">
          <h1 class="section-title">学习心得</h1>
          <div id="persistence" class="content-item">
            <h2 class="content-subtitle">坚持是成功的关键</h2>
            <p class="content-text">学习需要长期坚持，每天进步一点点，积少成多，最终会取得显著的成果。</p>
          </div>
          <div id="method" class="content-item">
            <h2 class="content-subtitle">找到适合自己的学习方法</h2>
            <p class="content-text">每个人的学习方式不同，找到适合自己的学习方法，提高学习效率。</p>
          </div>
          <div id="enjoy" class="content-item">
            <h2 class="content-subtitle">享受学习的过程</h2>
            <p class="content-text">学习不仅仅是为了获取知识，也是一种成长和自我提升的过程，享受学习的乐趣。</p>
          </div>
          <div id="goals" class="content-item">
            <h2 class="content-subtitle">设定明确的目标</h2>
            <p class="content-text">设定明确的学习目标，有方向地进行学习，提高学习的动力和效率。</p>
          </div>
          <div id="attitude" class="content-item">
            <h2 class="content-subtitle">保持积极的心态</h2>
            <p class="content-text">保持积极的学习心态，遇到困难时不要轻易放弃，相信自己可以克服。</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Document, Medal, Star, ArrowLeft } from '@element-plus/icons-vue'
import { useAudioManager } from '../utils/audioManager'

const router = useRouter()
const activeMenuItem = ref('tips')
const musicRef = ref(null)
const { getResourceUrl } = useAudioManager()

const goBack = () => {
  // 停止音乐播放
  if (musicRef.value) {
    musicRef.value.pause()
  }
  router.push('/')
}

// 播放点击音效的函数
const playClickSound = () => {
  // 随机选择一个点击音效
  const soundIndex = Math.floor(Math.random() * 3) + 1
  const sound = new window.Audio(`/audio/点击音效${soundIndex}.mp3`)
  sound.volume = 0.5 // 设置音量为50%
  sound.play().catch(err => {
    console.log('点击音效播放失败:', err)
  })
}

const scrollToSection = (section, id = null) => {
  // 播放点击音效
  playClickSound()
  
  activeMenuItem.value = section
  
  // 等待DOM更新后再滚动
  setTimeout(() => {
    if (id) {
      const element = document.getElementById(id)
      if (element) {
        // 只滚动内容区域，而不是整个页面
        const mainContent = document.querySelector('.main-content')
        if (mainContent) {
          // 先将内容区域滚动到顶部，确保内容可见
          mainContent.scrollTop = 0
          
          // 等待一小段时间，确保滚动到顶部完成
          setTimeout(() => {
            // 计算元素相对于内容区域的位置
            const elementRect = element.getBoundingClientRect()
            const mainRect = mainContent.getBoundingClientRect()
            const relativeTop = elementRect.top - mainRect.top
            
            // 滚动到计算的位置
            mainContent.scrollTo({
              top: relativeTop - 20, // 减去一些偏移量，使内容显示更美观
              behavior: 'smooth'
            })
          }, 50)
        }
      }
    }
  }, 100)
}

const handleMenuSelect = (key) => {
  activeMenuItem.value = key
}



onMounted(() => {
  // 页面加载时滚动到顶部，使用setTimeout确保DOM完全加载
  setTimeout(() => {
    // 强制页面滚动到顶部
    window.scrollTo({ top: 0, behavior: 'instant' })
    document.documentElement.scrollTop = 0
    document.body.scrollTop = 0
    
    // 同时将内容区域滚动到顶部
    const mainContent = document.querySelector('.main-content')
    if (mainContent) {
      mainContent.scrollTop = 0
    }
  }, 100)
  
  // 尝试自动播放音乐
  playMusic()
  
  // 监听用户交互事件，一旦有交互就播放音乐
  const handleUserInteraction = () => {
    console.log('用户交互事件触发')
    playMusic()
    // 移除事件监听器，避免重复触发
    document.removeEventListener('click', handleUserInteraction)
    document.removeEventListener('touchstart', handleUserInteraction)
    document.removeEventListener('keydown', handleUserInteraction)
    document.removeEventListener('mousemove', handleUserInteraction)
    document.removeEventListener('scroll', handleUserInteraction)
    document.removeEventListener('mouseover', handleUserInteraction)
    document.removeEventListener('touchmove', handleUserInteraction)
    document.removeEventListener('focus', handleUserInteraction, true)
  }
  
  // 添加多种交互事件监听器
  document.addEventListener('click', handleUserInteraction)
  document.addEventListener('touchstart', handleUserInteraction)
  document.addEventListener('keydown', handleUserInteraction)
  document.addEventListener('mousemove', handleUserInteraction)
  document.addEventListener('scroll', handleUserInteraction)
  document.addEventListener('mouseover', handleUserInteraction)
  document.addEventListener('touchmove', handleUserInteraction)
  document.addEventListener('focus', handleUserInteraction, true)
})

// 播放音乐的函数
const playMusic = () => {
  console.log('playMusic 函数被调用')
  if (musicRef.value) {
    console.log('音乐元素存在:', musicRef.value)
    console.log('音乐文件路径:', musicRef.value.src)
    musicRef.value.volume = 0.5 // 设置音量为30%
    musicRef.value.play().then(() => {
      console.log('音乐播放成功')
    }).catch(err => {
      console.log('音乐播放被阻止:', err)
    })
  } else {
    console.log('音乐元素不存在')
  }
}

onUnmounted(() => {
  // 组件卸载时停止音乐
  if (musicRef.value) {
    musicRef.value.pause()
  }
})
</script>

<style scoped>
/* 重置默认样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  margin: 0;
  padding: 0;
  overflow: hidden;
}

.help-center {
  padding: 0;
  margin: 0;
  min-height: 100vh;
  height: 100vh;
  width: 100vw;
  position: relative;
  font-family: '楷体', 'KaiTi', '黑体', serif;
  color: white;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.8);
  overflow: hidden;
  background-image: 
    url('/images/1.jpg'),
    radial-gradient(circle at 10% 20%, rgba(156, 39, 176, 0.3) 0%, transparent 20%),
    radial-gradient(circle at 90% 80%, rgba(64, 196, 255, 0.3) 0%, transparent 20%);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

/* 简单的 header 样式 */
.header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.back-button {
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  font-family: '楷体', 'KaiTi', '黑体', serif;
  font-size: 16px;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.8);
  padding: 8px 12px;
  border-radius: 50%;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-button:hover {
  background: rgba(128, 0, 128, 0.2);
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(128, 0, 128, 0.5);
}



.background-music {
  position: fixed;
  top: -9999px;
  left: -9999px;
  width: 1px;
  height: 1px;
  opacity: 0;
  pointer-events: none;
}

/* 雪花效果 */
.snow-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 9999;
  overflow: visible;
  background: transparent;
  /* 确保雪花容器在所有元素之上 */
  display: block;
  visibility: visible;
}

.snowflake {
  position: absolute;
  top: 50px;
  background: #ffffff;
  border-radius: 50%;
  animation: snowfall linear infinite;
  opacity: 0.8;
  box-shadow: 0 0 20px #ffffff, 0 0 30px rgba(255, 255, 255, 0.8), 0 0 40px rgba(255, 255, 255, 0.6);
  width: 20px;
  height: 20px;
  z-index: 9999;
  display: block;
  visibility: visible;
  border: 2px solid #ffffff;
  /* 确保雪花不被其他元素遮挡 */
  transform: translateZ(9999px);
}

@keyframes snowfall {
  0% {
    transform: translateY(-50px) translateX(0px) rotate(0deg) translateZ(9999px);
    opacity: 0.8;
  }
  100% {
    transform: translateY(100vh) translateX(50px) rotate(360deg) translateZ(9999px);
    opacity: 0;
  }
}

/* 风力效果 */
@keyframes windEffect {
  0%, 100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(20px);
  }
}



.help-content {
  display: flex;
  margin-top: 10px;
  gap: 20px;
  max-width: 1400px;
  margin-left: auto;
  margin-right: auto;
  padding: 0 20px;
  height: calc(100vh - 60px);
}

.sidebar {
  width: 300px;
  padding: 20px;
  overflow-y: auto;
  max-height: 80vh;
}

.navigation {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.nav-title {
  color: red;
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
  padding: 10px 0;
  cursor: pointer;
  transition: all 0.3s ease;
  text-shadow: 0 0 10px rgba(255, 0, 0, 0.5);
  border-bottom: 2px solid rgba(100, 181, 246, 0.3);
  font-family: '楷体', 'KaiTi', '黑体', serif;
}

.nav-subtitle {
  color: cyan;
  font-size: 1.1rem;
  font-weight: 500;
  margin: 0;
  padding: 8px 0 8px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
  font-family: '楷体', 'KaiTi', '黑体', serif;
}

.main-content {
  flex: 1;
  padding: 10px;
  overflow-y: auto;
  max-height: 80vh;
  background: transparent !important;
}

.section-title {
  color: red;
  margin-bottom: 30px;
  text-shadow: 0 0 20px rgba(255, 0, 0, 0.6);
  font-size: 2rem;
  font-weight: 700;
  animation: glow 2s ease-in-out infinite alternate;
}

@keyframes glow {
  from {
    text-shadow: 0 0 20px rgba(255, 0, 0, 0.6);
  }
  to {
    text-shadow: 0 0 30px rgba(255, 0, 0, 0.8), 0 0 40px rgba(255, 0, 0, 0.4);
  }
}

.content-item {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(100, 181, 246, 0.2);
}

.content-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.content-subtitle {
  color: cyan;
  margin-bottom: 15px;
  font-size: 1.5rem;
  font-weight: 600;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.content-text {
  color: pink;
  line-height: 1.8;
  font-size: 1.1rem;
  font-weight: 300;
  margin: 0;
}

.el-page-header {
  color: #e0f7fa;
  font-size: 1.5rem;
  font-weight: 600;
  background: rgba(20, 20, 40, 0.8);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 0 15px rgba(100, 181, 246, 0.2);
  border: 1px solid rgba(100, 181, 246, 0.3);
  backdrop-filter: blur(10px);
  max-width: 1400px;
  margin-left: auto;
  margin-right: auto;
}

.el-page-header__left .el-icon {
  color: #4fc3f7;
  font-size: 1.5rem;
}

.el-page-header__title {
  color: #4fc3f7;
  font-size: 1.8rem;
  font-weight: 700;
  text-shadow: 0 0 10px rgba(79, 195, 247, 0.5);
}

@media (max-width: 768px) {
  .help-content {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    max-height: 40vh;
  }
  
  .main-content {
    padding: 20px;
    max-height: 50vh;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .nav-title {
    font-size: 1.3rem;
  }
  
  .nav-subtitle {
    font-size: 1rem;
  }
  
  .content-subtitle {
    font-size: 1.3rem;
  }
  
  .content-text {
    font-size: 1rem;
  }
}
</style>