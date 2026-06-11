<template>
  <div class="course-detail">
    <el-page-header @back="() => { goBack(); playSound('点击音效1'); }" :content="course?.title || '课程详情'" />
    
    <el-card v-if="course" class="course-card">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="course-cover">
            <div class="image-overlay">
              <div class="anime-border"></div>
              <img :src="course.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20course%20cover%20illustration&image_size=landscape_16_9'" :alt="course.title" />
              <div class="play-icon" @click="() => { playLesson(course.chapters[0]); playSound('点击音效1'); }">
                <el-icon class="play-symbol"><VideoCamera /></el-icon>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="course-info">
            <h1 class="course-title">{{ course.title }}</h1>
            <div class="course-meta">
              <span class="course-category">{{ getCategoryName(course.categoryId) }}</span>
              <span class="course-level">{{ course.level }}</span>
              <span class="course-duration">{{ course.duration }} 课时</span>
              <span class="course-rating">
                <el-rate v-model="course.rating" disabled :max="5" show-score />
              </span>
            </div>
            <p class="course-description">{{ course.description }}</p>
            <div class="course-stats">
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>{{ course.popularity }} 人学习</span>
              </div>
              <div class="stat-item">
                <el-icon><Message /></el-icon>
                <span>{{ course.commentCount }} 条评论</span>
              </div>
              <div class="stat-item">
                <el-icon><Collection /></el-icon>
                <span>{{ course.collectionCount }} 人收藏</span>
              </div>
            </div>
            <div class="course-actions">
              <el-button type="primary" size="large" @click="() => { playLesson(course.chapters[0]); playSound('点击音效1'); }" class="enroll-button">立即学习</el-button>
              <el-button size="large" @click="() => { toggleCollection(); playSound('点击音效1'); }" class="collect-button">
                <el-icon v-if="isCollected"><Collection /></el-icon>
                <el-icon v-else><Star /></el-icon>
                {{ isCollected ? '已收藏' : '收藏' }}
              </el-button>
              <el-button size="large" @click="() => { shareCourse(); playSound('点击音效1'); }" class="share-button">
                <el-icon><Share /></el-icon>
                分享
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="course-content" v-if="course">
      <template #header>
        <div class="card-header">
          <span>课程内容</span>
        </div>
      </template>
      <div class="content-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="课程大纲" name="outline">
            <div class="course-outline">
              <el-timeline>
                <el-timeline-item 
                  v-for="(chapter, index) in course.chapters" 
                  :key="index"
                  :timestamp="chapter.title"
                  type="primary"
                  placement="top"
                >
                  <div class="chapter-content">
                    <h4>{{ chapter.title }}</h4>
                    <div class="lesson-item">
                      <el-button link @click="() => { playLesson(chapter); playSound('点击音效1'); }">
                        <el-icon><VideoCamera /></el-icon>
                        观看视频
                        <span class="lesson-duration">{{ chapter.startTimeFormatted }}</span>
                      </el-button>
                    </div>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-tab-pane>
          <el-tab-pane label="课程介绍" name="introduction">
            <div class="course-introduction">
              <h3>课程简介</h3>
              <p>{{ course.introduction }}</p>
              <h3>学习目标</h3>
              <ul class="learning-goals">
                <li v-for="(goal, index) in course.learningGoals" :key="index">
                  <el-icon><Check /></el-icon>
                  {{ goal }}
                </li>
              </ul>
              <h3>适合人群</h3>
              <ul class="target-audience">
                <li v-for="(audience, index) in course.targetAudience" :key="index">
                  <el-icon><User /></el-icon>
                  {{ audience }}
                </li>
              </ul>
            </div>
          </el-tab-pane>
          <el-tab-pane label="评论" name="comments">
            <div class="course-comments">
              <div class="comment-input">
                <el-input
                  v-model="commentContent"
                  type="textarea"
                  placeholder="写下你的评论..."
                  :rows="3"
                />
                <div class="comment-buttons">
                  <el-button type="primary" @click="submitComment" class="submit-comment">提交评论</el-button>
                  <el-button @click="clearComment" class="clear-comment">清空</el-button>
                </div>
              </div>
              <div class="comment-list">
                <el-card 
                  v-for="(comment, index) in course.comments" 
                  :key="index"
                  class="comment-item"
                >
                  <div class="comment-header">
                    <span class="comment-user">{{ comment.username }}</span>
                    <span class="comment-time">{{ comment.createTime }}</span>
                    <el-rate v-model="comment.rating" disabled :max="5" />
                  </div>
                  <div class="comment-content">{{ comment.content }}</div>
                  <div class="comment-actions">
                    <el-button link @click="likeComment(comment)">
                      <el-icon v-if="comment.liked"><StarFilled /></el-icon>
                      <el-icon v-else><Star /></el-icon>
                      {{ comment.likeCount }} 赞
                    </el-button>
                  </div>
                </el-card>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>

    <div v-if="!course" class="loading-state">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 视频播放器对话框 -->
    <el-dialog
      v-model="showVideoPlayer"
      :title="currentVideo?.title"
      width="80%"
      center
      @close="handleDialogClose"
    >
      <div class="video-player">
        <video
          v-if="currentVideo"
          ref="videoRef"
          :src="currentVideo.videoPath"
          controls
          autoplay
          width="100%"
          height="450px"
          @error="handleVideoError"
          @play="handleVideoPlay"
          @pause="console.log('视频暂停')"
        >
          您的浏览器不支持视频播放。
        </video>

      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Star, Message, Collection, Share, VideoCamera, Check, User, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
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

