<script setup lang="ts">
import { onMounted, ref } from 'vue'
import ExpiryLabel from '@/components/ExpiryLabel.vue'
import { consumePantryItem, fetchPantry, updatePantryItem, type PantryItem } from '@/api/pantry'
import { ApiError, loadPage } from '@/api/client'

const items = ref<PantryItem[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const notice = ref<string | null>(null)

onMounted(async () => {
  try {
    items.value = (await loadPage(fetchPantry)) ?? []
  } catch {
    error.value = 'Der Vorrat konnte nicht geladen werden. Läuft das Backend?'
  } finally {
    loading.value = false
  }
})

function replace(updated: PantryItem) {
  items.value = items.value.map((item) => (item.id === updated.id ? updated : item))
}

async function changeQuantity(item: PantryItem, delta: number) {
  const quantity = item.quantity + delta
  if (quantity < 1) {
    return consume(item)
  }
  await run(async () => replace(await updatePantryItem(item.id, { quantity })))
}

async function changeDate(item: PantryItem, value: string) {
  await run(async () =>
    replace(await updatePantryItem(item.id, value ? { bestBefore: value } : { clearBestBefore: true })),
  )
}

// Verbrauchen: amount Stück weniger; bei 0 verschwindet der Eintrag
async function consume(item: PantryItem, amount = 1) {
  await run(async () => {
    const updated = await consumePantryItem(item.id, amount)
    if (updated) {
      replace(updated)
    } else {
      items.value = items.value.filter((i) => i.id !== item.id)
      notice.value = `${item.productName} ist aufgebraucht.`
    }
  })
}

async function run(action: () => Promise<void>) {
  notice.value = null
  try {
    await action()
  } catch (e) {
    notice.value = e instanceof ApiError ? e.message : 'Das hat nicht geklappt. Versuch es noch einmal.'
  }
}
</script>

<template>
  <section>
    <h1 class="page-title">Vorrat</h1>

    <p v-if="loading" class="muted">Vorrat wird geladen …</p>
    <p v-else-if="error" class="error" role="alert">{{ error }}</p>
    <template v-else>
      <p v-if="notice" class="notice" role="status">{{ notice }}</p>
      <ul v-if="items.length > 0" class="box items">
        <li v-for="item in items" :key="item.id" class="row">
          <div class="main">
            <span class="name">{{ item.productName }}</span>
            <ExpiryLabel :status="item.expiryStatus" />
            <label class="date">
              <span>haltbar bis</span>
              <input
                type="date"
                :value="item.bestBefore ?? ''"
                :aria-label="`Mindesthaltbarkeit von ${item.productName}`"
                @change="changeDate(item, ($event.target as HTMLInputElement).value)"
              />
            </label>
          </div>
          <div class="controls">
            <button class="step" type="button" :aria-label="`${item.productName} weniger`" @click="changeQuantity(item, -1)">−</button>
            <span class="amount">{{ item.quantity }}</span>
            <button class="step" type="button" :aria-label="`${item.productName} mehr`" @click="changeQuantity(item, 1)">+</button>
            <button class="button" type="button" @click="consume(item, item.quantity)">Alles verbraucht</button>
          </div>
        </li>
      </ul>
      <div v-else class="box empty">
        <p>Dein Vorrat ist leer. Schließ einen Einkauf ab, dann landen die Artikel hier.</p>
        <RouterLink class="button primary" to="/liste">Zur Einkaufsliste</RouterLink>
      </div>
    </template>
  </section>
</template>

<style scoped>
.items {
  list-style: none;
  margin: 0;
  padding: 0;
}

.row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 10px 12px;
  border-bottom: 1px solid var(--border);
}

.row:last-child {
  border-bottom: none;
}

.main {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.name {
  font-weight: 600;
}

.date {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--muted);
  font-size: 0.875rem;
}

.controls {
  display: flex;
  align-items: center;
  gap: 6px;
}

.step {
  width: 44px;
  height: 44px;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: var(--surface);
  font-size: 1.25rem;
  cursor: pointer;
}

.amount {
  min-width: 2ch;
  text-align: center;
}

.empty {
  padding: 16px;
}

.muted {
  color: var(--muted);
}

.notice {
  color: var(--warn);
}

.error {
  color: var(--danger);
}
</style>
