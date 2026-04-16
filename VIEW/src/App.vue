<template>
  <div class="min-h-screen bg-cream">
    <!-- 네비게이션 -->
    <header class="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-zinc-100">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 h-14 flex items-center justify-between">
        <!-- 로고 -->
        <router-link to="/" class="flex items-center gap-2 group">
          <span class="font-serif text-xl font-semibold text-ink group-hover:text-indigo transition-colors">
            시인시
          </span>
          <span class="hidden sm:inline text-xs text-ink-lighter font-light tracking-widest">
            SEE IN POEM
          </span>
        </router-link>

        <!-- 우측 액션 -->
        <div class="flex items-center gap-1.5">
          <template v-if="auth.isLoggedIn">
            <router-link to="/my" class="btn-ghost text-xs px-3 py-2 hidden sm:inline-flex">
              {{ auth.nickname }}
            </router-link>
            <router-link to="/write" class="btn-primary text-xs px-4 py-2">
              ✏️ 시 쓰기
            </router-link>
            <button @click="handleLogout" class="btn-ghost text-xs px-3 py-2">
              로그아웃
            </button>
          </template>
          <template v-else>
            <router-link to="/login" class="btn-ghost text-xs">로그인</router-link>
            <router-link to="/register" class="btn-primary text-xs px-4 py-2">회원가입</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 메인 콘텐츠 -->
    <main>
      <router-view />
    </main>

    <!-- 푸터 -->
    <footer class="border-t border-zinc-100 mt-20 py-8 text-center text-xs text-ink-lighter">
      © 2025 시인시 — 당신의 마음을 시로
    </footer>

    <!-- 토스트 -->
    <ToastContainer />
  </div>
</template>

<script setup>
import { useAuthStore } from './stores/auth'
import { useRouter } from 'vue-router'
import ToastContainer from './components/ToastContainer.vue'

const auth = useAuthStore()
const router = useRouter()

function handleLogout() {
  auth.logout()
  router.push('/')
}
</script>
