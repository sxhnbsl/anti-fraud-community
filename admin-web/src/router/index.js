import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/Index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/users',
        name: 'Users',
        component: () => import('@/views/user/Index.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: '/user-audit',
        name: 'UserAudit',
        component: () => import('@/views/user/Audit.vue'),
        meta: { title: '用户审核' }
      },
      {
        path: '/knowledge',
        name: 'Knowledge',
        component: () => import('@/views/knowledge/Index.vue'),
        meta: { title: '知识库管理' }
      },
      {
        path: '/cases',
        name: 'Cases',
        component: () => import('@/views/case/Index.vue'),
        meta: { title: '案例管理' }
      },
      {
        path: '/posts',
        name: 'Posts',
        component: () => import('@/views/post/Index.vue'),
        meta: { title: '帖子管理' }
      },
      {
        path: '/comments',
        name: 'Comments',
        component: () => import('@/views/comment/Index.vue'),
        meta: { title: '评论管理' }
      },
      {
        path: '/reports',
        name: 'Reports',
        component: () => import('@/views/report/Index.vue'),
        meta: { title: '举报管理' }
      },
      {
        path: '/analysis',
        name: 'Analysis',
        component: () => import('@/views/analysis/Index.vue'),
        meta: { title: '数据分析' }
      },
      {
        path: '/banners',
        name: 'Banners',
        component: () => import('@/views/banner/Index.vue'),
        meta: { title: '轮播图管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
