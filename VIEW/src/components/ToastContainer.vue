<template>
  <div class="fixed top-4 right-4 z-50 flex flex-col gap-2 pointer-events-none">
    <TransitionGroup name="toast">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="pointer-events-auto flex items-center gap-3 px-4 py-3 rounded-xl shadow-lg text-sm font-medium min-w-[220px] max-w-xs"
        :class="{
          'bg-white border border-emerald-200 text-emerald-700': toast.type === 'success',
          'bg-white border border-rose-200 text-rose-600': toast.type === 'error',
          'bg-white border border-zinc-200 text-ink-light': toast.type === 'info',
        }"
      >
        <span v-if="toast.type === 'success'" class="text-emerald-500">✓</span>
        <span v-else-if="toast.type === 'error'" class="text-rose-400">✕</span>
        <span v-else class="text-zinc-400">i</span>
        {{ toast.message }}
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { useToast } from '../composables/useToast'
const { toasts } = useToast()
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.25s ease;
}
.toast-enter-from {
  opacity: 0;
  transform: translateX(16px);
}
.toast-leave-to {
  opacity: 0;
  transform: translateX(16px);
}
</style>
