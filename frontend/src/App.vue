<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { APP_NAME } from '@/config'
</script>

<template>
  <header class="topbar">
    <span class="brand">{{ APP_NAME }}</span>
    <nav>
      <RouterLink to="/liste">Liste</RouterLink>
    </nav>
  </header>

  <main class="content">
    <!-- Hier zeigt der Router die Komponente zur aktuellen URL an -->
    <RouterView />
  </main>
</template>

<style scoped>
.topbar {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 12px 16px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}

.brand {
  font-family: var(--font-serif);
  font-size: 1.25rem;
  font-weight: 600;
}

nav a {
  color: var(--text);
  text-decoration: none;
}

nav a.router-link-active {
  color: var(--accent);
  font-weight: 600;
}

.content {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px 16px;
}
</style>
