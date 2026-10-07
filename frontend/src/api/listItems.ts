import type { ListItem } from '@/types/listItem'
import { apiRequest } from './client'

export interface PantryHint {
  quantity: number
  bestBefore: string | null
}

export interface CreateListItemRequest {
  productId?: number
  productName?: string
  quantity: number
  force?: boolean
}

// Entweder item (angelegt) oder alreadyInPantry (Warnung, noch nichts angelegt)
export interface CreateListItemResponse {
  item: ListItem | null
  alreadyInPantry: PantryHint | null
}

export function fetchListItems(): Promise<ListItem[]> {
  return apiRequest<ListItem[]>('/api/list-items')
}

export function createListItem(request: CreateListItemRequest): Promise<CreateListItemResponse> {
  return apiRequest<CreateListItemResponse>('/api/list-items', { method: 'POST', body: request })
}

export function updateListItem(id: number, changes: { checked?: boolean; quantity?: number }): Promise<ListItem> {
  return apiRequest<ListItem>(`/api/list-items/${id}`, { method: 'PATCH', body: changes })
}

export function deleteListItem(id: number): Promise<void> {
  return apiRequest<void>(`/api/list-items/${id}`, { method: 'DELETE' })
}
