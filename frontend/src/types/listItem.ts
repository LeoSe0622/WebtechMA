// Gleiche Felder wie das Backend-DTO ListItemResponse (JSON von GET /api/list-items)
export interface ListItem {
  id: number
  productId: number
  productName: string
  quantity: number
  checked: boolean
  createdAt: string
}
