# Auftrag: Machbarkeitsbeweis „Korbgeld“

Version 1.3 · 07.10.2026

Dieses Dokument beschreibt das Gesamtziel für Claude Code. Es wird in drei Prompts umgesetzt:

| Prompt | Datei | Modus | Inhalt |
|---|---|---|---|
| 1 Lernen und Vorbereiten | `PROMPT-1-LERNEN.md` | interaktiv, Output-Stil „Learning“ | Phase 0 und 1 gemeinsam mit dem Team, alles erklärt |
| 2 Bauen | `goal-befehl.txt` | autonom innerhalb einer Phase, Halt mit Erklärung nach jeder Phase (Regel 15), Output-Stil „Explanatory“ | Phase 2 bis 5 |
| 3 Verstehen | `PROMPT-3-VERSTEHEN.md` | interaktiv, Output-Stil „Learning“ | Code-Tour, Prüfungsfragen, Spickzettel |

**Änderungen gegenüber v1.0:** Ablauf in drei Prompts. Neue Kostenregel (Abschnitt 3). Datenbank bei Neon statt bei Render, weil die kostenlose Render-Datenbank nach 30 Tagen gelöscht wird. Kein `render.yaml`-Blueprint mehr, sondern eine manuelle Anleitung `docs/DEPLOY.md`. Lokale Entwicklung geht auch ohne Docker über einen Neon-Branch. Spring Security kommt erst in Phase 2. Neuer Lernordner `docs/lernen/`.

**Änderungen gegenüber v1.1** (nach Review `docs/reviews/00-auftrag.md`, Entscheidungen E1 bis E4):
- Entwickelt wird nur noch lokal mit Docker. Neon dient ausschließlich als Produktionsdatenbank ab M3, der Neon-Branch für die Entwicklung entfällt (B1).
- Phase 4 ist verkleinert. Prompt 2 baut nur noch lesende Demo-Bereiche. Sparplan-Rechner, Risikoprofil, `TwelveDataClient`, Diagramm und die schreibenden Teile von UC3, UC6 und UC7 baut das Team bis M4 selbst (neuer Abschnitt 18) (M1, M5).
- Registrierung mit Haushaltsgröße, Pseudonym und Ranglisten-Opt-in (M2).
- Der Seeder füllt bei jedem Start bis zum letzten abgeschlossenen Monat auf (M3).
- Sandbox-Bereinigung auch beim Start und beim Demo-Login, dazu ein Tageslimit für Demo-Logins (M4, m12).

**Änderungen gegenüber v1.2** (Entscheidung E5): Das Team wird beim Aufbau von Backend und Frontend eingebunden und soll Datenbankanbindung, Datenbankaufrufe und das Zusammenspiel von Frontend und Backend verstehen. Neue Arbeitsregel 15. Prompt 2 hält nach jeder Phase an.

## 0. Platzhalter

| Platzhalter | Wert | Falls nicht ersetzt |
|---|---|---|
| APP_NAME | Korbgeld (Arbeitstitel) | so lassen |
| GITHUB_USER_A | `<github-name-a>` | Avatar mit Initiale „A“ |
| GITHUB_USER_B | `<github-name-b>` | Avatar mit Initiale „B“ |
| Java-Package | `de.htwberlin.webtech.korbgeld` | so lassen |

Der App-Name steht an genau einer Stelle pro Teil (Backend-Konfiguration, Frontend-Konstante), damit das Team ihn später mit einer Änderung umbenennen kann.

## 1. Ziel

Am Ende von Prompt 2 steht eine lokal lauffähige, durchklickbare Vorversion:

- **Kern-Kette echt:** Budget, Einkaufsliste mit Barcode-Scan, Einkauf abschließen, Vorrat und Dashboard speichern in Postgres und sind getestet.
- **Demo-Bereiche:** Gewohnheiten, Rangliste und Sparplan zeigen echte Seed-Daten über GET-Endpunkte, schreiben aber nichts. Den echten Ausbau zu M4 übernimmt das Team selbst (Abschnitt 18).
- **In Arbeit:** Rezepte, Preisvergleich und Profil zeigen automatisch die In-Arbeit-Seite.
- **Unbekannte Pfade** zeigen automatisch die 404-Seite.

Kontext: Semesterprojekt im Modul Web-Technologien (Wirtschaftsinformatik, HTW Berlin). Ein Zweierteam verantwortet das Projekt und muss jede Zeile in Demo und Papierklausur erklären können. Das Team kennt Java aus Programmieren 1 und 2, hat Spring Boot bisher nur als Bibliothek eingebunden und kennt Vue.js noch nicht. Deshalb gilt: lesbarer, konventioneller Code statt cleverer Abstraktion.

Pflichtvorgaben des Moduls: Spring Boot, Vue.js, Postgres, Deployment auf Render, Frontend- und Backend-Tests automatisiert in GitHub Actions, keine Zugangsdaten im Klartext auf GitHub, Nutzer legen über die Oberfläche Entities an, die in der Datenbank landen. Für die Bestnote reichen 7 funktionierende Use Cases.

## 2. Arbeitsregeln

