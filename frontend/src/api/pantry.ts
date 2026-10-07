import { apiRequest } from './client'

export type ExpiryStatus = 'EXPIRED' | 'EXPIRING_SOON' | 'OK' | 'NO_DATE'

export interface PantryItem {
  id: number
  productId: number
  productName: string
  quantity: number
  bestBefore: string | null
  expiryStatus: ExpiryStatus
}

export function fetchPantry(): Promise<PantryItem[]> {
  return apiRequest<PantryItem[]>('/api/pantry-items')
}

export function updatePantryItem(
  id: number,
  changes: { quantity?: number; bestBefore?: string; clearBestBefore?: boolean },
): Promise<PantryItem> {
  return apiRequest<PantryItem>(`/api/pantry-items/${id}`, { method: 'PATCH', body: changes })
}

// undefined, wenn der Eintrag aufgebraucht und gelöscht wurde (HTTP 204)
export function consumePantryItem(id: number, amount = 1): Promise<PantryItem | undefined> {
  return apiRequest<PantryItem | undefined>(`/api/pantry-items/${id}/consume`, { method: 'POST', body: { amount } })
}
