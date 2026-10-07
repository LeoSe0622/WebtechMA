import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import App from '../App.vue'
import { createAppRouter } from '../router'

// Attrappe, damit der Test kein echtes Backend aufruft
vi.mock('@/api/listItems', () => ({ fetchListItems: vi.fn<() => Promise<never[]>>().mockResolvedValue([]) }))

describe('App', () => {
  beforeEach(() => {
    sessionStorage.clear()
  })

  it('zeigt ohne Login die Startseite ohne Kopfleiste', async () => {
    const router = createAppRouter()
    const pinia = createPinia()
    setActivePinia(pinia)
    await router.push('/')
    await router.isReady()

    const wrapper = mount(App, { global: { plugins: [pinia, router] } })

    expect(wrapper.text()).toContain('Als Demo testen')
    expect(wrapper.find('header.topbar').exists()).toBe(false)
  })
})
