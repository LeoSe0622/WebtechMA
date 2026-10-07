import { describe, it, expect, vi, beforeEach } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import ShoppingListView from '../ShoppingListView.vue'
import { fetchListItems } from '@/api/listItems'
import type { ListItem } from '@/types/listItem'

// Attrappe statt echtem Backend-Aufruf (wie @MockitoBean im Backend)
vi.mock('@/api/listItems', () => ({ fetchListItems: vi.fn<() => Promise<ListItem[]>>() }))

const items: ListItem[] = [
  { id: 1, productId: 1, productName: 'Hafermilch', quantity: 2, checked: false, createdAt: '2026-10-07T10:00:00Z' },
  { id: 2, productId: 2, productName: 'Vollkornbrot', quantity: 1, checked: false, createdAt: '2026-10-07T10:00:00Z' },
  { id: 3, productId: 3, productName: 'Äpfel', quantity: 6, checked: true, createdAt: '2026-10-07T10:00:00Z' },
]

describe('ShoppingListView', () => {
  beforeEach(() => {
    vi.mocked(fetchListItems).mockReset()
  })

  it('zeigt während des Ladens einen Hinweis', () => {
    vi.mocked(fetchListItems).mockReturnValue(new Promise(() => {}))

    const wrapper = mount(ShoppingListView)

    expect(wrapper.text()).toContain('Liste wird geladen')
    expect(wrapper.findAll('li')).toHaveLength(0)
  })

  it('rendert die geladenen Einträge per v-for als eigene Zeilen', async () => {
    vi.mocked(fetchListItems).mockResolvedValue(items)

    const wrapper = mount(ShoppingListView)
    await flushPromises()

    const rows = wrapper.findAll('li')
    expect(rows).toHaveLength(3)
    expect(rows[0]!.text()).toContain('Hafermilch')
    expect(rows[2]!.text()).toContain('Äpfel')
  })

  it('zählt offene Einträge und aktualisiert die Zahl nach dem Abhaken', async () => {
    vi.mocked(fetchListItems).mockResolvedValue(structuredClone(items))

    const wrapper = mount(ShoppingListView)
    await flushPromises()
    expect(wrapper.find('.summary').text()).toBe('2 von 3 noch offen')

    await wrapper.findAll('input[type="checkbox"]')[0]!.trigger('change')

    expect(wrapper.find('.summary').text()).toBe('1 von 3 noch offen')
  })

  it('zeigt eine Fehlermeldung, wenn das Laden scheitert', async () => {
    vi.mocked(fetchListItems).mockRejectedValue(new Error('HTTP 500'))

    const wrapper = mount(ShoppingListView)
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('konnte nicht geladen werden')
    expect(wrapper.findAll('li')).toHaveLength(0)
  })

  it('zeigt einen Hinweis bei leerer Liste', async () => {
    vi.mocked(fetchListItems).mockResolvedValue([])

    const wrapper = mount(ShoppingListView)
    await flushPromises()

    expect(wrapper.text()).toContain('Deine Liste ist leer.')
  })
})
