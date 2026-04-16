import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomeView },
    { path: '/poem/:id', component: () => import('../views/PoemDetailView.vue') },
    { path: '/write', component: () => import('../views/WriteView.vue'), meta: { requiresAuth: true } },
    { path: '/write/:id', component: () => import('../views/WriteView.vue'), meta: { requiresAuth: true } },
    { path: '/my', component: () => import('../views/MyView.vue'), meta: { requiresAuth: true } },
    { path: '/login', component: () => import('../views/LoginView.vue') },
    { path: '/oauth2/callback', component: () => import('../views/OAuth2CallbackView.vue') },
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) return '/login'
})

export default router
