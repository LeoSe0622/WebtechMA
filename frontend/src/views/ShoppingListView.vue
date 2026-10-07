<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import ListItemRow from '@/components/ListItemRow.vue'
import { fetchListItems } from '@/api/listItems'
import type { ListItem } from '@/types/listItem'

// ref: reaktiver Zustand. Ändert sich ein Wert, aktualisiert Vue das Template selbst.
const items = ref<ListItem[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

// computed: abgeleiteter Wert, wird automatisch neu berechnet, wenn sich items ändert
const openCount = computed(() => items.value.filter((item) => !item.checked).length)

// onMounted: läuft einmal, sobald die Komponente im Browser angezeigt wird
onMounted(async () => {
  try {
    items.value = await fetchListItems()
  } catch {
    error.value = 'Die Liste konnte nicht geladen werden. Läuft das Backend?'
  } finally {
    loading.value = false
  }
})

// Abhaken ändert vorerst nur die Anzeige; Speichern per PATCH kommt in Prompt 2
function toggle(id: number) {
  const item = items.value.find((i) => i.id === id)
  if (item) {
    item.checked = !item.checked
  }
}
</script>

<template>
  <section>
    <h1>Einkaufsliste</h1>

    <!-- Drei Zustände: lädt, Fehler, Daten da -->
    <p v-if="loading" class="status">Liste wird geladen …</p>
    <p v-else-if="error" class="status error" role="alert">{{ error }}</p>
    <template v-else>
      <p class="summary">{{ openCount }} von {{ items.length }} noch offen</p>

      <!-- v-for erzeugt pro Eintrag eine Zeile; :key hilft Vue, Zeilen wiederzuerkennen -->
      <ul v-if="items.length > 0" class="box">
        <ListItemRow v-for="item in items" :key="item.id" :item="item" @toggle="toggle" />
      </ul>
      <p v-else class="empty">Deine Liste ist leer.</p>
    </template>
  </section>
</template>

<style scoped>
h1 {
  font-family: var(--font-serif);
  margin: 0 0 4px;
}

.summary {
  color: var(--muted);
  margin: 0 0 16px;
}

.box {
  list-style: none;
  margin: 0;
  padding: 0;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 6px;
}

.empty,
.status {
  color: var(--muted);
}

.error {
  color: var(--danger);
}
</style>
