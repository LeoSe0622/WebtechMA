<script setup lang="ts">
import type { ListItem } from '@/types/listItem'

// Props: Daten, die die Elternkomponente hereinreicht (nur lesen, nicht ändern)
defineProps<{ item: ListItem }>()

// Emits: Ereignisse, die diese Komponente nach oben meldet
const emit = defineEmits<{ toggle: [id: number]; remove: [id: number] }>()
</script>

<template>
  <li class="row" :class="{ done: item.checked }">
    <label>
      <!-- :checked bindet den Wert, @change meldet den Klick an die Elternkomponente -->
      <input type="checkbox" :checked="item.checked" @change="emit('toggle', item.id)" />
      <span class="name">{{ item.productName }}</span>
    </label>
    <span class="quantity">{{ item.quantity }}×</span>
    <button class="remove" type="button" :aria-label="`${item.productName} entfernen`" @click="emit('remove', item.id)">
      ✕
    </button>
  </li>
</template>

<style scoped>
.row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 52px;
  padding: 0 4px 0 12px;
  border-bottom: 1px solid var(--border);
}

.row:last-child {
  border-bottom: none;
}

label {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-height: 52px;
  cursor: pointer;
}

input {
  width: 22px;
  height: 22px;
  min-height: 0;
  accent-color: var(--accent-strong);
}

.done .name {
  color: var(--muted);
  text-decoration: line-through;
}

.quantity {
  color: var(--muted);
  font-variant-numeric: tabular-nums;
}

.remove {
  width: 44px;
  height: 44px;
  border: none;
  background: none;
  color: var(--muted);
  font-size: 1rem;
  cursor: pointer;
  border-radius: 6px;
}

.remove:hover {
  background: var(--bg);
  color: var(--danger);
}
</style>
