import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import StartView from '../StartView.vue'
import { createAppRouter } from '@/router'
import { loginDemo } from '@/api/auth'
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
})
