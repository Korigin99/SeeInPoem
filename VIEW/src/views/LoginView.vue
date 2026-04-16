<template>
  <div class="min-h-[80vh] flex items-center justify-center px-4">
    <div class="w-full max-w-sm">

      <div class="text-center mb-8">
        <h1 class="font-serif text-2xl font-semibold text-ink mb-1">시인시</h1>
        <p class="text-sm text-ink-light">마음속 풍경을 시로 담고, 함께 읽어요</p>
      </div>

      <div class="card p-7 space-y-4">
        <a
          :href="kakaoLoginUrl"
          class="flex items-center justify-center gap-3 w-full py-3 rounded-xl font-medium text-sm transition-all duration-150"
          style="background:#FEE500; color:#191919;"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="#191919">
            <path d="M12 3C6.477 3 2 6.477 2 10.5c0 2.608 1.557 4.9 3.938 6.306L4.9 20.5l4.356-2.87C10.057 17.87 11.012 18 12 18c5.523 0 10-3.477 10-7.5S17.523 3 12 3z"/>
          </svg>
          카카오로 시작하기
        </a>

        <p class="text-center text-xs text-ink-lighter pt-2">
          카카오 계정으로 간편하게 로그인하세요.<br>
          처음 방문이라면 자동으로 회원가입됩니다.
        </p>
      </div>

    </div>
  </div>
</template>

<script setup>
const kakaoLoginUrl = (import.meta.env.VITE_API_BASE_URL ?? '') + '/oauth2/authorization/kakao'
</script>
