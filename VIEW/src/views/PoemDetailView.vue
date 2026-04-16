<template>
  <div class="max-w-2xl mx-auto px-4 sm:px-6 py-10">

    <!-- 뒤로가기 -->
    <button @click="router.back()" class="flex items-center gap-1.5 text-sm text-ink-light hover:text-ink mb-8 transition-colors">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
      </svg>
      목록으로
    </button>

    <!-- 로딩 스켈레톤 -->
    <div v-if="loading" class="animate-pulse space-y-4">
      <div class="h-8 bg-zinc-100 rounded w-3/4"></div>
      <div class="h-4 bg-zinc-100 rounded w-1/3"></div>
      <div class="mt-8 space-y-3">
        <div class="h-4 bg-zinc-100 rounded w-full"></div>
        <div class="h-4 bg-zinc-100 rounded w-5/6"></div>
        <div class="h-4 bg-zinc-100 rounded w-4/5"></div>
      </div>
    </div>

    <template v-else-if="poem">
      <!-- 시 헤더 -->
      <div class="mb-8">
        <div class="flex items-start justify-between gap-4 mb-4">
          <h1 class="font-serif text-2xl sm:text-3xl font-semibold text-ink leading-snug">
            {{ poem.title }}
          </h1>
          <div class="flex items-center gap-2 shrink-0 mt-1">
            <span v-if="poem.category" class="category-chip">{{ categoryLabel(poem.category) }}</span>
            <!-- 작성자 액션 버튼 -->
            <template v-if="poem.isAuthor">
              <router-link
                :to="`/write/${poem.id}`"
                class="text-xs text-ink-lighter hover:text-indigo transition-colors px-2 py-1 rounded hover:bg-indigo-50"
              >
                수정
              </router-link>
              <button
                @click="confirmDelete"
                class="text-xs text-ink-lighter hover:text-rose-500 transition-colors px-2 py-1 rounded hover:bg-rose-50"
              >
                삭제
              </button>
            </template>
          </div>
        </div>
        <div class="flex items-center gap-3 text-sm text-ink-lighter flex-wrap">
          <span class="font-medium text-ink-light">{{ poem.authorNickname }}</span>
          <span>·</span>
          <span>{{ formatDate(poem.createdAt) }}</span>
          <span v-if="poem.updatedAt !== poem.createdAt" class="text-xs">(수정됨)</span>
          <span>·</span>
          <span class="flex items-center gap-1">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
            </svg>
            {{ poem.views }}
          </span>
        </div>
      </div>

      <!-- 구분선 -->
      <div class="border-t border-zinc-100 mb-10"></div>

      <!-- 시 본문 -->
      <div class="poem-text mb-12 min-h-32">{{ poem.content }}</div>

      <!-- 좋아요 -->
      <div class="flex flex-col items-center mb-12">
        <button
          @click="handleLike"
          :class="[
            'flex items-center gap-2 px-6 py-2.5 rounded-full border text-sm font-medium transition-all duration-200',
            liked
              ? 'bg-rose-50 border-rose-200 text-rose-500'
              : 'bg-white border-zinc-200 text-ink-light hover:border-rose-200 hover:text-rose-400'
          ]"
          :disabled="!auth.isLoggedIn"
        >
          <svg class="w-4 h-4" :fill="liked ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
          </svg>
          <span>{{ likeCount }}</span>
        </button>
        <p v-if="!auth.isLoggedIn" class="text-xs text-ink-lighter mt-2">
          <router-link to="/login" class="text-indigo hover:underline">로그인</router-link> 후 좋아요를 누를 수 있습니다
        </p>
      </div>

      <!-- 구분선 -->
      <div class="border-t border-zinc-100 mb-8"></div>

      <!-- 댓글 섹션 -->
      <section>
        <h2 class="text-sm font-semibold text-ink mb-5">댓글 {{ poem.comments.length }}</h2>

        <!-- 댓글 작성 -->
        <div v-if="auth.isLoggedIn" class="card p-4 mb-6">
          <textarea
            v-model="newComment"
            placeholder="이 시에 대한 감상을 남겨보세요..."
            class="w-full text-sm text-ink placeholder:text-zinc-400 resize-none focus:outline-none bg-transparent"
            rows="2"
            @input="autoResize"
          ></textarea>
          <div class="flex justify-end mt-3 pt-3 border-t border-zinc-100">
            <button
              class="btn-primary text-xs px-4 py-2"
              :disabled="!newComment.trim() || submittingComment"
              @click="submitComment"
            >
              {{ submittingComment ? '등록 중...' : '등록' }}
            </button>
          </div>
        </div>
        <div v-else class="bg-zinc-50 rounded-xl p-4 text-sm text-ink-light text-center mb-6">
          <router-link to="/login" class="text-indigo font-medium hover:underline">로그인</router-link>하면 댓글을 남길 수 있습니다
        </div>

        <!-- 댓글 목록 -->
        <div class="space-y-3">
          <div
            v-for="comment in poem.comments"
            :key="comment.id"
            class="group flex gap-3"
          >
            <div class="w-7 h-7 rounded-full bg-indigo-50 flex items-center justify-center text-indigo text-xs font-semibold shrink-0 mt-0.5">
              {{ comment.authorNickname?.[0] }}
            </div>
            <div class="flex-1">
              <div class="flex items-start justify-between gap-2">
                <div>
                  <span class="text-sm font-medium text-ink">{{ comment.authorNickname }}</span>
                  <span class="text-xs text-ink-lighter ml-2">{{ formatDate(comment.createdAt) }}</span>
                </div>
                <!-- 댓글 작성자 액션 -->
                <div v-if="auth.nickname === comment.authorNickname" class="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-all shrink-0">
                  <button
                    v-if="editingCommentId !== comment.id"
                    @click="startEditComment(comment)"
                    class="text-xs text-ink-lighter hover:text-indigo transition-colors"
                  >수정</button>
                  <button
                    @click="handleDeleteComment(comment.id)"
                    class="text-xs text-ink-lighter hover:text-rose-500 transition-colors"
                  >삭제</button>
                </div>
              </div>

              <!-- 댓글 수정 인라인 -->
              <template v-if="editingCommentId === comment.id">
                <textarea
                  v-model="editingContent"
                  class="w-full mt-1 text-sm text-ink border border-zinc-200 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo/30 resize-none"
                  rows="2"
                ></textarea>
                <div class="flex gap-2 mt-1.5">
                  <button @click="submitEditComment(comment.id)" class="text-xs text-indigo font-medium hover:underline">저장</button>
                  <button @click="cancelEdit" class="text-xs text-ink-lighter hover:text-ink">취소</button>
                </div>
              </template>
              <p v-else class="text-sm text-ink-light mt-1 whitespace-pre-wrap">{{ comment.content }}</p>
            </div>
          </div>

          <div v-if="poem.comments.length === 0" class="text-center py-8 text-xs text-ink-lighter">
            첫 댓글을 남겨보세요
          </div>
        </div>
      </section>
    </template>

    <!-- 삭제 확인 모달 -->
    <div v-if="showDeleteModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm">
      <div class="bg-white rounded-2xl p-6 w-80 shadow-xl">
        <h3 class="font-semibold text-ink mb-2">시를 삭제할까요?</h3>
        <p class="text-sm text-ink-light mb-6">삭제한 시는 복구할 수 없습니다.</p>
        <div class="flex gap-2 justify-end">
          <button @click="showDeleteModal = false" class="btn-secondary text-sm px-4 py-2">취소</button>
          <button @click="handleDelete" class="text-sm px-4 py-2 bg-rose-500 text-white rounded-lg hover:bg-rose-600 transition-colors">삭제</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { poemApi, commentApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { useToast } from '../composables/useToast'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const toast = useToast()

const poem = ref(null)
const loading = ref(false)
const liked = ref(false)
const likeCount = ref(0)
const newComment = ref('')
const submittingComment = ref(false)
const showDeleteModal = ref(false)
const editingCommentId = ref(null)
const editingContent = ref('')

const categories = [
  { label: '자유시', value: 'FREE' },
  { label: '서정시', value: 'LYRIC' },
  { label: '현대시', value: 'MODERN' },
  { label: '하이쿠', value: 'HAIKU' },
  { label: '서사시', value: 'EPIC' },
]
const categoryLabel = (val) => categories.find(c => c.value === val)?.label || val

async function fetchPoem() {
  loading.value = true
  try {
    const { data } = await poemApi.getPoem(route.params.id)
    poem.value = data
    likeCount.value = data.likeCount
    liked.value = data.isLiked
  } finally {
    loading.value = false
  }
}

async function handleLike() {
  if (!auth.isLoggedIn) return
  try {
    const { data } = await poemApi.toggleLike(route.params.id)
    likeCount.value = data
    liked.value = !liked.value
  } catch (e) {
    toast.error('좋아요 처리 중 오류가 발생했습니다.')
  }
}

function confirmDelete() {
  showDeleteModal.value = true
}

async function handleDelete() {
  try {
    await poemApi.deletePoem(poem.value.id)
    toast.success('시가 삭제되었습니다.')
    router.push('/')
  } catch (e) {
    toast.error('삭제 중 오류가 발생했습니다.')
    showDeleteModal.value = false
  }
}

async function submitComment() {
  if (!newComment.value.trim()) return
  submittingComment.value = true
  try {
    const { data } = await commentApi.addComment(route.params.id, { content: newComment.value })
    poem.value.comments.push(data)
    newComment.value = ''
    toast.success('댓글이 등록되었습니다.')
  } catch (e) {
    toast.error('댓글 등록 중 오류가 발생했습니다.')
  } finally {
    submittingComment.value = false
  }
}

function startEditComment(comment) {
  editingCommentId.value = comment.id
  editingContent.value = comment.content
}

function cancelEdit() {
  editingCommentId.value = null
  editingContent.value = ''
}

async function submitEditComment(commentId) {
  if (!editingContent.value.trim()) return
  try {
    const { data } = await commentApi.updateComment(route.params.id, commentId, { content: editingContent.value })
    const idx = poem.value.comments.findIndex(c => c.id === commentId)
    if (idx !== -1) poem.value.comments[idx] = data
    cancelEdit()
    toast.success('댓글이 수정되었습니다.')
  } catch (e) {
    toast.error('댓글 수정 중 오류가 발생했습니다.')
  }
}

async function handleDeleteComment(commentId) {
  try {
    await commentApi.deleteComment(route.params.id, commentId)
    poem.value.comments = poem.value.comments.filter(c => c.id !== commentId)
    toast.success('댓글이 삭제되었습니다.')
  } catch (e) {
    toast.error('댓글 삭제 중 오류가 발생했습니다.')
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

function autoResize(e) {
  e.target.style.height = 'auto'
  e.target.style.height = e.target.scrollHeight + 'px'
}

onMounted(fetchPoem)
</script>
