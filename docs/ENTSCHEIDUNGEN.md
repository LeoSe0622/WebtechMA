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

- **Status:** Die Verkleinerung von Prompt 2 gilt weiter. Wer den Ausbau zu M4 baut, regelt jetzt E9 (Claude statt Team).

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

- **Status:** Teilweise ersetzt durch E7. Prompt 2 hält **nicht** mehr nach jeder Phase an, sondern nur noch an den Haltepunkten aus AUFTRAG.md, Regel 15.

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
- **Entscheidung:** Das Team ist nur noch an der Verbindung von Frontend und Backend und an den Milestone-Abnahmen (M1–M4) beteiligt. Alles andere baut Claude autonom (AUFTRAG.md v1.4, Regel 15; CLAUDE.md „Einbindung des Teams“). Ersetzt den Halt nach jeder Phase aus E5.
- **Grund:** Wunsch des Teams. Die Zeit soll in die Stellen fließen, die das Zusammenspiel der Teile zeigen, und in die Abnahme der Ergebnisse.
- **Folge:** Im LOGBUCH werden die autonom gebauten Teile besonders gründlich erklärt, damit Prompt 3 sie für Demo und Klausur aufbereiten kann.
- **Erledigt:** Die Frage zum Ausbau zu M4 ist in E9 entschieden.
- **Verworfene Alternative:** Beteiligung an allen Spring- und Vue-Teilen (E5 in der ursprünglichen Form).

## E8: Umgang mit Review 01 (Gerüst)

- **Datum:** 07.10.2026
- **Behoben:**
  - M1: CORS erlaubt jetzt auch `PUT`, mit Preflight-Test (`CorsConfigTest.answersPreflightForPut`). Für Phase 2 vorgemerkt: In der `SecurityFilterChain` `http.cors(Customizer.withDefaults())` setzen und den Preflight mit `Access-Control-Request-Headers: Authorization` testen.
  - M2: E5 als teilweise ersetzt markiert, E7 und der Vermerk zu v1.3 angeglichen.
  - m1: `ListItemIntegrationTest` liest mit Testcontainers wirklich aus Postgres und prüft Reihenfolge und DTO-Abbildung.
  - m2: CI nutzt `npm run lint-ci` (ohne `--fix`).
  - m3: Das Dev-Profil erlaubt `FRONTEND_URL` und zusätzlich `localhost:5173`.
  - m5: `ListItem` mit `FetchType.LAZY`, primitiven `int`/`boolean`, `createdAt` als Konstruktor-Parameter.
  - m6: `App.spec.ts` mit Attrappe statt echtem fetch.
  - m8: `@tsconfig/node26` und `@types/node` 26.
  - m10: Verweise und Einrichtungshinweise in CLAUDE.md und DATENBANK.md korrigiert.
  - m11: Prompt-Dateien in `.gitignore`. Sie sind Arbeitsanweisungen für Claude, kein Projektbestandteil.
- **Festgehalten (M4):** An der Frontend-Backend-Verbindung hat das Team `CorsConfig.addCorsMappings` selbst geschrieben und die übrigen TODOs ausdrücklich an Claude übergeben („mach doch alle selbst“). Das ist eine bewusste Abweichung von Regel 15 (a) auf Wunsch des Teams. Empfehlung an das Team: Die zweite Person committet künftig selbst, damit der Anteil beider sichtbar ist.
- **Für Phase 2 vorgemerkt (m4):** V3 legt zuerst `app_user` an, fügt `owner_id` nullable hinzu, löscht die vorhandenen Testzeilen in `list_item` (lokal nur Testdaten, auf Neon und in Testcontainers ist die Tabelle leer), setzt dann `NOT NULL` und einen Index auf `owner_id`.
- **Zurückgestellt:** m7 (Prüfung von `VITE_API_BASE_URL`) kommt mit dem zentralen API-Client in Phase 2 und als Stolperstelle in DEPLOY.md. m9 (Abhaken speichern) kommt mit Phase 3. m12 ist kosmetisch und in V2 nicht mehr änderbar, ab V3 wird `CREATE TABLE` großgeschrieben.
- **M3:** entschieden in E9.

## E9: Ausbau zu M4 baut Claude (Review 01, M3)