const router = useRouter()
const route = useRoute()
const courseStore = useCourseStore()
const userStore = useUserStore()

const courseId = ref(route.params.id)
const course = ref(null)
const activeTab = ref('outline')
const isCollected = ref(false)
const commentContent = ref('')

const getCategoryName = (id) => {
  const category = courseStore.categories.find(c => c.id === id)
  return category ? category.name : '未分类'
}

const loadCourseDetail = async () => {
  try {
    console.log('加载课程详情，课程ID:', courseId.value);
    const courseData = await courseStore.getCourseById(courseId.value);
    console.log('获取到的课程数据:', courseData);
    
    if (courseData) {
      course.value = courseData;
      console.log('课程标题:', course.value.title);
      console.log('视频路径:', course.value.videoPath);
      console.log('封面图片路径:', course.value.coverImage);
      
      // 检查课程是否已经被收藏
      const getCollections = () => {
        const collections = localStorage.getItem('collections')
        return collections ? JSON.parse(collections) : []
      }
      const isCollectedCheck = (courseId) => {
        const collections = getCollections()
        return collections.some(item => item.id === courseId)
      }
      isCollected.value = isCollectedCheck(course.value.id);
      console.log('课程收藏状态:', isCollected.value);
      
      // 添加缺失的字段
      if (!course.value.learningGoals) {
        course.value.learningGoals = [
          '掌握课程核心知识',
          '提升实际应用能力',
          '培养解决问题的思维'
        ]
      }
      if (!course.value.targetAudience) {
        course.value.targetAudience = [
          '对课程内容感兴趣的学习者',
          '希望提升相关技能的专业人士',
          '想拓展知识面的爱好者'
        ]
      }
      if (!course.value.comments) {
        course.value.comments = [
          {
            id: 1,
            username: '小明',
            content: '课程内容很详细，讲解清晰，非常适合学习！',
            rating: 5,
            createTime: '2024-01-01 10:00:00',
            likeCount: 10,
            liked: false
          },
          {
            id: 2,
            username: '小红',
            content: '学习了这门课程后，我的知识水平有了明显提升，推荐给大家！',
            rating: 4,
            createTime: '2024-01-02 14:30:00',
            likeCount: 5,
            liked: true
          }
        ]
      }
      if (!course.value.duration) {
        course.value.duration = course.value.chapters ? course.value.chapters.length : 0
      }
      if (!course.value.commentCount) {
        course.value.commentCount = course.value.comments ? course.value.comments.length : 0
      }
      if (!course.value.collectionCount) {
        course.value.collectionCount = 0
      }
      // 确保章节对象有videoPath属性
      if (course.value.chapters) {
        course.value.chapters.forEach(chapter => {
          if (!chapter.videoPath) {
            chapter.videoPath = course.value.videoPath
          }
        })
      }
      console.log('最终课程数据:', course.value)
      
      // 获取视频实际时长并更新章节信息
      if (course.value && course.value.chapters && course.value.chapters.length > 0) {
        // 创建一个临时视频元素来获取时长
        const videoElement = document.createElement('video')
        videoElement.preload = 'metadata'
        
        // 使用第一个章节的视频路径
        const videoPath = course.value.chapters[0].videoPath || course.value.videoPath
        if (videoPath) {
          videoElement.src = videoPath
          
          videoElement.addEventListener('loadedmetadata', () => {
            const actualDuration = videoElement.duration
            const minutes = Math.floor(actualDuration / 60)
            const seconds = Math.floor(actualDuration % 60)
            const formattedDuration = `${minutes}:${seconds.toString().padStart(2, '0')}`
            console.log('视频实际时长:', formattedDuration)
            
            // 为每个章节计算开始时间和更新时长
            const m = course.value.chapters.length // 章节数量
            const avgChapterDuration = actualDuration / m // 每个章节的平均时长
            
            course.value.chapters.forEach((chapter, index) => {
              // 计算章节的开始时间
              chapter.startTime = index * avgChapterDuration
              
              // 计算章节的开始时间（格式化为 mm:ss）
              const startMinutes = Math.floor(chapter.startTime / 60)
              const startSeconds = Math.floor(chapter.startTime % 60)
              chapter.startTimeFormatted = `${startMinutes}:${startSeconds.toString().padStart(2, '0')}`
              
              // 保留视频的实际时长
              chapter.duration = formattedDuration
            })
            console.log('章节信息更新完成:', course.value.chapters)
          })
        }
      }
    }
  } catch (error) {
    console.error('获取课程详情失败:', error)
  }
}

