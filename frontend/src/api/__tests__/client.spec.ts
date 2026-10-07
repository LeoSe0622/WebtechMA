import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { ApiError, apiRequest, loadPage } from '../client'
import { useAuthStore } from '@/stores/auth'
import { useUiStore } from '@/stores/ui'

function jsonResponse(status: number, body: unknown): Response {
  return new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } })
}

describe('apiRequest', () => {
  beforeEach(() => {
    sessionStorage.clear()
    setActivePinia(createPinia())
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('schickt das Token im Authorization-Header mit', async () => {
    const fetchMock = vi.fn<typeof fetch>().mockResolvedValue(jsonResponse(200, []))
    vi.stubGlobal('fetch', fetchMock)
    useAuthStore().setSession({ token: 'abc', user: { id: 1, username: 'mia', displayName: 'Mia', householdSize: 1, leaderboardOptIn: false, sandbox: true } })

    await apiRequest('/api/list-items')

    const [url, init] = fetchMock.mock.calls[0]!
    expect(url).toBe('http://backend.test/api/list-items')
    expect((init!.headers as Record<string, string>)['Authorization']).toBe('Bearer abc')
  })

  it('wirft bei Fehlern einen ApiError mit Status und ProblemDetail', async () => {
    vi.stubGlobal('fetch', vi.fn<typeof fetch>().mockResolvedValue(jsonResponse(409, { status: 409, detail: 'Budget gesperrt' })))

    const error = await apiRequest('/api/budgets/2026-10', { method: 'PUT', body: { amount: 200 } }).catch((e: unknown) => e)

    expect(error).toBeInstanceOf(ApiError)
    expect((error as ApiError).status).toBe(409)
    expect((error as ApiError).message).toBe('Budget gesperrt')
  })

  it('meldet bei 401 ab', async () => {
    vi.stubGlobal('fetch', vi.fn<typeof fetch>().mockResolvedValue(jsonResponse(401, { status: 401 })))
    const auth = useAuthStore()
    auth.setSession({ token: 'abgelaufen', user: { id: 1, username: 'mia', displayName: 'Mia', householdSize: 1, leaderboardOptIn: false, sandbox: true } })

    await expect(apiRequest('/api/list-items')).rejects.toBeInstanceOf(ApiError)

    expect(auth.isAuthenticated).toBe(false)
    expect(sessionStorage.getItem('korbgeld.token')).toBeNull()
  })

  it('ein 404 bei einer Aktion (z. B. Barcode) leitet nicht auf die 404-Seite', async () => {
    vi.stubGlobal('fetch', vi.fn<typeof fetch>().mockResolvedValue(jsonResponse(404, { status: 404 })))

    await expect(apiRequest('/api/products/barcode/123')).rejects.toMatchObject({ status: 404 })

    expect(useUiStore().override).toBeNull()
  })
})

describe('loadPage', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('zeigt bei 404 die 404-Ansicht', async () => {
    await loadPage(() => Promise.reject(new ApiError(404, { status: 404 })))

    expect(useUiStore().override).toEqual({ kind: 'not-found' })
  })

  it('zeigt bei 501 die In-Arbeit-Ansicht mit Feature und Milestone', async () => {
    await loadPage(() => Promise.reject(new ApiError(501, { status: 501, feature: 'Rezepte', milestone: 'nach M4' })))

    expect(useUiStore().override).toEqual({ kind: 'wip', feature: 'Rezepte', milestone: 'nach M4' })
  })

  it('gibt andere Fehler an die Seite weiter', async () => {
    await expect(loadPage(() => Promise.reject(new ApiError(500, null)))).rejects.toMatchObject({ status: 500 })
    expect(useUiStore().override).toBeNull()
  })
})
