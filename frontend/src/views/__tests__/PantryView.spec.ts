import { describe, it, expect, vi, beforeEach } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PantryView from '../PantryView.vue'
import { consumePantryItem, fetchPantry, type PantryItem } from '@/api/pantry'

vi.mock('@/api/pantry', () => ({
  fetchPantry: vi.fn<() => Promise<PantryItem[]>>(),
  updatePantryItem: vi.fn<() => Promise<PantryItem>>(),
  consumePantryItem: vi.fn<() => Promise<PantryItem | undefined>>(),
}))

const pantry: PantryItem[] = [
  { id: 1, productId: 1, productName: 'Gouda in Scheiben', quantity: 1, bestBefore: '2026-10-05', expiryStatus: 'EXPIRED' },
  { id: 2, productId: 2, productName: 'Naturjoghurt', quantity: 2, bestBefore: '2026-10-08', expiryStatus: 'EXPIRING_SOON' },
  { id: 3, productId: 3, productName: 'Basmatireis', quantity: 1, bestBefore: '2027-11-01', expiryStatus: 'OK' },
]

describe('PantryView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.mocked(fetchPantry).mockResolvedValue(structuredClone(pantry))
  })

  it('zeigt die Ablauf-Labels „abgelaufen“ und „läuft bald ab“', async () => {
    const wrapper = mount(PantryView, { global: { stubs: { RouterLink: true } } })
    await flushPromises()

    const rows = wrapper.findAll('li.row')
    expect(rows[0]!.text()).toContain('abgelaufen')
    expect(rows[1]!.text()).toContain('läuft bald ab')
    expect(rows[2]!.find('.pill').exists()).toBe(false)
  })

  it('entfernt einen aufgebrauchten Eintrag', async () => {
    vi.mocked(consumePantryItem).mockResolvedValue(undefined)
    const wrapper = mount(PantryView, { global: { stubs: { RouterLink: true } } })
    await flushPromises()

    await wrapper.findAll('li.row')[0]!.find('button.button').trigger('click')
    await flushPromises()

    expect(wrapper.findAll('li.row')).toHaveLength(2)
    expect(wrapper.text()).toContain('Gouda in Scheiben ist aufgebraucht.')
  })
})