const currentVideo = ref(null)
const showVideoPlayer = ref(false)
const videoRef = ref(null)

const playLesson = (chapter) => {
  // 确保chapter对象有videoPath属性
  if (!chapter.videoPath) {
    // 如果没有videoPath，使用课程的videoPath
    chapter.videoPath = course.value.videoPath
  }
  
  // 确保在播放新视频前，先暂停之前的视频
  if (videoRef.value) {
    videoRef.value.pause()
  }
  
  currentVideo.value = chapter
  showVideoPlayer.value = true
  console.log('播放视频:', chapter.title, '路径:', chapter.videoPath)
  
  // 使用nextTick确保DOM已经更新，然后设置视频的播放位置
  nextTick(() => {
    if (videoRef.value) {
      // 设置视频的播放位置
      if (chapter.startTime !== undefined) {
        videoRef.value.currentTime = chapter.startTime
        console.log('设置视频播放位置:', chapter.startTime, '秒')
      }
      
      // 播放视频
      videoRef.value.play().catch(err => {
        console.log('视频播放失败:', err)
      })
    }
  })
}

const handleVideoError = (event) => {
  console.error('视频播放错误:', event.target.error)
  console.error('错误代码:', event.target.error.code)
  console.error('错误消息:', event.target.error.message)
  console.error('视频路径:', event.target.src)
}

const handleDialogClose = () => {
  // 当对话框关闭时，暂停视频播放
  if (videoRef.value) {
    videoRef.value.pause()
    console.log('视频已暂停')
    // 记录学习进度
    recordLearningProgress()
  }
  // 清空当前视频
  currentVideo.value = null
}

// 记录学习进度
const recordLearningProgress = () => {
  if (course.value) {
    // 生成当前时间（使用本地时间）
    const now = new Date().toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    })
    
    // 从本地存储获取现有学习进度
    let learningProgress = JSON.parse(localStorage.getItem('learningProgress') || '[]')
    
    // 检查是否已存在该课程的学习进度
    const existingIndex = learningProgress.findIndex(item => item.courseId === course.value.id)
    
    let progress = 0
    if (existingIndex !== -1) {
      // 已有学习进度，适当增加
      progress = Math.min(learningProgress[existingIndex].progress + 15, 100)
    } else {
      // 新开始学习，初始进度为5%
      progress = 5
    }
    
    // 构建学习进度数据
    const progressData = {
      id: Date.now(),
      courseId: course.value.id,
      courseTitle: course.value.title,
      progress: progress,
      lastLearnTime: now
    }
    
    if (existingIndex !== -1) {
      // 更新现有进度
      learningProgress[existingIndex] = progressData
    } else {
      // 添加新进度
      learningProgress.push(progressData)
    }
    
    // 保存到本地存储
    localStorage.setItem('learningProgress', JSON.stringify(learningProgress))
    console.log('学习进度已记录:', progressData)
  }
}

const toggleCollection = () => {
  // 直接操作 localStorage 实现收藏功能
  const getCollections = () => {
    const collections = localStorage.getItem('collections')
    return collections ? JSON.parse(collections) : []
  }

  const addCollection = (course) => {
    const collections = getCollections()
    const exists = collections.some(item => item.id === course.id)
    if (!exists) {
      collections.push(course)
      localStorage.setItem('collections', JSON.stringify(collections))
      return true
    }
    return false
  }

  const removeCollection = (courseId) => {
    const collections = getCollections()
    const newCollections = collections.filter(item => item.id !== courseId)
    if (newCollections.length !== collections.length) {
      localStorage.setItem('collections', JSON.stringify(newCollections))
      return true
    }
    return false
  }

  if (isCollected.value) {
    // 取消收藏
    const success = removeCollection(course.value.id)
    if (success) {
      isCollected.value = false
      console.log('取消收藏成功')
    }
  } else {
    // 添加收藏
    const success = addCollection({
      id: course.value.id,
      title: course.value.title,
      description: course.value.description,
      coverImage: course.value.coverImage
    })
    if (success) {
      isCollected.value = true
      console.log('收藏成功')
    }
  }
}

