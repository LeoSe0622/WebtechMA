<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import ReceiptStrip from '@/components/ReceiptStrip.vue'
import ExpiryLabel from '@/components/ExpiryLabel.vue'
import { fetchBudgetSummary, type BudgetSummary } from '@/api/budget'
import { fetchPantry, type PantryItem } from '@/api/pantry'
import { loadPage } from '@/api/client'
import { useAuthStore } from '@/stores/auth'
import { formatEuro } from '@/config'

const auth = useAuthStore()
const summary = ref<BudgetSummary | null>(null)
const pantry = ref<PantryItem[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    const loaded = await loadPage(() => Promise.all([fetchBudgetSummary(), fetchPantry()]))
    if (loaded) {
      summary.value = loaded[0]
      pantry.value = loaded[1]
    }
  } catch {
    error.value = 'Das Dashboard konnte nicht geladen werden. Läuft das Backend?'
  } finally {
    loading.value = false
  }
})

// Ablauf-Warnungen: abgelaufen oder in höchstens 3 Tagen
const warnings = computed(() =>
  pantry.value.filter((item) => item.expiryStatus === 'EXPIRED' || item.expiryStatus === 'EXPIRING_SOON'),
)

const lastRate = computed(() => {
  const rate = summary.value?.lastCompleted?.savingsRate
  return rate === undefined ? null : `${Math.round(rate * 100)} %`
})

const lastMonthName = computed(() => {
  const key = summary.value?.lastCompleted?.yearMonth
  if (!key) return ''
  const [year, month] = key.split('-').map(Number)
  return new Date(year!, month! - 1, 1).toLocaleDateString('de-DE', { month: 'long' })
})
</script>

<template>
  <section>
    <h1 class="page-title">Hallo {{ auth.user?.displayName }}</h1>

    <p v-if="loading" class="muted">Wird geladen …</p>
    <p v-else-if="error" class="error" role="alert">{{ error }}</p>
    <template v-else-if="summary">
      <ReceiptStrip :remaining="summary.remaining" :amount="summary.amount" />

      <div class="cards">
        <article class="box card">
          <h2>Ausgegeben diesen Monat</h2>
          <p class="big amount">{{ formatEuro(summary.spent) }}</p>
          <p class="muted">{{ summary.spent > 0 ? 'Bisherige Einkäufe in diesem Monat' : 'Noch kein Einkauf erfasst' }}</p>
        </article>

        <article class="box card">
          <h2>Sparquote {{ lastMonthName }}</h2>
          <template v-if="summary.lastCompleted && lastRate">
            <p class="big amount">{{ lastRate }}</p>
            <p v-if="summary.lastCompleted.remaining >= 0" class="muted">
              {{ formatEuro(summary.lastCompleted.remaining) }} übrig von {{ formatEuro(summary.lastCompleted.amount) }}
            </p>
            <p v-else class="over">
              {{ formatEuro(-summary.lastCompleted.remaining) }} über dem Budget von
              {{ formatEuro(summary.lastCompleted.amount) }}
            </p>
          </template>
          <p v-else class="muted">Noch kein abgeschlossener Monat mit Budget.</p>
        </article>

        <article class="box card">
          <h2>Läuft bald ab</h2>
          <ul v-if="warnings.length > 0" class="warnings">
            <li v-for="item in warnings" :key="item.id">
              {{ item.productName }} <ExpiryLabel :status="item.expiryStatus" />
            </li>
          </ul>
          <p v-else class="muted">Nichts läuft in den nächsten 3 Tagen ab.</p>
          <RouterLink to="/vorrat">Zum Vorrat</RouterLink>
        </article>
      </div>

      <RouterLink class="button primary go" to="/liste">Zur Einkaufsliste</RouterLink>
    </template>
  </section>
</template>

<style scoped>
.cards {
  display: grid;
  gap: 16px;
  margin-bottom: 24px;
}

@media (min-width: 720px) {
  .cards {
    grid-template-columns: repeat(3, 1fr);
  }
}

.card {
  padding: 16px;
}

h2 {
  margin: 0 0 8px;
  font-size: 0.95rem;
  color: var(--muted);
  font-weight: 600;
}

.big {
  font-family: var(--font-serif);
  font-size: 1.75rem;
  font-weight: 600;
  margin: 0;
}

.warnings {
  list-style: none;
  margin: 0 0 8px;
  padding: 0;
}

.warnings li {
  padding: 4px 0;
}

.muted {
  color: var(--muted);
}

.over {
  color: var(--danger);
}

.error {
  color: var(--danger);
}

.go {
  width: 100%;
  max-width: 360px;
}
</style>
