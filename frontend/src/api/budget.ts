import { apiRequest } from './client'

export interface MonthResult {
  yearMonth: string
  amount: number
  spent: number
  savingsRate: number
}

export interface BudgetSummary {
  yearMonth: string
  amount: number | null
  spent: number
  remaining: number | null
  locked: boolean
  lastCompleted: MonthResult | null
}

export interface Budget {
  yearMonth: string
  amount: number
  locked: boolean
}

export function fetchBudgetSummary(): Promise<BudgetSummary> {
  return apiRequest<BudgetSummary>('/api/budgets/current/summary')
}

export function fetchBudget(yearMonth: string): Promise<Budget> {
  return apiRequest<Budget>(`/api/budgets/${yearMonth}`)
}

export function saveBudget(yearMonth: string, amount: number): Promise<Budget> {
  return apiRequest<Budget>(`/api/budgets/${yearMonth}`, { method: 'PUT', body: { amount } })
}
