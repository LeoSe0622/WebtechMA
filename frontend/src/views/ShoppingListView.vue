<script setup lang="ts">
import { computed, ref } from 'vue'
import ListItemRow from '@/components/ListItemRow.vue'
import type { ListItem } from '@/types/listItem'

// ref: reaktiver Zustand. Ändert sich items.value, aktualisiert Vue das Template selbst.
// Vorerst feste Beispieldaten; bei der Anbindung kommen sie von GET /api/list-items.
const items = ref<ListItem[]>([
  { id: 1, productId: 1, productName: 'Hafermilch', quantity: 2, checked: false, createdAt: '2026-10-07T19:07:25Z' },
  { id: 2, productId: 2, productName: 'Vollkornbrot', quantity: 1, checked: false, createdAt: '2026-10-07T19:07:25Z' },
  { id: 3, productId: 3, productName: 'Äpfel', quantity: 6, checked: true, createdAt: '2026-10-07T19:07:25Z' },
])

// computed: abgeleiteter Wert, wird automatisch neu berechnet, wenn sich items ändert
const openCount = computed(() => items.value.filter((item) => !item.checked).length)

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
    <p class="summary">{{ openCount }} von {{ items.length }} noch offen</p>

    <!-- v-for erzeugt pro Eintrag eine Zeile; :key hilft Vue, Zeilen wiederzuerkennen -->
    <ul v-if="items.length > 0" class="box">
      <ListItemRow v-for="item in items" :key="item.id" :item="item" @toggle="toggle" />
    </ul>
    <p v-else class="empty">Deine Liste ist leer.</p>
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

.empty {
  color: var(--muted);
}
</style>
