<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import IssueCard from '@/components/IssueCard.vue'
import StatusLabel from '@/components/StatusLabel.vue'
import { ASSIGNEES, HOME_PATH } from '@/config'

// Angaben kommen entweder direkt (bei 501 vom Backend) oder aus den Meta-Daten der Route
const props = defineProps<{ feature?: string; milestone?: string }>()
const route = useRoute()

const feature = computed(() => props.feature ?? route.meta.feature ?? 'Diese Funktion')
const milestone = computed(() => props.milestone ?? route.meta.milestone ?? '')
const tasks = computed(() => route.meta.tasks ?? [])
</script>

<template>
  <IssueCard :title="`${feature} ist in Arbeit`">
    <template #meta>
      <StatusLabel status="wip" />
      <span v-if="milestone">Milestone: {{ milestone }}</span>
      <span class="assignees">
        <template v-for="person in ASSIGNEES" :key="person.initial">
          <img
            v-if="person.login"
            class="avatar"
            :src="`https://github.com/${person.login}.png?size=96`"
            :alt="person.login"
          />
          <span v-else class="avatar initial" aria-hidden="true">{{ person.initial }}</span>
        </template>
        <span class="badge">Claude Code</span>
      </span>
    </template>

    <ul v-if="tasks.length > 0" class="tasks">
      <li v-for="task in tasks" :key="task">
        <input type="checkbox" disabled /> {{ task }}
      </li>
    </ul>
    <p v-else>Daran wird gerade gearbeitet.</p>

    <RouterLink class="button" :to="HOME_PATH">Zurück zum Dashboard</RouterLink>
  </IssueCard>
</template>

<style scoped>
.assignees {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 1px solid var(--border);
}

.initial {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--accent-weak);
  color: var(--accent-strong);
  font-size: 0.75rem;
  font-weight: 600;
}

.badge {
  margin-left: 4px;
  padding: 0 8px;
  border: 1px solid var(--border);
  border-radius: 999px;
  font-size: 0.75rem;
  line-height: 20px;
}

.tasks {
  list-style: none;
  padding: 0;
  margin: 0 0 16px;
}

.tasks li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
}
</style>
