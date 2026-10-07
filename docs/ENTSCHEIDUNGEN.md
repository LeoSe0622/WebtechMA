# Entscheidungen

Format: Datum, Entscheidung, Grund, verworfene Alternative.

## E1: Datenbank für Entwicklung und Produktion

- **Datum:** 07.10.2026
- **Entscheidung:** Die Entwicklung läuft komplett lokal mit Postgres in Docker Compose. Die Produktion nutzt ab M3 Neon Free (Branch `main`, direkter Host ohne `-pooler`).
- **Grund:** Lokal funktioniert das offline, ist schnell und braucht kein Konto. Die App auf Render braucht aber eine Datenbank, die im Internet erreichbar ist. Neon ist dafür der einzige Weg, der dauerhaft kostenlos ist und ohne Kreditkarte auskommt (AUFTRAG.md, Abschnitt 3).
- **Verworfene Alternativen:**
  - Neon-Branch `dev-<name>` für die Entwicklung: nicht nötig, weil Docker auf dem Teamrechner läuft.
  - Oracle Cloud Always Free: verlangt bei der Anmeldung eine Kreditkarte (verstößt gegen die Kostenregel), und die Oracle-Datenbank ist kein Postgres (Modulvorgabe).
  - Render-Postgres: Die kostenlose Datenbank wird nach 30 Tagen gelöscht.
