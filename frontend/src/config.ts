// Der App-Name steht im Frontend nur hier (AUFTRAG.md, Abschnitt 0)
export const APP_NAME = 'Korbgeld'

// Ziel nach dem Login und für „Zurück zum Dashboard“
export const HOME_PATH = '/dashboard'

// Assignees auf der In-Arbeit-Seite. Tragt eure GitHub-Namen ein (AUFTRAG.md, Abschnitt 0).
// Ohne Namen erscheint ein Kreis mit der Initiale.
export const ASSIGNEES: { login: string | null; initial: string }[] = [
  { login: null, initial: 'A' },
  { login: null, initial: 'B' },
]

// Anzeige von Geldbeträgen in Euro (AUFTRAG.md, Abschnitt 2, Regel 8)
const euroFormat = new Intl.NumberFormat('de-DE', { style: 'currency', currency: 'EUR' })

export function formatEuro(amount: number | string): string {
  return euroFormat.format(Number(amount))
}
