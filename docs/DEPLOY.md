# Deployment auf Neon und Render

Klick-für-Klick-Anleitung, um Korbgeld öffentlich erreichbar zu machen (Milestone M3, 22. Nov.). Alles bleibt dauerhaft kostenlos.

> **Kostenregel:** Verlangt eine Seite eine Kreditkarte, eine Zahlungsmethode oder das Starten einer Testphase, **nichts eingeben und abbrechen**. Dann Tutor oder Prof fragen. Es gibt kein `render.yaml` und **keine Render-Datenbank**: Die kostenlose Render-Postgres wird nach 30 Tagen gelöscht (siehe `docs/ENTSCHEIDUNGEN.md`, E1).

Plant dafür etwa eine Stunde ein. Probiert es spätestens eine Woche vor M3 aus (E3: bis 15.11.).

```
 Browser ──► Render Static Site (Frontend, dist/) ──fetch + JWT──► Render Web Service (Backend, Docker) ──► Neon (Postgres)
```

## 1. Datenbank bei Neon

1. https://neon.com öffnen, **Sign up** und **Continue with GitHub** wählen. Der Free-Plan braucht keine Kreditkarte.
2. **Create project**:
   - Name: `korbgeld`
   - Postgres version: **17**. So läuft dieselbe Version wie lokal und in den Tests (E6). Steht 17 nicht zur Auswahl, die nächste angebotene Version nehmen und in `docs/ENTSCHEIDUNGEN.md` notieren.
   - Region: **AWS Europe (Frankfurt)**
3. Im Projekt bleibt der Branch **`main`**. Das ist die Produktionsdatenbank.
4. **Connect** öffnen und die Verbindungsdaten notieren. **Wichtig: „Connection pooling“ ausschalten**, damit der Hostname **kein** `-pooler` enthält. Flyway verträgt sich nicht mit dem Transaktions-Pooler.
   - Host, z. B. `ep-xxx-123456.eu-central-1.aws.neon.tech` (ohne `-pooler`)
   - Database, z. B. `neondb`
   - User, z. B. `neondb_owner`
   - Password
5. Die Datenbank schläft nach 5 Minuten ohne Abfragen ein und wacht beim nächsten Zugriff in Sekundenbruchteilen auf. Das Backend ist darauf eingestellt (kleiner Pool, kurze `max-lifetime`).

## 2. Backend als Render Web Service

1. https://render.com öffnen, **Get Started** und **GitHub** wählen.
2. **New** → **Web Service** → das Repository `WebtechMA` verbinden.
3. Einstellungen:

| Feld | Wert |
|---|---|
| Name | `korbgeld-backend` |
| Region | Frankfurt (EU Central) |
| Branch | `main` |
| Root Directory | `backend` |
| Runtime / Language | **Docker** (Render hat keine eingebaute Java-Laufzeit; `backend/Dockerfile` baut alles) |
| Instance Type | **Free** |
| Health Check Path | `/actuator/health` (unter „Advanced“) |

4. **Environment Variables** eintragen:

| Variable | Wert |
|---|---|
| `DB_HOST` | Neon-Host **ohne** `-pooler` |
| `DB_PORT` | `5432` |
| `DB_NAME` | Neon-Datenbankname |
| `DB_USER` | Neon-User |
| `DB_PASSWORD` | Neon-Passwort |
| `DB_SSLMODE` | `require` |
| `JWT_SECRET` | mindestens 32 zufällige Zeichen, z. B. aus `openssl rand -hex 32`. Nicht das lokale Secret wiederverwenden. |
| `FRONTEND_URL` | vorerst leer lassen, kommt in Schritt 4 |
| `TWELVEDATA_API_KEY` | optional, erst ab M4 |

   `PORT` setzt Render selbst. Das Backend liest ihn über `server.port: ${PORT:8080}`.
5. **Create Web Service**. Der erste Build dauert einige Minuten. Im Log steht danach `Successfully applied … migrations` und `Started KorbgeldApplication`.
6. Prüfen: `https://korbgeld-backend.onrender.com/actuator/health` zeigt `{"status":"UP"}`. Die Adresse ohne Schrägstrich am Ende notieren.

**Speicher:** Free-Instanzen haben 512 MB. Das Dockerfile begrenzt den Heap auf 75 % (`JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75`). Bricht der Dienst mit „Out of memory“ ab, `JAVA_TOOL_OPTIONS` in den Umgebungsvariablen auf `-XX:MaxRAMPercentage=60` setzen und neu deployen (E3).

