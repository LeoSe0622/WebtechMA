import { describe, it, expect, vi, beforeEach } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import ShoppingListView from '../ShoppingListView.vue'
import { createListItem, fetchListItems, updateListItem, type CreateListItemResponse } from '@/api/listItems'
import { fetchBudgetSummary, type BudgetSummary } from '@/api/budget'
import type { ListItem } from '@/types/listItem'
import type { Product } from '@/api/products'

// Attrappen statt echter Backend-Aufrufe (wie @MockitoBean im Backend)
vi.mock('@/api/listItems', () => ({
  fetchListItems: vi.fn<() => Promise<ListItem[]>>(),
  createListItem: vi.fn<() => Promise<CreateListItemResponse>>(),
  updateListItem: vi.fn<() => Promise<ListItem>>(),
  deleteListItem: vi.fn<() => Promise<void>>(),
}))
vi.mock('@/api/budget', () => ({ fetchBudgetSummary: vi.fn<() => Promise<BudgetSummary>>() }))
vi.mock('@/api/products', () => ({
  searchProducts: vi.fn<() => Promise<Product[]>>(),
  findProductByBarcode: vi.fn<() => Promise<Product>>(),
}))

const items: ListItem[] = [
  { id: 1, productId: 1, productName: 'Hafermilch', quantity: 2, checked: false, createdAt: '2026-10-07T10:00:00Z' },
  { id: 2, productId: 2, productName: 'Vollkornbrot', quantity: 1, checked: false, createdAt: '2026-10-07T10:00:00Z' },
  { id: 3, productId: 3, productName: 'Äpfel', quantity: 6, checked: true, createdAt: '2026-10-07T10:00:00Z' },
]
const summary: BudgetSummary = { yearMonth: '2026-10', amount: 260, spent: 56.25, remaining: 203.75, locked: true, lastCompleted: null }

async function mountView() {
  const wrapper = mount(ShoppingListView, { global: { stubs: { RouterLink: true } } })
  await flushPromises()
  return wrapper
}

describe('ShoppingListView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.mocked(fetchListItems).mockResolvedValue(structuredClone(items))
    vi.mocked(fetchBudgetSummary).mockResolvedValue(summary)
    vi.mocked(createListItem).mockReset()
    vi.mocked(updateListItem).mockReset()
  })

  it('rendert die geladenen Einträge per v-for und zeigt das Restbudget', async () => {
    const wrapper = await mountView()

    const rows = wrapper.findAll('li.row')
    expect(rows).toHaveLength(3)
    expect(rows[0]!.text()).toContain('Hafermilch')
    expect(wrapper.find('.receipt').text()).toContain('203,75')
    expect(wrapper.find('.summary').text()).toBe('2 von 3 noch offen')
  })

  it('speichert das Abhaken per PATCH und aktualisiert den Zähler', async () => {
    vi.mocked(updateListItem).mockResolvedValue({ ...items[0]!, checked: true })
    const wrapper = await mountView()

    await wrapper.findAll('input[type="checkbox"]')[0]!.trigger('change')
    await flushPromises()

    expect(updateListItem).toHaveBeenCalledWith(1, { checked: true })
    expect(wrapper.find('.summary').text()).toBe('1 von 3 noch offen')
  })

  it('zeigt die Vorrats-Warnung und fügt erst nach „Trotzdem hinzufügen“ hinzu', async () => {
    vi.mocked(createListItem)
      .mockResolvedValueOnce({ item: null, alreadyInPantry: { quantity: 3, bestBefore: null } })
      .mockResolvedValueOnce({
        item: { id: 9, productId: 5, productName: 'Spaghetti', quantity: 1, checked: false, createdAt: '2026-10-07T11:00:00Z' },
        alreadyInPantry: null,
      })
    const wrapper = await mountView()

    await wrapper.find('input[placeholder^="Artikel"]').setValue('Spaghetti')
    await wrapper.find('form.add').trigger('submit')
    await flushPromises()

    expect(wrapper.find('.warning').text()).toContain('Spaghetti hast du schon im Vorrat (3×)')
    expect(wrapper.findAll('li.row')).toHaveLength(3)   // noch nichts hinzugefügt

    await wrapper.find('.warning button.primary').trigger('click')
    await flushPromises()

    expect(createListItem).toHaveBeenLastCalledWith({ productName: 'Spaghetti', quantity: 1, force: true })
    expect(wrapper.findAll('li.row')).toHaveLength(4)
    expect(wrapper.find('.warning').exists()).toBe(false)
  })

  it('zeigt eine Fehlermeldung, wenn das Laden scheitert', async () => {
    vi.mocked(fetchListItems).mockRejectedValue(new Error('Netzwerk'))

    const wrapper = await mountView()

    expect(wrapper.find('[role="alert"]').text()).toContain('konnte nicht geladen werden')
  })
})
