<template>
  <div class="max-w-3xl mx-auto px-4 sm:px-6 py-10">

    <!-- 프로필 헤더 -->
    <div v-if="profile" class="flex items-center gap-5 mb-10">
      <div class="w-14 h-14 rounded-full bg-indigo-50 flex items-center justify-center text-indigo text-xl font-semibold font-serif">
        {{ profile.nickname?.[0] }}
      </div>
      <div>
        <h1 class="font-serif text-xl font-semibold text-ink">{{ profile.nickname }}</h1>
        <p class="text-sm text-ink-lighter mt-0.5">{{ profile.email }}</p>
        <p class="text-xs text-ink-lighter mt-1">
          {{ profile.poemCount }}편의 시 · {{ formatDate(profile.createdAt) }}에 가입
        </p>
      </div>
      <router-link to="/write" class="btn-primary text-sm ml-auto">새 시 쓰기</router-link>
    </div>

    <!-- 구분선 -->
    <div class="border-t border-zinc-100 mb-8"></div>

    <!-- 내 시 목록 -->
    <h2 class="text-sm font-semibold text-ink mb-5">내가 쓴 시</h2>

    <div v-if="loading" class="space-y-3">
      <div v-for="i in 3" :key="i" class="card p-5 animate-pulse">
        <div class="h-4 bg-zinc-100 rounded w-2/3 mb-3"></div>
        <div class="h-3 bg-zinc-100 rounded w-1/3"></div>
      </div>
    </div>

    <template v-else>
      <div v-if="poems.length === 0" class="text-center py-20 text-ink-lighter">
        <div class="text-4xl mb-3">🪶</div>
        <p class="text-sm">아직 쓴 시가 없습니다</p>
        <router-link to="/write" class="btn-primary mt-4 inline-flex text-sm">첫 시를 써보세요</router-link>
      </div>

      <div v-else class="space-y-3">
        <div
          v-for="poem in poems"
          :key="poem.id"
          class="card p-5 flex items-start justify-between gap-4 group"
        >
          <router-link :to="`/poem/${poem.id}`" class="flex-1 min-w-0">
            <h3 class="font-serif text-base font-semibold text-ink group-hover:text-indigo transition-colors mb-1 truncate">
              {{ poem.title }}
            </h3>
            <div class="flex items-center gap-2 text-xs text-ink-lighter">
              <span v-if="poem.category" class="category-chip">{{ categoryLabel(poem.category) }}</span>
              <span>{{ formatDate(poem.createdAt) }}</span>
              <span class="flex items-center gap-1 text-rose-400">
                <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                </svg>
                {{ poem.likeCount }}
              </span>
              <span class="flex items-center gap-1 text-sky-400">
                <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
                </svg>
                {{ poem.commentCount }}
              </span>
            </div>
          </router-link>

          <div class="flex items-center gap-1 shrink-0 opacity-0 group-hover:opacity-100 transition-all">
            <router-link
              :to="`/write/${poem.id}`"
              class="text-xs text-ink-lighter hover:text-indigo px-2 py-1 rounded hover:bg-indigo-50 transition-colors"
            >수정</router-link>
            <button
              @click="openDeleteModal(poem.id)"
              class="text-xs text-ink-lighter hover:text-rose-500 px-2 py-1 rounded hover:bg-rose-50 transition-colors"
            >삭제</button>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div v-if="totalPages > 1" class="flex items-center justify-center gap-1 mt-8">
        <button @click="changePage(page - 1)" :disabled="page === 1"
          class="px-3 py-1.5 text-sm rounded-lg border border-zinc-200 disabled:opacity-30 hover:bg-zinc-50 transition-colors">←</button>
        <template v-for="p in pageNumbers" :key="p">
          <button @click="changePage(p)"
            :class="['px-3 py-1.5 text-sm rounded-lg transition-colors', p === page ? 'bg-indigo text-white' : 'border border-zinc-200 hover:bg-zinc-50']">{{ p }}</button>
        </template>
        <button @click="changePage(page + 1)" :disabled="page === totalPages"
          class="px-3 py-1.5 text-sm rounded-lg border border-zinc-200 disabled:opacity-30 hover:bg-zinc-50 transition-colors">→</button>
      </div>
    </template>

    <!-- 계정 설정 -->
    <div class="mt-12 border-t border-zinc-100 pt-8">
      <h2 class="text-sm font-semibold text-ink mb-5">계정 설정</h2>

      <!-- 닉네임 변경 -->
      <div class="card p-5 mb-3">
        <button
          @click="showNicknameForm = !showNicknameForm"
          class="flex items-center justify-between w-full text-sm font-medium text-ink"
        >
          닉네임 변경
          <svg
            :class="['w-4 h-4 text-ink-lighter transition-transform', showNicknameForm ? 'rotate-180' : '']"
            fill="none" stroke="currentColor" viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
          </svg>
        </button>

        <div v-if="showNicknameForm" class="mt-4 space-y-3">
          <input
            v-model="newNickname"
            type="text"
            placeholder="새 닉네임 (2~20자)"
            maxlength="20"
            class="input-field"
          />
          <div class="flex justify-end gap-2">
            <button @click="showNicknameForm = false; newNickname = ''" class="btn-secondary text-sm px-4 py-2">취소</button>
            <button @click="handleNicknameUpdate" :disabled="nicknameLoading || newNickname.length < 2" class="btn-primary text-sm px-4 py-2">
              {{ nicknameLoading ? '변경 중...' : '변경하기' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 계정 삭제 -->
      <div class="card p-5 border-rose-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-ink">계정 삭제</p>
            <p class="text-xs text-ink-lighter mt-0.5">삭제한 계정과 모든 시는 복구할 수 없습니다.</p>
          </div>
          <button
            @click="showDeleteAccountModal = true"
            class="text-sm text-rose-500 hover:text-rose-700 border border-rose-200 hover:border-rose-400 px-4 py-2 rounded-lg transition-colors"
          >계정 삭제</button>
        </div>
      </div>
    </div>

    <!-- 시 삭제 확인 모달 -->
    <div v-if="deletingPoemId" class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm">
      <div class="bg-white rounded-2xl p-6 w-80 shadow-xl">
        <h3 class="font-semibold text-ink mb-2">시를 삭제할까요?</h3>
        <p class="text-sm text-ink-light mb-6">삭제한 시는 복구할 수 없습니다.</p>
        <div class="flex gap-2 justify-end">
          <button @click="deletingPoemId = null" class="btn-secondary text-sm px-4 py-2">취소</button>
          <button @click="handleDelete" class="text-sm px-4 py-2 bg-rose-500 text-white rounded-lg hover:bg-rose-600 transition-colors">삭제</button>
        </div>
      </div>
    </div>

    <!-- 계정 삭제 확인 모달 -->
    <div v-if="showDeleteAccountModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm">
      <div class="bg-white rounded-2xl p-6 w-80 shadow-xl">
        <h3 class="font-semibold text-rose-600 mb-2">계정을 정말 삭제할까요?</h3>
        <p class="text-sm text-ink-light mb-4">
          계정과 작성한 모든 시, 댓글이 영구적으로 삭제됩니다. 이 작업은 되돌릴 수 없습니다.
        </p>
        <input
          v-model="deleteConfirmText"
          type="text"
          placeholder="'삭제'를 입력해주세요"
          class="input-field mb-4"
        />
        <div class="flex gap-2 justify-end">
          <button @click="showDeleteAccountModal = false; deleteConfirmText = ''" class="btn-secondary text-sm px-4 py-2">취소</button>
          <button
            @click="handleDeleteAccount"
            :disabled="deleteConfirmText !== '삭제'"
            class="text-sm px-4 py-2 bg-rose-500 text-white rounded-lg hover:bg-rose-600 transition-colors disabled:opacity-40"
          >삭제 확인</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { poemApi, userApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { useToast } from '../composables/useToast'

const router = useRouter()
const auth = useAuthStore()
const toast = useToast()

const profile = ref(null)
const poems = ref([])
const loading = ref(false)
const page = ref(1)
const totalPages = ref(1)
const deletingPoemId = ref(null)

// nickname change
const showNicknameForm = ref(false)
const nicknameLoading = ref(false)
const newNickname = ref('')

// account deletion
const showDeleteAccountModal = ref(false)
const deleteConfirmText = ref('')

const pageNumbers = computed(() => {
  const nums = []
  const start = Math.max(1, page.value - 2)
  const end = Math.min(totalPages.value, start + 4)
  for (let i = start; i <= end; i++) nums.push(i)
  return nums
})

const categories = [
  { label: '자유시', value: 'FREE' },
  { label: '서정시', value: 'LYRIC' },
  { label: '현대시', value: 'MODERN' },
  { label: '하이쿠', value: 'HAIKU' },
  { label: '서사시', value: 'EPIC' },
]
const categoryLabel = (val) => categories.find(c => c.value === val)?.label || val

async function fetchProfile() {
  const { data } = await userApi.getMyProfile()
  profile.value = data
}

async function fetchPoems() {
  loading.value = true
  try {
    const { data } = await poemApi.getMyPoems({ page: page.value - 1, size: 10 })
    poems.value = data.content
    totalPages.value = data.totalPages
  } finally {
    loading.value = false
  }
}

function changePage(p) {
  if (p < 1 || p > totalPages.value) return
  page.value = p
  fetchPoems()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function openDeleteModal(id) {
  deletingPoemId.value = id
}

async function handleDelete() {
  try {
    await poemApi.deletePoem(deletingPoemId.value)
    poems.value = poems.value.filter(p => p.id !== deletingPoemId.value)
    if (profile.value) profile.value.poemCount--
    toast.success('시가 삭제되었습니다.')
  } catch {
    toast.error('삭제 중 오류가 발생했습니다.')
  } finally {
    deletingPoemId.value = null
  }
}

async function handleNicknameUpdate() {
  if (newNickname.value.length < 2) return
  nicknameLoading.value = true
  try {
    await userApi.updateNickname(newNickname.value)
    if (profile.value) profile.value.nickname = newNickname.value
    await auth.fetchMe()
    toast.success('닉네임이 변경되었습니다.')
    showNicknameForm.value = false
    newNickname.value = ''
  } catch (e) {
    toast.error(e.response?.data?.message || '닉네임 변경에 실패했습니다.')
  } finally {
    nicknameLoading.value = false
  }
}

async function handleDeleteAccount() {
  try {
    await userApi.deleteAccount()
    await auth.logout()
    toast.success('계정이 삭제되었습니다.')
    router.push('/')
  } catch {
    toast.error('계정 삭제 중 오류가 발생했습니다.')
  } finally {
    showDeleteAccountModal.value = false
    deleteConfirmText.value = ''
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  fetchProfile()
  fetchPoems()
})
</script>
