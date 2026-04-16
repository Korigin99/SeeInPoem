<template>
  <div class="min-h-[80vh] flex items-center justify-center px-4">
    <div class="w-full max-w-sm">

      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-indigo-50 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-indigo" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
              d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
          </svg>
        </div>
        <h1 class="font-serif text-2xl font-semibold text-ink mb-1">이메일 인증</h1>
        <p class="text-sm text-ink-light">
          <span class="font-medium text-ink">{{ email }}</span>으로<br>
          발송된 6자리 코드를 입력해주세요
        </p>
      </div>

      <div class="card p-7 space-y-5">

        <div v-if="errorMsg" class="bg-rose-50 border border-rose-200 text-rose-600 text-sm px-4 py-3 rounded-lg">
          {{ errorMsg }}
        </div>
        <div v-if="successMsg" class="bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm px-4 py-3 rounded-lg">
          {{ successMsg }}
        </div>

        <!-- 6자리 코드 입력 -->
        <div>
          <label class="block text-xs font-medium text-ink-light mb-2">인증 코드</label>
          <div class="flex gap-2 justify-between">
            <input
              v-for="(_, i) in 6"
              :key="i"
              :ref="el => inputs[i] = el"
              v-model="digits[i]"
              type="text"
              inputmode="numeric"
              maxlength="1"
              @input="onInput(i)"
              @keydown="onKeydown($event, i)"
              @paste="onPaste($event)"
              class="w-11 h-12 text-center text-lg font-semibold border border-zinc-200 rounded-lg focus:outline-none focus:border-indigo focus:ring-1 focus:ring-indigo transition-colors"
              :class="digits[i] ? 'border-indigo bg-indigo/5' : ''"
            />
          </div>
        </div>

        <button
          @click="verify"
          :disabled="loading || code.length < 6"
          class="btn-primary w-full py-2.5"
        >
          {{ loading ? '확인 중...' : '인증 완료' }}
        </button>

        <!-- 재발송 -->
        <div class="text-center text-sm text-ink-light">
          코드를 받지 못하셨나요?
          <button
            @click="resend"
            :disabled="resendCooldown > 0 || resending"
            class="text-indigo font-medium hover:underline disabled:text-ink-lighter disabled:no-underline ml-1"
          >
            {{ resendCooldown > 0 ? `${resendCooldown}초 후 재발송` : '재발송' }}
          </button>
        </div>

      </div>

      <p class="text-center text-xs text-ink-lighter mt-5">
        이메일 주소를 잘못 입력하셨나요?
        <router-link to="/register" class="text-indigo hover:underline">회원가입으로 돌아가기</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authApi } from '../api'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const email = ref(route.query.email || '')
const digits = ref(Array(6).fill(''))
const inputs = ref([])
const loading = ref(false)
const resending = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const resendCooldown = ref(0)

const code = computed(() => digits.value.join(''))

// 이메일 없으면 회원가입으로
onMounted(() => {
  if (!email.value) router.replace('/register')
  inputs.value[0]?.focus()
})

function onInput(i) {
  const val = digits.value[i]
  // 숫자만 허용
  if (!/^\d$/.test(val)) {
    digits.value[i] = ''
    return
  }
  if (i < 5) inputs.value[i + 1]?.focus()
  if (code.value.length === 6) verify()
}

function onKeydown(e, i) {
  if (e.key === 'Backspace' && !digits.value[i] && i > 0) {
    inputs.value[i - 1]?.focus()
  }
}

function onPaste(e) {
  e.preventDefault()
  const text = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6)
  text.split('').forEach((char, i) => {
    if (i < 6) digits.value[i] = char
  })
  const focusIdx = Math.min(text.length, 5)
  inputs.value[focusIdx]?.focus()
  if (text.length === 6) verify()
}

async function verify() {
  if (code.value.length < 6 || loading.value) return
  errorMsg.value = ''
  loading.value = true
  try {
    const { data } = await authApi.verifyEmail({ email: email.value, code: code.value })
    auth.setAuth(data)
    router.push('/')
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '인증에 실패했습니다.'
    digits.value = Array(6).fill('')
    inputs.value[0]?.focus()
  } finally {
    loading.value = false
  }
}

async function resend() {
  if (resendCooldown.value > 0 || resending.value) return
  errorMsg.value = ''
  resending.value = true
  try {
    await authApi.resendCode(email.value)
    successMsg.value = '인증 코드를 재발송했습니다.'
    setTimeout(() => successMsg.value = '', 3000)
    startCooldown(60)
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '재발송에 실패했습니다.'
  } finally {
    resending.value = false
  }
}

function startCooldown(seconds) {
  resendCooldown.value = seconds
  const timer = setInterval(() => {
    resendCooldown.value--
    if (resendCooldown.value <= 0) clearInterval(timer)
  }, 1000)
}
</script>