1. Arbeite die Phasen aus Abschnitt 16 in dieser Reihenfolge ab. Phase 0 kommt vor jeglichem App-Code.
2. Allererste Aktion: `.gitignore` anlegen, mindestens mit `.env`, `*.env.local`, `node_modules/`, `dist/`, `build/`, `.gradle/`, `.idea/`. Eine bereits vorhandene `.env` niemals committen.
3. Kleine Commits nach jedem abgeschlossenen Schritt, Format `feat(bereich): …`, `fix(…)`, `test(…)`, `chore(…)`, `docs(…)`. Im Commit-Text ein bis zwei Sätze zum Warum.
4. Scope-Disziplin: Baue nur, was hier steht. Eigene Ideen kommen nach `docs/IDEEN.md`.
5. Frag in Prompt 2 bei Unklarheiten nicht nach. Triff eine begründete Annahme und halte sie in `docs/ENTSCHEIDUNGEN.md` fest (Datum, Entscheidung, Grund, verworfene Alternative).
6. Versionen nicht aus dem Gedächtnis: Ermittle aktuelle stabile Versionen (start.spring.io, `npm create vue@latest`, npm-Registry). Fester Rahmen: Java 25, Gradle 9.x, Node 26.x.
7. Bezeichner, Klassen und Endpunkte auf Englisch. UI-Texte auf Deutsch in du-Form. Kommentare nur, wo das Warum nicht offensichtlich ist.
8. Geldbeträge im Backend immer `BigDecimal` mit zwei Nachkommastellen, nie `double`. Im Frontend Anzeige über `Intl.NumberFormat('de-DE', { style: 'currency', currency: 'EUR' })`.
9. Tests rufen nie echte externe APIs auf.
10. Zeichne oder kopiere keine Logos, Marken oder Maskottchen (kein Octocat, kein Claude-Logo). Logos erscheinen nur, wenn das Team die offiziellen Dateien selbst ablegt (Abschnitt 8).
11. Nicht selbst deployen. Pushe nur, wenn ein Remote eingerichtet ist und `git push` ohne Rückfrage funktioniert, dann nach jeder Phase.
12. Rufe den Subagenten `prof-kritiker` (Anhang A) nach Phase 0, nach Phase 1, nach Phase 3 und am Ende auf. Blocker behebst du sofort. Major-Befunde behebst du oder stellst sie begründet in `docs/ENTSCHEIDUNGEN.md` zurück.
13. Die Kostenregel (Abschnitt 3) ist nicht verhandelbar.
14. Halte nach jeder Phase in `docs/lernen/LOGBUCH.md` fest, was entstanden ist und welche Konzepte darin vorkommen (mit Dateipfaden). Neue Fachbegriffe kommen mit einem Satz Erklärung in `docs/lernen/GLOSSAR.md`. Prompt 3 baut darauf auf.
15. **Team einbinden.** Das Team will verstehen, wie Datenbankanbindung und Datenbankaufrufe funktionieren und wie Backend und Frontend zusammenspielen. Deshalb gilt in jedem Prompt: vor dem Bauen den Platz im Request-Weg erklären, tragende Stellen vom Team schreiben lassen (`TODO(human)`), bei Datenbankzugriffen das erzeugte SQL und die Verbindungskette (`.env` → `application.yml` → Hikari → JDBC → Postgres) zeigen und nach dem Bauen einen Request von Anfang bis Ende gemeinsam verfolgen. In Prompt 2 endet jede Phase mit dieser Erklärung, und die nächste Phase beginnt erst nach dem „weiter“ des Teams. Das geht vor Regel 5: Zwischen den Phasen darf und soll Prompt 2 Fragen stellen.

## 3. Kostenregel

Alles muss dauerhaft kostenlos sein. Erlaubt sind nur Dienste mit einem unbefristeten Gratis-Tarif, ohne Testphase, ohne Startguthaben und ohne Kreditkarte:

| Zweck | Dienst | Bemerkung |
|---|---|---|
| Code, Zusammenarbeit, CI | GitHub (Free) mit GitHub Actions | |
| Datenbank Produktion (ab M3) | Neon (Free-Plan) | Anmeldung mit GitHub. Datenbank schläft nach 5 Minuten ohne Abfragen und wacht in Sekundenbruchteilen auf. Nicht für die Entwicklung (E1). |
| Hosting Backend und Frontend | Render: kostenloser Web Service und Static Site | Pflicht laut Modul. Dienste von Hand anlegen, kein Blueprint, **keine Render-Datenbank**. |
| Produktdaten | Open Food Facts | ohne Konto und ohne Key |
| Kursdaten | Twelve Data, Basic-Tarif | optional. Ohne Key läuft alles mit Seed-Kursen. |
| Lokal | Docker Desktop (Pflicht für Entwicklung und Testcontainers), Playwright, Google Fonts | |

Würde ein Schritt Bezahlung, eine Testphase oder eine Kreditkarte verlangen, führst du ihn nicht aus, dokumentierst das in ENTSCHEIDUNGEN.md und nimmst eine Alternative aus dieser Tabelle.

## 4. Produkt

**Kernfrage:** „Wie viel spare ich beim Einkaufen, und was würde aus dem Geld, wenn ich es anlege?“

**Zielgruppe:** Studierende und WGs, die beim Lebensmitteleinkauf auf ihr Geld achten wollen.

**Die Kette:**
1. Monatsbudget für Einkäufe festlegen. Es wird gesperrt, sobald im Monat der erste Einkauf erfasst ist.
2. Artikel auf die Einkaufsliste setzen, per Eingabe oder Barcode. Steht der Artikel schon im Vorrat, warnt die App vor dem Doppelkauf. Gewohnheiten füllen die Liste später automatisch (vorerst nur Demo).
3. Einkauf abschließen: Laden wählen, Summe vom Kassenbon eintragen. Abgehakte Artikel wandern in den Vorrat, das Restbudget sinkt.
4. Was am Monatsende übrig ist, ergibt die Sparquote für die Rangliste.
5. Die Ersparnis fließt in einen Spielgeld-Sparplan mit echten historischen ETF-Kursen und einem Musterportfolio passend zum Risikoprofil.

**Bewusst nicht enthalten:** Budget für Miete oder Abos. Echte Geldanlage oder Einzelempfehlungen für Wertpapiere (das wäre in Deutschland erlaubnispflichtige Anlageberatung). Preise pro Einzelartikel als Pflichtfeld. Rezepte und Preisvergleich (nur In-Arbeit-Seiten).

## 5. Tech-Stack und Repo-Struktur

**Backend:** Java 25, Spring Boot (aktuelle stabile Version mit Java-25-Unterstützung), Gradle 9.x mit Wrapper, Spring Web, Spring Data JPA, PostgreSQL-Treiber, Flyway, Bean Validation, Actuator (nur `health` freigeben), `RestClient` für externe APIs. Ab Phase 2 zusätzlich Spring Security mit OAuth2 Resource Server (JWT, HS256). Tests mit JUnit 5, Mockito, Spring Boot Test, `MockRestServiceServer`, Testcontainers (Postgres, `@ServiceConnection`, `@Testcontainers(disabledWithoutDocker = true)`).

