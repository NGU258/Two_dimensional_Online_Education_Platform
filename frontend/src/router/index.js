import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../pages/Home.vue')
  },
  {
    path: '/courses',
    name: 'Courses',
    component: () => import('../pages/Courses.vue')
  },
  {
    path: '/course/:id',
    name: 'CourseDetail',
    component: () => import('../pages/CourseDetail.vue')
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('../pages/UserProfile.vue'),
    meta: { requiresAuth: true, hideNav: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../pages/Register.vue')
  },
  {
    path: '/help-center',
    name: 'HelpCenter',
    component: () => import('../pages/HelpCenter.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/course-management',
    name: 'CourseManagement',
    component: () => import('../pages/CourseManagement.vue'),
    meta: { requiresAuth: true, role: 'teacher', hideNav: true, hideFooter: true }
  },
  {
    path: '/system-management',
    name: 'SystemManagement',
    component: () => import('../pages/SystemManagement.vue'),
    meta: { requiresAuth: true, role: 'admin', hideNav: true, hideFooter: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 每次路由切换时滚动到顶部
    return { top: 0, behavior: 'smooth' }
  }
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  if (token && (to.path === '/login' || to.path === '/register')) {
    next('/')
  } else if (requiresAuth && !token) {
    next('/login')
  } else if (requiresAuth && token) {
    const user = localStorage.getItem('user')
    if (user) {
      try {
        const userObj = JSON.parse(user)
        const requiredRole = to.meta.role
        if (requiredRole && userObj.role !== requiredRole) {
          next('/')
          return
        }
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
    next()
  } else {
    next()
  }
})

export default router