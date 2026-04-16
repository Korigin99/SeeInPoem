<template>
  <div class="max-w-2xl mx-auto px-4 sm:px-6 py-10">

    <div class="flex items-center justify-between mb-8">
      <h1 class="font-serif text-2xl font-semibold text-ink">
        {{ isEditMode ? '시 수정' : '새 시 쓰기' }}
      </h1>
      <div class="flex items-center gap-1 bg-zinc-100 rounded-lg p-1">
        <button
          @click="mode = 'write'"
          :class="['px-3 py-1.5 text-xs rounded-md transition-colors', mode === 'write' ? 'bg-white shadow-sm font-medium' : 'text-ink-light']"
        >
          작성
        </button>
        <button
          @click="mode = 'preview'"
          :class="['px-3 py-1.5 text-xs rounded-md transition-colors', mode === 'preview' ? 'bg-white shadow-sm font-medium' : 'text-ink-light']"
        >
          미리보기
        </button>
      </div>
    </div>

    <!-- 작성 모드 -->
    <div v-show="mode === 'write'" class="space-y-4">
      <div>
        <input
          v-model="form.title"
          type="text"
          placeholder="제목"
          class="input-field font-serif text-lg"
          :class="errors.title ? 'border-rose-300 ring-1 ring-rose-200' : ''"
        />
        <p v-if="errors.title" class="text-rose-500 text-xs mt-1">{{ errors.title }}</p>
      </div>

      <select v-model="form.category" class="input-field">
        <option :value="null">카테고리 선택 (선택사항)</option>
        <option value="FREE">자유시</option>
        <option value="LYRIC">서정시</option>
        <option value="MODERN">현대시</option>
        <option value="HAIKU">하이쿠</option>
        <option value="EPIC">서사시</option>
      </select>

      <div>
        <textarea
          v-model="form.content"
          placeholder="당신의 시를 여기에 쓰세요...&#10;&#10;Enter로 행을 나누고&#10;빈 줄로 연을 구분해요."
          class="input-field font-serif text-base leading-loose resize-none"
          :class="errors.content ? 'border-rose-300 ring-1 ring-rose-200' : ''"
          rows="16"
        ></textarea>
        <p v-if="errors.content" class="text-rose-500 text-xs mt-1">{{ errors.content }}</p>
      </div>

      <label class="flex items-center gap-2.5 cursor-pointer">
        <div
          @click="form.anonymous = !form.anonymous"
          :class="[
            'w-10 h-5 rounded-full transition-colors duration-200 relative',
            form.anonymous ? 'bg-indigo' : 'bg-zinc-200'
          ]"
        >
          <div :class="[
            'w-4 h-4 bg-white rounded-full shadow absolute top-0.5 transition-transform duration-200',
            form.anonymous ? 'translate-x-5' : 'translate-x-0.5'
          ]"></div>
        </div>
        <span class="text-sm text-ink-light">익명으로 게시</span>
      </label>
    </div>

    <!-- 미리보기 모드 -->
    <div v-show="mode === 'preview'" class="card p-8">
      <h2 class="font-serif text-2xl font-semibold text-ink mb-2">{{ form.title || '(제목 없음)' }}</h2>
      <div class="text-sm text-ink-lighter mb-8">
        {{ form.anonymous ? '익명' : auth.nickname }}
        <span v-if="form.category" class="ml-2 category-chip">{{ categoryLabel(form.category) }}</span>
      </div>
      <div class="border-t border-zinc-100 mb-8"></div>
      <p class="poem-text">{{ form.content || '(내용 없음)' }}</p>
    </div>

    <!-- 액션 버튼 -->
    <div class="flex items-center justify-end gap-3 mt-6">
      <button @click="router.back()" class="btn-secondary">취소</button>
      <button @click="submit" class="btn-primary" :disabled="submitting">
        {{ submitting ? (isEditMode ? '저장 중...' : '게시 중...') : (isEditMode ? '저장하기' : '게시하기') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { poemApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { useToast } from '../composables/useToast'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const toast = useToast()

const isEditMode = computed(() => !!route.params.id)
const mode = ref('write')
const form = reactive({ title: '', content: '', category: null, anonymous: false })
const errors = reactive({ title: '', content: '' })
const submitting = ref(false)

const categories = [
  { label: '자유시', value: 'FREE' },
  { label: '서정시', value: 'LYRIC' },
  { label: '현대시', value: 'MODERN' },
  { label: '하이쿠', value: 'HAIKU' },
  { label: '서사시', value: 'EPIC' },
]
const categoryLabel = (val) => categories.find(c => c.value === val)?.label || val

async function loadPoem() {
  if (!isEditMode.value) return
  try {
    const { data } = await poemApi.getPoem(route.params.id)
    if (!data.isAuthor) {
      toast.error('수정 권한이 없습니다.')
      router.push(`/poem/${route.params.id}`)
      return
    }
    form.title = data.title
    form.content = data.content
    form.category = data.category
    form.anonymous = data.authorNickname === '익명'
  } catch (e) {
    toast.error('시를 불러오는데 실패했습니다.')
    router.push('/')
  }
}

function validate() {
  errors.title = form.title.trim() ? '' : '제목을 입력해주세요.'
  errors.content = form.content.trim() ? '' : '내용을 입력해주세요.'
  return !errors.title && !errors.content
}

async function submit() {
  if (!validate()) return
  submitting.value = true
  try {
    if (isEditMode.value) {
      const { data } = await poemApi.updatePoem(route.params.id, form)
      toast.success('시가 수정되었습니다.')
      router.push(`/poem/${data.id}`)
    } else {
      const { data } = await poemApi.createPoem(form)
      toast.success('시가 게시되었습니다.')
      router.push(`/poem/${data.id}`)
    }
  } catch (e) {
    toast.error(e.response?.data?.message || '오류가 발생했습니다.')
  } finally {
    submitting.value = false
  }
}

onMounted(loadPoem)
</script>