**Frontend:** Node 26.x, Vue 3 mit TypeScript und Vite (erzeugt mit `create-vue`), Vue Router im History-Modus, Pinia, Vitest mit `@vue/test-utils` und jsdom, ESLint. `vue-chartjs` mit Chart.js für Diagramme, `@zxing/browser` für den Barcode-Scan. Kein UI-Framework, eigenes CSS mit Design-Tokens.

**Datenbank lokal:** Entwickelt wird mit `docker-compose.yml` und Postgres (gleiche Major-Version wie Neon, Wahl in ENTSCHEIDUNGEN.md). Ein Neon-Branch für die Entwicklung ist nicht vorgesehen (E1). Produktion nutzt ab M3 den Neon-Branch `main`. Verbindung immer über den direkten Neon-Host (ohne `-pooler` im Namen), weil Flyway mit dem Transaktions-Pooler Probleme bekommt. Hikari mit kleinem Pool (maximal 5 Verbindungen) und kurzer `max-lifetime`, weil Neon Verbindungen beim Einschlafen trennt.

**Konfiguration:** Eine `.env` im Repo-Wurzelordner (gitignored) und eine vollständige `.env.example` mit `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_SSLMODE` (lokal `disable`, Neon `require`), `JWT_SECRET`, `FRONTEND_URL`, `TWELVEDATA_API_KEY`, `VITE_API_BASE_URL`. Das Backend liest sie über `spring.config.import: optional:file:../.env[.properties]`, Vite über `envDir: '..'`, Docker Compose automatisch. `application.yml` enthält nur `${…}`-Platzhalter, die Datasource-URL lautet `jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?sslmode=${DB_SSLMODE}`, und `server.port` ist `${PORT:8080}`, weil Render den Port vorgibt.

```
/
├─ AUFTRAG.md, PROMPT-1-LERNEN.md, PROMPT-3-VERSTEHEN.md, goal-befehl.txt
├─ CLAUDE.md, README.md
├─ docker-compose.yml, .env.example, .gitignore
├─ .claude/agents/prof-kritiker.md
├─ .github/workflows/backend.yml, frontend.yml
├─ docs/  STATUS.md, ENTSCHEIDUNGEN.md, IDEEN.md, DEPLOY.md, reviews/, screenshots/, lernen/
├─ backend/   Gradle-Projekt, Dockerfile
└─ frontend/  Vite-Projekt
```

**Backend-Pakete nach Fachbereich:** `auth`, `budget`, `shopping` (Liste, Einkauf, Laden), `pantry`, `product` (inkl. Open Food Facts), `habit`, `leaderboard`, `invest` (Kurse, Musterportfolios, Sparplan), `common` (Fehler, Konfiguration, Security, Seeder). In jedem Bereich Controller → Service → Repository. Nach außen gehen nur DTOs als Java-Records, nie Entities.

## 6. Datenmodell

| Entity | Felder | Hinweise |
|---|---|---|
| `AppUser` | id, username (unique), passwordHash, displayName, householdSize (1–6), leaderboardOptIn, sandbox, createdAt | Passwort nur als BCrypt-Hash. displayName ist ein Pseudonym. |
| `Store` | id, owner, name | Läden des Nutzers, Name max. 60 Zeichen |
| `Product` | id, name, barcode (nullable, unique), category, imageUrl, nutriScore, source (MANUAL, OPEN_FOOD_FACTS) | Gemeinsamer Katalog, dient auch als Cache für Open Food Facts |
| `ListItem` | id, owner, product, quantity (1–99), checked, createdAt | **Entity für M1, Endpunkt `GET /api/list-items`** |
| `Purchase` | id, owner, store, date, totalAmount, lines | lines als `@ElementCollection` (productId, quantity) |
| `PantryItem` | id, owner, product, quantity, bestBefore (nullable), addedAt | |
| `MonthlyBudget` | id, owner, yearMonth, amount | unique (owner, yearMonth) |
| `Habit` | id, owner, product, quantity, intervalDays, nextDue | vorerst nur lesend |
| `PricePoint` | symbol, month, close | Primärschlüssel (symbol, month), Kurs-Cache |

In Phase 1 gibt es `owner` noch nicht, weil es noch keine Nutzer gibt. Phase 2 ergänzt die Spalte per neuer Flyway-Migration. Musterportfolios sind ein Enum im Code, keine Tabelle. Ab Phase 2 filtert jede nutzerbezogene Abfrage nach dem eingeloggten Owner.

## 7. Fachregeln

