<script setup lang="ts">
// Karte im Stil eines GitHub-Issues, gemeinsam genutzt von In-Arbeit- und 404-Seite
defineProps<{ title: string }>()
</script>

<template>
  <article class="issue">
    <header class="issue-head">
      <h1>{{ title }}</h1>
      <div class="meta"><slot name="meta" /></div>
    </header>
    <div class="issue-body">
      <slot />
    </div>
  </article>
</template>

<style scoped>
.issue {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 6px;
  max-width: 640px;
  margin: 24px auto;
}

.issue-head {
  padding: 16px;
  border-bottom: 1px solid var(--border);
}

h1 {
  font-family: var(--font-serif);
  font-size: 1.5rem;
  margin: 0 0 8px;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  color: var(--muted);
  font-size: 0.875rem;
}

.issue-body {
  padding: 16px;
}
</style>
