<script setup lang="ts">
import { onMounted, ref } from 'vue'
import DemoBanner from '@/components/DemoBanner.vue'
import { fetchPortfolios, type Portfolio } from '@/api/demoAreas'
import { loadPage } from '@/api/client'

const portfolios = ref<Portfolio[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    portfolios.value = (await loadPage(fetchPortfolios)) ?? []
  } catch {
    error.value = 'Die Musterportfolios konnten nicht geladen werden.'
  } finally {
    loading.value = false
  }
})

function percent(share: number): string {
  return `${Math.round(share * 100)} %`
}
</script>

<template>
  <section>
    <h1 class="page-title">Sparplan</h1>
    <DemoBanner milestone="M4 · 13. Dez." />

    <!-- Pflichthinweis laut AUFTRAG.md, Abschnitt 7 (wörtlich) -->
    <p class="disclaimer box" role="note">
      Keine Anlageberatung. Musterportfolios dienen zum Lernen. Kursdaten von US-gelisteten ETFs,
      Währungseffekte vereinfacht.
    </p>

    <p v-if="loading" class="muted">Wird geladen …</p>
    <p v-else-if="error" class="error" role="alert">{{ error }}</p>
    <template v-else>
      <p class="muted">
        So würde deine Ersparnis aufgeteilt. Welches Musterportfolio zu dir passt, ermittelt ab M4 ein kurzes
        Risikoprofil mit drei Fragen.
      </p>
      <div class="cards">
        <article v-for="portfolio in portfolios" :key="portfolio.id" class="box card">
          <h2>{{ portfolio.label }}</h2>
          <ul class="weights">
            <li v-for="weight in portfolio.weights" :key="weight.symbol">
              <!-- CSS braucht "30%" ohne Leerzeichen; percent() ist für die Anzeige gedacht ("30 %") -->
              <span class="bar" :style="{ width: `${weight.share * 100}%` }" aria-hidden="true"></span>
              <span class="amount">{{ percent(weight.share) }}</span> {{ weight.name }} ({{ weight.symbol }})
            </li>
          </ul>
        </article>
      </div>

      <div class="actions">
        <button class="button" type="button" disabled title="Kommt mit Milestone M4">Risikoprofil ermitteln</button>
        <button class="button" type="button" disabled title="Kommt mit Milestone M4">Sparplan berechnen</button>
      </div>
    </template>
  </section>
</template>

<style scoped>
.disclaimer {
  padding: 12px;
  margin: 0 0 16px;
  border-color: var(--warn);
  background: #fffbea;
}

.cards {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;
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
  font-size: 1.125rem;
  margin: 0 0 12px;
}

.weights {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.bar {
  display: block;
  height: 8px;
  margin-bottom: 4px;
  border-radius: 4px;
  background: var(--accent);
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.muted {
  color: var(--muted);
}

.error {
  color: var(--danger);
}
</style>