- **Budget-Obergrenze:** `amount` ≤ 500 € × householdSize. Der Wert pro Person ist als `app.budget.max-per-person` konfigurierbar.
- **Budget-Sperre:** Ein Monatsbudget ist änderbar, bis im selben Monat der erste Einkauf erfasst ist. Danach antwortet die API mit 409.
- **Restbudget:** amount minus Summe aller `totalAmount` des Monats. Darf negativ werden und wird dann rot angezeigt.
- **Sparquote:** max(0, Restbudget) ÷ amount. Gewertet werden nur abgeschlossene Monate, der laufende Monat ist „vorläufig“.
- **Vorrats-Warnung:** Beim Hinzufügen zur Liste prüft das Backend, ob ein `PantryItem` mit derselben productId und quantity > 0 existiert. Die Antwort enthält dann `alreadyInPantry { quantity, bestBefore }`, und die UI zeigt einen Hinweis mit „Trotzdem hinzufügen“.
- **Artikel-Abgleich:** Beim Tippen schlägt die UI vorhandene Produkte vor (`GET /api/products?query=`). Abgleich immer über die productId, nie über den Text.
- **Einkauf abschließen:** Nur abgehakte Listeneinträge, mindestens einer. Laden ist Pflicht (vorhandene storeId oder neuer storeName). totalAmount zwischen 0,01 € und 1.000 €. In einer Transaktion: Purchase anlegen, Vorrat erhöhen (gleiches Produkt wird zusammengefasst), abgehakte Listeneinträge löschen. Die Antwort enthält das neue Restbudget.
- **Ablauf-Warnung:** bestBefore in ≤ 3 Tagen heißt „läuft bald ab“, in der Vergangenheit „abgelaufen“.
- **Verbrauchen:** verringert quantity. Bei 0 wird der Eintrag gelöscht.
- **Rangliste:** Nutzer mit Opt-in, ohne Sandbox-Nutzer. Sortiert nach Sparquote des letzten abgeschlossenen Monats, bei Gleichstand nach Serie (aufeinanderfolgende Monate im Budget). Angezeigt werden Pseudonym, Haushaltsgröße, Sparquote und Serie. Der eingeloggte Nutzer sieht zusätzlich „Dein Platz wäre: N“, ohne selbst in die Liste aufgenommen zu werden.
- **Risikoprofil:** Drei Fragen mit je 0 bis 2 Punkten. Anlagehorizont (unter 2 Jahre, 2–5, über 5), Reaktion auf 20 % Verlust (verkaufen, abwarten, nachkaufen), Erfahrung (keine, etwas, viel). 0–2 Punkte VORSICHTIG, 3–4 AUSGEWOGEN, 5–6 MUTIG.
- **Musterportfolios:** VORSICHTIG 30 % ACWI und 70 % AGG, AUSGEWOGEN 60/40, MUTIG 100 % ACWI. ACWI und AGG sind US-gelistete ETFs (Aktien weltweit, Anleihen).
- **Sparplan-Simulation:** Für jeden abgeschlossenen Monat mit Ersparnis > 0 werden Anteile zum Monatsschlusskurs gekauft (Ersparnis × Gewicht ÷ Kurs). Depotwert ist die Summe aus Anteilen × aktuellem Kurs. Fehlt ein Kurs, gilt der letzte verfügbare davor. Das Diagramm zeigt Einzahlungen kumuliert und Depotwert pro Monat. Der Ausblick auf 10 Jahre nutzt die durchschnittliche Ersparnis der letzten 6 Monate und drei feste Jahresrenditen (2 %, 5 %, 7 %), beschriftet als Annahme.
- **Pflichthinweis auf der Sparplan-Seite:** „Keine Anlageberatung. Musterportfolios dienen zum Lernen. Kursdaten von US-gelisteten ETFs, Währungseffekte vereinfacht.“

## 8. Fehlerbehandlung, 404 und „In Arbeit“

**Backend:** Ein `@RestControllerAdvice` antwortet immer mit `ProblemDetail` (RFC 9457).

| Ausnahme | Status | Zusatzfelder |
|---|---|---|
| `NotFoundException`, unbekannter Pfad (`NoResourceFoundException`) | 404 | `path` |
| `FeatureNotAvailableException` | 501 | `feature`, `milestone` |
| Validierungsfehler | 400 | `errors` (Feld, Meldung) |
| `BudgetLockedException`, Obergrenze überschritten | 409 | |
| Externe API nicht erreichbar | 502 | `source` |
| `DemoLimitReachedException` | 429 | |

Stub-Controller für `/api/recipes/**`, `/api/price-comparison/**` und `/api/profile/**` werfen `FeatureNotAvailableException`.

**Frontend:**
- Jede Route hat `meta.status` (`ready`, `demo`, `wip`), dazu `meta.feature`, `meta.milestone` und bei `wip` eine kurze Liste `meta.tasks`.
- Ein globaler Router-Guard rendert für `wip`-Routen die `WorkInProgressView` mit den Meta-Daten. Die URL bleibt erhalten.
- Die Catch-all-Route `/:pathMatch(.*)*` rendert die `NotFoundView`.
- Der API-Client wirft typisierte Fehler (`ApiError` mit status und ProblemDetail). Eine Hilfsfunktion für das Laden ganzer Seiten leitet 404 auf die 404-Ansicht und 501 auf die In-Arbeit-Ansicht. Aktionen wie der Barcode-Lookup behandeln 404 selbst, dort bietet die UI die manuelle Eingabe an. 401 führt zum Login.
- Die Navigation zeigt kleine Status-Labels „Demo“ und „In Arbeit“. Schreibende Buttons auf Demo-Seiten sind deaktiviert und erklären per Tooltip: „Kommt mit Milestone …“.

**In-Arbeit-Seite im GitHub-Stil:** aufgebaut wie eine Issue-Karte. Titel „<Feature> ist in Arbeit“, Label „in Arbeit“ in der Akzentfarbe, Milestone (z. B. „M4 · 13. Dez.“), Assignees als runde Avatare von `https://github.com/<GITHUB_USER_A>.png?size=96` und `<GITHUB_USER_B>` (Fallback: Initialen-Kreis) und daneben ein Badge „Claude Code“. Darunter eine Checkliste aus `meta.tasks` und ein Button „Zurück zum Dashboard“. Das Badge zeigt nur dann ein Logo, wenn das Team die offizielle Datei `frontend/src/assets/brand/claude-logo.svg` abgelegt hat, sonst nur Text.

**404-Seite:** gleiche Kartenoptik, Titel „Diese Seite gibt es nicht“, angefragter Pfad in Monospace, Links zu Dashboard, Liste und Vorrat.

## 9. Seiten, Routen und Endpunkte

| Route | Inhalt | Status | Use Case | Milestone im Label |
|---|---|---|---|---|
| `/` | Start: Login, Registrieren, „Als Demo testen“ | ready | – | – |
| `/dashboard` | Restbudget, Sparquote, Ablauf-Warnungen | ready | UC1, UC5 | – |
| `/budget` | Monatsbudget anlegen und ansehen | ready | UC1 | – |
| `/liste` | Einkaufsliste, Barcode-Scan, Vorrats-Warnung, abhaken | ready | UC2 | – |
| `/liste/abschliessen` | Einkauf abschließen | ready | UC4 | – |
| `/vorrat` | Vorrat, Menge ändern, verbrauchen, Ablauf | ready | UC5 | – |
| `/gewohnheiten` | Gewohnheiten | demo | UC3 | M4 · 13. Dez. |
| `/rangliste` | Rangliste | demo | UC6 | M4 · 13. Dez. |
| `/sparplan` | Musterportfolios mit Gewichtung und Pflichthinweis. Risikoprofil, Rechner und Diagramm folgen zu M4 (Team). | demo | UC7 | M4 · 13. Dez. |
| `/rezepte` | Rezepte aus dem Vorrat | wip | Erweiterung | nach M4 |
| `/preise` | Preisvergleich mit Open Prices | wip | Erweiterung | nach M4 |
| `/profil` | Profil und Einstellungen | wip | – | M4 · 13. Dez. |

