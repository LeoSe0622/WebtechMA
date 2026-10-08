# Status

Stand: 08.10.2026, Ende von Prompt 2 (Phasen 0–5). Grundlage: AUFTRAG.md v1.6.

## Use Cases

| Use Case | Status | Route | Zugehörige Tests | Screenshot |
|---|---|---|---|---|
| Login, Registrierung, Demo-Login | **echt** | `/` | `AuthIntegrationTest`, `DemoLimitIntegrationTest`, `SandboxCleanupIntegrationTest`, `StartView.spec.ts`, `client.spec.ts` | `start-390.png`, `start-1280.png` |
| UC1 Monatsbudget anlegen und ansehen | **echt** | `/budget` | `BudgetServiceTest`, `CoreChainIntegrationTest`, `BudgetAndCheckout.spec.ts` | `budget-*.png` |
| UC2 Einkaufsliste mit Barcode, Vorrats-Warnung, abhaken | **echt** | `/liste` | `ListItemControllerTest`, `ListItemIntegrationTest`, `CoreChainIntegrationTest`, `CatalogAndSeparationIntegrationTest`, `OpenFoodFactsClientTest`, `ShoppingListView.spec.ts`, `ReceiptStrip.spec.ts` | `liste-*.png`, `liste-warnung-*.png` (Vorrats-Warnung) |
| UC4 Einkauf abschließen | **echt** | `/liste/abschliessen` | `PurchaseServiceTest`, `CoreChainIntegrationTest`, `BudgetAndCheckout.spec.ts` | `liste-abschliessen-*.png` (Formular), `liste-abschliessen-erfolg-*.png` (neues Restbudget) |
| UC5 Vorrat, Menge ändern, verbrauchen, Ablauf | **echt** | `/vorrat` | `PantryServiceTest`, `CatalogAndSeparationIntegrationTest`, `PantryView.spec.ts` | `vorrat-*.png` |
| Dashboard (Restbudget, Sparquote, Ablauf-Warnungen) | **echt** | `/dashboard` | `CoreChainIntegrationTest` (Summary), `ReceiptStrip.spec.ts` | `dashboard-*.png` |
| UC3 Gewohnheiten | Demo (lesend) | `/gewohnheiten` | `DemoAreasIntegrationTest`, `DemoAreas.spec.ts` | `gewohnheiten-*.png` |
| UC6 Rangliste | Demo (lesend) | `/rangliste` | `RankingTest`, `DemoAreasIntegrationTest`, `DemoAreas.spec.ts` | `rangliste-*.png` |
| UC7 Sparplan | Demo (lesend) | `/sparplan` | `DemoAreasIntegrationTest`, `DemoAreas.spec.ts` | `sparplan-*.png` |
| Rezepte | in Arbeit (501) | `/rezepte` | `AuthIntegrationTest` (501), `router.spec.ts` | `rezepte-*.png` |
| Preisvergleich | in Arbeit (501) | `/preise` | `router.spec.ts` | `preise-*.png` |
| Profil | in Arbeit (501) | `/profil` | `router.spec.ts` | `profil-*.png` |
| Unbekannte Pfade | 404-Seite | z. B. `/gibt-es-nicht` | `AuthIntegrationTest` (404), `router.spec.ts` | `404-*.png` |

Alle Screenshots liegen in `docs/screenshots/`, jeweils bei 390 px (Handy) und 1280 px (Desktop), eingeloggt als Demo-Nutzer, aufgenommen vom Produktions-Build.

**Tests** (Stand 08.10.2026): Backend 41 (Unit, Web, Integration mit Testcontainers gegen Postgres 17), Frontend 36 (Vitest). Beide laufen in GitHub Actions.

**Echte Use Cases:** 4 (UC1, UC2, UC4, UC5) plus Dashboard und Login. Für die Bestnote fehlen noch drei. Sie entstehen mit dem Ausbau zu M4 (AUFTRAG.md, Abschnitt 18: UC3, UC6, UC7 schreibend, dazu Profil als Reserve).

## Was nicht geschafft wurde und warum

- **Schreibende Demo-Bereiche (UC3, UC6, UC7) und Profil:** bewusst nicht in Prompt 2. Sie sind für den Ausbau zu M4 geplant (E2, E9).
- **Sparplan-Rechner, Risikoprofil, Diagramm, Twelve Data:** ebenfalls Ausbau zu M4. Die Kurse in `backend/src/main/resources/seed/prices.csv` sind **synthetisch** und im Dateikopf so gekennzeichnet.
- **Deployment:** Laut Auftrag deployt Claude nicht selbst. `docs/DEPLOY.md` beschreibt das manuelle Anlegen bei Neon und Render. Ein Probe-Deploy steht bis 15.11. aus (E3).
- **Assignees auf der In-Arbeit-Seite:** Die GitHub-Namen in AUFTRAG.md, Abschnitt 0 sind nicht eingetragen. Es erscheinen die Initialen A und B (`frontend/src/config.ts`, `ASSIGNEES`).

