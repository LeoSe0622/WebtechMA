<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { fetchListItems } from '@/api/listItems'
import { completePurchase, fetchStores, type CompletePurchaseResponse, type Store } from '@/api/purchases'
import { ApiError, loadPage } from '@/api/client'
import { formatEuro, parseEuro } from '@/config'
import type { ListItem } from '@/types/listItem'

const checkedItems = ref<ListItem[]>([])
const stores = ref<Store[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

const storeId = ref<number | 'new' | null>(null)
const newStoreName = ref('')
const totalText = ref('')
const busy = ref(false)
const result = ref<CompletePurchaseResponse | null>(null)

onMounted(async () => {
  try {
    const loaded = await loadPage(() => Promise.all([fetchListItems(), fetchStores()]))
    if (loaded) {
      checkedItems.value = loaded[0].filter((item) => item.checked)
      stores.value = loaded[1]
      storeId.value = stores.value[0]?.id ?? 'new'
    }
  } catch {
    error.value = 'Die Daten konnten nicht geladen werden. Läuft das Backend?'
  } finally {
    loading.value = false
  }
})

// "12,34", "12.34" oder "1.000" → Zahl; ungültige Eingaben ergeben NaN
const total = computed(() => parseEuro(totalText.value))
const totalValid = computed(() => total.value >= 0.01 && total.value <= 1000)
const storeValid = computed(() => storeId.value !== 'new' || newStoreName.value.trim().length > 0)

async function submit() {
  busy.value = true
  error.value = null
  try {
    result.value = await completePurchase({
      ...(storeId.value === 'new' ? { storeName: newStoreName.value.trim() } : { storeId: storeId.value! }),
      totalAmount: Math.round(total.value * 100) / 100,
    })
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : 'Der Einkauf konnte nicht gespeichert werden.'
  } finally {
    busy.value = false
  }
}
</script>

<template>
  <section>
    <h1 class="page-title">Einkauf abschließen</h1>

    <p v-if="loading" class="muted">Wird geladen …</p>

    <div v-else-if="result" class="box done">
      <h2>Einkauf gespeichert</h2>
      <p>
        {{ formatEuro(result.purchase.totalAmount) }} bei {{ result.purchase.storeName }}.
        {{ result.purchase.lines.length }} Artikel sind jetzt im Vorrat.
      </p>
      <p v-if="result.remainingBudget !== null" class="remaining" :class="{ negative: result.remainingBudget < 0 }">
        Restbudget: <span class="amount">{{ formatEuro(result.remainingBudget) }}</span>
      </p>
      <div class="actions">
        <RouterLink class="button primary" to="/vorrat">Zum Vorrat</RouterLink>
        <RouterLink class="button" to="/dashboard">Zum Dashboard</RouterLink>
      </div>
    </div>

    <div v-else-if="checkedItems.length === 0" class="box empty">
      <p>Es ist noch nichts abgehakt. Hake auf der Liste ab, was du gekauft hast.</p>
      <RouterLink class="button primary" to="/liste">Zur Einkaufsliste</RouterLink>
    </div>

    <form v-else class="box form" @submit.prevent="submit">
      <h2>{{ checkedItems.length }} abgehakte Artikel</h2>
      <ul class="items">
        <li v-for="item in checkedItems" :key="item.id">{{ item.quantity }}× {{ item.productName }}</li>
      </ul>

      <label>
        Laden
        <select v-model="storeId">
          <option v-for="store in stores" :key="store.id" :value="store.id">{{ store.name }}</option>
          <option value="new">Neuer Laden …</option>
        </select>
      </label>
      <label v-if="storeId === 'new'">
        Name des Ladens
        <input v-model="newStoreName" maxlength="60" placeholder="z. B. Lidl Kreuzberg" required />
      </label>

      <label>
        Summe laut Kassenbon (€)
        <input v-model="totalText" inputmode="decimal" placeholder="z. B. 23,45" required />
      </label>
      <p v-if="totalText && !totalValid" class="hint">Die Summe muss zwischen 0,01 € und 1.000 € liegen.</p>

      <p v-if="error" class="error" role="alert">{{ error }}</p>
      <button class="button primary" type="submit" :disabled="busy || !totalValid || !storeValid">
        Einkauf abschließen
      </button>
    </form>
  </section>
</template>

<style scoped>
.form,
.done,
.empty {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  max-width: 560px;
}

h2 {
  font-size: 1.125rem;
  margin: 0;
}

.items {
  margin: 0;
  padding-left: 20px;
  color: var(--muted);
}

label {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 0.875rem;
}

.remaining .amount {
  font-family: var(--font-serif);
  font-size: 1.5rem;
  font-weight: 600;
}

.negative .amount {
  color: var(--danger);
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.muted,
.hint {
  color: var(--muted);
}

.error {
  color: var(--danger);
}
</style>
