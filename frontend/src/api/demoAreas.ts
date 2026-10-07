import { apiRequest } from './client'

// Lesende Endpunkte der Demo-Bereiche (Phase 4); Schreiben kommt zu M4

export interface Habit {
  id: number
  productId: number
  productName: string
  quantity: number
  intervalDays: number
  nextDue: string
}

export interface LeaderboardEntry {
  rank: number
  displayName: string
  householdSize: number
  savingsRate: number
  streak: number
}

export interface Leaderboard {
  month: string
  entries: LeaderboardEntry[]
  yourPosition: number | null
  yourSavingsRate: number | null
  yourStreak: number
}

export interface Portfolio {
  id: 'VORSICHTIG' | 'AUSGEWOGEN' | 'MUTIG'
  label: string
  weights: { symbol: string; name: string; share: number }[]
}

export function fetchHabits(): Promise<Habit[]> {
  return apiRequest<Habit[]>('/api/habits')
}

export function fetchLeaderboard(): Promise<Leaderboard> {
  return apiRequest<Leaderboard>('/api/leaderboard')
}

export function fetchPortfolios(): Promise<Portfolio[]> {
  return apiRequest<Portfolio[]>('/api/invest/portfolios')
}
