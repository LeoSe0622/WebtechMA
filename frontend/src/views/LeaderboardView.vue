<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import DemoBanner from '@/components/DemoBanner.vue'
import { fetchLeaderboard, type Leaderboard } from '@/api/demoAreas'
import { loadPage } from '@/api/client'

const board = ref<Leaderboard | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    board.value = (await loadPage(fetchLeaderboard)) ?? null
  } catch {
    error.value = 'Die Rangliste konnte nicht geladen werden.'
  } finally {
    loading.value = false
  }
})

const monthName = computed(() => {
  if (!board.value) return ''
  const [year, month] = board.value.month.split('-').map(Number)
  return new Date(year!, month! - 1, 1).toLocaleDateString('de-DE', { month: 'long', year: 'numeric' })
})

function percent(rate: number): string {
  return `${Math.round(rate * 100)} %`
}
</script>

<template>
  <section>
    <h1 class="page-title">Rangliste</h1>
    <DemoBanner milestone="M4 · 13. Dez." />

    <p v-if="loading" class="muted">Wird geladen …</p>
    <p v-else-if="error" class="error" role="alert">{{ error }}</p>
    <template v-else-if="board">
      <p class="muted">Sparquote im {{ monthName }}. Bei Gleichstand zählt die Serie: Monate in Folge im Budget.</p>

      <p v-if="board.yourPosition !== null" class="mine box">
        Dein Platz wäre: <strong class="amount">{{ board.yourPosition }}</strong>
        <span class="muted">(Sparquote {{ percent(board.yourSavingsRate ?? 0) }}, Serie {{ board.yourStreak }})</span>
      </p>

      <div class="box table-wrap">
        <table>
          <thead>
            <tr>
              <th scope="col">Platz</th>
              <th scope="col">Pseudonym</th>
              <th scope="col" class="num wide">Haushalt</th>
              <th scope="col" class="num">Sparquote</th>
              <th scope="col" class="num">Serie</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="entry in board.entries" :key="entry.rank">
              <td class="amount">{{ entry.rank }}</td>
              <td>
                {{ entry.displayName }}
                <span class="narrow muted">Haushalt {{ entry.householdSize }}</span>
              </td>
              <td class="num amount wide">{{ entry.householdSize }}</td>
              <td class="num amount">{{ percent(entry.savingsRate) }}</td>
              <td class="num amount">{{ entry.streak }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <p class="demo-action">
        <button class="button" type="button" disabled aria-describedby="coming-soon" title="Kommt mit Milestone M4">In der Rangliste erscheinen</button>
        <span id="coming-soon" class="coming-soon">Kommt mit Milestone M4</span>
      </p>
    </template>
  </section>
</template>

<style scoped>
.mine {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 8px;
  padding: 12px;
}

.mine strong {
  font-family: var(--font-serif);
  font-size: 1.5rem;
}

.table-wrap {
  overflow-x: auto;
  margin-bottom: 16px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 10px 12px;
  text-align: left;
  border-bottom: 1px solid var(--border);
}

tbody tr:last-child td {
  border-bottom: none;
}

th {
  font-size: 0.875rem;
  color: var(--muted);
  font-weight: 600;
}

.num {
  text-align: right;
}

.narrow {
  display: none;
}

/* Schmale Bildschirme: Spalte Haushalt als Zusatz unter dem Namen, damit die Serie sichtbar bleibt */
@media (max-width: 519px) {
  .wide {
    display: none;
  }

  .narrow {
    display: block;
    font-size: 0.8125rem;
  }

  th,
  td {
    padding: 10px 8px;
  }
}

.muted {
  color: var(--muted);
}

.demo-action {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.error {
  color: var(--danger);
}
</style>
