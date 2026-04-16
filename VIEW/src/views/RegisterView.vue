<template>
  <div class="min-h-[80vh] flex items-center justify-center px-4">
    <div class="w-full max-w-sm">

      <div class="text-center mb-8">
        <h1 class="font-serif text-2xl font-semibold text-ink mb-1">시인시 시작하기</h1>
        <p class="text-sm text-ink-light">당신의 시를 세상과 나눠보세요</p>
      </div>

      <div class="card p-7 space-y-4">
        <div v-if="errorMsg" class="bg-rose-50 border border-rose-200 text-rose-600 text-sm px-4 py-3 rounded-lg">
          {{ errorMsg }}
        </div>

        <div>
          <label class="block text-xs font-medium text-ink-light mb-1.5">이메일</label>
          <input v-model="form.email" type="email" class="input-field" placeholder="hello@example.com" />
        </div>

        <div>
          <label class="block text-xs font-medium text-ink-light mb-1.5">닉네임 <span class="font-normal text-ink-lighter">(2~20자)</span></label>
          <input v-model="form.nickname" type="text" class="input-field" placeholder="나의 필명" />
        </div>

        <div>
          <label class="block text-xs font-medium text-ink-light mb-1.5">비밀번호 <span class="font-normal text-ink-lighter">(8자 이상)</span></label>
          <input
            v-model="form.password"
            type="password"
            class="input-field"
            placeholder="••••••••"
            @keyup.enter="register"
          />
        </div>

        <button
          @click="register"
          class="btn-primary w-full py-2.5"
          :disabled="loading"
        >
          {{ loading ? '가입 중...' : '가입하기' }}
        </button>

        <p class="text-center text-sm text-ink-light pt-1">
          이미 계정이 있으신가요?
          <router-link to="/login" class="text-indigo font-medium hover:underline">로그인</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({ email: '', nickname: '', password: '' })
const loading = ref(false)
const errorMsg = ref('')

async function register() {
  errorMsg.value = ''
  loading.value = true
  try {
    await auth.register(form.email, form.password, form.nickname)
    router.push({ path: '/verify-email', query: { email: form.email } })
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '회원가입에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>
