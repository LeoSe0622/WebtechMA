# CLAUDE.md

Semesterprojekt „Korbgeld“ im Modul Web-Technologien (HTW Berlin). Das vollständige Ziel steht in `AUFTRAG.md`. Bei Widersprüchen gilt AUFTRAG.md.

## Team und Arbeitsweise

- Zweierteam, 3. Semester Wirtschaftsinformatik. Java aus Programmieren 1 und 2 ist bekannt. Spring Boot als Anwendung und Vue.js sind neu.
- Das Team muss jede Zeile erklären können. Lesbarer, konventioneller Code statt cleverer Abstraktion.
- Doku, Konfiguration, Git-Commits und alle übrigen Bauarbeiten erledigt Claude selbstständig und hält sie in `docs/lernen/LOGBUCH.md` fest.

## Einbindung des Teams (E5, angepasst durch E7)

Das Team wird nur an zwei Stellen beteiligt, sonst arbeitet Claude selbstständig:

1. **Verbindung von Frontend und Backend.** Dazu gehört alles, was die beiden Seiten verbindet: API-Client im Frontend (`fetch`, `VITE_API_BASE_URL`), CORS im Backend, der erste Aufruf eines Endpunkts aus Vue, Lade- und Fehlerzustände, ab Phase 2 das JWT im `Authorization`-Header und das Weiterleiten von 401/404/501. Hier gilt:
   - Vor dem Bauen den Weg Browser → Vue → HTTP → Controller → Service → Repository → JPA/Hibernate → SQL → Postgres erklären.
   - Die tragenden Stellen schreibt das Team selbst (`TODO(human)`).
   - Danach einen Request gemeinsam verfolgen: Browser-DevTools (Netzwerk), Backend-Log, SQL-Log, Tabelle.
2. **Jeder Milestone (M1, M2, M3, M4).** Ist ein Milestone erreicht, hält Claude an und führt eine Milestone-Abnahme durch: zeigen, was entstanden ist und wie es zusammenhängt, das Team startet und prüft es selbst, offene Punkte nennen. Erst nach dem „weiter“ des Teams geht es weiter.

**So sieht „beteiligt“ aus (Festlegung des Teams):** Claude schreibt ein `TODO(human)` direkt in die Datei und legt darin genau fest, **was** zu tun ist und **wie** es geht: Ziel, betroffene Datei und Stelle, nötige Klassen und Annotationen bzw. Syntax, Schritt-für-Schritt-Anleitung, erwartetes Ergebnis und wie man es prüft. Im Chat steht dieselbe Anleitung mit Erklärung. Das Team setzt sie um, danach prüft Claude das Ergebnis (Test, Build, echte Datenbank) und gibt Rückmeldung.

Alles andere (Entities, Services, Tests, Migrationen, Layout, CI, Dockerfile, Doku) baut Claude autonom, ohne `TODO(human)`. Es wird gründlich geprüft (Tests, Build, Gegenprobe) und kurz im LOGBUCH erklärt, damit Prompt 3 darauf aufbauen kann. Datenbankzugriffe werden weiterhin mit SQL-Log nachvollziehbar gemacht (`docs/lernen/DATENBANK.md`).

**Offen:** Ob der Team-Ausbau zu M4 (AUFTRAG.md, Abschnitt 18) weiter vom Team selbst geschrieben wird, entscheidet das Team spätestens bei der Abnahme von M2.

## Arbeitsregeln (Kurzform von AUFTRAG.md, Abschnitt 2)

1. Phasen in der Reihenfolge aus AUFTRAG.md, Abschnitt 16.
2. `.env` niemals committen. Keine Zugangsdaten im Repo.
3. Kleine Commits: `feat(bereich): …`, `fix(…)`, `test(…)`, `chore(…)`, `docs(…)`. Im Text ein bis zwei Sätze zum Warum.
4. Nur bauen, was im Auftrag steht. Eigene Ideen gehören nach `docs/IDEEN.md`.
5. Annahmen und Entscheidungen kommen in `docs/ENTSCHEIDUNGEN.md` (Datum, Entscheidung, Grund, verworfene Alternative).
6. Versionen nachschlagen, nicht aus dem Gedächtnis. Fest vorgegeben: Java 25, Gradle 9.x, Node 26.x.
7. Code, Bezeichner und Endpunkte auf Englisch. UI-Texte auf Deutsch in du-Form.
8. Geld im Backend immer `BigDecimal`, nie `double`.
9. Tests rufen nie echte externe APIs auf.
10. Keine fremden Logos.
11. Nicht selbst deployen.
12. Nach Phase 0, Phase 1, Phase 3 und am Ende prüft der Subagent `prof-kritiker`. Berichte kommen nach `docs/reviews/`.
13. **Kostenregel:** nur dauerhaft kostenlose Dienste, ohne Testphase und ohne Kreditkarte (AUFTRAG.md, Abschnitt 3).
14. Nach jeder Phase oder Etappe `docs/lernen/LOGBUCH.md` und `docs/lernen/GLOSSAR.md` ergänzen.
15. Team einbinden (siehe nächster Abschnitt).

## Datenbank

- Entwicklung: lokal mit Postgres in Docker (`docker compose up -d`).
- Produktion (ab M3): Neon Free, Branch `main`, über den direkten Host ohne `-pooler`.
- Keine Render-Datenbank, keine Oracle Cloud (siehe `docs/ENTSCHEIDUNGEN.md`).

## Repo-Struktur

```
AUFTRAG.md, CLAUDE.md, README.md
docker-compose.yml, .env.example, .gitignore
.claude/agents/prof-kritiker.md
.github/workflows/      backend.yml, frontend.yml
docs/                   ENTSCHEIDUNGEN.md, IDEEN.md, STATUS.md, DEPLOY.md, reviews/, lernen/
backend/                Spring Boot, Gradle, Paket de.htwberlin.webtech.korbgeld
frontend/               Vue 3, TypeScript, Vite
```

## Befehle

Sie funktionieren, sobald die jeweiligen Teile angelegt sind.

| Zweck | Befehl |
|---|---|
| Datenbank starten | `docker compose up -d` |
| Backend starten | `cd backend; ./gradlew bootRun` |
| Backend testen | `cd backend; ./gradlew test` |
| Frontend starten | `cd frontend; npm run dev` |
| Frontend testen | `cd frontend; npm run test:unit -- --run` |
| Frontend linten | `cd frontend; npm run lint` |

## Wichtig!
Bevor du irgendetwas committest oder als erledigt oder fertig anerkennst, überprüfe genau, ob es wirklich stimmt und alles korrekt ist!
