
# 小喵仙入梦学堂 🌟

> 踏入你的专属次元，让学习成为一场舒适的探索之旅

---

## 📖 项目简介

小喵仙入梦学堂是一款二次元风格的在线教育平台，采用现代化的前后端分离架构，为用户提供沉浸式的学习体验。

### ✨ 核心特色

- **二次元视觉设计**：采用深色主题配合霓虹色彩，打造独特的动漫风格界面
- **沉浸式交互**：全屏视频背景、动态轮播、音效反馈等丰富的交互体验
- **多角色系统**：支持学生、教师、管理员三种角色登录
- **完整的学习功能**：课程浏览、学习进度追踪、收藏管理、评论互动等

---

## 📹 视频演示

项目演示视频：[点我查看喵~](https://pan.baidu.com/s/1nd9_9YR9x6MiAEGz2l18GA?pwd=6666)

---

## 🛠️ 技术栈

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | ^3.3.0 | 前端框架 |
| Vite | ^4.4.0 | 构建工具 |
| Element Plus | ^2.13.6 | UI组件库 |
| Pinia | ^3.0.4 | 状态管理 |
| Vue Router | ^4.2.0 | 路由管理 |
| Pixi.js | ^8.17.1 | 2D渲染引擎 |
| pixi-live2d-display | ^0.4.0 | Live2D虚拟角色 |
| Axios | ^1.13.6 | HTTP客户端 |

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.0 | 后端框架 |
| MyBatis Plus | 3.5.6 | ORM框架 |
| Spring Security | - | 安全框架 |
| JWT | 0.9.0 | 身份认证 |
| MySQL | 8.0.33 | 数据库 |
| Knife4j | 3.0.3 | API文档 |
| 阿里云OSS | 3.17.0 | 对象存储 |

---

## 📁 项目结构

```
二次元化在线教育平台/
├── backend/                          # 后端代码
│   ├── src/main/java/com/edu/platform/
│   │   ├── controller/              # REST API控制器
│   │   ├── service/                 # 业务逻辑层
│   │   ├── mapper/                  # 数据访问层
│   │   ├── model/                   # 实体模型
│   │   ├── config/                  # 配置类
│   │   ├── utils/                   # 工具类
│   │   └── Application.java         # 启动类
│   ├── src/main/resources/
│   │   ├── sql/                     # 数据库初始化脚本
│   │   └── application.yml          # 应用配置
│   └── pom.xml                      # Maven配置
│
├── frontend/                        # 前端代码
│   ├── public/                      # 静态资源
│   │   ├── audio/                   # 音效文件
│   │   ├── images/                  # 图片资源
│   │   ├── music/                   # 背景音乐
│   │   ├── videos/                  # 视频资源
│   │   └── png/                     # 图标资源
│   ├── src/
│   │   ├── pages/                   # 页面组件
│   │   ├── router/                  # 路由配置
│   │   ├── store/                   # 状态管理
│   │   ├── utils/                   # 工具函数
│   │   ├── assets/                  # 样式资源
│   │   ├── App.vue                  # 根组件
│   │   └── main.js                  # 入口文件
│   ├── index.html                   # HTML模板
│   ├── package.json                 # 依赖配置
│   └── vite.config.js               # Vite配置
└── .gitignore                       # Git忽略配置
```

---

## 🚀 快速开始

### 环境要求

- **Java**: JDK 8+
- **Node.js**: v16+
- **MySQL**: 8.0+

### 后端启动

1. 配置数据库连接信息（`backend/src/main/resources/application.yml`）

2. 设置阿里云 OSS 环境变量（敏感信息已从配置文件中移除）：
```bash
# Linux/Mac
export ALIYUN_ACCESS_KEY_ID=your-access-key-id
export ALIYUN_ACCESS_KEY_SECRET=your-access-key-secret

# Windows (PowerShell)
$env:ALIYUN_ACCESS_KEY_ID="your-access-key-id"
$env:ALIYUN_ACCESS_KEY_SECRET="your-access-key-secret"
```

3. 创建数据库并执行初始化脚本：
```sql
CREATE DATABASE education_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
执行 `backend/src/main/resources/sql/init.sql`

4. 启动后端服务：
```bash
cd backend
mvn spring-boot:run
```

### 前端启动

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

3. 构建生产版本：
```bash
npm run build
```

---

## 🔌 API 接口

### 用户管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/user/login` | POST | 用户登录 |
| `/api/user/register` | POST | 用户注册 |
| `/api/user/info` | GET | 获取用户信息 |
| `/api/user/update` | PUT | 更新用户信息 |

### 课程管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/course/list` | GET | 获取课程列表 |
| `/api/course/get/{id}` | GET | 获取课程详情 |
| `/api/course/create` | POST | 创建课程 |
| `/api/course/update` | PUT | 更新课程 |
| `/api/course/delete/{id}` | DELETE | 删除课程 |

### 其他接口

- **收藏管理**: `/api/collection/*`
- **评论管理**: `/api/comment/*`
- **学习进度**: `/api/learning-progress/*`
- **任务系统**: `/api/task/*`
- **成就系统**: `/api/achievement/*`
- **积分记录**: `/api/points-record/*`

---

## 📱 功能模块

### 用户模块
- 用户注册/登录（支持用户名、邮箱、手机号）
- 用户信息管理
- 密码重置

### 课程模块
- 课程列表展示
- 课程分类浏览
- 课程详情查看

### 学习模块
- 学习进度追踪
- 视频播放
- 学习记录

### 互动模块
- 课程收藏
- 评论功能
- 积分系统

### 管理模块
- 课程管理（教师）
- 系统管理（管理员）

---

## 🎨 界面预览

### 首页特色
- 全屏动态视频背景
- 轮播图展示热门课程
- 分类导航快速筛选
- 钢琴键式课程卡片布局
- 霓虹风格UI设计

### 交互体验
- 点击音效反馈
- 悬停动画效果
- 平滑滚动导航
- 响应式设计适配多端

---

## 📄 许可证

MIT License

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

> ⭐ 如果觉得项目不错，欢迎给个 Star！

---

*Made with ❤️ by 小喵仙团队*