const shareCourse = () => {
  console.log('分享课程:', course.value.title)
  
  // 生成课程分享链接
  const shareUrl = window.location.href
  
  // 生成符合格式的分享内容
  const shareContent = `课程名：${course.value.title}
课程描述：${course.value.description || '暂无描述'}
总时长：${course.value.duration || '未知'}课时
学习入口：${shareUrl}`
  
  // 复制到剪贴板
  copyToClipboard(shareContent)
}

const copyToClipboard = (text) => {
  if (navigator.clipboard && window.isSecureContext) {
    // 使用现代的Clipboard API
    navigator.clipboard.writeText(text)
      .then(() => {
        ElMessage.success('链接已复制到剪贴板')
        console.log('链接已复制到剪贴板')
      })
      .catch(err => {
        console.error('复制失败:', err)
        ElMessage.error('复制失败，请手动复制链接')
      })
  } else {
    // 回退到传统的复制方法
    const textArea = document.createElement('textarea')
    textArea.value = text
    textArea.style.position = 'fixed'
    textArea.style.left = '-999999px'
    textArea.style.top = '-999999px'
    document.body.appendChild(textArea)
    textArea.focus()
    textArea.select()
    try {
      document.execCommand('copy')
      ElMessage.success('链接已复制到剪贴板')
      console.log('链接已复制到剪贴板')
    } catch (err) {
      console.error('复制失败:', err)
      ElMessage.error('复制失败，请手动复制链接')
    } finally {
      document.body.removeChild(textArea)
    }
  }
}

const submitComment = () => {
  if (!commentContent.value.trim()) return
  console.log('提交评论:', commentContent.value)
  
  // 生成当前时间（使用本地时间）
  const now = new Date().toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
  
  // 生成新评论
  const newComment = {
    id: Date.now(),
    username: '元气仙喵', // 假设当前用户是元气仙喵
    content: commentContent.value.trim(),
    rating: 5, // 默认评分5星
    createTime: now,
    likeCount: 0,
    liked: false
  }
  
  // 将新评论添加到课程的评论列表中
  if (course.value && course.value.comments) {
    course.value.comments.unshift(newComment)
    console.log('评论已添加到课程评论列表')
  }
  
  // 记录评论到本地存储，以便在个人中心-我的评论中显示
  const userComment = {
    id: newComment.id,
    courseId: course.value.id,
    courseTitle: course.value.title,
    content: newComment.content,
    rating: newComment.rating,
    createTime: newComment.createTime
  }
  
  // 从本地存储获取现有用户评论
  let userComments = JSON.parse(localStorage.getItem('userComments') || '[]')
  
  // 添加新评论
  userComments.unshift(userComment)
  
  // 保存到本地存储
  localStorage.setItem('userComments', JSON.stringify(userComments))
  console.log('评论已记录到本地存储')
  
  // 清空评论输入框
  commentContent.value = ''
}

// 清空评论输入框
const clearComment = () => {
  commentContent.value = ''
  console.log('评论输入框已清空')
  playSound('点击音效1')
}

const likeComment = (comment) => {
  comment.liked = !comment.liked
  comment.likeCount += comment.liked ? 1 : -1
}

const goBack = () => {
  const from = route.query.from
  if (from === 'home') {
    router.push('/')
  } else if (from === 'progress') {
    router.push('/user/profile#progress')
  } else if (from === 'collections') {
    router.push('/user/profile#collections')
  } else if (from === 'comments') {
    router.push('/user/profile#comments')
  } else if (from === 'courses') {
    router.push('/courses')
  } else {
    router.push('/courses')
  }
}

