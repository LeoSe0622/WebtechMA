import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import StartView from '../StartView.vue'
import { createAppRouter } from '@/router'
import { loginDemo, register } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import { HOME_PATH } from '@/config'
import type { TokenResponse } from '@/api/auth'

vi.mock('@/api/auth', () => ({
  loginDemo: vi.fn<() => Promise<TokenResponse>>(),
  login: vi.fn<() => Promise<TokenResponse>>(),
  register: vi.fn<() => Promise<TokenResponse>>(),
}))
vi.mock('@/api/listItems', () => ({ fetchListItems: vi.fn<() => Promise<never[]>>().mockResolvedValue([]) }))

describe('StartView', () => {
  beforeEach(() => {
    sessionStorage.clear()
  })

  it('„Als Demo testen“ speichert das Token und wechselt in die App', async () => {
    const router = createAppRouter()
    const pinia = createPinia()
    setActivePinia(pinia)
    vi.mocked(loginDemo).mockResolvedValue({
      token: 'demo-token',
      user: { id: 7, username: 'demo-1', displayName: 'Mia (Demo)', householdSize: 1, leaderboardOptIn: false, sandbox: true },
    })
    await router.push('/')
    const wrapper = mount(StartView, { global: { plugins: [pinia, router] } })

    await wrapper.find('button.big').trigger('click')
    await flushPromises()

    expect(useAuthStore().token).toBe('demo-token')
    expect(sessionStorage.getItem('korbgeld.token')).toBe('demo-token')
    expect(router.currentRoute.value.path).toBe(HOME_PATH)
  })

  it('Registrieren schickt alle Pflichtfelder mit Haushaltsgröße und Opt-in', async () => {
    const router = createAppRouter()
    const pinia = createPinia()
    setActivePinia(pinia)
    vi.mocked(register).mockResolvedValue({
      token: 'neu',
      user: { id: 8, username: 'wg-kreuzberg', displayName: 'WG Kreuzberg', householdSize: 3, leaderboardOptIn: true, sandbox: false },
    })
    await router.push('/')
    const wrapper = mount(StartView, { global: { plugins: [pinia, router] } })

    await wrapper.findAll('[role="tab"]')[1]!.trigger('click')
    // Felder, die nur beim Registrieren erscheinen
    const householdInput = wrapper.find('input[type="number"]')
    expect(householdInput.attributes('min')).toBe('1')
    expect(householdInput.attributes('max')).toBe('6')

    await wrapper.find('input[autocomplete="username"]').setValue('wg-kreuzberg')
    await wrapper.find('input[type="password"]').setValue('geheim-123')
    await wrapper.find('input[maxlength="40"]').setValue('WG Kreuzberg')
    await householdInput.setValue(3)
    await wrapper.find('label.check input').setValue(true)
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(register).toHaveBeenCalledWith({
      username: 'wg-kreuzberg',
      password: 'geheim-123',
      displayName: 'WG Kreuzberg',
      householdSize: 3,
      leaderboardOptIn: true,
    })
    expect(useAuthStore().token).toBe('neu')
  })
})
