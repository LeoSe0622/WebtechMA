<script setup lang="ts">
import { useRoute } from 'vue-router'
import IssueCard from '@/components/IssueCard.vue'

const route = useRoute()
</script>

<template>
  <IssueCard title="Diese Seite gibt es nicht">
    <template #meta>
      <code class="path">{{ route.fullPath }}</code>
    </template>
    <p>Vielleicht hat sich ein Tippfehler eingeschlichen. Von hier aus geht es weiter:</p>
    <nav class="links">
      <RouterLink to="/dashboard">Dashboard</RouterLink>
      <RouterLink to="/liste">Liste</RouterLink>
      <RouterLink to="/vorrat">Vorrat</RouterLink>
    </nav>
  </IssueCard>
</template>

<style scoped>
.path {
  font-family: ui-monospace, 'Cascadia Code', Consolas, monospace;
  background: var(--bg);
  padding: 2px 6px;
  border-radius: 6px;
}

.links {
  display: flex;
  gap: 16px;
}
</style>
