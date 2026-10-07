import { describe, it, expect, vi, beforeEach } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import LeaderboardView from '../LeaderboardView.vue'
import SavingsPlanView from '../SavingsPlanView.vue'
import HabitsView from '../HabitsView.vue'
import { fetchHabits, fetchLeaderboard, fetchPortfolios, type Habit, type Leaderboard, type Portfolio } from '@/api/demoAreas'

vi.mock('@/api/demoAreas', () => ({
  fetchHabits: vi.fn<() => Promise<Habit[]>>(),
  fetchLeaderboard: vi.fn<() => Promise<Leaderboard>>(),
  fetchPortfolios: vi.fn<() => Promise<Portfolio[]>>(),
}))

describe('Demo-Bereiche', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('Rangliste zeigt Einträge und „Dein Platz wäre“, der Opt-in-Button ist deaktiviert', async () => {
    vi.mocked(fetchLeaderboard).mockResolvedValue({
      month: '2026-09',
      entries: [
        { rank: 1, displayName: 'Sparfuchs Kreuzberg', householdSize: 1, savingsRate: 0.28, streak: 6 },
        { rank: 2, displayName: 'Pfandheld', householdSize: 2, savingsRate: 0.2, streak: 3 },
      ],
      yourPosition: 2,
      yourSavingsRate: 0.2,
      yourStreak: 4,
    })
    const wrapper = mount(LeaderboardView)
    await flushPromises()

    expect(wrapper.findAll('tbody tr')).toHaveLength(2)
    expect(wrapper.find('tbody tr').text()).toContain('28 %')
    expect(wrapper.find('.mine').text()).toContain('Dein Platz wäre: 2')
    const optIn = wrapper.find('button[disabled]')
    expect(optIn.attributes('title')).toBe('Kommt mit Milestone M4')
    expect(wrapper.text()).toContain('Demo')
  })

  it('Sparplan zeigt den Pflichthinweis wörtlich und die Musterportfolios', async () => {
    vi.mocked(fetchPortfolios).mockResolvedValue([
      { id: 'AUSGEWOGEN', label: 'Ausgewogen', weights: [
        { symbol: 'ACWI', name: 'Aktien weltweit', share: 0.6 },
        { symbol: 'AGG', name: 'Anleihen', share: 0.4 },
      ] },
    ])
    const wrapper = mount(SavingsPlanView)
    await flushPromises()

    expect(wrapper.find('.disclaimer').text().replace(/\s+/g, ' ')).toBe(
      'Keine Anlageberatung. Musterportfolios dienen zum Lernen. Kursdaten von US-gelisteten ETFs, Währungseffekte vereinfacht.',
    )
    expect(wrapper.text()).toContain('60 % Aktien weltweit (ACWI)')
    expect(wrapper.findAll('button[disabled]')).toHaveLength(2)
  })

  it('Gewohnheiten zeigen Intervall und deaktivierten Anlegen-Button', async () => {
    vi.mocked(fetchHabits).mockResolvedValue([
      { id: 1, productId: 1, productName: 'Hafermilch', quantity: 2, intervalDays: 7, nextDue: '2026-10-09' },
    ])
    const wrapper = mount(HabitsView)
    await flushPromises()

    expect(wrapper.text()).toContain('2× Hafermilch')
    expect(wrapper.text()).toContain('jede Woche')
    expect(wrapper.find('button[disabled]').attributes('title')).toBe('Kommt mit Milestone M4')
  })
})
