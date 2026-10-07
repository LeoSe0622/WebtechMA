<script setup lang="ts">
// Kleine Pille "Demo" oder "In Arbeit" (AUFTRAG.md, Abschnitt 8)
defineProps<{ status: 'demo' | 'wip' }>()
</script>

<template>
  <span class="label" :class="status">{{ status === 'demo' ? 'Demo' : 'In Arbeit' }}</span>
</template>

<style scoped>
.label {
  display: inline-block;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
  line-height: 20px;
  white-space: nowrap;
}

.demo {
  background: #e7f0fb;
  color: #0b4a8b;
}

.wip {
  background: var(--accent-weak);
  color: var(--accent-strong);
}
</style>