// 视频播放事件处理函数
const handleVideoPlay = () => {
  console.log('视频开始播放')
  // 尝试进入全屏
  if (videoRef.value) {
    if (videoRef.value.requestFullscreen) {
      videoRef.value.requestFullscreen().catch(err => {
        console.log('进入全屏失败:', err)
      })
    } else if (videoRef.value.mozRequestFullScreen) {
      videoRef.value.mozRequestFullScreen().catch(err => {
        console.log('进入全屏失败:', err)
      })
    } else if (videoRef.value.webkitRequestFullscreen) {
      videoRef.value.webkitRequestFullscreen().catch(err => {
        console.log('进入全屏失败:', err)
      })
    } else if (videoRef.value.msRequestFullscreen) {
      videoRef.value.msRequestFullscreen().catch(err => {
        console.log('进入全屏失败:', err)
      })
    }
  }
}

onMounted(async () => {
  // 多种方式强制滚动到顶部
  // 1. 直接操作window
  window.scrollTo({ top: 0, behavior: 'instant' })
  // 2. 操作document.documentElement
  document.documentElement.scrollTop = 0
  // 3. 操作document.body
  document.body.scrollTop = 0
  // 4. 尝试找到main-content-wrapper并滚动
  const mainContentWrapper = document.querySelector('.main-content-wrapper')
  if (mainContentWrapper) {
    mainContentWrapper.scrollTop = 0
  }
  // 5. 尝试找到main-content并滚动
  const mainContent = document.querySelector('.main-content')
  if (mainContent) {
    mainContent.scrollTop = 0
  }
  
  await loadCourseDetail()
  // 检查是否从"我的评论"页面跳转，如果是，默认显示评论选项卡
  const from = route.query.from
  if (from === 'comments') {
    activeTab.value = 'comments'
    console.log('从我的评论页面跳转，默认显示评论选项卡')
  }
  // 移除自动播放逻辑，让用户点击"立即学习"后再播放视频
  
  // 数据加载完成后再次滚动到顶部，确保页面完全加载后也在顶部
  setTimeout(() => {
    window.scrollTo({ top: 0, behavior: 'instant' })
    document.documentElement.scrollTop = 0
    document.body.scrollTop = 0
    if (mainContentWrapper) {
      mainContentWrapper.scrollTop = 0
    }
    if (mainContent) {
      mainContent.scrollTop = 0
    }
  }, 300)
  
  // 再延迟一点时间，确保所有DOM元素都已渲染完成
  setTimeout(() => {
    window.scrollTo({ top: 0, behavior: 'instant' })
    document.documentElement.scrollTop = 0
    document.body.scrollTop = 0
    if (mainContentWrapper) {
      mainContentWrapper.scrollTop = 0
    }
    if (mainContent) {
      mainContent.scrollTop = 0
    }
  }, 800)
})
</script>

<style scoped>
.course-detail {
  padding: 20px;
  background-color: black;
  min-height: 100vh;
  background-image: 
    radial-gradient(circle at 10% 20%, rgba(156, 39, 176, 0.1) 0%, transparent 20%),
    radial-gradient(circle at 90% 80%, rgba(64, 196, 255, 0.1) 0%, transparent 20%);
}

/* 白色主题 */
html.light-theme .course-detail {
  background-color: #ffffff;
  background-image: none;
}

.course-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, rgba(26, 26, 26, 0.9) 0%, rgba(22, 33, 62, 0.9) 100%);
  border: 1px solid #9c27b0;
  border-radius: 15px;
  box-shadow: 0 0 15px rgba(156, 39, 176, 0.3);
  --el-card-bg-color: transparent;
  --el-card-border-color: #9c27b0;
  position: relative;
  overflow: hidden;
}

.course-card::before {
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

.course-cover {
  height: 300px;
  overflow: hidden;
  border-radius: 12px;
  position: relative;
}

.course-cover .image-overlay {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: 12px;
}

.course-cover .anime-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2px solid transparent;
  border-radius: 12px;
  background: linear-gradient(45deg, #ff6b9d, #5f27cd, #40c4ff, #ff6b9d) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  animation: borderGlow 3s ease-in-out infinite;
  pointer-events: none;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
  filter: contrast(1.1) saturate(1.1);
}

.course-cover:hover img {
  transform: scale(1.1);
}

.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
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
  font-size: 32px;
  color: #00f5ff;
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.8);
  animation: pulse 2s ease-in-out infinite;
}

.course-info {
  padding: 20px;
}

.course-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.5);
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.course-category,
.course-level,
.course-duration {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  border: 1px solid transparent;
  transition: all 0.3s ease;
  text-shadow: 0 0 3px rgba(0, 0, 0, 0.5);
}

.course-category {
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.2), rgba(103, 194, 58, 0.1));
  color: #67c23a;
  border-color: rgba(103, 194, 58, 0.3);
  text-shadow: 0 0 3px rgba(103, 194, 58, 0.5);
}