**Endpunkte (alle unter `/api`; ab Phase 2 außer `auth/**` und `/actuator/health` nur mit JWT):**
- `POST auth/register`, `POST auth/login`, `POST auth/demo`, `GET me`
- `GET budgets/current/summary`, `GET budgets/{yearMonth}`, `PUT budgets/{yearMonth}`
- `GET list-items`, `POST list-items`, `PATCH list-items/{id}`, `DELETE list-items/{id}`
- `GET products?query=`, `GET products/barcode/{code}`
- `GET stores`, `POST stores`
- `POST purchases`, `GET purchases?month=`
- `GET pantry-items`, `PATCH pantry-items/{id}`, `POST pantry-items/{id}/consume`
- `GET habits` (demo), `GET leaderboard` (demo)
- `GET invest/portfolios`
- erst zu M4 durch das Team (Abschnitt 18): `PUT invest/risk-profile`, `GET invest/savings-plan`, schreibende Endpunkte für Gewohnheiten und Profil
- Stubs mit 501: `recipes/**`, `price-comparison/**`, `profile/**`

## 10. Sicherheit (ab Phase 2)

- Passwörter mit BCrypt. Login und Registrierung liefern ein JWT (HS256, Secret aus `JWT_SECRET`, mindestens 32 Byte, Laufzeit 12 Stunden). Frontend und Backend laufen auf Render unter verschiedenen Domains, deshalb Token im `Authorization`-Header statt Cookie. Das Frontend hält das Token in Pinia und `sessionStorage`.
- **Registrierung:** `POST auth/register` erwartet `username` (3–30 Zeichen, unique), `password` (mindestens 8 Zeichen), `displayName` (Pseudonym, 3–40 Zeichen), `householdSize` (1–6, Standard 1) und `leaderboardOptIn` (Standard `false`). Das Formular auf `/` fragt alle Felder ab. Damit sind Budget-Obergrenze und Rangliste für echte Nutzer definiert.
- CORS erlaubt nur `FRONTEND_URL`, im Dev-Profil zusätzlich `http://localhost:5173`.
- Jede Abfrage ist auf den Owner beschränkt. Ein Integrationstest beweist, dass Nutzer B die Listeneinträge von Nutzer A weder lesen noch ändern kann.
- **Demo-Login:** `POST /api/auth/demo` legt aus der Persona-Vorlage (Abschnitt 11) einen frischen Sandbox-Nutzer an und liefert dessen JWT. So kann jeder Prüfer alles ausprobieren, ohne andere zu stören. Sandbox-Nutzer, die älter als 7 Tage sind, werden gelöscht: beim Start der App, bei jedem `POST auth/demo` und zusätzlich durch einen täglichen `@Scheduled`-Job. Der Job allein genügt nicht, weil der kostenlose Render-Dienst einschläft. Pro Kalendertag sind höchstens 100 neue Sandbox-Nutzer erlaubt (konfigurierbar als `app.demo.max-per-day`). Darüber antwortet die API mit 429. Das hält die Neon-Datenbank klein (Free-Plan: 0,5 GB).
- Das Test-Profil darf ein offensichtliches Dummy-Secret enthalten, mit Kommentar „nur für Tests“. Sonst steht kein Secret im Repo.

## 11. Seed- und Demo-Daten

Alle Daten werden **relativ zum aktuellen Datum** erzeugt, damit Rangliste und Sparplan auch im Januar noch frische Monate zeigen. Flyway legt das Schema an. Ein idempotenter `ApplicationRunner` ergänzt fehlende Demo-Daten. Er füllt **bei jedem Start** die Historie der Seed-Nutzer und der Persona-Vorlage bis zum letzten abgeschlossenen Monat auf. Ohne das wäre die Rangliste im Januar leer, wenn die Daten im Oktober angelegt wurden. Der Seeder nutzt eine injizierte `java.time.Clock`. Ein Test rückt die Uhr um zwei Monate vor und prüft, dass die Monate ergänzt werden, ohne doppelte Einträge zu erzeugen.

- **Produktkatalog:** etwa 40 typische deutsche Supermarktprodukte ohne Barcode (Barcodes nicht erfinden).
- **Persona-Vorlage „Mia“:** Studentin in einer WG, Haushalt 1, Budget 260 € pro Monat. 12 Monate Historie mit je 3–6 Einkäufen in 3 Läden. Sparquoten zwischen 0 und 25 %, ein Monat über Budget. Im laufenden Monat: Budget gesetzt, 2 Einkäufe, Liste mit 6 Einträgen (2 davon schon im Vorrat, damit die Warnung sichtbar ist), Vorrat mit 12 Einträgen (2 laufen in ≤ 3 Tagen ab, 1 ist abgelaufen), 4 Gewohnheiten.
- **Rangliste:** 15 feste Seed-Nutzer mit Pseudonymen (z. B. „Sparfuchs Kreuzberg“), Haushalt 1–4, je 6 abgeschlossene Monate. Sie können sich nicht einloggen.
- **Kurse:** monatliche Schlusskurse von ACWI und AGG für mindestens 10 Jahre in `backend/src/main/resources/seed/prices.csv`. Prompt 2 erzeugt plausible synthetische Kurse, markiert die Datei im Kopf als „SYNTHETISCH“ und notiert das in ENTSCHEIDUNGEN.md. Echte Kurse über Twelve Data holt das Team zu M4 (Abschnitt 18).

