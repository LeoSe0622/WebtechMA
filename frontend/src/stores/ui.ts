import { ref } from 'vue'
import { defineStore } from 'pinia'

type Override = { kind: 'not-found' } | { kind: 'wip'; feature: string; milestone: string } | null

/**
 * Ersetzt den Seiteninhalt durch die 404- oder In-Arbeit-Ansicht, ohne die URL zu ändern.
 * Wird bei jedem Seitenwechsel zurückgesetzt (router.afterEach).
 */
export const useUiStore = defineStore('ui', () => {
  const override = ref<Override>(null)

  function showNotFound() {
    override.value = { kind: 'not-found' }
  }

  function showWorkInProgress(feature: string, milestone: string) {
    override.value = { kind: 'wip', feature, milestone }
  }

  function reset() {
    override.value = null
  }

  return { override, showNotFound, showWorkInProgress, reset }
})
