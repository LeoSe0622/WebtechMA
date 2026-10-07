import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ShoppingListView from '../ShoppingListView.vue'

describe('ShoppingListView', () => {
  it('rendert jeden Eintrag per v-for als eigene Zeile', () => {
    const wrapper = mount(ShoppingListView)

    const rows = wrapper.findAll('li')
    expect(rows).toHaveLength(3)
    expect(rows[0]!.text()).toContain('Hafermilch')
    expect(rows[2]!.text()).toContain('Äpfel')
  })

  it('zählt offene Einträge und aktualisiert die Zahl nach dem Abhaken', async () => {
    const wrapper = mount(ShoppingListView)
    expect(wrapper.find('.summary').text()).toBe('2 von 3 noch offen')

    await wrapper.findAll('input[type="checkbox"]')[0]!.trigger('change')

    expect(wrapper.find('.summary').text()).toBe('1 von 3 noch offen')
  })
})
