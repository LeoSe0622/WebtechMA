# Glossar

Ein Satz pro Begriff, alphabetisch sortiert.

- **API (REST-API)**: Die Schnittstelle des Backends. Das Frontend ruft Adressen wie `GET /api/list-items` auf und bekommt Daten als JSON zurück.
- **Auto-Configuration**: Spring Boot richtet Komponenten automatisch ein, wenn ihre Bedingungen erfüllt sind (z. B. Treiber im Klassenpfad → Datenbankverbindung).
- **Bean**: Ein Objekt, das Spring erzeugt und verwaltet und per Dependency Injection an andere Klassen übergibt.
- **CI (Continuous Integration)**: Bei jedem Push baut und testet ein Server automatisch den Code, bei uns GitHub Actions.
- **CLAUDE.md**: Datei im Repo-Wurzelordner, die Claude Code zu Beginn jeder Session automatisch liest, das Projektgedächtnis.
- **Commit**: Ein gespeicherter Schnappschuss des Projekts mit Nachricht, Autor und Zeitpunkt.
- **Container**: Abgeschottete Laufzeitumgebung, in der ein Programm (z. B. Postgres) mit allem läuft, was es braucht, ohne es ins Betriebssystem zu installieren.
- **DataSource / Hikari**: Die DataSource liefert Datenbankverbindungen. Hikari ist ein Pool, der einige Verbindungen offen hält und an Anfragen verleiht.
- **Dependency Injection**: Eine Klasse bekommt die Objekte, die sie braucht, über den Konstruktor von Spring übergeben, statt sie selbst mit new zu erzeugen.
- **Docker Compose**: Beschreibt in `docker-compose.yml`, welche Container mit welchen Einstellungen laufen, und startet sie mit einem Befehl.
- **Docker Engine**: Der Hintergrunddienst, der Container tatsächlich ausführt. Der Befehl `docker` ist nur die Fernbedienung dafür.
- **DTO (Data Transfer Object)**: Einfaches Objekt nur für die Übertragung nach außen, damit die interne Entity nicht direkt preisgegeben wird.
- **Entity**: Java-Klasse mit @Entity, deren Objekte Zeilen einer Datenbanktabelle entsprechen.
- **.env**: Datei mit Zugangsdaten und Konfigurationswerten, die nur lokal existiert und nie ins Repo gelangt.
- **Flyway / Migration**: Flyway führt versionierte SQL-Dateien (V1__…, V2__…) genau einmal und in Reihenfolge aus und merkt sich das in flyway_schema_history.
- **.gitignore**: Liste von Dateien und Ordnern, die Git nicht verfolgt und die deshalb nie in einem Commit landen.
- **Hibernate / JPA**: JPA ist der Java-Standard, um Objekte in Datenbanktabellen zu speichern. Hibernate ist die Implementierung, die Spring Boot dafür nutzt.
- **Inversion of Control**: Nicht der eigene Code ruft das Framework auf, sondern das Framework erzeugt und ruft den eigenen Code auf.
- **JDBC**: Javas Standardschnittstelle für Datenbankzugriffe. Der Postgres-Treiber implementiert sie.
- **JSON**: Textformat für Daten (`{"id": 1, "checked": true}`), in dem Frontend und Backend miteinander sprechen.
- **MockMvc / Mockito**: MockMvc simuliert HTTP-Anfragen im Test ohne echten Server. Mockito ersetzt Abhängigkeiten durch Attrappen mit festen Antworten.
- **N+1-Problem**: Statt einer Abfrage mit JOIN werden für N Einträge N zusätzliche Einzelabfragen geschickt. Wird mit @EntityGraph vermieden.
- **Neon**: Anbieter für Postgres in der Cloud mit dauerhaft kostenlosem Tarif, bei uns ab M3 für die Produktionsdatenbank.
- **Profil (Spring)**: Benannte Konfigurationsvariante, z. B. dev, deren Einstellungen (application-dev.yml) nur gelten, wenn das Profil aktiv ist.
- **Push / Remote**: Das Remote ist die Kopie des Repos auf GitHub. `git push` überträgt lokale Commits dorthin.
- **Record**: Kompakte Java-Klasse für unveränderliche Daten. Konstruktor und Getter erzeugt Java selbst. Bei uns für DTOs.
- **Render**: Hosting-Dienst, auf dem Backend und Frontend öffentlich laufen (Modulvorgabe).
- **Repository (Spring Data)**: Interface, zu dem Spring beim Start die Implementierung mit save, findAll usw. erzeugt. Methodennamen werden zu Abfragen.
- **Review-Befund (Blocker / Major / Minor)**: Blocker muss sofort behoben werden. Major wird behoben oder begründet zurückgestellt. Minor ist eine Verbesserung ohne Dringlichkeit.
- **Staging-Bereich**: Zwischenablage von Git. Nur was dort per `git add` liegt, kommt in den nächsten Commit.
- **Subagent**: Eigene Claude-Instanz mit leerem Kontext und begrenzten Werkzeugen, die eine Teilaufgabe unabhängig erledigt, bei uns der `prof-kritiker`.
- **Testcontainers**: Bibliothek, die für Tests automatisch einen echten Datenbank-Container startet und danach wieder entfernt.
- **@WebMvcTest**: Testart, die nur die Web-Schicht (Controller) startet, ohne Datenbank, und schnell HTTP und JSON prüft.
- **WSL 2**: Windows-Subsystem für Linux. Darin läuft die Docker Engine unter Windows.
