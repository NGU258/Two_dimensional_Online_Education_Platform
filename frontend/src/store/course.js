import { defineStore } from 'pinia'
import axios from 'axios'

// 可用的图片列表
const availableImages = [
  '/images/1.jpg', '/images/2.jpg', '/images/3.jpg', '/images/4.jpg', '/images/5.jpg',
  '/images/6.jpg', '/images/7.jpg', '/images/8.jpg', '/images/9.jpg', '/images/10.jpg',
  '/images/11.jpg', '/images/12.jpg', '/images/13.jpg', '/images/14.jpg', '/images/15.jpg',
  '/images/16.jpg', '/images/17.jpg', '/images/18.jpg', '/images/19.jpg', '/images/20.jpg',
  '/images/21.jpg', '/images/22.jpg', '/images/23.jpg', '/images/24.jpg', '/images/25.jpg',
  '/images/26.jpg', '/images/27.jpg', '/images/28.jpg', '/images/29.jpg', '/images/30.jpg'
];

// 模拟课程数据
const mockCourses = [
  // 社会经验
  {
    id: 1,
    title: '老子学十遍',
    description: '深入学习老子的智慧，掌握道家思想的精髓',
    coverImage: '/images/1.jpg',
    videoPath: '/videos/社会经验/老子学十遍.mp4',
    categoryId: 1,
    level: '中级',
    rating: 4.8,
    popularity: 98,
    createTime: '2024-03-01'
  },
  
  // 职场经验
  {
    id: 2,
    title: '泰康人寿空中宣讲会',
    description: '了解泰康人寿的企业文化和职业发展机会',
    coverImage: '/images/2.jpg',
    videoPath: '/videos/职场经验/泰康人寿空中宣讲会.mp4',
    categoryId: 2,
    level: '初级',
    rating: 4.5,
    popularity: 85,
    createTime: '2024-03-05'
  },
  {
    id: 3,
    title: '职场经验分享',
    description: '分享职场中的经验和技巧，帮助你快速适应职场',
    coverImage: '/images/3.jpg',
    videoPath: '/videos/职场经验/职场经验.mp4',
    categoryId: 2,
    level: '中级',
    rating: 4.7,
    popularity: 92,
    createTime: '2024-03-10'
  },
  
  // 动漫
  {
    id: 4,
    title: '02号精彩片段',
    description: 'Darling in the Franxx中02的精彩表现，感受机甲战斗的魅力',
    coverImage: '/images/4.jpg',
    videoPath: '/videos/动漫/02号.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.8,
    popularity: 93,
    createTime: '2024-03-20'
  },
  {
    id: 5,
    title: '02沐浴',
    description: 'Darling in the Franxx中02的沐浴场景',
    coverImage: '/images/5.jpg',
    videoPath: '/videos/动漫/02沐浴.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 91,
    createTime: '2024-03-21'
  },
  {
    id: 6,
    title: 'Marry',
    description: 'Darling in the Franxx精彩片段',
    coverImage: '/images/6.jpg',
    videoPath: '/videos/动漫/Marry.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.6,
    popularity: 89,
    createTime: '2024-03-22'
  },
  {
    id: 7,
    title: '公主现身',
    description: 'Darling in the Franxx精彩片段',
    coverImage: '/images/7.jpg',
    videoPath: '/videos/动漫/公主现身.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 90,
    createTime: '2024-03-23'
  },
  {
    id: 8,
    title: '单身狗',
    description: 'Darling in the Franxx精彩片段',
    coverImage: '/images/8.jpg',
    videoPath: '/videos/动漫/单身狗.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.5,
    popularity: 88,
    createTime: '2024-03-24'
  },
  {
    id: 9,
    title: '大结局',
    description: 'Darling in the Franxx大结局',
    coverImage: '/images/9.jpg',
    videoPath: '/videos/动漫/大结局.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.9,
    popularity: 95,
    createTime: '2024-03-25'
  },
  {
    id: 10,
    title: '比翼鸟',
    description: 'Darling in the Franxx精彩片段',
    coverImage: '/images/10.jpg',
    videoPath: '/videos/动漫/比翼鸟.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.8,
    popularity: 92,
    createTime: '2024-03-26'
  },
  {
    id: 11,
    title: '莓',
    description: 'Darling in the Franxx中莓的精彩表现',
    coverImage: '/images/11.jpg',
    videoPath: '/videos/动漫/莓.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 90,
    createTime: '2024-03-27'
  },
  
  // 仙逆
  {
    id: 12,
    title: '一生友人并不多',
    description: '仙逆动画精彩片段，感受修真世界的魅力',
    coverImage: '/images/12.jpg',
    videoPath: '/videos/动漫/一生友人并不多.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 90,
    createTime: '2024-03-28'
  },
  {
    id: 13,
    title: '你不过是瓮中之鳖',
    description: '仙逆动画精彩片段',
    coverImage: '/images/13.jpg',
    videoPath: '/videos/动漫/你不过是瓮中之鳖 .mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.8,
    popularity: 91,
    createTime: '2024-03-29'
  },
  {
    id: 14,
    title: '你可想好了，要跟随我？',
    description: '仙逆动画精彩片段',
    coverImage: '/images/14.jpg',
    videoPath: '/videos/动漫/你可想好了，要跟随我？.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 90,
    createTime: '2024-03-30'
  },
  {
    id: 15,
    title: '你许爷爷来啦~',
    description: '仙逆动画精彩片段',
    coverImage: '/images/15.jpg',
    videoPath: '/videos/动漫/你许爷爷来啦~.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.6,
    popularity: 89,
    createTime: '2024-03-31'
  },
  {
    id: 16,
    title: '动手，则危矣~',
    description: '仙逆动画精彩片段',
    coverImage: '/images/16.jpg',
    videoPath: '/videos/动漫/动手，则危矣~.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 90,
    createTime: '2024-04-01'
  },
  {
    id: 17,
    title: '十年内保平安，十年后靠自己',
    description: '仙逆动画精彩片段',
    coverImage: '/images/17.jpg',
    videoPath: '/videos/动漫/十年内保平安，十年后靠自己.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.8,
    popularity: 92,
    createTime: '2024-04-02'
  },
  {
    id: 18,
    title: '婉儿',
    description: '仙逆动画中婉儿的精彩表现',
    coverImage: '/images/18.jpg',
    videoPath: '/videos/动漫/婉儿.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.9,
    popularity: 94,
    createTime: '2024-04-03'
  },
  {
    id: 19,
    title: '孙子哪里跑~',
    description: '仙逆动画精彩片段',
    coverImage: '/images/19.jpg',
    videoPath: '/videos/动漫/孙子哪里跑~.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.6,
    popularity: 88,
    createTime: '2024-04-04'
  },
  {
    id: 20,
    title: '尽情攻击的机会~',
    description: '仙逆动画精彩片段',
    coverImage: '/images/20.jpg',
    videoPath: '/videos/动漫/尽情攻击的机会~.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 90,
    createTime: '2024-04-05'
  },
  {
    id: 21,
    title: '报信法术',
    description: '仙逆动画精彩片段',
    coverImage: '/images/21.jpg',
    videoPath: '/videos/动漫/报信法术.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.6,
    popularity: 89,
    createTime: '2024-04-06'
  },
  {
    id: 22,
    title: '春风一度~',
    description: '仙逆动画精彩片段',
    coverImage: '/images/22.jpg',
    videoPath: '/videos/动漫/春风一度~.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 91,
    createTime: '2024-04-07'
  },
  {
    id: 23,
    title: '罢了罢了',
    description: '仙逆动画精彩片段',
    coverImage: '/images/23.jpg',
    videoPath: '/videos/动漫/罢了罢了.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.6,
    popularity: 88,
    createTime: '2024-04-08'
  },
  {
    id: 24,
    title: '还不是被劈了',
    description: '仙逆动画精彩片段',
    coverImage: '/images/24.jpg',
    videoPath: '/videos/动漫/还不是被劈了.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.5,
    popularity: 87,
    createTime: '2024-04-09'
  },
  {
    id: 25,
    title: '速速献上仙玉~',
    description: '仙逆动画精彩片段',
    coverImage: '/images/25.jpg',
    videoPath: '/videos/动漫/速速献上仙玉~.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 90,
    createTime: '2024-04-10'
  },
  
  // 吞噬星空
  {
    id: 26,
    title: '虫族女王的魅力~',
    description: '吞噬星空动画精彩片段，感受宇宙级的战斗',
    coverImage: '/images/26.jpg',
    videoPath: '/videos/动漫/虫族女王的魅力~.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.6,
    popularity: 88,
    createTime: '2024-04-11'
  },
  {
    id: 27,
    title: '保护了我',
    description: '吞噬星空动画精彩片段',
    coverImage: '/images/27.jpg',
    videoPath: '/videos/动漫/保护了我.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 90,
    createTime: '2024-04-12'
  },
  {
    id: 28,
    title: '啥？就控制完虫族女王了？',
    description: '吞噬星空动画精彩片段',
    coverImage: '/images/28.jpg',
    videoPath: '/videos/动漫/啥？就控制完虫族女王了？.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.8,
    popularity: 92,
    createTime: '2024-04-13'
  },
  {
    id: 29,
    title: '好~',
    description: '吞噬星空动画精彩片段',
    coverImage: '/images/29.jpg',
    videoPath: '/videos/动漫/好~.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.6,
    popularity: 89,
    createTime: '2024-04-14'
  },
  {
    id: 30,
    title: '灵活走位~',
    description: '吞噬星空动画精彩片段',
    coverImage: '/images/30.jpg',
    videoPath: '/videos/动漫/灵活走位~.mp4',
    categoryId: 3,
    level: '初级',
    rating: 4.7,
    popularity: 91,
    createTime: '2024-04-15'
  },
  
  // 游戏
  {
    id: 31,
    title: '龙王神威',
    description: '永劫无间游戏中的精彩操作，感受武侠风格的战斗',
    coverImage: '/images/14.jpg',
    videoPath: '/videos/游戏/龙王神威.mp4',
    categoryId: 4,
    level: '高级',
    rating: 4.9,
    popularity: 96,
    createTime: '2024-04-16'
  },
  {
    id: 32,
    title: '偷袭哈哈',
    description: '永劫无间游戏中的精彩操作',
    coverImage: '/images/13.jpg',
    videoPath: '/videos/游戏/偷袭哈哈.mp4',
    categoryId: 4,
    level: '中级',
    rating: 4.7,
    popularity: 91,
    createTime: '2024-04-17'
  },
  {
    id: 33,
    title: '反杀哈哈',
    description: '永劫无间游戏中的精彩操作',
    coverImage: '/images/12.jpg',
    videoPath: '/videos/游戏/反杀哈哈.mp4',
    categoryId: 4,
    level: '中级',
    rating: 4.8,
    popularity: 93,
    createTime: '2024-04-18'
  },
  
  // 治愈
  {
    id: 34,
    title: 'English interesting',
    description: '有趣的英语学习视频，提高你的英语水平',
    coverImage: '/images/11.jpg',
    videoPath: '/videos/治愈/English interesting.mp4',
    categoryId: 5,
    level: '初级',
    rating: 4.6,
    popularity: 87,
    createTime: '2024-04-19'
  },
  {
    id: 35,
    title: '可爱猫猫',
    description: '可爱的猫咪视频，治愈你的心灵',
    coverImage: '/images/10.jpg',
    videoPath: '/videos/治愈/可爱猫猫.mp4',
    categoryId: 5,
    level: '初级',
    rating: 4.9,
    popularity: 95,
    createTime: '2024-04-20'
  },
  
  // 舞蹈演示
  {
    id: 36,
    title: '可以…让我再靠近一点点吗？❤️双子花嫁【勾指起誓】',
    description: '优美的舞蹈表演，展现双子花嫁的魅力',
    coverImage: '/images/9.jpg',
    videoPath: '/videos/舞蹈演示/可以…让我再靠近一点点吗？❤️双子花嫁【勾指起誓】.mp4',
    categoryId: 6,
    level: '初级',
    rating: 4.8,
    popularity: 94,
    createTime: '2024-04-21'
  },
  {
    id: 37,
    title: '性 感 甜 妹！！元 气 rollin【十元酱】',
    description: '元气满满的舞蹈表演，展现青春活力',
    coverImage: '/images/8.jpg',
    videoPath: '/videos/舞蹈演示/性 感 甜 妹！！元 气 rollin【十元酱】.mp4',
    categoryId: 6,
    level: '初级',
    rating: 4.7,
    popularity: 92,
    createTime: '2024-04-22'
  },
  
  // 英语口语
  {
    id: 38,
    title: '英语口语练习',
    description: '英语口语练习视频，提高你的英语水平',
    coverImage: '/images/7.jpg',
    videoPath: '/videos/英语口语/gm.mp4',
    categoryId: 7,
    level: '初级',
    rating: 4.6,
    popularity: 87,
    createTime: '2024-04-23'
  },
  
  // 电脑操作小技巧
  {
    id: 39,
    title: '奇怪的知识又增加了',
    description: '分享电脑操作的小技巧，提高你的工作效率',
    coverImage: '/images/6.jpg',
    videoPath: '/videos/电脑操作小技巧/奇怪的知识又增加了.mp4',
    categoryId: 8,
    level: '初级',
    rating: 4.7,
    popularity: 86,
    createTime: '2024-04-24'
  },
  {
    id: 40,
    title: '魔幻桌面',
    description: '教你如何创建魔幻桌面效果，让你的电脑更加个性化',
    coverImage: '/images/5.jpg',
    videoPath: '/videos/电脑操作小技巧/魔幻桌面.mp4',
    categoryId: 8,
    level: '中级',
    rating: 4.8,
    popularity: 90,
    createTime: '2024-04-25'
  },
  
  // 电视剧
  {
    id: 41,
    title: '消失的下雨天，我好想再淋一遍',
    description: '电视剧精彩片段，感受青春的美好',
    coverImage: '/images/4.jpg',
    videoPath: '/videos/电视剧/消失的下雨天，我好想再淋一遍.mp4',
    categoryId: 9,
    level: '初级',
    rating: 4.7,
    popularity: 91,
    createTime: '2024-04-26'
  },
  {
    id: 42,
    title: '理想伴侣上',
    description: '电视剧精彩片段，感受爱情的美好',
    coverImage: '/images/3.jpg',
    videoPath: '/videos/电视剧/理想伴侣上.mp4',
    categoryId: 9,
    level: '初级',
    rating: 4.6,
    popularity: 89,
    createTime: '2024-04-27'
  },
  
  // 知识博主
  {
    id: 43,
    title: '摸了九年的鱼~',
    description: '知识博主分享的学习经验，让你少走弯路',
    coverImage: '/images/2.jpg',
    videoPath: '/videos/知识博主翻车瞬间/摸了九年的鱼~.mp4',
    categoryId: 10,
    level: '中级',
    rating: 4.7,
    popularity: 91,
    createTime: '2024-04-28'
  },
  
  // AI相关知识
  {
    id: 44,
    title: '摸鱼水时长~',
    description: 'AI相关知识分享',
    coverImage: '/images/1.jpg',
    videoPath: '/videos/AI相关知识/摸鱼水时长~.mp4',
    categoryId: 10,
    level: '中级',
    rating: 4.6,
    popularity: 87,
    createTime: '2024-04-29'
  },
  
  // 毕业论文
  {
    id: 45,
    title: '两分钟速成三线表',
    description: '毕业论文必备技能，两分钟学会制作专业三线表',
    coverImage: '/images/1.jpg',
    videoPath: '/videos/毕业论文/【毕业论文】两分钟速成三线表.mp4',
    categoryId: 11,
    level: '初级',
    rating: 4.8,
    popularity: 95,
    createTime: '2024-04-30'
  }
];

