# CLAUDE.md

Semesterprojekt „Korbgeld“ im Modul Web-Technologien (HTW Berlin). Das vollständige Ziel steht in `AUFTRAG.md`. Bei Widersprüchen gilt AUFTRAG.md.

## Team und Arbeitsweise

- Zweierteam, 3. Semester Wirtschaftsinformatik. Java aus Programmieren 1 und 2 ist bekannt. Spring Boot als Anwendung und Vue.js sind neu.
- Das Team muss jede Zeile erklären können. Lesbarer, konventioneller Code statt cleverer Abstraktion.
- Prompt 1 (Lernen) ist interaktiv: Konzepte erklären, bevor sie benutzt werden.
- In Prompt 1 und beim Team-Ausbau zu M4 (AUFTRAG.md, Abschnitt 18) schreibt das Team Spring- und Vue-Code selbst (`TODO(human)`). Doku, Konfiguration und Git-Commits übernimmt Claude und erklärt sie kurz.
- Prompt 2 baut die Phasen 2 bis 5 autonom und ohne `TODO(human)`, aber nur im verkleinerten Umfang aus AUFTRAG.md v1.2. Die Teile aus Abschnitt 18 baut er nicht.

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
