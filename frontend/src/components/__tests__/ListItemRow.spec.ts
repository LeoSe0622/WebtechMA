import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ListItemRow from '../ListItemRow.vue'
import type { ListItem } from '@/types/listItem'

const milk: ListItem = {
  id: 7,
  productId: 1,
  productName: 'Hafermilch',
  quantity: 2,
  checked: false,
  createdAt: '2026-10-07T10:00:00Z',
}

describe('ListItemRow', () => {
  it('zeigt Produktname und Menge', () => {
    const wrapper = mount(ListItemRow, { props: { item: milk } })

    expect(wrapper.text()).toContain('Hafermilch')
    expect(wrapper.text()).toContain('2×')
  })

  it('meldet beim Anklicken toggle mit der id', async () => {
    const wrapper = mount(ListItemRow, { props: { item: milk } })

    await wrapper.find('input[type="checkbox"]').trigger('change')

    expect(wrapper.emitted('toggle')).toEqual([[7]])
  })

  it('markiert abgehakte Einträge', () => {
    const wrapper = mount(ListItemRow, { props: { item: { ...milk, checked: true } } })

    expect(wrapper.find('li').classes()).toContain('done')
    expect((wrapper.find('input').element as HTMLInputElement).checked).toBe(true)
  })
})
