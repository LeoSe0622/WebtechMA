import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import AppHeader from '../AppHeader.vue'
import { createAppRouter } from '@/router'
import { useAuthStore } from '@/stores/auth'

describe('AppHeader', () => {
  beforeEach(() => {
    sessionStorage.clear()
  })

  it('Tab-Leiste zeigt die echten Bereiche, „Mehr“ öffnet Demo- und In-Arbeit-Bereiche mit Labels', async () => {
    const router = createAppRouter()
    const pinia = createPinia()
    setActivePinia(pinia)
    useAuthStore().setSession({
      token: 't',
      user: { id: 1, username: 'mia', displayName: 'Mia', householdSize: 1, leaderboardOptIn: false, sandbox: true },
    })
    await router.push('/dashboard')
    const wrapper = mount(AppHeader, { global: { plugins: [pinia, router] } })

    const tabs = wrapper.findAll('.tabbar > a').map((a) => a.text())
    expect(tabs).toEqual(['Dashboard', 'Budget', 'Liste', 'Vorrat'])
    expect(wrapper.find('.more').exists()).toBe(false)

    await wrapper.find('.tabbar > button').trigger('click')

    const more = wrapper.find('.more').text()
    expect(more).toContain('Gewohnheiten')
    expect(more).toContain('Demo')
    expect(more).toContain('Rezepte')
    expect(more).toContain('In Arbeit')
  })
})
