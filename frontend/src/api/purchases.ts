import { apiRequest } from './client'

export interface Store {
  id: number
  name: string
}

export interface Purchase {
  id: number
  storeId: number
  storeName: string
  date: string
  totalAmount: number
  lines: { productId: number; quantity: number }[]
}

export interface CompletePurchaseRequest {
  storeId?: number
  storeName?: string
  totalAmount: number
}

export interface CompletePurchaseResponse {
  purchase: Purchase
  remainingBudget: number | null
}

export function fetchStores(): Promise<Store[]> {
  return apiRequest<Store[]>('/api/stores')
}

export function completePurchase(request: CompletePurchaseRequest): Promise<CompletePurchaseResponse> {
  return apiRequest<CompletePurchaseResponse>('/api/purchases', { method: 'POST', body: request })
}
