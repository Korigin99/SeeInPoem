import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, userApi } from '../api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const nickname = computed(() => user.value?.nickname || '')
  const email = computed(() => user.value?.email || '')

  function setAuth(data) {
    token.value = data.token
    localStorage.setItem('token', data.token)
    localStorage.setItem('refreshToken', data.refreshToken)
    if (data.nickname && data.email) {
      user.value = { nickname: data.nickname, email: data.email }
      localStorage.setItem('user', JSON.stringify(user.value))
    }
  }

  async function fetchMe() {
    try {
      const { data } = await userApi.getMyProfile()
      user.value = { nickname: data.nickname, email: data.email }
      localStorage.setItem('user', JSON.stringify(user.value))
    } catch {
      // ignore
    }
  }

  async function logout() {
    try {
      await authApi.logout()
    } catch {
      // ignore
    } finally {
      token.value = null
      user.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('user')
    }
  }

  return { token, user, isLoggedIn, nickname, email, setAuth, fetchMe, logout }
})
