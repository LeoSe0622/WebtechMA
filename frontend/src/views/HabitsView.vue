<script setup lang="ts">
import { onMounted, ref } from 'vue'
import DemoBanner from '@/components/DemoBanner.vue'
import { fetchHabits, type Habit } from '@/api/demoAreas'
import { loadPage } from '@/api/client'

const habits = ref<Habit[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    habits.value = (await loadPage(fetchHabits)) ?? []
  } catch {
    error.value = 'Die Gewohnheiten konnten nicht geladen werden.'
  } finally {
    loading.value = false
  }
})

function interval(days: number): string {
  if (days === 7) return 'jede Woche'
  if (days === 14) return 'alle 2 Wochen'
  if (days === 30) return 'jeden Monat'
  return `alle ${days} Tage`
}

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString('de-DE', { weekday: 'short', day: 'numeric', month: 'short' })
}
</script>

<template>
  <section>
    <h1 class="page-title">Gewohnheiten</h1>
    <DemoBanner milestone="M4 · 13. Dez." />

    <p v-if="loading" class="muted">Wird geladen …</p>
    <p v-else-if="error" class="error" role="alert">{{ error }}</p>
    <template v-else>
      <ul v-if="habits.length > 0" class="box items">
        <li v-for="habit in habits" :key="habit.id" class="row">
          <span class="name">{{ habit.quantity }}× {{ habit.productName }}</span>
          <span class="muted">{{ interval(habit.intervalDays) }}, nächstes Mal {{ formatDate(habit.nextDue) }}</span>
        </li>
      </ul>
      <p v-else class="muted">Noch keine Gewohnheiten.</p>

      <!-- Schreibende Buttons sind auf Demo-Seiten deaktiviert und erklären per Tooltip, warum -->
      <button class="button" type="button" disabled title="Kommt mit Milestone M4">Gewohnheit anlegen</button>
    </template>
  </section>
</template>

<style scoped>
.items {
  list-style: none;
  margin: 0 0 16px;
  padding: 0;
}

.row {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 4px 12px;
  padding: 12px;
  border-bottom: 1px solid var(--border);
}

.row:last-child {
  border-bottom: none;
}

.name {
  font-weight: 600;
}

.muted {
  color: var(--muted);
}

.error {
  color: var(--danger);
}
</style>