## 12. Externe APIs

- **Open Food Facts:** `GET https://world.openfoodfacts.org/api/v2/product/{barcode}.json?fields=product_name,product_name_de,image_front_small_url,nutriscore_grade,categories_tags`. Nur über das Backend, mit eigenem `User-Agent` („Korbgeld/0.1 (HTW Berlin Studienprojekt)“) und 3 Sekunden Timeout. Erst in der eigenen Datenbank nachsehen, dann extern fragen, das Ergebnis als `Product` speichern. Unbekannte Barcodes ergeben 404, Ausfall ergibt 502.
- **Twelve Data (optional, Team zu M4):** `TwelveDataClient` mit Key aus `TWELVEDATA_API_KEY`. Beim Start, wenn ein Key vorhanden ist und der neueste `PricePoint` älter als 35 Tage ist, werden die Monatskurse aktualisiert. Ohne Key oder bei Fehlern läuft alles mit den Seed-Kursen weiter, mit einem Log-Eintrag.
- **Barcode-Scan im Browser:** `@zxing/browser` über die Kamera. Das funktioniert nur unter HTTPS oder localhost. Ein Eingabefeld für die Barcode-Nummer ist immer da.

## 13. Tests (Mindestumfang am Ende von Prompt 2)

**Backend, mindestens 20 Tests:**
- Unit: `BudgetService` (Sperre, Obergrenze, Restbudget, Sparquote), `PurchaseService` (Abschluss, Zusammenfassen im Vorrat), `LeaderboardService` (Sortierung, Gleichstand, Sandbox ausgeschlossen), Seeder (Auffüllen mit vorgerückter `Clock`), Demo-Login (Tageslimit 429)
- Web: `@WebMvcTest` für `ListItemController` (GET 200, POST mit ungültiger Menge 400), ProblemDetail für 404 und 501
- Integration (Testcontainers): die ganze Kern-Kette über MockMvc (Demo-Login, Liste, abhaken, Einkauf abschließen, Vorrat, Restbudget) und die Datentrennung zwischen zwei Nutzern
- Open-Food-Facts-Client mit `MockRestServiceServer`: Erfolg, 404 und Timeout

**Frontend, mindestens 10 Tests:**
- Router: `wip`-Route rendert die In-Arbeit-Ansicht, unbekannter Pfad die 404-Ansicht
- API-Client: 501 und 404 werden richtig weitergeleitet, Barcode-404 nicht
- Einkaufsliste rendert Einträge per `v-for` und zeigt die Vorrats-Warnung
- Restbudget-Anzeige (positiv, negativ), Ablauf-Labels im Vorrat
- Registrierungsformular (Pflichtfelder, Haushaltsgröße 1–6)

## 14. CI/CD und Deployment

- `.github/workflows/backend.yml`: bei Push und PR mit Änderungen in `backend/**`, Temurin 25, `./gradlew build`. Auf GitHub-Runnern ist Docker vorhanden, dort laufen die Testcontainers-Tests mit.
- `.github/workflows/frontend.yml`: bei Änderungen in `frontend/**`, Node 26, `npm ci`, Lint, `npm run test:unit -- --run`, `npm run build`.
- `backend/Dockerfile`: mehrstufig, Build mit Gradle, Laufzeit mit Temurin-25-JRE, `JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75` wegen des knappen Speichers im Free-Plan. Render hat keine eingebaute Java-Laufzeit, deshalb Docker.
- **Kein `render.yaml`.** Stattdessen `docs/DEPLOY.md` mit einer Klick-für-Klick-Anleitung zum manuellen Anlegen:
  1. Neon: Projekt im Free-Plan per GitHub-Login, Branch `main` für Produktion, direkte Verbindungsdaten notieren.
  2. Render Web Service: Repo verbinden, Root Directory `backend`, Runtime Docker, Instance Type Free, Health Check `/actuator/health`, Umgebungsvariablen `DB_*` mit `DB_SSLMODE=require`, `JWT_SECRET` (zufällig, mindestens 32 Zeichen), `FRONTEND_URL`, optional `TWELVEDATA_API_KEY`.
  3. Render Static Site: Root Directory `frontend`, Build `npm ci && npm run build`, Publish `dist`, Rewrite-Regel `/*` → `/index.html` (sonst liefert Neuladen auf Unterseiten 404), Umgebungsvariable `VITE_API_BASE_URL`.
  4. `FRONTEND_URL` im Web Service auf die Adresse der Static Site setzen, Smoke-Test.
  5. Hinweise: Kostenlose Render-Dienste schlafen nach 15 Minuten ohne Aufrufe ein, vor einer Demo die App einmal öffnen. Verlangt Render eine Kreditkarte, nichts eingeben (Kostenregel), sondern Tutor oder Prof fragen.

## 15. Design

**Vorgabe des Teams:** „Claude- und GitHub-Stil“. Umsetzung: Struktur und Bauteile wie bei GitHub, Farbwelt warm wie bei Claude, ohne fremde Logos oder Grafiken zu übernehmen.

- **Struktur:** obere Kopfleiste mit App-Name, Navigation und Avatar-Menü. Inhalte in Boxen mit 1 px Rand und 6 px Radius. Labels als Pillen. Zahlen mit `font-variant-numeric: tabular-nums`.
- **Tokens:** `--bg #FAF9F5`, `--surface #FFFFFF`, `--border #E5E1D8`, `--text #1F1E1D`, `--muted #6B6862`, `--accent #C96442`, `--accent-weak #F3E3DA`, `--ok #1A7F37`, `--warn #9A6700`, `--danger #CF222E`. Kontrast mindestens WCAG AA prüfen und dokumentieren.
- **Schrift:** System-Schriftstapel für UI und Fließtext, eine Serifenschrift (z. B. „Source Serif 4“, selbst gehostet oder Google Fonts mit Fallback) nur für Seitentitel und große Beträge.
- **Das eine Erkennungsmerkmal:** Das Restbudget erscheint oben auf Dashboard und Liste als schmaler Kassenbon-Streifen (gezackte Unterkante, Betrag in der Serifenschrift). Alles andere bleibt ruhig.
- **Mobil zuerst:** Die Einkaufsliste wird im Laden mit einer Hand bedient. Große Tipp-Flächen (mindestens 44 px), Abhaken mit einem Tipp, „Einkauf abschließen“ als fixierter Button unten. Funktioniert ab 360 px Breite.
- **Qualität:** sichtbarer Tastaturfokus, `prefers-reduced-motion` respektieren, leere Zustände mit klarer Handlungsaufforderung („Leg dein erstes Budget an“), Fehlermeldungen sagen, was passiert ist und was zu tun ist.
- **Texte:** Buttons sagen, was passiert („Einkauf abschließen“, nicht „Absenden“). Ein Vorgang heißt überall gleich.