.course-category:hover {
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.3), rgba(103, 194, 58, 0.2));
  box-shadow: 0 0 10px rgba(103, 194, 58, 0.4);
}

.course-level {
  background: linear-gradient(135deg, rgba(64, 196, 255, 0.2), rgba(64, 196, 255, 0.1));
  color: #40c4ff;
  border-color: rgba(64, 196, 255, 0.3);
  text-shadow: 0 0 3px rgba(64, 196, 255, 0.5);
}

.course-level:hover {
  background: linear-gradient(135deg, rgba(64, 196, 255, 0.3), rgba(64, 196, 255, 0.2));
  box-shadow: 0 0 10px rgba(64, 196, 255, 0.4);
}

.course-duration {
  background: linear-gradient(135deg, rgba(254, 202, 87, 0.2), rgba(254, 202, 87, 0.1));
  color: #feca57;
  border-color: rgba(254, 202, 87, 0.3);
  text-shadow: 0 0 3px rgba(254, 202, 87, 0.5);
}

.course-duration:hover {
  background: linear-gradient(135deg, rgba(254, 202, 87, 0.3), rgba(254, 202, 87, 0.2));
  box-shadow: 0 0 10px rgba(254, 202, 87, 0.4);
}

.course-description {
  font-size: 16px;
  color: #81d4fa;
  margin-bottom: 20px;
  line-height: 1.5;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
}

.course-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #81d4fa;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
  transition: all 0.3s ease;
}

.stat-item:hover {
  color: #00f5ff;
  transform: translateY(-2px);
}

.stat-item :deep(.el-icon) {
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.8);
  transition: all 0.3s ease;
}

.stat-item:hover :deep(.el-icon) {
  color: #ff8fab;
  transform: scale(1.1);
}

.course-actions {
  display: flex;
  gap: 12px;
}

.enroll-button {
  flex: 1;
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 1px solid #ff6b9d !important;
  color: white !important;
  border-radius: 25px !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
  position: relative;
  overflow: hidden;
}

.enroll-button::before {
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

.enroll-button:hover {
  background: linear-gradient(135deg, #ff8fab, #7e47c2) !important;
  border-color: #ff8fab !important;
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6) !important;
  transform: translateY(-2px);
}

.enroll-button:active {
  transform: translateY(0) scale(0.98);
}

.collect-button,
.share-button {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.8), rgba(22, 33, 62, 0.8)) !important;
  border: 1px solid #9c27b0 !important;
  color: #40c4ff !important;
  border-radius: 25px !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 0 5px rgba(156, 39, 176, 0.2) !important;
}

.collect-button:hover,
.share-button:hover {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.9), rgba(22, 33, 62, 0.9)) !important;
  border-color: #ba68c8 !important;
  color: #00f5ff !important;
  box-shadow: 0 0 15px rgba(186, 104, 200, 0.4) !important;
  transform: translateY(-2px);
}

.course-content {
  margin-bottom: 20px;
  background: linear-gradient(135deg, rgba(26, 26, 26, 0.9) 0%, rgba(22, 33, 62, 0.9) 100%);
  border: 1px solid #9c27b0;
  border-radius: 15px;
  box-shadow: 0 0 15px rgba(156, 39, 176, 0.3);
  --el-card-bg-color: transparent;
  --el-card-border-color: #9c27b0;
  position: relative;
  overflow: hidden;
}

.course-content::before {
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

.card-header {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.5);
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
}

.content-tabs {
  margin-top: 20px;
}

.content-tabs :deep(.el-tabs__header) {
  border-bottom: 1px solid #67c23a !important;
  background: transparent !important;
}

/* 确保标签页容器没有其他边框 */
.content-tabs {
  border-bottom: none !important;
}

/* 确保整个标签页组件没有其他边框 */
.content-tabs :deep(.el-tabs) {
  border-bottom: 1px solid #67c23a !important;
}

.content-tabs :deep(.el-tabs__item) {
  color: #81d4fa;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
  transition: all 0.3s ease;
}

.content-tabs :deep(.el-tabs__item:hover) {
  color: #00f5ff;
  text-shadow: 0 0 5px rgba(0, 245, 255, 0.8);
}

.content-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #ff6b9d, #5f27cd);
  height: 3px;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.6);
}

.content-tabs :deep(.el-tabs__item.is-active) {
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.5);
}

.course-outline {
  padding: 20px 0;
}

