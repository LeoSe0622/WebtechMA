import type { ListItem } from '@/types/listItem'
import { apiRequest } from './client'

export function fetchListItems(): Promise<ListItem[]> {
  return apiRequest<ListItem[]>('/api/list-items')
}