## 16. Phasen

**Phase 0: Fundament und Kritiker (Prompt 1)**
`.gitignore`, `.claude/agents/prof-kritiker.md` wörtlich aus Anhang A als erster Commit vor jedem App-Code, `CLAUDE.md` (Arbeitsregeln in Kurzform, Repo-Struktur, Befehle, Verweis auf AUFTRAG.md), `docs/lernen/`. Danach prüft der Kritiker AUFTRAG.md, Bericht als `docs/reviews/00-auftrag.md`. Ist der Subagent in der laufenden Session noch nicht als eigener Typ verfügbar, startest du einen allgemeinen Subagenten und gibst ihm den Inhalt von `prof-kritiker.md` als Anweisung.

**Phase 1: Gerüst (Prompt 1)**
Backend über start.spring.io mit den Abhängigkeiten aus Abschnitt 5, **noch ohne Security**. `Product` und `ListItem` mit Flyway-Migration und `GET /api/list-items` (Stand M1). Lokale Datenbank über Docker Compose, `.env`-Anbindung. Frontend mit `create-vue`, Listenansicht per `v-for` (Stand M2), Anbindung an das Backend inklusive CORS. Beide Workflows in `.github/workflows/`. Kritiker prüft das Gerüst, Bericht als `docs/reviews/01-geruest.md`.

**Phase 2: Querschnitt (Prompt 2)**
Spring Security mit JWT und Demo-Login (Abschnitt 10), `owner`-Spalten per neuer Migration, Fehlerbehandlung (Abschnitt 8), vollständiges Schema und Seeder inklusive Kurs-Seed (Abschnitt 11), Router mit Status, In-Arbeit- und 404-Ansicht, API-Client, Layout und Design-Tokens (Abschnitt 15), `backend/Dockerfile`. Tests dazu. Commit.

**Phase 3: Kern-Kette (Prompt 2)**
UC1 Budget, UC2 Liste mit Barcode und Vorrats-Warnung, UC4 Einkauf abschließen, UC5 Vorrat, Dashboard. Integrationstest der ganzen Kette. Danach Kritiker, Bericht als `docs/reviews/02-kern.md`, Blocker beheben. Commit.

**Phase 4: Demo-Bereiche (Prompt 2)**
Nur lesende Demo-Bereiche: UC3 Gewohnheiten (`GET habits`), UC6 Rangliste (`GET leaderboard` mit Sortierung nach Abschnitt 7) und UC7 Sparplan-Seite mit Musterportfolios (`GET invest/portfolios`) und Pflichthinweis. Kein Rechner, kein Risikoprofil, kein `TwelveDataClient`, kein Diagramm, denn diese Teile baut das Team (Abschnitt 18). Schreibende Buttons sind deaktiviert mit Tooltip „Kommt mit Milestone M4“. Tests. Commit.

**Phase 5: Abschluss (Prompt 2)**
1. Stubs und `wip`-Routen prüfen.
2. Wenn Playwright installierbar ist: Screenshots aller Routen bei 390 px und 1280 px Breite nach `docs/screenshots/`, eingeloggt als Demo-Nutzer. Das ist die Grundlage für die Screenshot-Dokumentation pro Use Case.
3. README (Start lokal mit Docker, Tests), `docs/DEPLOY.md` und `docs/STATUS.md` schreiben.
4. Kritiker final, Bericht als `docs/reviews/03-final.md`, Blocker beheben.
5. Smoke-Test mit curl gegen das lokal laufende Backend (siehe Zielbedingung), Ausgabe zeigen.
6. Abschluss-Commit, `git status` sauber.

## 17. Inhalt von docs/STATUS.md

- Tabelle: Use Case, Status (echt, Demo, in Arbeit), Route, zugehörige Tests, Screenshot
- Was nicht geschafft wurde und warum
- Bekannte Schwächen und Annahmen (Verweis auf ENTSCHEIDUNGEN.md)
- Nächste Schritte entlang der Milestones: M2 8. Nov., M3 22. Nov. (Deployment auf Render), M4 13. Dez., Abgabe 17. Jan., 23:59 Uhr
- Die „Fragen, die ich in der Demo stellen würde“ aus dem letzten Kritiker-Bericht

## 18. Ausbau bis M4 durch das Team (nicht Teil von Prompt 2)

Diese Teile schreibt das Team selbst, mit interaktiver Begleitung wie in Prompt 1. Prompt 2 baut sie **nicht**. Ziel: Danach funktionieren mindestens 7 Use Cases von Anfang bis Ende, und das Team kennt jede Zeile davon.

| Use Case | Was echt wird | Tests |
|---|---|---|
| UC7 Sparplan | Risikoprofil (Abschnitt 7) berechnen und speichern (`PUT invest/risk-profile`), Musterportfolio wählen und speichern (neue Spalten `riskProfile`, `chosenPortfolio` an `AppUser` per Migration), `SavingsPlanCalculator`, `GET invest/savings-plan`, Diagramm mit `vue-chartjs` | `SavingsPlanCalculator` (feste Kurse, fehlender Monat), `RiskProfile` (Punktgrenzen), Frontend-Umrechnung für das Diagramm |
| UC7 Kurse (optional) | `TwelveDataClient` nach Abschnitt 12 | `MockRestServiceServer`: Erfolg, 404, Timeout |
| UC3 Gewohnheiten | Gewohnheiten anlegen, ändern, löschen. Fällige Gewohnheiten setzen Artikel auf die Liste. | Service-Test für Fälligkeit |
| UC6 Rangliste | Opt-in und Pseudonym über das Profil ändern. Die Rangliste spiegelt die Änderung. | Integrationstest Opt-in/Opt-out |
| Profil (Reserve, UC8) | `/profil` wechselt von `wip` zu `ready`: `displayName`, `householdSize`, `leaderboardOptIn` bearbeiten | Validierung `householdSize` 1–6 |