.chapter-content {
  padding: 16px;
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.8), rgba(22, 33, 62, 0.8));
  border-radius: 10px;
  margin-top: 8px;
  border: 1px solid #9c27b0;
  transition: all 0.3s ease;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.2);
}

.chapter-content:hover {
  box-shadow: 0 0 20px rgba(156, 39, 176, 0.4);
  transform: translateY(-2px);
}

.chapter-content h4 {
  margin-bottom: 12px;
  color: #ff6b9d;
  text-shadow: 0 0 3px rgba(255, 107, 157, 0.5);
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
}

.lesson-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.lesson-list li {
  margin-bottom: 8px;
}

.lesson-list :deep(.el-button) {
  --el-button-text-color: #40c4ff;
  --el-button-hover-text-color: #00f5ff;
  --el-button-hover-bg-color: rgba(156, 39, 176, 0.1);
  transition: all 0.3s ease;
}

.lesson-list :deep(.el-button:hover) {
  transform: translateX(5px);
}

.lesson-list :deep(.el-button .el-icon) {
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.8);
  margin-right: 8px;
}

.lesson-duration {
  float: right;
  color: #81d4fa;
  font-size: 14px;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
}

.course-introduction {
  padding: 20px 0;
}

.course-introduction h3 {
  margin-bottom: 12px;
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.5);
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
}

.course-introduction p {
  margin-bottom: 20px;
  color: #81d4fa;
  line-height: 1.5;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
}

.learning-goals,
.target-audience {
  list-style: none;
  padding: 0;
  margin: 0 0 20px 0;
}

.learning-goals li,
.target-audience li {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #81d4fa;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
  transition: all 0.3s ease;
}

.learning-goals li:hover,
.target-audience li:hover {
  color: #00f5ff;
  transform: translateX(5px);
}

.learning-goals li :deep(.el-icon),
.target-audience li :deep(.el-icon) {
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.8);
}

.course-comments {
  padding: 20px 0;
}

.comment-input :deep(.el-input) {
  --el-input-bg-color: black !important;
  --el-input-border-color: #9c27b0 !important;
  --el-input-text-color: #67c23a !important;
  --el-input-placeholder-color: #67c23a !important;
  border-radius: 10px !important;
}

.comment-input :deep(.el-input__wrapper) {
  background-color: black !important;
  border: 1px solid #9c27b0 !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
}

.comment-input :deep(.el-input__wrapper.is-focus) {
  border: 1px solid #9c27b0 !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.5) !important;
  background-color: black !important;
}

.comment-input :deep(.el-input__inner) {
  color: #67c23a !important;
  background-color: black !important;
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif !important;
}

.comment-input :deep(.el-input__inner::placeholder) {
  color: #67c23a !important;
}

.comment-input :deep(.el-textarea) {
  --el-input-bg-color: black !important;
  --el-input-border-color: #9c27b0 !important;
  --el-input-text-color: #67c23a !important;
  --el-input-placeholder-color: #67c23a !important;
  border-radius: 10px !important;
}

.comment-input :deep(.el-textarea__wrapper) {
  background-color: black !important;
  border: 1px solid #9c27b0 !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
}

.comment-input :deep(.el-textarea__wrapper.is-focus) {
  border: 1px solid #9c27b0 !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.5) !important;
  background-color: black !important;
}

.comment-input :deep(.el-textarea__inner) {
  border: 1px solid #9c27b0 !important;
  color: #67c23a !important;
  background-color: black !important;
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif !important;
}

.comment-input :deep(.el-textarea__inner::placeholder) {
  color: #67c23a !important;
}

.submit-comment {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 1px solid #ff6b9d !important;
  color: white !important;
  border-radius: 20px !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
  padding: 8px 24px !important;
}

.submit-comment:hover {
  background: linear-gradient(135deg, #ff8fab, #7e47c2) !important;
  border-color: #ff8fab !important;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.6) !important;
  transform: translateY(-2px);
}

/* 评论按钮容器样式 */
.comment-buttons {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  gap: 12px;
}

/* 清空按钮样式 */
.clear-comment {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 1px solid #ff6b9d !important;
  color: white !important;
  border-radius: 20px !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
  padding: 8px 24px !important;
}

.clear-comment:hover {
  background: linear-gradient(135deg, #ff8fab, #7e47c2) !important;
  border-color: #ff8fab !important;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.6) !important;
  transform: translateY(-2px);
}

.comment-item {
  margin-bottom: 16px;
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.8), rgba(22, 33, 62, 0.8));
  border: 1px solid #9c27b0;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.2);
  --el-card-bg-color: transparent;
  --el-card-border-color: #9c27b0;
  transition: all 0.3s ease;
}

