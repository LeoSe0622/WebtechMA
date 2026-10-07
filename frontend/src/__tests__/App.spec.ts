import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import App from '../App.vue'
import router from '../router'

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