- **Datum:** 07.10.2026
- **Entscheidung:** Den Ausbau zu M4 (AUFTRAG.md, Abschnitt 18: Risikoprofil, Sparplan-Rechner, Diagramm, Twelve Data, Gewohnheiten anlegen, Ranglisten-Opt-in, Profil) baut Claude nach Prompt 2. Das Team ist nach Regel 15 an den Frontend-Backend-Verbindungen beteiligt (genaue `TODO(human)`) und nimmt M4 ab.
- **Grund:** Passt zur Arbeitsweise aus E7. So stehen zu M4 mindestens 7 echte Use Cases, und das Team schreibt die Teile, die das Zusammenspiel zeigen.
- **Folge:** Prompt 2 bleibt verkleinert (E2): Phase 4 baut nur lesende Demo-Bereiche. Der Ausbau folgt danach als eigener Schritt zu M4.
- **Verworfene Alternativen:** Das Team schreibt den Ausbau komplett selbst (ursprünglicher Plan aus E2, zu viel Zeit). Claude baut ihn ganz ohne Team-Beteiligung (keine Verbindungsteile vom Team).

## E10: Prompt 2 ohne Haltepunkte

- **Datum:** 07.10.2026
- **Entscheidung:** Das Team hat Prompt 2 mit „starte, mach alles“ freigegeben. Claude baut Phase 2 bis 5 vollständig, auch die Frontend-Backend-Verbindungen (API-Client mit JWT und ApiError, Demo-Login), ohne Haltepunkte und ohne `TODO(human)`. Am Ende steht eine Abnahme durch das Team. Die Goal-Dateien A/B (zwei Läufe mit Halt) werden dafür nicht gebraucht.
- **Grund:** Wunsch des Teams. Das Verständnis entsteht über LOGBUCH, die Abnahme und Prompt 3.
- **Folge:** Regel 15 (a) ist für Prompt 2 ausgesetzt. Die Milestone-Abnahmen (M3, M4) und die Beteiligung am Ausbau zu M4 (E9) bleiben.
- **Verworfene Alternative:** Zwei Goal-Läufe mit Halt bei H1/H2.

## E11: Kontraste und zusätzlicher Token `--accent-strong` (Phase 2)