## Bekannte Schwächen und Annahmen

Alle Annahmen stehen mit Begründung in `docs/ENTSCHEIDUNGEN.md` (E1–E20). Die wichtigsten Schwächen:

- **Tageslimit für Demo-Logins** (100 pro Tag, global): Wer den Endpunkt oft aufruft, sperrt die Demo bis Mitternacht, auch für Prüfer. Vor einer Demo frühzeitig einloggen (Review 02, m12, Idee in `docs/IDEEN.md`).
- **Login:** keine Begrenzung von Fehlversuchen, und die Antwortzeit kann verraten, ob ein Benutzername existiert (Review 02, m16).
- **Budget-Obergrenze im Frontend** nur als Hinweistext (500 € pro Person fest eingetragen). Maßgeblich ist das Backend, das mit 409 antwortet (E19).
- **Abhaken auf der Liste** wird sofort gespeichert. Einen Einkauf rückgängig zu machen ist nicht vorgesehen.
- **Kalender-Grenzen:** Alle Monatsrechnungen laufen in deutscher Zeit (`Europe/Berlin`), auch wenn der Server in UTC läuft.
- **Rangliste:** rund 200 kleine SQL-Abfragen pro Aufruf (eine pro Nutzer und Monat). Bei 15 Seed-Nutzern unkritisch, vor echten Nutzern zu M4 ersetzen (E21).
- **Alle Commits von einem Konto:** Bei einem Zweierteam fragt die Prüfung, wer was gebaut hat. Empfehlung: Die zweite Person committet ihre Teile beim Ausbau zu M4 selbst.
- **Kostenlose Render-Dienste schlafen** nach 15 Minuten ein. Der erste Aufruf dauert dann bis zu einer Minute.
- **Open Food Facts** kennt nicht jeden Barcode. Unbekannte Barcodes ergeben 404, dann bietet die Oberfläche die manuelle Eingabe an.

**Abnahme:** Das Team hat den Stand nach Prompt 2 am 08.10.2026 lokal geprüft und abgenommen.

## Nächste Schritte entlang der Milestones

| Milestone | Datum | Inhalt |
|---|---|---|
| M2 | 8. Nov. | erreicht (Vue-Liste per `v-for`, vom Team abgenommen) |
| M3 | 22. Nov. | Deployment nach `docs/DEPLOY.md`. Probelauf bis 15.11., Speicher prüfen, Smoke-Test |
| M4 | 13. Dez. | Ausbau nach AUFTRAG.md, Abschnitt 18: Risikoprofil und Sparplan-Rechner mit Diagramm, Gewohnheiten anlegen, Ranglisten-Opt-in, Profil. Danach 7+ echte Use Cases |
| Abgabe | 17. Jan., 23:59 Uhr | Screenshot-Dokumentation pro Use Case, Prompt 3 (Code-Tour, Prüfungsfragen, Spickzettel) |

## Fragen, die ich in der Demo stellen würde

Aus dem letzten Kritiker-Bericht (`docs/reviews/03-final.md`):

1. `SandboxService.deleteExpiredSandboxUsers` löscht mit einem einzigen `DELETE` einen Demo-Nutzer, der ein eigenes Produkt „Hafermilch Bio“ eingetippt und gekauft hat. Welche Zeilen in welchen Tabellen verschwinden, und warum blockiert der Fremdschlüssel von `purchase_line.product_id` auf `product` das Löschen nicht, obwohl er kein `ON DELETE CASCADE` hat?
2. Wie viele SQL-Abfragen löst ein `GET /api/leaderboard` aus? Zeigt im Code, woher sie kommen, und skizziert, wie ihr sie auf eine Handvoll reduziert. Warum filtert ihr Sandbox-Nutzer an zwei Stellen, und welche davon wirkt in Produktion? _(Seit E21 nur noch an einer Stelle: `Ranking.rank`.)_
3. Auf `/liste` tippt der Prüfer „Hafermilch“, wählt den Vorschlag und klickt dann „Trotzdem hinzufügen“. Verfolgt den Weg: Welcher Watcher-Zweig verhindert, dass die Auswahl verloren geht, was steht in der Closure `retry`, welcher Request geht mit welchem Body raus, und warum antwortet das Backend beim ersten Mal mit 200 und beim zweiten Mal mit 201?
