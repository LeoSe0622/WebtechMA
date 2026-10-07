<script setup lang="ts">
import type { ListItem } from '@/types/listItem'

// Props: Daten, die die Elternkomponente hereinreicht (nur lesen, nicht ändern)
defineProps<{ item: ListItem }>()

// Emits: Ereignisse, die diese Komponente nach oben meldet
const emit = defineEmits<{ toggle: [id: number] }>()
</script>

<template>
  <li class="row" :class="{ done: item.checked }">
    <label>
      <!-- :checked bindet den Wert, @change meldet den Klick an die Elternkomponente -->
      <input type="checkbox" :checked="item.checked" @change="emit('toggle', item.id)" />
      <span class="name">{{ item.productName }}</span>
    </label>
    <span class="quantity">{{ item.quantity }}×</span>
  </li>
</template>

<style scoped>
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 44px;
  padding: 0 12px;
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
  min-height: 44px;
  cursor: pointer;
}

input {
  width: 20px;
  height: 20px;
  accent-color: var(--accent);
}

.done .name {
  color: var(--muted);
  text-decoration: line-through;
}

.quantity {
  color: var(--muted);
  font-variant-numeric: tabular-nums;
}
</style>
