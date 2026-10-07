import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import App from '@/App.vue'
import { createAppRouter } from '@/router'
import { useAuthStore } from '@/stores/auth'

// Seiten, die Daten laden, bekommen in diesen Tests eine leere Antwort
vi.mock('@/api/listItems', () => ({ fetchListItems: vi.fn<() => Promise<never[]>>().mockResolvedValue([]) }))

async function visit(path: string) {
  const router = createAppRouter()
  const pinia = createPinia()
  setActivePinia(pinia)
  useAuthStore().setSession({
    token: 'test',
    user: { id: 1, username: 'mia', displayName: 'Mia', householdSize: 1, leaderboardOptIn: false, sandbox: true },
  })
  await router.push(path)
  await router.isReady()
  const wrapper = mount(App, { global: { plugins: [pinia, router] } })
  await flushPromises()
  return { wrapper, router }
}

describe('Router', () => {
  beforeEach(() => {
    sessionStorage.clear()
  })

  it('zeigt für eine wip-Route die In-Arbeit-Seite, die URL bleibt', async () => {
    const { wrapper, router } = await visit('/rezepte')

    expect(router.currentRoute.value.path).toBe('/rezepte')
    expect(wrapper.find('h1').text()).toBe('Rezepte ist in Arbeit')
    expect(wrapper.text()).toContain('nach M4')
    expect(wrapper.text()).toContain('Claude Code')
  })

  it('zeigt für einen unbekannten Pfad die 404-Seite mit dem Pfad', async () => {
    const { wrapper } = await visit('/gibt-es-nicht')

    expect(wrapper.find('h1').text()).toBe('Diese Seite gibt es nicht')
    expect(wrapper.find('code').text()).toBe('/gibt-es-nicht')
  })

  it('leitet ohne Login zur Startseite und merkt sich das Ziel', async () => {
    const router = createAppRouter()
    const pinia = createPinia()
    setActivePinia(pinia)
    mount(App, { global: { plugins: [pinia, router] } })
    await router.push('/liste')

    expect(router.currentRoute.value.name).toBe('start')
    expect(router.currentRoute.value.query.redirect).toBe('/liste')
  })
})
