<script setup lang="ts">
import StatusLabel from '@/components/StatusLabel.vue'

// Kennzeichnet Demo-Seiten ehrlich: echte Beispieldaten, aber noch ohne eigene Eingaben
defineProps<{ milestone: string }>()
</script>

<template>
  <p class="banner box">
    <StatusLabel status="demo" />
    <span>Diese Seite zeigt Beispieldaten. Eigene Eingaben folgen mit Milestone {{ milestone }}</span>
  </p>
</template>

<style scoped>
.banner {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  margin: 0 0 16px;
  color: var(--muted);
}
</style>
