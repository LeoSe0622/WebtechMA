import { useAuthStore } from '@/stores/auth'
import { useUiStore } from '@/stores/ui'
import router from '@/router'

// Fehlerformat des Backends (ProblemDetail, RFC 9457)
export interface ProblemDetail {
  status: number
  title?: string
  detail?: string
  path?: string
  feature?: string
  milestone?: string
  errors?: { field: string; message: string }[]
}

/** Typisierter Fehler: Status und, falls vorhanden, das ProblemDetail des Backends. */
export class ApiError extends Error {
  constructor(
    public readonly status: number,
    public readonly problem: ProblemDetail | null,
  ) {
    super(problem?.detail ?? `Anfrage fehlgeschlagen (HTTP ${status})`)
    this.name = 'ApiError'
  }
}

interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'
  body?: unknown
}

/**
 * Zentrale Stelle für alle Backend-Aufrufe: setzt das Token, wandelt JSON um und wirft bei
 * Fehlern einen ApiError. Bei 401 ist das Token ungültig oder abgelaufen, dann geht es zum Login.
 */
export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const baseUrl = import.meta.env.VITE_API_BASE_URL
  if (!baseUrl) {
    throw new Error('VITE_API_BASE_URL fehlt. Bitte in der .env (lokal) bzw. auf Render setzen.')
  }

  const auth = useAuthStore()
  const headers: Record<string, string> = { Accept: 'application/json' }
  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }
  if (auth.token) {
    headers['Authorization'] = `Bearer ${auth.token}`
  }

  const response = await fetch(`${baseUrl}${path}`, {
    method: options.method ?? 'GET',
    headers,
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
  })

  if (!response.ok) {
    const problem = await readProblem(response)
    if (response.status === 401 && auth.token) {
      auth.logout()
      await router.push({ name: 'start', query: { redirect: router.currentRoute.value.fullPath } })
    }
    throw new ApiError(response.status, problem)
  }
  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}

async function readProblem(response: Response): Promise<ProblemDetail | null> {
  try {
    return (await response.json()) as ProblemDetail
  } catch {
    return null   // keine JSON-Antwort, z. B. leerer Body
  }
}

/**
 * Für das Laden ganzer Seiten: 404 zeigt die 404-Ansicht, 501 die In-Arbeit-Ansicht, die URL bleibt.
 * Andere Fehler gehen an die Seite zurück, damit sie eine passende Meldung zeigt.
 */
export async function loadPage<T>(load: () => Promise<T>): Promise<T | undefined> {
  try {
    return await load()
  } catch (e) {
    if (e instanceof ApiError && e.status === 404) {
      useUiStore().showNotFound()
      return undefined
    }
    if (e instanceof ApiError && e.status === 501) {
      useUiStore().showWorkInProgress(e.problem?.feature ?? 'Diese Funktion', e.problem?.milestone ?? '')
      return undefined
    }
    throw e
  }
}
