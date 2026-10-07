import { describe, it, expect } from 'vitest'
import { parseEuro } from '../config'

describe('parseEuro', () => {
  it('liest deutsche und englische Schreibweisen', () => {
    expect(parseEuro('12,34')).toBe(12.34)
    expect(parseEuro('12.34')).toBe(12.34)
    expect(parseEuro('260')).toBe(260)
    expect(parseEuro('23,45 €')).toBe(23.45)
  })

  it('versteht Punkte als Tausendertrenner', () => {
    expect(parseEuro('1.000')).toBe(1000)
    expect(parseEuro('1.000,50')).toBe(1000.5)
  })

  it('ergibt NaN für ungültige Eingaben', () => {
    expect(parseEuro('')).toBeNaN()
    expect(parseEuro('zwölf')).toBeNaN()
  })
})
