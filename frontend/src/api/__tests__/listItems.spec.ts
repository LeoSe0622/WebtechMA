import { describe, it, expect, vi, afterEach } from 'vitest'
import { fetchListItems } from '../listItems'

describe('fetchListItems', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('ruft GET /api/list-items auf und gibt die Einträge zurück', async () => {
    const body = [{ id: 1, productId: 1, productName: 'Hafermilch', quantity: 2, checked: false, createdAt: '2026-10-07T10:00:00Z' }]
    const fetchMock = vi.fn<typeof fetch>().mockResolvedValue(new Response(JSON.stringify(body), { status: 200 }))
    vi.stubGlobal('fetch', fetchMock)

    const result = await fetchListItems()

    expect(fetchMock).toHaveBeenCalledWith(expect.stringMatching(/\/api\/list-items$/))
    expect(result).toEqual(body)
  })

  it('wirft einen Fehler, wenn das Backend keinen Erfolg meldet', async () => {
    vi.stubGlobal('fetch', vi.fn<typeof fetch>().mockResolvedValue(new Response('', { status: 500 })))

    await expect(fetchListItems()).rejects.toThrow('HTTP 500')
  })
})
