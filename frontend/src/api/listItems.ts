import type { ListItem } from '@/types/listItem'

// Adresse des Backends aus der .env (lokal http://localhost:8080, später die Render-Adresse)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

export async function fetchListItems(): Promise<ListItem[]> {
  const response = await fetch(`${API_BASE_URL}/api/list-items`)
  // fetch wirft bei 404 oder 500 keinen Fehler, deshalb selbst prüfen
  if (!response.ok) {
    throw new Error(`Liste konnte nicht geladen werden (HTTP ${response.status})`)
  }
  return (await response.json()) as ListItem[]
}
