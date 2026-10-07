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

## E2: Arbeitsteilung zwischen Prompt 2 und Team (Review 00, M1 und M5)

- **Datum:** 07.10.2026
- **Entscheidung:** Prompt 2 baut Querschnitt und Kern-Kette (UC1, UC2, UC4, UC5) autonom, die Demo-Bereiche nur lesend. Rechner, Risikoprofil, Diagramm, Twelve Data und die schreibenden Teile von UC3, UC6, UC7 und Profil baut das Team bis M4 selbst (AUFTRAG.md, Abschnitt 18).
- **Grund:** Das Team muss in der Klausur jede Zeile erklären. Code, den es selbst schreibt, versteht es besser, und es entstehen echte Use Cases, die für die 1,0 zählen, statt reiner Demo-Ansichten.
- **Verworfene Alternativen:** Alles autonom durch Prompt 2 (zu viel fremder Code für die Klausur, Demo-Bereiche zählen nicht). Alles selbst schreiben (zu großer Zeitaufwand bis M3).

## E3: Kein Probe-Deploy vor M3 (Review 00, M7 zurückgestellt)

- **Datum:** 07.10.2026
- **Entscheidung:** Render und Neon werden erst zu M3 eingerichtet. Prompt 2 schreibt Dockerfile und `docs/DEPLOY.md`, deployt aber nicht.
- **Grund:** Das Team will bis M3 vollständig lokal arbeiten und keine weiteren Konten anlegen.
- **Risiko und Gegenmaßnahme:** Speicher- und Build-Probleme auf Render fallen spät auf. Deshalb das Deployment spätestens eine Woche vor M3 (bis 15.11.2026) ausprobieren. `MaxRAMPercentage` beim ersten Deploy prüfen und die Node-Version in DEPLOY.md festhalten.
- **Verworfene Alternative:** Probe-Deploy direkt nach Phase 1.

## E4: Übrige Review-Befunde aus Review 00

- **Datum:** 07.10.2026
- **Übernommen in AUFTRAG.md v1.2:** B1 (nur Docker lokal), M2 (Registrierungsfelder), M3 (Seeder füllt bei jedem Start auf), M4 und m12 (Sandbox-Bereinigung, Tageslimit 429).
- **Zurückgestellt:** M6 (Zielbedingung im Repo). Sie steht in `goal-befehl.txt`, die vor Prompt 2 bereitliegen muss. Die Minor-Befunde m2–m11 hat das Team nicht übernommen. Prompt 2 darf dazu begründete Annahmen treffen und sie hier festhalten.

## E5: Team wird in die Entwicklung eingebunden

- **Datum:** 07.10.2026
- **Entscheidung:** Beim Aufbau von Backend und Frontend wird das Team in jedem Prompt eingebunden (AUFTRAG.md Regel 15, CLAUDE.md „Einbindung des Teams“). Schwerpunkte: Datenbankanbindung, Datenbankaufrufe, Zusammenspiel von Frontend und Backend. Prompt 2 hält nach jeder Phase für eine Erklärung an.
- **Grund:** Das Team muss Demo und Papierklausur bestehen und will den Code nicht nur erklärt bekommen, sondern beim Entstehen verstehen.
- **Verworfene Alternative:** Prompt 2 komplett autonom mit `/goal` und Erklärung erst in Prompt 3. Das ist schneller, aber das Team sieht den Code dann erst, wenn er fertig ist.

## E6: Postgres 17 lokal und in Tests

- **Datum:** 07.10.2026
- **Entscheidung:** `docker-compose.yml` und `TestcontainersConfiguration` nutzen `postgres:17`, nicht `latest`.
- **Grund:** Lokal, in Tests und in der Produktion soll dieselbe Hauptversion laufen. Neon unterstützt Postgres 17. Die Seite mit Neons aktueller Standardversion war beim Anlegen wegen einer technischen Störung nicht abrufbar. **Annahme, bei der Neon-Einrichtung zu M3 prüfen:** Neon-Projekt mit Postgres 17 anlegen.
- **Verworfene Alternative:** `postgres:latest`. Die Version könnte unbemerkt springen, und lokal würde etwas anderes getestet als in der Produktion läuft.

## E7: Beteiligung des Teams eingegrenzt

- **Datum:** 07.10.2026
- **Entscheidung:** Das Team ist nur noch an der Verbindung von Frontend und Backend und an den Milestone-Abnahmen (M1–M4) beteiligt. Alles andere baut Claude autonom (AUFTRAG.md v1.4, Regel 15; CLAUDE.md „Einbindung des Teams“). Präzisiert E5.
- **Grund:** Wunsch des Teams. Die Zeit soll in die Stellen fließen, die das Zusammenspiel der Teile zeigen, und in die Abnahme der Ergebnisse.
- **Folge:** Im LOGBUCH werden die autonom gebauten Teile besonders gründlich erklärt, damit Prompt 3 sie für Demo und Klausur aufbereiten kann.
- **Offen:** Ob der Team-Ausbau zu M4 (AUFTRAG.md, Abschnitt 18) beim Team bleibt, wird spätestens bei der Abnahme von M2 entschieden.
- **Verworfene Alternative:** Beteiligung an allen Spring- und Vue-Teilen (E5 in der ursprünglichen Form).