export const useCourseStore = defineStore('course', {
  state: () => ({
    courses: [],
    currentCourse: null,
    categories: []
  }),
  actions: {
    async getCourses(params) {
      try {
        // 初始化 filteredCourses 变量
        let filteredCourses = [...mockCourses];
        
        // 分类筛选
        const categoryId = params && params.categoryId ? parseInt(params.categoryId) : 0;
        if (!isNaN(categoryId) && categoryId !== 0) {
          filteredCourses = filteredCourses.filter(course => course.categoryId === categoryId)
        }
        
        // 关键词搜索
        if (params && params.keyword) {
          const keyword = params.keyword.toLowerCase()
          filteredCourses = filteredCourses.filter(course => 
            course.title.toLowerCase().includes(keyword) || 
            course.description.toLowerCase().includes(keyword)
          )
        }
        
        // 排序
        if (params && params.sortBy === 'popularity') {
          filteredCourses.sort((a, b) => b.popularity - a.popularity)
        } else if (params && params.sortBy === 'rating') {
          filteredCourses.sort((a, b) => b.rating - a.rating)
        } else {
          // 默认按创建时间排序
          filteredCourses.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
        }
        
        // 为每个课程设置默认图片
        const coursesWithDefaultImages = filteredCourses.map((course, index) => {
          // 使用课程ID或索引来选择图片，确保每个课程都有一个固定的图片
          const imageIndex = course.id % availableImages.length;
          return {
            ...course,
            coverImage: availableImages[imageIndex]
          };
        });
        
        // 分页
        const total = coursesWithDefaultImages.length
        const page = params && params.page ? params.page : 1
        const pageSize = params && params.pageSize ? params.pageSize : 12
        const start = (page - 1) * pageSize
        const end = start + pageSize
        const paginatedCourses = coursesWithDefaultImages.slice(start, end)
        
        this.courses = paginatedCourses
        return { courses: paginatedCourses, total }
      } catch (error) {
        console.error('获取课程列表失败:', error)
        return { courses: [], total: 0 }
      }
    },
    
    async getCourseById(id) {
      try {
        // 模拟数据，从本地 videos 目录获取
        const courseId = parseInt(id);
        console.log('查找课程ID:', courseId);
        
        // 从mockCourses数组中查找对应的课程
        let course = mockCourses.find(c => c.id === courseId);
        console.log('找到的课程:', course);
        
        // 为课程设置默认图片
        const imageIndex = courseId % availableImages.length;
        console.log('图片索引:', imageIndex);
        console.log('可用图片数量:', availableImages.length);
        const coverImage = availableImages[imageIndex];
        console.log('封面图片:', coverImage);
        
        // 如果没有找到，构建默认课程数据
        if (!course) {
          // 构建课程数据
          course = {
            id: courseId,
            title: '课程标题',
            description: '课程描述',
            coverImage: coverImage,
            videoPath: '/videos/社会经验/老子学十遍.mp4',
            categoryId: 1,
            level: '中级',
            rating: 4.8,
            popularity: 98,
            createTime: '2024-03-01',
            author: '教师',
            viewCount: 1200,
            likeCount: 890,
            commentCount: 120,
            tags: ['标签1', '标签2'],
            chapters: [
              { id: 1, title: '第一章', duration: '15:30', videoPath: '/videos/社会经验/老子学十遍.mp4' },
              { id: 2, title: '第二章', duration: '20:15', videoPath: '/videos/社会经验/老子学十遍.mp4' }
            ]
          };
        } else {
          // 为找到的课程设置默认图片
          course.coverImage = coverImage;
          
          // 如果找到了，添加缺失的字段
          if (!course.chapters) {
            course.chapters = [
              { id: 1, title: '第一章', duration: '15:30', videoPath: course.videoPath },
              { id: 2, title: '第二章', duration: '20:15', videoPath: course.videoPath }
            ];
          } else {
            // 确保章节对象有videoPath属性
            course.chapters.forEach(chapter => {
              if (!chapter.videoPath) {
                chapter.videoPath = course.videoPath;
              }
            });
          }
          
          if (!course.learningGoals) {
            course.learningGoals = [
              '掌握课程核心知识',
              '提升实际应用能力',
              '培养解决问题的思维'
            ];
          }
          
          if (!course.targetAudience) {
            course.targetAudience = [
              '对课程内容感兴趣的学习者',
              '希望提升相关技能的专业人士',
              '想拓展知识面的爱好者'
            ];
          }
          
          if (!course.comments) {
            course.comments = [
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
            ];
          }
          
          if (!course.duration) {
            course.duration = course.chapters ? course.chapters.length : 0;
          }
          
          if (!course.commentCount) {
            course.commentCount = course.comments ? course.comments.length : 0;
          }
          
          if (!course.collectionCount) {
            course.collectionCount = 0;
          }
        }
        
        console.log('最终课程数据:', course);
        this.currentCourse = course
        return course
      } catch (error) {
        console.error('获取课程详情失败:', error)
        throw error
      }
    },
    
    async getCategories() {
      try {
        // 模拟分类数据
        const mockCategories = [
          { id: 1, name: '社会经验' },
          { id: 2, name: '职场经验' },
          { id: 3, name: '动漫' },
          { id: 4, name: '游戏' },
          { id: 5, name: '治愈' },
          { id: 6, name: '舞蹈演示' },
          { id: 7, name: '英语口语' },
          { id: 8, name: '电脑操作小技巧' },
          { id: 9, name: '电视剧' },
          { id: 10, name: '知识博主' },
          { id: 11, name: '毕业论文' }
        ]
        
        this.categories = mockCategories
        return mockCategories
      } catch (error) {
        console.error('获取分类失败:', error)
        return []
      }
    }
  }
})