# Glossar

Ein Satz pro Begriff, alphabetisch sortiert.

- **.env**: Datei mit Zugangsdaten und Konfigurationswerten, die nur lokal existiert und nie ins Repo gelangt.
- **.gitignore**: Liste von Dateien und Ordnern, die Git nicht verfolgt und die deshalb nie in einem Commit landen.
- **API (REST-API)**: Die Schnittstelle des Backends. Das Frontend ruft Adressen wie `GET /api/list-items` auf und bekommt Daten als JSON zurück.
- **CI (Continuous Integration)**: Bei jedem Push baut und testet ein Server automatisch den Code, bei uns GitHub Actions.
- **CLAUDE.md**: Datei im Repo-Wurzelordner, die Claude Code zu Beginn jeder Session automatisch liest, das Projektgedächtnis.
- **Commit**: Ein gespeicherter Schnappschuss des Projekts mit Nachricht, Autor und Zeitpunkt.
- **Container**: Abgeschottete Laufzeitumgebung, in der ein Programm (z. B. Postgres) mit allem läuft, was es braucht, ohne es ins Betriebssystem zu installieren.
- **Docker Engine**: Der Hintergrunddienst, der Container tatsächlich ausführt. Der Befehl `docker` ist nur die Fernbedienung dafür.
- **Docker Compose**: Beschreibt in `docker-compose.yml`, welche Container mit welchen Einstellungen laufen, und startet sie mit einem Befehl.
- **DTO (Data Transfer Object)**: Einfaches Objekt nur für die Übertragung nach außen, damit die interne Entity nicht direkt preisgegeben wird.
- **Hibernate / JPA**: JPA ist der Java-Standard, um Objekte in Datenbanktabellen zu speichern. Hibernate ist die Implementierung, die Spring Boot dafür nutzt.
- **JSON**: Textformat für Daten (`{"id": 1, "checked": true}`), in dem Frontend und Backend miteinander sprechen.
- **Neon**: Anbieter für Postgres in der Cloud mit dauerhaft kostenlosem Tarif, bei uns ab M3 für die Produktionsdatenbank.
- **Push / Remote**: Das Remote ist die Kopie des Repos auf GitHub. `git push` überträgt lokale Commits dorthin.
- **Render**: Hosting-Dienst, auf dem Backend und Frontend öffentlich laufen (Modulvorgabe).
- **Staging-Bereich**: Zwischenablage von Git. Nur was dort per `git add` liegt, kommt in den nächsten Commit.
- **Subagent**: Eigene Claude-Instanz mit leerem Kontext und begrenzten Werkzeugen, die eine Teilaufgabe unabhängig erledigt, bei uns der `prof-kritiker`.
- **Testcontainers**: Bibliothek, die für Tests automatisch einen echten Datenbank-Container startet und danach wieder entfernt.
- **WSL 2**: Windows-Subsystem für Linux. Darin läuft die Docker Engine unter Windows.
