# Korbgeld

Semesterprojekt im Modul Web-Technologien (Wirtschaftsinformatik, HTW Berlin).

**Wie viel spare ich beim Einkaufen, und was würde aus dem Geld, wenn ich es anlege?** Korbgeld begleitet einen Lebensmitteleinkauf von Anfang bis Ende: Monatsbudget festlegen, Einkaufsliste per Eingabe oder Barcode füllen, im Laden abhaken, Einkauf mit der Summe vom Kassenbon abschließen. Die Artikel wandern in den Vorrat, und das Restbudget sinkt.

| Bereich | Stand |
|---|---|
| Budget, Liste mit Barcode, Einkauf abschließen, Vorrat, Dashboard | fertig, Daten in Postgres, getestet |
| Gewohnheiten, Rangliste, Sparplan | Demo mit echten Beispieldaten, noch ohne eigene Eingaben (M4) |
| Rezepte, Preisvergleich, Profil | in Arbeit |

Details zu Use Cases, Tests und Screenshots: [`docs/STATUS.md`](docs/STATUS.md). Deployment auf Render und Neon: [`docs/DEPLOY.md`](docs/DEPLOY.md).

## Technik

- **Backend:** Java 25, Spring Boot 4, Gradle 9, Spring Data JPA mit Postgres, Flyway, Spring Security mit JWT (HS256)
- **Frontend:** Vue 3 mit TypeScript, Vite, Vue Router, Pinia, Vitest, eigenes CSS
- **Datenbank:** Postgres 17, lokal in Docker, in der Produktion bei Neon
- **CI:** GitHub Actions für Backend und Frontend

```
Browser (Vue) ──HTTP/JSON + JWT──► Spring Boot (Controller → Service → Repository) ──JPA──► Postgres
```

## Lokal starten

Voraussetzungen: Git, Java 25, Node 26, Docker Desktop (läuft). Alles kostenlos.

**1. Konfiguration anlegen** (einmalig, im Repo-Wurzelordner):

```bash
cp .env.example .env
```

In `.env` zwei Werte eintragen:
- `DB_PASSWORD`: ein beliebiges Passwort für die lokale Datenbank
- `JWT_SECRET`: mindestens 32 zufällige Zeichen, z. B. aus `openssl rand -hex 32`

Die `.env` ist in `.gitignore` und landet nie im Repo.

**2. Datenbank starten:**

```bash
docker compose up -d
```

**3. Backend starten** (Port 8080, beim ersten Start legt Flyway die Tabellen an und der Seeder die Beispieldaten):

```bash
cd backend
./gradlew bootRun        # Windows: .\gradlew.bat bootRun
```

**4. Frontend starten** (in einem zweiten Terminal):

```bash
cd frontend
npm ci
npm run dev              # Windows-PowerShell: npm.cmd run dev
```

Dann http://localhost:5173 öffnen und **„Als Demo testen“** klicken. Die Demo legt einen eigenen Zugang mit 12 Monaten Beispieldaten an.

Prüfen, ob das Backend läuft: http://localhost:8080/actuator/health zeigt `{"status":"UP"}`.

**`bootRun` bricht mit „non-zero exit value 1“ ab?** Meist läuft die Datenbank nicht, z. B. nach einem Neustart des Rechners. Docker Desktop öffnen und `docker compose up -d` im Wurzelordner ausführen. Die eigentliche Ursache steht im Terminal weiter oben beim untersten `Caused by:`. Weitere mögliche Ursachen: Port 8080 ist schon belegt (ein zweites Backend läuft noch) oder `JWT_SECRET` in der `.env` hat weniger als 32 Zeichen.

## Tests

```bash
cd backend && ./gradlew test                       # Unit-, Web- und Integrationstests (Testcontainers, Docker nötig)
cd frontend && npm run test:unit -- --run          # Komponenten-, Router- und API-Tests (Vitest)
cd frontend && npm run lint-ci                     # Lint wie in CI
```

Ohne Docker werden die Testcontainers-Tests übersprungen. In GitHub Actions laufen sie immer.

## Projektstruktur

```
backend/     Spring Boot, Pakete nach Fachbereich (auth, budget, shopping, pantry, product, habit, leaderboard, invest, common)
frontend/    Vue-App (views, components, api, stores, router)
docs/        STATUS, DEPLOY, ENTSCHEIDUNGEN, Reviews, Screenshots, Lernmaterial (lernen/)
```

## Datenbank ansehen

```bash
docker exec -it korbgeld-db psql -U korbgeld -d korbgeld
```

Mehr dazu in [`docs/lernen/DATENBANK.md`](docs/lernen/DATENBANK.md).
