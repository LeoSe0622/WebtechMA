<script setup lang="ts">
import { computed } from 'vue'
import { formatEuro } from '@/config'

// Das Erkennungsmerkmal der App: Restbudget als schmaler Kassenbon-Streifen (AUFTRAG.md, Abschnitt 15)
const props = defineProps<{ remaining: number | null; amount: number | null }>()

const negative = computed(() => props.remaining !== null && props.remaining < 0)
</script>

<template>
  <section class="receipt" :class="{ negative }" aria-label="Restbudget">
    <template v-if="remaining !== null && amount !== null">
      <span class="label">Restbudget diesen Monat</span>
      <span class="value amount">{{ formatEuro(remaining) }}</span>
      <span class="of">von {{ formatEuro(amount) }}</span>
    </template>
    <template v-else>
      <span class="label">Noch kein Budget für diesen Monat</span>
      <RouterLink class="button primary" to="/budget">Leg dein erstes Budget an</RouterLink>
    </template>
  </section>
</template>

<style scoped>
.receipt {
  --tooth: 8px;
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 4px 12px;
  padding: 16px 16px calc(16px + var(--tooth));
  margin-bottom: 24px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-bottom: none;
  /* Gezackte Unterkante wie ein abgerissener Kassenbon */
  -webkit-mask: conic-gradient(from -45deg at bottom, #0000, #000 1deg 89deg, #0000 90deg) 50% / calc(2 * var(--tooth)) 100%;
  mask: conic-gradient(from -45deg at bottom, #0000, #000 1deg 89deg, #0000 90deg) 50% / calc(2 * var(--tooth)) 100%;
}

.label {
  width: 100%;
  color: var(--muted);
  font-size: 0.875rem;
}

.value {
  font-family: var(--font-serif);
  font-size: 2.25rem;
  font-weight: 600;
  line-height: 1.1;
}

.negative .value {
  color: var(--danger);
}

.of {
  color: var(--muted);
}
</style>
