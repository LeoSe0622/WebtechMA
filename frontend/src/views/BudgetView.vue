<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchBudget, fetchBudgetSummary, saveBudget, type Budget } from '@/api/budget'
import { ApiError } from '@/api/client'
import { useAuthStore } from '@/stores/auth'
import { formatEuro, parseEuro } from '@/config'

const auth = useAuthStore()

// "2026-12" → "2027-01"; reine Textrechnung, ohne Browser-Uhr und Zeitzone
function nextMonth(key: string): string {
  const [year, month] = key.split('-').map(Number)
  return month === 12 ? `${year! + 1}-01` : `${year}-${String(month! + 1).padStart(2, '0')}`
}

function monthLabel(key: string): string {
  const [year, month] = key.split('-').map(Number)
  return new Date(year!, month! - 1, 1).toLocaleDateString('de-DE', { month: 'long', year: 'numeric' })
}

interface MonthState {
  key: string
  budget: Budget | null
  input: string
  message: string | null
  error: string | null
  busy: boolean
}

const months = ref<MonthState[]>([])
const loading = ref(true)
const loadError = ref<string | null>(null)
const maxBudget = (auth.user?.householdSize ?? 1) * 500

onMounted(async () => {
  // Den laufenden Monat bestimmt das Backend (deutsche Zeit), nicht die Uhr des Browsers
  let current: string
  try {
    current = (await fetchBudgetSummary()).yearMonth
  } catch {
    loadError.value = 'Die Budgets konnten nicht geladen werden. Läuft das Backend?'
    loading.value = false
    return
  }
  // Budgets lassen sich für den laufenden und den nächsten Monat festlegen
  months.value = [current, nextMonth(current)].map((key) => ({
    key, budget: null, input: '', message: null, error: null, busy: false,
  }))
  await Promise.all(months.value.map(async (month) => {
    try {
      month.budget = await fetchBudget(month.key)
      month.input = String(month.budget.amount).replace('.', ',')
    } catch (e) {
      // 404 heißt hier nur: noch kein Budget, kein Fehler
      if (!(e instanceof ApiError && e.status === 404)) {
        month.error = 'Das Budget konnte nicht geladen werden.'
      }
    }
  }))
  loading.value = false
})

async function save(month: MonthState) {
  month.message = null
  month.error = null
  const amount = parseEuro(month.input)
  if (!(amount >= 1)) {
    month.error = 'Gib einen Betrag ab 1 € ein.'
    return
  }
  month.busy = true
  try {
    month.budget = await saveBudget(month.key, Math.round(amount * 100) / 100)
    month.message = 'Budget gespeichert.'
  } catch (e) {
    month.error = e instanceof ApiError ? e.message : 'Speichern hat nicht geklappt.'
  } finally {
    month.busy = false
  }
}
</script>

<template>
  <section>
    <h1 class="page-title">Monatsbudget</h1>
    <p class="intro">
      Wie viel willst du im Monat für Lebensmittel ausgeben? Mit dem ersten erfassten Einkauf ist das Budget
      für den Monat festgelegt. Höchstens {{ formatEuro(maxBudget) }} bei {{ auth.user?.householdSize ?? 1 }}
      Person(en) im Haushalt.
    </p>

    <p v-if="loading" class="muted">Wird geladen …</p>
    <p v-else-if="loadError" class="error" role="alert">{{ loadError }}</p>
    <div v-else class="months">
      <form v-for="month in months" :key="month.key" class="box month" @submit.prevent="save(month)">
        <h2>{{ monthLabel(month.key) }}</h2>
        <p v-if="month.budget" class="current">
          <span class="amount">{{ formatEuro(month.budget.amount) }}</span>
          <span v-if="month.budget.locked" class="locked">gesperrt, weil schon eingekauft wurde</span>
        </p>
        <p v-else class="muted">Noch kein Budget. Leg eins an.</p>

        <template v-if="!month.budget?.locked">
          <label>
            Betrag in €
            <input v-model="month.input" inputmode="decimal" placeholder="z. B. 260" />
          </label>
          <button class="button primary" type="submit" :disabled="month.busy">{{ month.budget ? 'Budget ändern' : 'Budget anlegen' }}</button>
        </template>
        <p v-if="month.message" class="ok" role="status">{{ month.message }}</p>
        <p v-if="month.error" class="error" role="alert">{{ month.error }}</p>
      </form>
    </div>
  </section>
</template>

<style scoped>
.intro,
.muted {
  color: var(--muted);
}

.months {
  display: grid;
  gap: 16px;
}

@media (min-width: 720px) {
  .months {
    grid-template-columns: 1fr 1fr;
  }
}

.month {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px;
}

h2 {
  margin: 0;
  font-size: 1.125rem;
}

.current {
  display: flex;
  flex-direction: column;
  margin: 0;
}

.current .amount {
  font-family: var(--font-serif);
  font-size: 2rem;
  font-weight: 600;
}

.locked {
  color: var(--muted);
  font-size: 0.875rem;
}

label {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 0.875rem;
}

.ok {
  color: var(--ok);
}

.error {
  color: var(--danger);
}
</style>
