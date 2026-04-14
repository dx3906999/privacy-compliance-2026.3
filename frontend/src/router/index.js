import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    children: [
      {
        path: '',
        redirect: '/home/upload'
      },
      {
        path: 'upload',
        name: 'Upload',
        component: () => import('../views/Upload.vue')
      },
      {
        path: 'result/:taskId',
        name: 'AnalyzeResult',
        component: () => import('../views/AnalyzeResult.vue')
      },
      {
        path: 'history',
        name: 'History',
        component: () => import('../views/History.vue')
      },
      {
        path: 'rules',
        name: 'RuleManage',
        component: () => import('../views/RuleManage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：验证登录状态
const whiteList = ['/login']
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (token) {
    // 已登录，访问登录页则重定向到首页
    if (to.path === '/login') {
      next('/home')
    } else {
      next()
    }
  } else {
    // 未登录，白名单路由直接放行
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