---

## Anhang A: `.claude/agents/prof-kritiker.md` (wörtlich anlegen)

```markdown
---
name: prof-kritiker
description: Strenger Gutachter im Stil eines Software-Engineering-Professors für das Modul Web-Technologien. Proaktiv einsetzen nach jeder Phase, vor größeren Commits und bevor etwas als fertig gilt. Prüft Plan, Code, Tests und Oberfläche gegen die Bewertungskriterien des Moduls und liefert priorisierte Befunde mit Notenschätzung.
tools: Read, Grep, Glob, Bash
---

Du bist ein erfahrener, strenger Professor für Software Engineering. Du bewertest Semesterprojekte im Modul Web-Technologien (Spring Boot, Vue.js, Postgres, Render, GitHub Actions). Du hast lange in der Beratung echte Systeme gebaut und erkennst sofort, ob Studierende verstanden haben, was sie abgeben. Du bist fair, sachlich und knapp. Du lobst nicht, um nett zu sein, und du erfindest keine Befunde, um streng zu wirken.

## Grundsätze
- Du änderst nichts. Du liest, suchst und führst höchstens lesende Befehle und Tests aus, z. B. `./gradlew test`, `npm run test:unit -- --run`, `git log`, `git grep`.
- Jeder Befund nennt die Fundstelle (Datei:Zeile oder Route), warum er zählt und einen konkreten Vorschlag.
- Du prüfst, was tatsächlich im Repo steht, nicht was Doku-Dateien behaupten.
- Du berücksichtigst den Projektstand: Ein Gerüst nach Phase 1 bewertest du als Gerüst, nicht als fertiges Projekt.

## Bewertungsgrundlage
Mindestanforderungen. Fehlt eine im fertigen Projekt, ist das ein Blocker:
1. Die App ist nach Render deploybar und öffentlich erreichbar (Dockerfile, docs/DEPLOY.md, Umgebungsvariablen vollständig).
2. Nutzer legen über die Oberfläche Entities an, die in Postgres gespeichert werden.
3. Frontend- und Backend-Tests laufen automatisiert in GitHub Actions.
4. Keine Zugangsdaten im Klartext im Repo.
5. Spring Boot, Vue.js und Postgres werden genutzt.
6. Für jeden Use Case ist ein aussagekräftiger Screenshot möglich.

Bewertungsdimensionen. Schätze je eine Note zwischen 1,0 und 3,0 und begründe sie:
- Funktionsumfang: Wie viele Use Cases funktionieren echt von Anfang bis Ende? 7 reichen für 1,0. Demo- und In-Arbeit-Bereiche zählen nicht als fertig.
- Styling: Gibt es eigenes, durchdachtes CSS? Sieht alles vernünftig aus, auch auf dem Handy?
- Dynamisches Frontend: Komponentenstruktur, Zustandsverwaltung, sinnvoll viel Logik im Frontend.
- Tests und Continuous Delivery: Prüfen die Tests echtes Verhalten oder sind es Alibi-Tests? Läuft CI? Ist die Deploy-Anleitung plausibel?
- Backend-Strukturierung: Schichten (Controller, Service, Repository), DTOs statt Entities nach außen, Validierung, zentrale Fehlerbehandlung.
- Zusatzpunkte: Authentisierung, Multi-User mit sauberer Datentrennung, externe APIs mit Fehlerbehandlung und Caching, Input-Validierung.
- Minuspunkte: Zugangsdaten im Repo, Passwörter im Klartext, Bugs, Buttons ohne Rückmeldung.

## Worauf du zusätzlich achtest
- Fachlogik: Stimmen Budget-Sperre, Obergrenze, Restbudget, Sparquote und Sparplan mit AUFTRAG.md überein? Ist Geld überall BigDecimal?
- Datentrennung: Kann ein Nutzer Daten eines anderen lesen oder ändern? Gibt es einen Test dafür?
- Fehlerfälle: Was passiert, wenn Open Food Facts, Twelve Data oder die Datenbank nicht antworten?
- Kostenregel: Hängt irgendetwas von einem Dienst ab, der Geld, eine Testphase oder eine Kreditkarte verlangt?
- Demo-Tauglichkeit: Sieht ein Prüfer nach dem Demo-Login in unter zwei Minuten die ganze Kette?
- Verständlichkeit: Können zwei Studierende im dritten Semester diesen Code erklären? Unnötige Abstraktion ist ein Befund.
- Ehrlichkeit der Oberfläche: Sind Demo- und In-Arbeit-Bereiche klar gekennzeichnet? Steht der Hinweis „Keine Anlageberatung“ auf der Sparplan-Seite?
- Umfang: Wurde etwas gebaut, das nicht im Auftrag steht?

Prüfst du einen Plan statt Code (z. B. AUFTRAG.md vor dem Bauen), bewertest du Machbarkeit, Widersprüche, Lücken und Risiken für die Bewertung.

## Ausgabeformat
Gib deinen Bericht vollständig als Markdown zurück. Der Aufrufer speichert ihn unter docs/reviews/.

# Review: <Anlass> (<Datum>)
## Urteil
Zwei bis drei Sätze: Wo steht das Projekt, was ist das größte Problem?
## Notenschätzung
| Dimension | Note | Begründung in einem Satz |
## Befunde
### Blocker
- [B1] Fundstelle: Problem. Warum es zählt. Vorschlag.
### Major
### Minor
## Fragen, die ich in der Demo stellen würde
Drei Fragen, die prüfen, ob das Team den eigenen Code versteht.
```