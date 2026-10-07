<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref, watch } from 'vue'
import ListItemRow from '@/components/ListItemRow.vue'
import ReceiptStrip from '@/components/ReceiptStrip.vue'
import { createListItem, deleteListItem, fetchListItems, updateListItem, type PantryHint } from '@/api/listItems'
import { findProductByBarcode, searchProducts, type Product } from '@/api/products'
import { fetchBudgetSummary, type BudgetSummary } from '@/api/budget'
import { ApiError, loadPage } from '@/api/client'
import type { ListItem } from '@/types/listItem'

// Der Scanner (@zxing/browser) ist groß; er wird erst geladen, wenn jemand auf „Kamera“ tippt
const BarcodeScanner = defineAsyncComponent(() => import('@/components/BarcodeScanner.vue'))

// ref: reaktiver Zustand. Ändert sich ein Wert, aktualisiert Vue das Template selbst.
const items = ref<ListItem[]>([])
const summary = ref<BudgetSummary | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

// Eingabe für neue Einträge
const query = ref('')
const quantity = ref(1)
const suggestions = ref<Product[]>([])
const selected = ref<Product | null>(null)
const barcode = ref('')
const scanning = ref(false)
const notice = ref<string | null>(null)
const busy = ref(false)
const pantryWarning = ref<{ hint: PantryHint; name: string; retry: () => Promise<void> } | null>(null)

// computed: abgeleitete Werte, werden automatisch neu berechnet
const openCount = computed(() => items.value.filter((item) => !item.checked).length)
const checkedCount = computed(() => items.value.filter((item) => item.checked).length)

// onMounted: läuft einmal, sobald die Komponente im Browser angezeigt wird
onMounted(async () => {
  try {
    const [list, budget] = (await loadPage(() => Promise.all([fetchListItems(), fetchBudgetSummary()]))) ?? [[], null]
    items.value = list
    summary.value = budget
  } catch {
    error.value = 'Die Liste konnte nicht geladen werden. Läuft das Backend?'
  } finally {
    loading.value = false
  }
})

// Vorschläge beim Tippen, kurz verzögert, damit nicht jeder Buchstabe eine Anfrage auslöst
let searchTimer: ReturnType<typeof setTimeout> | undefined
watch(query, (text) => {
  // Nach Klick auf einen Vorschlag steht dessen Name im Feld: Auswahl behalten, nicht neu suchen
  if (selected.value && text === selected.value.name) {
    return
  }
  selected.value = null
  clearTimeout(searchTimer)
  if (text.trim().length < 2) {
    suggestions.value = []
    return
  }
  searchTimer = setTimeout(async () => {
    suggestions.value = await searchProducts(text).catch(() => [])
  }, 250)
})

function choose(product: Product) {
  selected.value = product
  query.value = product.name
  clearTimeout(searchTimer)
  suggestions.value = []
}

async function add(force = false) {
  const name = query.value.trim()
  if (!selected.value && !name) {
    return
  }
  // Abgleich über die productId; nur ohne Auswahl wird der Name geschickt
  const request = selected.value
    ? { productId: selected.value.id, quantity: quantity.value, force }
    : { productName: name, quantity: quantity.value, force }
  await submit(request, selected.value?.name ?? name)
}

async function submit(request: Parameters<typeof createListItem>[0], name: string) {
  notice.value = null
  busy.value = true
  try {
    const response = await createListItem(request)
    if (response.alreadyInPantry) {
      // Noch nichts angelegt: erst nachfragen
      pantryWarning.value = {
        hint: response.alreadyInPantry,
        name,
        retry: () => submit({ ...request, force: true }, name),
      }
      return
    }
    pantryWarning.value = null
    items.value.push(response.item!)
    query.value = ''
    quantity.value = 1
    selected.value = null
    suggestions.value = []
  } catch (e) {
    notice.value = e instanceof ApiError ? e.message : 'Hinzufügen hat nicht geklappt. Versuch es noch einmal.'
  } finally {
    busy.value = false
  }
}

async function lookupBarcode(code: string) {
  scanning.value = false
  notice.value = null
  busy.value = true
  try {
    const product = await findProductByBarcode(code.trim())
    barcode.value = ''
    await submit({ productId: product.id, quantity: 1 }, product.name)
  } catch (e) {
    // 404 behandelt die Seite selbst: manuelle Eingabe anbieten
    if (e instanceof ApiError && e.status === 404) {
      notice.value = 'Diesen Barcode kennen wir nicht. Gib den Namen oben von Hand ein.'
    } else if (e instanceof ApiError && e.status === 502) {
      notice.value = 'Die Produktdatenbank antwortet gerade nicht. Gib den Namen oben von Hand ein.'
    } else if (e instanceof ApiError && e.status === 400) {
      notice.value = e.message
    } else {
      notice.value = 'Der Barcode konnte nicht geprüft werden.'
    }
  } finally {
    busy.value = false
  }
}

async function toggle(id: number) {
  const item = items.value.find((i) => i.id === id)
  if (!item) {
    return
  }
  item.checked = !item.checked   // sofort anzeigen, dann speichern
  try {
    await updateListItem(id, { checked: item.checked })
  } catch {
    item.checked = !item.checked   // zurücknehmen, wenn das Speichern scheitert
    notice.value = 'Abhaken hat nicht geklappt. Prüf deine Verbindung.'
  }
}

