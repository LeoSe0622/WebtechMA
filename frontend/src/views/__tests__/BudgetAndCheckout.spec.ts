import { describe, it, expect, vi, beforeEach } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import BudgetView from '../BudgetView.vue'
import CheckoutView from '../CheckoutView.vue'
import { ApiError } from '@/api/client'
import { fetchBudget, saveBudget, type Budget } from '@/api/budget'
import { completePurchase, fetchStores, type CompletePurchaseResponse, type Store } from '@/api/purchases'
import { fetchListItems } from '@/api/listItems'
import type { ListItem } from '@/types/listItem'

vi.mock('@/api/budget', () => ({
  fetchBudget: vi.fn<() => Promise<Budget>>(),
  saveBudget: vi.fn<() => Promise<Budget>>(),
}))
vi.mock('@/api/purchases', () => ({
  fetchStores: vi.fn<() => Promise<Store[]>>(),
  completePurchase: vi.fn<() => Promise<CompletePurchaseResponse>>(),
}))
vi.mock('@/api/listItems', () => ({ fetchListItems: vi.fn<() => Promise<ListItem[]>>() }))

const stubs = { global: { stubs: { RouterLink: true } } }

describe('BudgetView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.mocked(fetchBudget).mockRejectedValue(new ApiError(404, { status: 404 }))   // noch kein Budget
    vi.mocked(saveBudget).mockReset()
  })

  it('speichert „1.000“ als 1000 € und nicht als 1 €', async () => {
    vi.mocked(saveBudget).mockResolvedValue({ yearMonth: '2026-10', amount: 1000, locked: false })
    const wrapper = mount(BudgetView, stubs)
    await flushPromises()

    await wrapper.find('input').setValue('1.000')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(vi.mocked(saveBudget).mock.calls[0]![1]).toBe(1000)
    expect(wrapper.text()).toContain('Budget gespeichert.')
  })

  it('zeigt die Meldung des Backends, wenn das Budget gesperrt ist', async () => {
    vi.mocked(saveBudget).mockRejectedValue(new ApiError(409, { status: 409, detail: 'Das Budget für diesen Monat ist gesperrt.' }))
    const wrapper = mount(BudgetView, stubs)
    await flushPromises()

    await wrapper.find('input').setValue('300')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('gesperrt')
  })
})

describe('CheckoutView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.mocked(fetchListItems).mockResolvedValue([
      { id: 1, productId: 1, productName: 'Hafermilch', quantity: 2, checked: true, createdAt: '2026-10-07T10:00:00Z' },
      { id: 2, productId: 2, productName: 'Butter', quantity: 1, checked: false, createdAt: '2026-10-07T10:00:00Z' },
    ])
    vi.mocked(fetchStores).mockResolvedValue([])
  })

  it('schließt mit neuem Laden und deutscher Summe ab und zeigt das neue Restbudget', async () => {
    vi.mocked(completePurchase).mockResolvedValue({
      purchase: { id: 5, storeId: 3, storeName: 'Lidl', date: '2026-10-07', totalAmount: 23.45, lines: [{ productId: 1, quantity: 2 }] },
      remainingBudget: 180.3,
    })
    const wrapper = mount(CheckoutView, stubs)
    await flushPromises()

    expect(wrapper.text()).toContain('1 abgehakte Artikel')
    await wrapper.find('input[placeholder^="z. B. Lidl"]').setValue('Lidl')
    await wrapper.find('input[inputmode="decimal"]').setValue('23,45')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(completePurchase).toHaveBeenCalledWith({ storeName: 'Lidl', totalAmount: 23.45 })
    expect(wrapper.text()).toContain('Einkauf gespeichert')
    expect(wrapper.text().replace(/\s/g, ' ')).toContain('180,30 €')
  })
})
