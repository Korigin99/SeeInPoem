<template>
  <div class="max-w-3xl mx-auto px-4 sm:px-6 py-10">

    <!-- 히어로 -->
    <div class="text-center mb-12">
      <h1 class="font-serif text-4xl sm:text-5xl font-semibold text-ink mb-3 leading-tight">
        당신의 시를<br class="sm:hidden"> 나누어요
      </h1>
      <p class="text-ink-light text-sm sm:text-base">마음속 풍경을 시로 담고, 함께 읽어요</p>
    </div>

    <!-- 검색 -->
    <div class="relative mb-6">
      <svg class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-4.35-4.35M17 11A6 6 0 1 1 5 11a6 6 0 0 1 12 0z"/>
      </svg>
      <input
        v-model="keyword"
        type="text"
        placeholder="제목이나 내용으로 검색..."
        class="input-field pl-10 pr-4"
        @keyup.enter="search"
        @input="onKeywordInput"
      />
    </div>

    <!-- 카테고리 탭 -->
    <div class="flex gap-2 overflow-x-auto pb-2 mb-6 scrollbar-hide">
      <button
        v-for="cat in categories"
        :key="cat.value ?? 'all'"
        @click="selectCategory(cat.value)"
        :class="[
          'whitespace-nowrap px-4 py-1.5 rounded-full text-xs font-medium transition-all duration-150 border',
          selectedCategory === cat.value
            ? 'bg-indigo text-white border-indigo'
            : 'bg-white text-ink-light border-zinc-200 hover:border-indigo hover:text-indigo'
        ]"
      >
        {{ cat.label }}
      </button>
    </div>

    <!-- 초기 로딩 스켈레톤 (첫 진입 시에만) -->
    <div v-if="initialLoading" class="space-y-3">
      <div v-for="i in 5" :key="i" class="card p-5 animate-pulse">
        <div class="h-4 bg-zinc-100 rounded w-2/3 mb-3"></div>
        <div class="h-3 bg-zinc-100 rounded w-1/3"></div>
      </div>
    </div>

    <!-- 시 목록 (카테고리/검색 전환 시 목록 유지하며 dim 처리) -->
    <template v-else>
      <div v-if="!refreshing && poems.length === 0" class="text-center py-20 text-ink-lighter">
        <div class="text-4xl mb-3">🪶</div>
        <p class="text-sm">아직 작성된 시가 없습니다</p>
        <router-link v-if="auth.isLoggedIn" to="/write" class="btn-primary mt-4 inline-flex text-sm">
          첫 시를 써보세요
        </router-link>
      </div>

      <div
        v-else
        class="space-y-3 transition-opacity duration-200"
        :class="refreshing ? 'opacity-40 pointer-events-none' : 'opacity-100'"
      >
        <router-link
          v-for="poem in poems"
          :key="poem.id"
          :to="`/poem/${poem.id}`"
          class="card p-5 flex items-start justify-between gap-4 hover:border-indigo/30 hover:shadow-md transition-all duration-200 group block"
        >
          <div class="flex-1 min-w-0">
            <h2 class="font-serif text-base font-semibold text-ink group-hover:text-indigo transition-colors mb-1 truncate">
              {{ poem.title }}
            </h2>
            <div class="flex items-center gap-2 text-xs text-ink-lighter">
              <span>{{ poem.authorNickname }}</span>
              <span v-if="poem.category" class="category-chip">{{ categoryLabel(poem.category) }}</span>
            </div>
          </div>
          <div class="flex flex-col items-end gap-1 text-xs text-ink-lighter shrink-0">
            <span>{{ formatDate(poem.createdAt) }}</span>
            <div class="flex items-center gap-2">
              <span class="flex items-center gap-0.5 text-zinc-400">
                <EyeIcon class="w-3.5 h-3.5" />
                {{ poem.views }}
              </span>
              <span class="flex items-center gap-0.5 text-rose-400">
                <HeartIcon class="w-3.5 h-3.5" />
                {{ poem.likeCount }}
              </span>
              <span class="flex items-center gap-0.5 text-sky-400">
                <ChatIcon class="w-3.5 h-3.5" />
                {{ poem.commentCount }}
              </span>
            </div>
          </div>
        </router-link>
      </div>

      <!-- 페이지네이션 -->
      <div v-if="totalPages > 1" class="flex items-center justify-center gap-1 mt-8">
        <button
          @click="changePage(page - 1)"
          :disabled="page === 1"
          class="px-3 py-1.5 text-sm rounded-lg border border-zinc-200 disabled:opacity-30 hover:bg-zinc-50 transition-colors"
        >
          ←
        </button>
        <template v-for="p in pageNumbers" :key="p">
          <button
            @click="changePage(p)"
            :class="[
              'px-3 py-1.5 text-sm rounded-lg transition-colors',
              p === page
                ? 'bg-indigo text-white'
                : 'border border-zinc-200 hover:bg-zinc-50'
            ]"
          >
            {{ p }}
          </button>
        </template>
        <button
          @click="changePage(page + 1)"
          :disabled="page === totalPages"
          class="px-3 py-1.5 text-sm rounded-lg border border-zinc-200 disabled:opacity-30 hover:bg-zinc-50 transition-colors"
        >
          →
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { poemApi } from '../api'
import { useAuthStore } from '../stores/auth'

// 인라인 아이콘 컴포넌트 (외부 의존성 없음)
const EyeIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>`
}
const HeartIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/></svg>`
}
const ChatIcon = {
  template: `<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/></svg>`
}

const auth = useAuthStore()
const poems = ref([])
const initialLoading = ref(true)   // 첫 진입 시 스켈레톤
const refreshing = ref(false)      // 카테고리/검색 전환 시 dim 처리
const keyword = ref('')
const selectedCategory = ref(null)
const page = ref(1)
const totalPages = ref(1)
let searchTimeout = null

const categories = [
  { label: '전체', value: null },
  { label: '자유시', value: 'FREE' },
  { label: '서정시', value: 'LYRIC' },
  { label: '현대시', value: 'MODERN' },
  { label: '하이쿠', value: 'HAIKU' },
  { label: '서사시', value: 'EPIC' },
]

const pageNumbers = computed(() => {
  const nums = []
  const start = Math.max(1, page.value - 2)
  const end = Math.min(totalPages.value, start + 4)
  for (let i = start; i <= end; i++) nums.push(i)
  return nums
})

const categoryLabel = (val) => categories.find(c => c.value === val)?.label || val

async function fetchPoems(isInitial = false) {
  if (isInitial) {
    initialLoading.value = true
  } else {
    refreshing.value = true
  }
  try {
    const params = { page: page.value - 1, size: 10 }
    if (keyword.value) params.keyword = keyword.value
    if (selectedCategory.value) params.category = selectedCategory.value
    const { data } = await poemApi.getPoems(params)
    poems.value = data.content
    totalPages.value = data.totalPages
  } finally {
    initialLoading.value = false
    refreshing.value = false
  }
}

function selectCategory(val) {
  selectedCategory.value = val
  page.value = 1
  fetchPoems()
}

function changePage(p) {
  if (p < 1 || p > totalPages.value) return
  page.value = p
  fetchPoems()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function search() {
  page.value = 1
  fetchPoems()
}

function onKeywordInput() {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    page.value = 1
    fetchPoems()
  }, 400)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

onMounted(() => fetchPoems(true))
</script>
