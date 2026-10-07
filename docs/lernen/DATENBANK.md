# Datenbank: Anbindung, Verwaltung, Beobachtung

Spickzettel für die lokale Entwicklungsdatenbank (Postgres 17 in Docker). Befehle immer im Repo-Wurzelordner ausführen.

## 1. Wer baut die Verbindung auf?

Eigener Java-Code ist dafür nicht nötig. Wir liefern nur **Konfiguration**, die Arbeit erledigt Spring Boot.

| Datei (unsere) | Rolle |
|---|---|
| `backend/build.gradle` | `spring-boot-starter-data-jpa` bringt HikariCP, Hibernate und `spring-boot-jdbc` mit. `runtimeOnly 'org.postgresql:postgresql'` ist der JDBC-Treiber. |
| `.env` | Die Werte: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_SSLMODE` |
| `backend/src/main/resources/application.yml` | `spring.config.import` liest die `.env`. `spring.datasource.*` setzt URL, Nutzer und Passwort, `spring.datasource.hikari.*` stellt den Pool ein. |
| `docker-compose.yml` | Startet Postgres mit denselben Werten aus der `.env` |

| Klasse (Spring Boot 4.1.1) | Was sie tut |
|---|---|
| `org.springframework.boot.jdbc.autoconfigure.DataSourceProperties` | `@ConfigurationProperties("spring.datasource")`: überträgt die YAML-Werte in ein Java-Objekt (`url`, `username`, `password`) |
| `org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration` | Startpunkt. Läuft, wenn `DataSource` im Klassenpfad ist. |
| `org.springframework.boot.jdbc.autoconfigure.DataSourceConfiguration.Hikari` | `@ConditionalOnClass(HikariDataSource.class)` und `@ConditionalOnMissingBean(DataSource.class)`. Erzeugt die Bean `HikariDataSource` und überträgt `spring.datasource.hikari.*` darauf. |
| `org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration` | Führt beim Start die Migrationen aus `db/migration` aus |
| `org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration` | Startet Hibernate auf derselben `DataSource`. `ddl-auto: validate` prüft die Entities gegen die Tabellen. |
| `org.springframework.boot.jdbc.health.DataSourceHealthIndicator` | Teil von `/actuator/health`. Holt sich eine Verbindung und prüft, ob die Datenbank antwortet. |

In IntelliJ öffnet `Strg+N` und dann der Klassenname den Quelltext.

## 2. Container verwalten

| Zweck | Befehl |
|---|---|
| Starten | `docker compose up -d` |
| Status (healthy?) | `docker compose ps` |
| Anhalten / fortsetzen (Daten bleiben) | `docker compose stop` / `docker compose start` |
| Container entfernen (Daten bleiben im Volume) | `docker compose down` |
| **Alles löschen, auch die Daten** | `docker compose down -v` ⚠️ Danach baut Flyway das Schema beim nächsten Backend-Start neu auf. |
| Logs der Datenbank live | `docker compose logs -f postgres` |

## 3. In die Datenbank schauen (psql)

Interaktiv, in einem **eigenen Terminal** (nicht über `!` im Chat, weil `-it` eine Tastatur braucht):

```
docker exec -it korbgeld-db psql -U korbgeld -d korbgeld
```

| psql-Befehl | Bedeutung |
|---|---|
| `\dt` | Tabellen auflisten |
| `\d list_item` | Spalten, Typen und Constraints einer Tabelle |
| `SELECT * FROM list_item;` | Daten ansehen (Semikolon nicht vergessen) |
| `\x` | Breite Zeilen untereinander anzeigen |
| `\q` | Beenden |

Einzelne Abfrage ohne interaktive Sitzung (geht auch über `!`):
```
docker exec korbgeld-db psql -U korbgeld -d korbgeld -c "SELECT * FROM flyway_schema_history;"
```

## 4. Beobachten

| Frage | Wo man es sieht |
|---|---|
| Lebt die Verbindung? | `http://localhost:8080/actuator/health` → `UP` (200) oder `DOWN` (503) |
| Wer ist verbunden? | `SELECT client_addr, application_name, state, query FROM pg_stat_activity WHERE client_addr IS NOT NULL;` |
| Welche Migrationen sind gelaufen? | `SELECT installed_rank, version, description, success FROM flyway_schema_history;` |
| Welches SQL schickt Hibernate? | Backend-Log im Dev-Profil (`application-dev.yml`: `org.hibernate.SQL` und `org.hibernate.orm.jdbc.bind`) |
| Wie groß ist die Datenbank? (Neon Free: 0,5 GB) | `SELECT pg_size_pretty(pg_database_size('korbgeld'));` |
| Was macht der Pool? | Backend-Log `HikariPool-1 …` |

## 5. Regeln für Schemaänderungen

- Tabellen ändert **nur Flyway**: neue Datei `V2__beschreibung.sql`, `V3__…` in `backend/src/main/resources/db/migration`.
- Eine bereits ausgeführte Migration **nie nachträglich ändern**. Flyway speichert eine Prüfsumme und verweigert sonst den Start.
- Nicht von Hand per psql oder GUI Spalten anlegen. Sonst passt die lokale Datenbank nicht mehr zu den Migrationen, und die Produktion (Neon) bekommt die Änderung nie.
- Daten ansehen und zum Testen von Hand einfügen ist erlaubt.

## 6. Sichern und zurücksetzen

| Zweck | Befehl |
|---|---|
| Sicherung als SQL-Datei | `docker exec korbgeld-db pg_dump -U korbgeld korbgeld > backup.sql` (nicht committen) |
| Komplett neu anfangen | `docker compose down -v` und dann `docker compose up -d`, Backend neu starten |
