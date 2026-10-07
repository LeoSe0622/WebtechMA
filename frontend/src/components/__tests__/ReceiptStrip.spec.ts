import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ReceiptStrip from '../ReceiptStrip.vue'

// Intl.NumberFormat setzt ein geschütztes Leerzeichen (U+00A0) vor das Euro-Zeichen
const euro = (text: string) => text.replace(/\s/g, ' ')

const stubs = { global: { stubs: { RouterLink: { template: '<a><slot /></a>' } } } }

describe('ReceiptStrip', () => {
  it('zeigt ein positives Restbudget in Euro', () => {
    const wrapper = mount(ReceiptStrip, { props: { remaining: 203.75, amount: 260 }, ...stubs })

    expect(euro(wrapper.find('.value').text())).toBe('203,75 €')
    expect(euro(wrapper.text())).toContain('von 260,00 €')
    expect(wrapper.classes()).not.toContain('negative')
  })

  it('markiert ein negatives Restbudget rot', () => {
    const wrapper = mount(ReceiptStrip, { props: { remaining: -15.6, amount: 260 }, ...stubs })

    expect(euro(wrapper.find('.value').text())).toBe('-15,60 €')
    expect(wrapper.classes()).toContain('negative')
  })

  it('fordert ohne Budget zum Anlegen auf', () => {
    const wrapper = mount(ReceiptStrip, { props: { remaining: null, amount: null }, ...stubs })

    expect(wrapper.text()).toContain('Leg dein erstes Budget an')
  })
})
