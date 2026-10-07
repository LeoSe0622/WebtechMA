# Logbuch

Was in welcher Etappe entstanden ist und welche Konzepte darin vorkommen. Prompt 3 baut darauf auf.

## Etappe 0: Das große Bild (07.10.2026)

- **Entstanden:** keine Dateien.
- **Konzepte:** Drei-Schichten-Architektur (Vue im Browser, Spring Boot, Postgres), HTTP und JSON als Sprache zwischen Frontend und Backend, Weg eines Klicks (Komponente → API-Client → Controller → Service → Repository → Datenbank und zurück), Rollen von GitHub (Code, CI), Render (Hosting) und Neon (Datenbank).
- **Entscheidung:** Entwickelt wird lokal mit Docker. Neon kommt erst zu M3 für die Produktion. Oracle Cloud ist verworfen (Kreditkarte, kein Postgres).

## Etappe 1: Werkzeuge und Konten (07.10.2026)

- **Entstanden:** keine Dateien. Geprüft: git 2.52, Java 25 (Oracle JDK), Node 26.10, npm 11.19, Docker 29.6 mit WSL 2.
- **Konzepte:** Docker-Client und Docker-Engine, Container, Testcontainers. Keine Render-Datenbank, weil sie nach 30 Tagen gelöscht wird.

## Etappe 2: Fundament und Prof-Kritiker (07.10.2026)

- **Entstanden:**
  - `.gitignore`: hält Geheimnisse (`.env`) und erzeugte Dateien aus dem Repo heraus.
  - `.claude/agents/prof-kritiker.md`: Subagent als unabhängiger Gutachter, wörtlich aus AUFTRAG.md, Anhang A.
  - `CLAUDE.md`: Projektregeln, die Claude Code bei jeder Session liest.
  - `AUFTRAG.md`: umbenannt von `Auftrag.md`, weil Linux-Server Groß- und Kleinschreibung unterscheiden.
  - `docs/lernen/LOGBUCH.md`, `docs/lernen/GLOSSAR.md`, `docs/ENTSCHEIDUNGEN.md`
- **Konzepte:** `.gitignore`, Staging-Bereich, Commit-Konventionen, gepushte Historie nicht umschreiben, Subagent mit eigenem Kontext, CLAUDE.md als Projektgedächtnis.
- **Git-Lektion:** Der erste Commit `1f57f1b first` enthält Kritiker, `.gitignore` und Auftrag zusammen. Er war schon gepusht und bleibt deshalb so.
- **Kritiker-Review:** `docs/reviews/00-auftrag.md`. Ein Blocker (AUFTRAG.md widersprach der Datenbank-Entscheidung), 7 Major-, 12 Minor-Befunde. Die Entscheidungen dazu stehen in `docs/ENTSCHEIDUNGEN.md` (E2–E4). AUFTRAG.md ist jetzt v1.2 mit neuem Abschnitt 18 (Team-Ausbau bis M4).
- **Wunsch des Teams (E5):** Es will Datenbankanbindung, Datenbankaufrufe und das Zusammenspiel von Frontend und Backend verstehen und beim Aufbau eingebunden werden. Festgehalten in AUFTRAG.md v1.3 (Regel 15) und CLAUDE.md („Einbindung des Teams“).
- **Lektion:** Eine Entscheidung muss überall nachgezogen werden, wo sie gilt. E1 stand nur in ENTSCHEIDUNGEN.md, aber AUFTRAG.md hat Vorrang, und genau das hat der Kritiker gefunden.