## 3. Frontend als Render Static Site

1. **New** → **Static Site** → dasselbe Repository.
2. Einstellungen:

| Feld | Wert |
|---|---|
| Name | `korbgeld` |
| Branch | `main` |
| Root Directory | `frontend` |
| Build Command | `npm ci && npm run build` |
| Publish Directory | `dist` |

3. **Environment Variables:**

| Variable | Wert |
|---|---|
| `VITE_API_BASE_URL` | Adresse des Backends aus Schritt 2, z. B. `https://korbgeld-backend.onrender.com` (ohne `/` am Ende) |
| `NODE_VERSION` | `26` (zusätzlich steht `26` in `frontend/.node-version`) |

   ⚠️ `VITE_API_BASE_URL` wird **beim Bauen** fest ins JavaScript geschrieben. Fehlt sie, meldet die App bei jedem Aufruf „VITE_API_BASE_URL fehlt“. Nach einer Änderung muss die Static Site neu gebaut werden (**Manual Deploy** → **Clear build cache & deploy**).
4. **Redirects/Rewrites** (im Menü der Static Site), damit Neuladen auf Unterseiten wie `/liste` funktioniert:

| Source | Destination | Action |
|---|---|---|
| `/*` | `/index.html` | **Rewrite** |

   Ohne diese Regel liefert Render beim Neuladen von `/liste` eine 404, weil es keine Datei `liste` gibt. Die App ist eine Single-Page-Application, und der Vue Router übernimmt im Browser.
5. **Create Static Site**, die Adresse notieren, z. B. `https://korbgeld.onrender.com`.

## 4. Beide verbinden (CORS)

1. Im Web Service (Backend) `FRONTEND_URL` auf die Adresse der Static Site setzen, **genau so**, mit `https://` und **ohne** Schrägstrich am Ende: `https://korbgeld.onrender.com`. CORS vergleicht die Adresse Zeichen für Zeichen.
2. Speichern, Render deployt das Backend neu.

## 5. Smoke-Test

1. Frontend öffnen → **Als Demo testen** → das Dashboard zeigt das Restbudget 203,75 € (Beispieldaten von „Mia“).
2. Liste: einen Artikel abhaken → **Einkauf abschließen** → Laden und Summe eingeben → das neue Restbudget erscheint, der Artikel steht im Vorrat.
3. `/rezepte` zeigt „Rezepte ist in Arbeit“, `/gibt-es-nicht` zeigt „Diese Seite gibt es nicht“.
4. Seite auf `/liste` neu laden: Die Liste erscheint wieder (Rewrite-Regel aktiv).
5. Per curl:
   ```bash
   curl -s https://korbgeld-backend.onrender.com/actuator/health
   curl -s -X POST https://korbgeld-backend.onrender.com/api/auth/demo | head -c 80
   ```

## Bekannte Stolperstellen

| Problem | Ursache | Lösung |
|---|---|---|
| Erster Aufruf dauert 30–60 Sekunden | Kostenlose Render-Dienste schlafen nach 15 Minuten ohne Aufrufe ein | **Vor einer Demo die App einmal öffnen** und warten, bis das Dashboard lädt |
| Browser-Konsole: `blocked by CORS policy` | `FRONTEND_URL` stimmt nicht genau (Schrägstrich, `http` statt `https`) | Wert in Schritt 4 prüfen |
| „VITE_API_BASE_URL fehlt“ | Variable fehlte beim Build | Setzen und mit geleertem Cache neu bauen |
| Backend startet nicht: „JWT_SECRET fehlt oder hat weniger als 32 Zeichen“ | Secret fehlt oder ist zu kurz | Längeres Secret eintragen |
| Flyway-Fehler beim Start | Neon-Host mit `-pooler` | Direkten Host ohne `-pooler` eintragen |
| „Heute sind keine weiteren Demo-Zugänge möglich“ (429) | Tageslimit von 100 Demo-Logins erreicht (Schutz der 0,5 GB bei Neon) | Am nächsten Tag geht es wieder. Vor der Demo frühzeitig einloggen und den Tab offen lassen. |
| Kamera für den Barcode geht nicht | Kamera nur unter HTTPS oder localhost | Auf Render ist HTTPS aktiv. Sonst die Nummer von Hand eingeben. |