async function remove(id: number) {
  try {
    await deleteListItem(id)
    items.value = items.value.filter((i) => i.id !== id)
  } catch {
    notice.value = 'Entfernen hat nicht geklappt.'
  }
}
</script>

<template>
  <section class="list-page">
    <h1 class="page-title">Einkaufsliste</h1>

    <!-- Drei Zustände: lädt, Fehler, Daten da -->
    <p v-if="loading" class="status">Liste wird geladen …</p>
    <p v-else-if="error" class="status error" role="alert">{{ error }}</p>
    <template v-else>
      <ReceiptStrip v-if="summary" :remaining="summary.remaining" :amount="summary.amount" />

      <form class="add box" @submit.prevent="add()">
        <div class="add-row">
          <label class="grow">
            <span class="sr-only">Artikel</span>
            <input v-model="query" placeholder="Artikel hinzufügen, z. B. Hafermilch" autocomplete="off" />
          </label>
          <label>
            <span class="sr-only">Menge</span>
            <input v-model.number="quantity" class="qty" type="number" min="1" max="99" />
          </label>
          <button class="button primary" type="submit" :disabled="busy">Hinzufügen</button>
        </div>
        <ul v-if="suggestions.length > 0" class="suggestions" role="listbox">
          <li v-for="product in suggestions" :key="product.id">
            <button type="button" @click="choose(product)">{{ product.name }}</button>
          </li>
        </ul>

        <div class="barcode-row">
          <input v-model="barcode" inputmode="numeric" placeholder="Barcode-Nummer" aria-label="Barcode-Nummer" />
          <button class="button" type="button" :disabled="busy || !barcode.trim()" @click="lookupBarcode(barcode)">
            {{ busy ? 'Suche …' : 'Barcode suchen' }}
          </button>
          <button class="button" type="button" @click="scanning = !scanning">Kamera</button>
        </div>
        <BarcodeScanner v-if="scanning" @detected="lookupBarcode" @close="scanning = false" />
      </form>

      <div v-if="pantryWarning" class="warning box" role="alert">
        <p>
          <strong>{{ pantryWarning.name }}</strong> hast du schon im Vorrat ({{ pantryWarning.hint.quantity }}×<template
            v-if="pantryWarning.hint.bestBefore"
            >, haltbar bis {{ new Date(pantryWarning.hint.bestBefore).toLocaleDateString('de-DE') }}</template
          >).
        </p>
        <div class="actions">
          <button class="button primary" type="button" :disabled="busy" @click="pantryWarning.retry()">Trotzdem hinzufügen</button>
          <button class="button" type="button" @click="pantryWarning = null">Nicht hinzufügen</button>
        </div>
      </div>
      <p v-if="notice" class="notice" role="status">{{ notice }}</p>

      <p class="summary">{{ openCount }} von {{ items.length }} noch offen</p>
      <!-- v-for erzeugt pro Eintrag eine Zeile; :key hilft Vue, Zeilen wiederzuerkennen -->
      <ul v-if="items.length > 0" class="box items">
        <ListItemRow v-for="item in items" :key="item.id" :item="item" @toggle="toggle" @remove="remove" />
      </ul>
      <p v-else class="empty">Deine Liste ist leer. Füg oben deinen ersten Artikel hinzu.</p>

      <div class="sticky-action">
        <RouterLink
          v-if="checkedCount > 0"
          class="button primary checkout"
          to="/liste/abschliessen"
        >
          Einkauf abschließen ({{ checkedCount }})
        </RouterLink>
        <span v-else class="button checkout" aria-disabled="true">Hake Artikel ab, um den Einkauf abzuschließen</span>
      </div>
    </template>
  </section>
</template>

<style scoped>
.add {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.add-row,
.barcode-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.grow {
  flex: 1 1 180px;
}

.grow input {
  width: 100%;
}

.qty {
  width: 72px;
}

.barcode-row input {
  flex: 1 1 140px;
}

.suggestions {
  list-style: none;
  margin: 0;
  padding: 0;
  border: 1px solid var(--border);
  border-radius: 6px;
}

.suggestions button {
  width: 100%;
  min-height: 44px;
  padding: 0 12px;
  text-align: left;
  background: none;
  border: none;
  border-bottom: 1px solid var(--border);
  font: inherit;
  cursor: pointer;
}

.suggestions li:last-child button {
  border-bottom: none;
}

.suggestions button:hover {
  background: var(--bg);
}

.warning {
  padding: 12px;
  margin-bottom: 12px;
  border-color: var(--warn);
  background: #fffbea;
}

.warning p {
  margin: 0 0 8px;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.notice {
  color: var(--warn);
}

.summary,
.status,
.empty {
  color: var(--muted);
}

.error {
  color: var(--danger);
}

.items {
  list-style: none;
  margin: 0;
  padding: 0;
}

.sticky-action {
  position: fixed;
  left: 0;
  right: 0;
  /* direkt über der Tab-Leiste (auf breiten Bildschirmen 0) */
  bottom: var(--tabbar-height);
  padding: 12px 16px;
  background: linear-gradient(to top, var(--bg) 70%, transparent);
  display: flex;
  justify-content: center;
}

.checkout {
  width: 100%;
  max-width: 560px;
  min-height: 52px;
}

span.checkout {
  color: var(--muted);
  font-weight: 400;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0 0 0 0);
}
</style>
