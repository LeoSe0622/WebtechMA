<script setup lang="ts">
import type { ExpiryStatus } from '@/api/pantry'

defineProps<{ status: ExpiryStatus }>()
</script>

<template>
  <span v-if="status === 'EXPIRED'" class="pill expired">abgelaufen</span>
  <span v-else-if="status === 'EXPIRING_SOON'" class="pill soon">läuft bald ab</span>
</template>

<style scoped>
.pill {
  display: inline-block;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
  line-height: 20px;
  white-space: nowrap;
}

.expired {
  background: #ffebe9;
  color: var(--danger);
}

.soon {
  background: #fff8c5;
  color: var(--warn);
}
</style>
