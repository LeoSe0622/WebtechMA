import { apiRequest } from './client'

export interface Product {
  id: number
  name: string
  barcode: string | null
  category: string | null
  imageUrl: string | null
  nutriScore: string | null
  source: 'MANUAL' | 'OPEN_FOOD_FACTS'
}

export function searchProducts(query: string): Promise<Product[]> {
  return apiRequest<Product[]>(`/api/products?query=${encodeURIComponent(query)}`)
}

// Wirft ApiError mit 404, wenn der Barcode unbekannt ist; die Seite bietet dann die manuelle Eingabe an
export function findProductByBarcode(code: string): Promise<Product> {
  return apiRequest<Product>(`/api/products/barcode/${encodeURIComponent(code)}`)
}
