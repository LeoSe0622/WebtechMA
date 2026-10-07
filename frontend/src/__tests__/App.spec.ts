import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import App from '../App.vue'
import router from '../router'
import type { ListItem } from '@/types/listItem'

// Attrappe, damit der Test kein echtes Backend aufruft
vi.mock('@/api/listItems', () => ({
  fetchListItems: vi.fn<() => Promise<ListItem[]>>().mockResolvedValue([]),
}))

describe('App', () => {
  it('leitet / auf die Einkaufsliste weiter', async () => {
    router.push('/')
    await router.isReady()

    const wrapper = mount(App, { global: { plugins: [router] } })

    expect(router.currentRoute.value.path).toBe('/liste')
    expect(wrapper.text()).toContain('Korbgeld')
    expect(wrapper.find('h1').text()).toBe('Einkaufsliste')
  })
})
