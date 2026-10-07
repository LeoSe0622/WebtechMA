<script setup lang="ts">
import { RouterView } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import WorkInProgressView from '@/views/WorkInProgressView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import { useAuthStore } from '@/stores/auth'
import { useUiStore } from '@/stores/ui'

const auth = useAuthStore()
const ui = useUiStore()
</script>

<template>
  <AppHeader v-if="auth.isAuthenticated" />

  <main class="content">
    <!-- Meldet das Backend 404 oder 501 beim Laden einer Seite, ersetzt diese Ansicht den Inhalt -->
    <WorkInProgressView
      v-if="ui.override?.kind === 'wip'"
      :feature="ui.override.feature"
      :milestone="ui.override.milestone"
    />
    <NotFoundView v-else-if="ui.override?.kind === 'not-found'" />
    <RouterView v-else />
  </main>
</template>

<style scoped>
.content {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 16px calc(96px + var(--tabbar-height));
}
</style>
