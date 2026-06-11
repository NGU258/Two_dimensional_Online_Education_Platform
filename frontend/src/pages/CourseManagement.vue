<template>
  <div class="course-management">
    <div class="back-button-container">
      <el-button type="primary" plain class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回首页
      </el-button>
    </div>
    
    <div class="action-bar">
      <el-button type="primary" @click="showAddCourseDialog = true" class="add-btn">
        <el-icon><Plus /></el-icon>
        添加课程
      </el-button>
    </div>

    <div class="courses-grid">
      <el-card v-for="course in courses" :key="course.id" class="course-card">
        <div class="course-header">
          <img :src="course.coverImage" :alt="course.title" class="course-cover" />
        </div>
        <div class="course-body">
          <h3 class="course-title">{{ course.title }}</h3>
          <p class="course-description">{{ course.description }}</p>
          <div class="course-meta">
            <span class="meta-item">{{ course.category }}</span>
            <span class="meta-item">{{ course.level }}</span>
            <span class="meta-item">{{ course.students }} 名学生</span>
          </div>
          <div class="course-actions">
            <el-button size="small" @click="editCourse(course)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCourse(course.id)">删除</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <el-dialog title="添加课程" :visible.sync="showAddCourseDialog" width="500px">
      <el-form :model="courseForm" label-width="80px">
        <el-form-item label="课程名称" required>
          <el-input v-model="courseForm.title" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="课程描述" required>
          <el-textarea v-model="courseForm.description" placeholder="请输入课程描述"></el-textarea>
        </el-form-item>
        <el-form-item label="课程分类">
          <el-select v-model="courseForm.category" placeholder="请选择分类">
            <el-option label="绘画" value="绘画"></el-option>
            <el-option label="动画" value="动画"></el-option>
            <el-option label="游戏" value="游戏"></el-option>
            <el-option label="设计" value="设计"></el-option>
            <el-option label="编程" value="编程"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程级别">
          <el-select v-model="courseForm.level" placeholder="请选择级别">
            <el-option label="入门" value="入门"></el-option>
            <el-option label="进阶" value="进阶"></el-option>
            <el-option label="高级" value="高级"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddCourseDialog = false">取消</el-button>
        <el-button type="primary" @click="addCourse">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const goBack = () => {
  router.push('/')
}

const showAddCourseDialog = ref(false)

const courseForm = ref({
  title: '',
  description: '',
  category: '绘画',
  level: '入门'
})

const courses = ref([
  {
    id: 1,
    title: '二次元绘画基础',
    description: '从零开始学习二次元绘画技巧，掌握人物绘制基础',
    coverImage: '/images/1.jpg',
    category: '绘画',
    level: '入门',
    students: 128
  },
  {
    id: 2,
    title: '角色设计进阶',
    description: '深入学习角色设计原理，创建独特的二次元角色',
    coverImage: '/images/2.jpg',
    category: '绘画',
    level: '进阶',
    students: 89
  },
  {
    id: 3,
    title: '游戏原画创作',
    description: '学习游戏原画创作技巧，设计游戏角色和场景',
    coverImage: '/images/3.jpg',
    category: '游戏',
    level: '高级',
    students: 67
  }
])

const addCourse = () => {
  if (!courseForm.value.title || !courseForm.value.description) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  const coverImages = ['/images/4.jpg', '/images/5.jpg', '/images/6.jpg', '/images/7.jpg', '/images/8.jpg']
  const randomImage = coverImages[Math.floor(Math.random() * coverImages.length)]
  
  const newCourse = {
    id: Date.now(),
    title: courseForm.value.title,
    description: courseForm.value.description,
    coverImage: randomImage,
    category: courseForm.value.category,
    level: courseForm.value.level,
    students: 0
  }
  
  courses.value.push(newCourse)
  showAddCourseDialog.value = false
  courseForm.value = {
    title: '',
    description: '',
    category: '绘画',
    level: '入门'
  }
  ElMessage.success('课程添加成功')
}

const editCourse = (course) => {
  ElMessage.info(`编辑课程: ${course.title}`)
}

const deleteCourse = (courseId) => {
  courses.value = courses.value.filter(c => c.id !== courseId)
  ElMessage.success('课程删除成功')
}
</script>

<style scoped>
.course-management {
  padding: 30px;
  min-height: 100vh;
}

.back-button-container {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 20px;
}

.back-btn {
  background: linear-gradient(45deg, #5f27cd, #ff6b9d);
  border: 1px solid #9c27b0;
  color: #fff;
  border-radius: 15px;
  padding: 10px 20px;
  font-weight: bold;
  box-shadow: 0 0 10px rgba(156, 39, 176, 0.5);
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: linear-gradient(45deg, #7e47c2, #ff8fab);
  box-shadow: 0 0 20px rgba(156, 39, 176, 0.8);
  transform: translateX(-3px);
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

.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.add-btn {
  background: linear-gradient(45deg, #ff6b9d, #5f27cd);
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-weight: bold;
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.5);
}

.add-btn:hover {
  background: linear-gradient(45deg, #ff8fab, #7e47c2);
  box-shadow: 0 0 25px rgba(255, 107, 157, 0.8);
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.course-card {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.95), rgba(22, 33, 62, 0.95));
  border: 1px solid #9c27b0;
  border-radius: 15px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(156, 39, 176, 0.5);
}

.course-header {
  height: 180px;
  overflow: hidden;
}

.course-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.course-card:hover .course-cover {
  transform: scale(1.1);
}

.course-body {
  padding: 16px;
}

.course-title {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b9d;
  margin-bottom: 8px;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.5);
}

.course-description {
  font-size: 14px;
  color: #81d4fa;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-item {
  font-size: 12px;
  padding: 4px 10px;
  background: rgba(64, 196, 255, 0.2);
  color: #40c4ff;
  border-radius: 10px;
  border: 1px solid rgba(64, 196, 255, 0.3);
}

.course-actions {
  display: flex;
  gap: 10px;
}

.course-actions .el-button {
  flex: 1;
  border-radius: 15px;
}

.course-actions .el-button:first-child {
  background: rgba(64, 196, 255, 0.2);
  border: 1px solid #40c4ff;
  color: #40c4ff;
}

.course-actions .el-button:first-child:hover {
  background: rgba(64, 196, 255, 0.3);
}

.course-actions .el-button:last-child {
  background: rgba(255, 107, 157, 0.2);
  border: 1px solid #ff6b9d;
  color: #ff6b9d;
}

.course-actions .el-button:last-child:hover {
  background: rgba(255, 107, 157, 0.3);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-footer .el-button--primary {
  background: linear-gradient(45deg, #ff6b9d, #5f27cd);
  border: none;
  border-radius: 15px;
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

.el-select {
  width: 100%;
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
</style>