.comment-item:hover {
  box-shadow: 0 0 20px rgba(156, 39, 176, 0.4);
  transform: translateY(-2px);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.comment-user {
  font-weight: bold;
  color: #ff6b9d;
  text-shadow: 0 0 3px rgba(255, 107, 157, 0.5);
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif;
}

.comment-time {
  font-size: 12px;
  color: #81d4fa;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
}

.comment-header :deep(.el-rate__icon) {
  color: #feca57 !important;
  text-shadow: 0 0 5px rgba(254, 202, 87, 0.8);
}

.comment-content {
  margin-bottom: 12px;
  color: #81d4fa;
  line-height: 1.5;
  text-shadow: 0 0 3px rgba(129, 212, 250, 0.3);
}

.comment-actions {
  text-align: right;
}

.comment-actions :deep(.el-button) {
  --el-button-text-color: #40c4ff;
  --el-button-hover-text-color: #00f5ff;
  --el-button-hover-bg-color: rgba(156, 39, 176, 0.1);
  transition: all 0.3s ease;
}

.comment-actions :deep(.el-button:hover) {
  transform: scale(1.05);
}

.comment-actions :deep(.el-button .el-icon) {
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.8);
  margin-right: 4px;
}

.loading-state {
  margin: 40px 0;
}

.loading-state :deep(.el-skeleton__item) {
  background: linear-gradient(90deg, #2a2a2a, #1a1a2e, #2a2a2a);
  background-size: 200% 100%;
  animation: loadingShimmer 2s ease-in-out infinite;
}

/* 视频播放器对话框样式 */
:deep(.el-dialog) {
  background: linear-gradient(135deg, rgba(26, 26, 26, 0.95), rgba(22, 33, 62, 0.95)) !important;
  border: 2px solid #9c27b0 !important;
  border-radius: 20px !important;
  box-shadow: 0 0 30px rgba(156, 39, 176, 0.6) !important;
  overflow: hidden !important;
  animation: dialogAppear 0.5s ease-out;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #9c27b0 !important;
  padding: 20px !important;
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.9), rgba(22, 33, 62, 0.9)) !important;
  position: relative;
}

:deep(.el-dialog__header)::before {
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

:deep(.el-dialog__title) {
  color: #ff6b9d !important;
  font-family: "楷体", "KaiTi", "Sitka Text Semibold", serif !important;
  font-size: 20px !important;
  font-weight: bold !important;
  text-shadow: 0 0 10px rgba(255, 107, 157, 0.6) !important;
}

:deep(.el-dialog__body) {
  padding: 20px !important;
  background: rgba(0, 0, 0, 0.8) !important;
  position: relative;
}

:deep(.el-dialog__body)::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 10% 20%, rgba(156, 39, 176, 0.1) 0%, transparent 20%),
              radial-gradient(circle at 90% 80%, rgba(64, 196, 255, 0.1) 0%, transparent 20%);
  pointer-events: none;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #9c27b0 !important;
  padding: 20px !important;
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.9), rgba(22, 33, 62, 0.9)) !important;
}

/* 视频播放器样式 */
.video-player {
  position: relative;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(156, 39, 176, 0.4);
  border: 1px solid rgba(156, 39, 176, 0.3);
}

.video-player video {
  border-radius: 10px;
  transition: all 0.3s ease;
}

.video-player video:hover {
  box-shadow: 0 0 30px rgba(156, 39, 176, 0.6);
}

/* 对话框出现动画 */
@keyframes dialogAppear {
  from {
    opacity: 0;
    transform: scale(0.8) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #ff6b9d, #5f27cd) !important;
  border: 1px solid #ff6b9d !important;
  color: white !important;
  border-radius: 20px !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.3) !important;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #ff8fab, #7e47c2) !important;
  border-color: #ff8fab !important;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.6) !important;
}

:deep(.el-button--default) {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.8), rgba(22, 33, 62, 0.8)) !important;
  border: 1px solid #9c27b0 !important;
  color: #40c4ff !important;
  border-radius: 20px !important;
  font-weight: bold !important;
  transition: all 0.3s ease !important;
}

:deep(.el-button--default:hover) {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.9), rgba(22, 33, 62, 0.9)) !important;
  border-color: #ba68c8 !important;
  color: #00f5ff !important;
  box-shadow: 0 0 15px rgba(186, 104, 200, 0.4) !important;
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

@keyframes shine {
  0% {
    transform: translateX(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) rotate(45deg);
  }
}

@keyframes loadingShimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
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
</style>