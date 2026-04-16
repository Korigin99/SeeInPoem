<template>
  <div class="min-h-[80vh] flex items-center justify-center">
    <div class="text-center">
      <div class="w-10 h-10 border-2 border-indigo border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
      <p class="text-sm text-ink-light">로그인 처리 중...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useToast } from '../composables/useToast'

const router = useRouter()
const auth = useAuthStore()
const toast = useToast()

onMounted(() => {
  const params = new URLSearchParams(window.location.search)
  const token = params.get('token')
  const refreshToken = params.get('refreshToken')

  if (token && refreshToken) {
    auth.setAuth({ token, refreshToken, nickname: null, email: null })
    // 실제 닉네임/이메일은 /api/v1/users/me에서 가져옴
    auth.fetchMe().then(() => {
      toast.success('로그인되었습니다.')
      router.replace('/')
    })
  } else {
    toast.error('로그인에 실패했습니다.')
    router.replace('/login')
  }
})
</script>