- **Datum:** 07.10.2026
- **Entscheidung:** Neuer Token `--accent-strong: #9C4427` für Text, Links und Primär-Buttons. `--accent` (#C96442) bleibt für Rahmen, Fokus und Dekoration.
- **Grund:** WCAG-AA-Prüfung (Kontrastformel aus WCAG 2.x): `--accent` auf Weiß hat nur 3,90:1, AA verlangt aber 4,5:1 für normalen Text. Gemessen: Text/Hintergrund 15,80, Text/Fläche 16,64, muted/Fläche 5,55, muted/Hintergrund 5,27, accent-strong/Fläche 6,42, Weiß auf accent-strong 6,42, accent-strong auf accent-weak 5,14, ok 5,08, warn 4,87, danger 5,36, Demo-Label 7,71. Alle Paare, die jetzt für Text verwendet werden, bestehen AA.
- **Verworfene Alternative:** `--accent` für Buttontext auf Weiß, fällt durch AA.

## E12: In-Arbeit- und 404-Ansicht im Frontend (Phase 2)

- **Datum:** 07.10.2026
- **Entscheidung:** wip-Routen verwenden direkt `WorkInProgressView` als Komponente und lesen Feature, Milestone und Aufgaben aus `meta`. Der globale Router-Guard regelt den Login-Schutz. Meldet das Backend beim Laden einer Seite 404 oder 501, setzt `loadPage` einen Eintrag im UI-Store, und `App.vue` zeigt statt der Seite die 404- bzw. In-Arbeit-Ansicht. In allen Fällen bleibt die URL erhalten. Der Router entsteht über `createAppRouter()`, damit jeder Test einen eigenen Router mit eigenem Login-Zustand bekommt.
- **Grund:** Am einfachsten zu erklären: eine Route, eine Komponente. Der UI-Store vermeidet, dass für 404/501 eine andere URL entsteht.
- **Verworfene Alternative:** Guard, der für jede wip-Route die Komponente austauscht. Das ist schwerer nachzuvollziehen und bringt keinen Vorteil.

## E13: Seed- und Sandbox-Nutzer ohne Passwort (Phase 2)

- **Datum:** 07.10.2026
- **Entscheidung:** `app_user.password_hash` ist nullable. Seed-Nutzer (Rangliste) und Sandbox-Nutzer (Demo) haben keinen Hash und können sich deshalb nicht per Passwort anmelden. Sandbox-Nutzer erreichen ihre Daten nur über das JWT aus `POST /api/auth/demo`. `ON DELETE CASCADE` an allen `owner_id` sorgt dafür, dass das Löschen alter Sandbox-Nutzer alle ihre Daten mitnimmt.
- **Grund:** AUFTRAG.md, Abschnitt 11 („können sich nicht einloggen“) und Abschnitt 10 (Bereinigung nach 7 Tagen).
- **Verworfene Alternative:** Zufallspasswort für Seed-Nutzer. Das ist unnötig und wäre ein Geheimnis, das nirgends gebraucht wird.

## E14: Neue Produkte beim Hinzufügen zur Liste (Phase 3)

- **Datum:** 07.10.2026
- **Entscheidung:** `POST /api/list-items` nimmt bevorzugt eine `productId` aus den Vorschlägen. Fehlt sie, wird ein Produkt mit genau gleichem Namen (Groß- und Kleinschreibung egal) genommen, sonst ein neues Produkt mit Quelle `MANUAL` angelegt.
- **Grund:** AUFTRAG.md sieht „per Eingabe“ vor, nennt aber keinen eigenen Endpunkt zum Anlegen von Produkten. Der Abgleich bleibt über die productId. Nur bei exakt gleichem Namen wird kein Duplikat erzeugt, ähnliche Namen werden nicht zusammengelegt.
- **Verworfene Alternative:** Eigener Endpunkt `POST /api/products`. Das wäre ein zweiter Aufruf aus der Oberfläche für denselben Klick.

## E15: Budget nur für den laufenden und den nächsten Monat (Phase 3)

- **Datum:** 07.10.2026
- **Status:** Ergänzt nach Review 02 (M2): Die Sperre betrifft nur das **Ändern** eines vorhandenen Budgets. Ein erstes Budget kann man auch nach einem Einkauf noch anlegen. `locked` ist nur `true`, wenn ein Budget existiert und im Monat eingekauft wurde.
- **Entscheidung:** `PUT /api/budgets/{yearMonth}` akzeptiert nur den laufenden und den nächsten Monat, sonst 400. Ein Einkauf ohne Budget ist erlaubt, `remainingBudget` ist dann `null`.
- **Grund:** Vergangene Monate sind abgeschlossen und zählen für Sparquote und Rangliste. Sie nachträglich zu ändern, würde die Wertung verfälschen. Die Untergrenze 1 € verhindert eine Division durch 0 bei der Sparquote (Review 00, m5).
- **Verworfene Alternative:** Beliebige Monate. Dann ließe sich die Sparquote im Nachhinein schönrechnen.

## E16: Jackson 3 und primitive Felder in Request-DTOs (Phase 3)

- **Datum:** 07.10.2026
- **Entscheidung:** Optionale Wahrheitswerte in Requests sind `Boolean` (nicht `boolean`), fehlend gilt als `false`.
- **Grund:** Spring Boot 4 nutzt Jackson 3. Dort schlägt das Einlesen fehl, wenn ein primitives Feld im JSON fehlt (`FAIL_ON_NULL_FOR_PRIMITIVES`), und die Anfrage endet mit 400. Aufgefallen durch den Integrationstest der Kern-Kette.
- **Verworfene Alternative:** Die Jackson-Einstellung global abschalten. Das würde echte Fehler bei Pflichtfeldern verstecken.

## E17: Sichtbarkeit von Produkten (Review 02, B1 und M3)

- **Datum:** 07.10.2026
- **Entscheidung:** Neue Spalte `product.created_by` (V5). Selbst eingetippte Produkte gehören ihrem Ersteller und sind nur für ihn sichtbar (Suche, Verwendung per productId). Katalog- und Open-Food-Facts-Produkte bleiben gemeinsam (`created_by IS NULL`). Weil Produktnamen nicht eindeutig sind, sucht der Code nie mehr mit „genau ein Treffer“, sondern immer mit `findFirst … OrderByIdAsc`. Der Seed-Katalog wird über Quelle `MANUAL`, `created_by IS NULL` und Namen gefunden. Wird ein Sandbox-Nutzer gelöscht, löscht `ON DELETE CASCADE` seine eigenen Produkte mit.
- **Grund:** Ein Scan mit einem Katalognamen (z. B. „Butter“) erzeugte zwei gleichnamige Produkte, danach scheiterte der Demo-Login für alle mit 500. Freitext eines Nutzers erschien außerdem in den Vorschlägen aller anderen. Regressionstests: `CatalogAndSeparationIntegrationTest`.
- **Verworfene Alternativen:** Eindeutiger Produktname per UNIQUE-Constraint. Dann könnte Open Food Facts kein Produkt speichern, das zufällig wie ein Katalogprodukt heißt. Produkte ganz pro Nutzer speichern: Dann ginge der gemeinsame Cache für Barcodes verloren.

## E18: Vorrat beim Einkauf nur mit Einträgen ohne Datum zusammenfassen (Review 02, m5)

- **Datum:** 07.10.2026
- **Entscheidung:** Ein gekauftes Produkt erhöht nur einen Vorratseintrag **ohne** Mindesthaltbarkeitsdatum. Gibt es nur Einträge mit Datum, entsteht ein neuer Eintrag ohne Datum.
- **Grund:** Sonst landet frisch Gekauftes in einem womöglich abgelaufenen Eintrag und würde als „abgelaufen“ gewarnt. AUFTRAG.md, Abschnitt 7 („gleiches Produkt wird zusammengefasst“) bleibt für undatierte Einträge erfüllt.
- **Verworfene Alternative:** Beim Aufstocken das Datum löschen. Dann ginge die Warnung für die alte Ware verloren.

## E19: Übrige Befunde aus Review 02

- **Datum:** 07.10.2026
- **Behoben:** B1, M1–M4 (siehe E15, E17 und Tests), m1 (`parseEuro` für „1.000“ und „1.000,50“), m3 (Busy-Zustände), m4 („Alles verbraucht“ statt „Verbraucht“), m5 (E18), m6 (Ersatz-Handler für unerwartete Fehler mit 500 als ProblemDetail, zu lange Bild-URLs werden verworfen), m7 (ungültiges Barcode-Format → 400, kein externer Aufruf innerhalb einer Transaktion), m8 (`StoreService`), m9 (Scanner-Text und Kamera beim schnellen Schließen), m10 (Restbudget des Vormonats vom Backend, „über dem Budget“), m13 (Geldvergleich mit BigDecimal, Alibi-Test entfernt), m14 (Sparquoten der Persona als BigDecimal), m15 (Prompt-Tabelle, `docs/IDEEN.md`).
- **In Phase 4 erledigt:** m11 (Navigation auf schmalen Bildschirmen).
- **Zurückgestellt mit Grund:** m2 (Obergrenze im Frontend nur als Hinweistext, maßgeblich ist das Backend mit 409). m12 und m16 kommen als bekannte Risiken in STATUS.md und als Ideen in IDEEN.md. 401 ohne Body bleibt: Das Frontend reagiert nur auf den Status.

## E20: Demo-Bereiche in Phase 4 (Rangliste, Musterportfolios)

- **Datum:** 07.10.2026
- **Entscheidung:**
  - Rangliste: gewertet wird der letzte abgeschlossene Monat. Die **Serie** zählt aufeinanderfolgende abgeschlossene Monate bis dahin, in denen ein Budget existiert und die Ausgaben es nicht überschreiten (höchstens 24). Ein Monat ohne Budget oder über Budget unterbricht die Serie. Bei gleicher Sparquote und Serie entscheidet das Pseudonym alphabetisch, damit die Reihenfolge stabil bleibt. Der eingeloggte Nutzer steht nie selbst in der Liste. „Dein Platz wäre“ zählt alle, die strikt besser sind, plus 1. Die Regeln stehen als reine Funktionen in `leaderboard/Ranking.java`.
  - Musterportfolios: `InvestController` liest direkt das Enum `ModelPortfolio`, ohne Service. Es gibt keine Datenbankabfrage und keine Logik, ein Service wäre eine leere Weiterleitung. Der Rechner zu M4 bekommt einen eigenen Service.
- **Grund:** AUFTRAG.md, Abschnitt 7 nennt die Serie, ohne sie zu definieren (Review 00, m6).
- **Verworfene Alternative:** Serie auch über Monate ohne Budget fortsetzen. Ohne Budget gibt es aber keine Sparquote, die man vergleichen könnte.

## E21: Umgang mit dem finalen Review 03

- **Datum:** 08.10.2026
- **Behoben:**
  - M1: Auf schmalen Bildschirmen (unter 720 px) steht oben nur noch eine schmale Leiste mit Logo und Konto. Die Navigation ist eine untere Tab-Leiste mit den vier echten Bereichen plus „Mehr“ für Demo- und In-Arbeit-Bereiche. Der Button „Einkauf abschließen“ sitzt darüber (`--tabbar-height`). Test: `AppHeader.spec.ts`.
  - M2: Neue Screenshots `liste-warnung-*.png` (Vorrats-Warnung), `liste-abschliessen-*.png` (ausgefülltes Formular) und `liste-abschliessen-erfolg-*.png` (neues Restbudget).
  - M3: `SandboxCleanupIntegrationTest` löscht einen abgelaufenen Demo-Nutzer mit eigenem Produkt, Einkauf, Vorrat, Liste und Gewohnheiten und prüft, dass der Katalog bleibt und der nächste Demo-Login funktioniert. Kommt zu M4 eine Tabelle ohne `ON DELETE CASCADE` dazu, schlägt dieser Test an.
  - m1 (Rangliste auf dem Handy: Haushalt unter dem Namen, Serie sichtbar), m3 (`locked` nach dem ersten Budget), m4 (sichtbarer Hinweis „Kommt mit Milestone M4“ neben deaktivierten Buttons), m5 (Scanner wird erst beim Tippen auf „Kamera“ geladen, Haupt-Bundle 612 kB → 138 kB), m6 (`DataIntegrityViolationException` → 409), m7 (Budget-Monate vom Backend statt von der Browser-Uhr), m8 (Sandbox-Nutzer nur noch in `Ranking.rank` gefiltert, Abfrage `findByLeaderboardOptInTrue`), m9 (Vorrat: Datum auf dem Handy in eigener Zeile), m10 (Doku).
- **Zurückgestellt mit Grund:**
  - m1, zweiter Teil (Seed-Nutzer mit gleicher Sparquote, damit die Gleichstandsregel in der Demo sichtbar wird): Die Regel ist im `RankingTest` geprüft. Für die Demo zu M4 einplanen.
  - m2 (viele Einzelabfragen in der Rangliste, rund 200 pro Aufruf): Bei 15 Seed-Nutzern lokal unter einer Sekunde. Zu M4 mit gruppierten Abfragen ersetzen, bevor echte Nutzer dazukommen. Eingetragen in `docs/IDEEN.md`.
  - m11 (alle Commits von einem Konto): Das liegt beim Team. Die Empfehlung steht in STATUS.md.

## E22: npm-audit-Meldungen in Entwicklungswerkzeugen

- **Datum:** 08.10.2026
- **Entscheidung:** Die 4 „high“-Meldungen von `npm audit` werden nicht mit `npm audit fix --force` behoben. Alle vier gehen auf eine Lücke in `braces` zurück (GHSA-vfj7-8cjw-p6xm), die nur über `@vue/eslint-config-typescript` → `fast-glob` → `micromatch` geladen wird. Bei einem Update der ESLint-Konfiguration erneut prüfen.
- **Grund:** Nur ein Entwicklungswerkzeug ist betroffen. `npm audit --omit=dev` meldet 0 Lücken in dem, was ausgeliefert wird. Ausnutzbar wäre die Lücke nur mit bösartigen Suchmustern, die im Projekt nicht vorkommen. `--force` erlaubt Versionssprünge, die Lint oder Build brechen können.
- **Verworfene Alternative:** `npm audit fix --force`.
