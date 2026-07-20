# RCPL Partner Platform — Spring Boot backend

Legacy (non-agentic) system of record for the RCPL Partner Platform.
**Java 21 · Spring Boot 3 · Oracle · Flyway.** No LLMs, no agents, no copilot — all
scoring/routing/gating is deterministic Java and every state-changing action writes an audit row.

---

## 1. Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| JDK | 21 | `java -version` |
| Maven | 3.9+ | `mvn -v` |
| Oracle DB | 19c / 21c / 23ai | reachable JDBC URL (see step 2) |

> No Maven installed? You can use an IDE (IntelliJ / VS Code + Java) to run it instead — it will
> download dependencies and run `RcplApplication` for you.

---

## 2. Get an Oracle database

The app needs *any* reachable Oracle. Pick one:

### Option A — Docker (one command, nothing to install)
```bash
docker compose up -d          # starts Oracle Database Free on localhost:1521
```
First boot takes 1–2 minutes; the compose healthcheck gates readiness. This creates an app
user `rcpl` / `rcpl` and a pluggable DB `FREEPDB1` — which the default config already points at.

### Option B — Use an Oracle you already have
Point the app at it with env vars (step 3); no Docker needed:
```
DATABASE_URL = jdbc:oracle:thin:@//your-host:1521/YOURSERVICE
DB_USER = your_user
DB_PASSWORD = your_password
```

You do **not** need to create any tables — Flyway builds the entire schema on first startup.

---

## 3. Configure (environment variables)

All optional for local dev (sensible defaults are baked in). Override for real environments:

| Var | Default | Purpose |
|-----|---------|---------|
| `DATABASE_URL` | `jdbc:oracle:thin:@//localhost:1521/FREEPDB1` | Oracle JDBC URL |
| `DB_USER` / `DB_PASSWORD` | `rcpl` / `rcpl` | App DB credentials |
| `JWT_SECRET` | dev secret | HS256 signing key — **set a real 32+ byte value in prod** |
| `JWT_TTL_MINUTES` | `720` | Token lifetime |
| `CORS_ORIGINS` | `http://localhost:4200,http://localhost:5173` | Allowed SPA origins (Angular dev = 4200) |
| `SERVER_PORT` | `8080` | HTTP port |
| `APP_BOOTSTRAP_ADMIN_PASSWORD` | `admin123` | Bootstrap admin password |
| `INTAKE_POLL_ENABLED` | `false` | Turn on the IMAP intake poller |
| `IMAP_HOST/PORT/USER/PASSWORD` | — | Intake mailbox (only when polling is enabled) |

Set them inline, e.g.:
```bash
JWT_SECRET="a-very-long-random-string-at-least-32-bytes" mvn spring-boot:run
```

---

## 4. Run

```bash
cd backend-springboot
mvn spring-boot:run
```

On startup the app:
1. connects to Oracle,
2. runs Flyway migrations (`V1`–`V4`: schema + roles/permissions + template/onboarding/GTM reference data),
3. creates a **bootstrap admin** if the users table is empty,
4. serves on **http://localhost:8080**.

**Bootstrap admin (first run only):** `admin@rcpl.in` / `admin123` — change it immediately.
No demo/business data is seeded; everything else is created through the API.

Build a runnable jar instead:
```bash
mvn clean package        # -> target/rcpl-platform-0.1.0.jar
java -jar target/rcpl-platform-0.1.0.jar
```

---

## 5. Verify it's working

```bash
# health
curl http://localhost:8080/actuator/health
# -> {"status":"UP"}

# log in -> returns a JWT + profile
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@rcpl.in","password":"admin123"}'

# call a protected endpoint with the token
TOKEN="<paste accessToken from the login response>"
curl http://localhost:8080/api/partners -H "Authorization: Bearer $TOKEN"
```

**Interactive API docs (Swagger UI):** http://localhost:8080/swagger-ui — every route, with an
"Authorize" button to paste your bearer token and try calls in the browser.

---

## 6. The API at a glance

`Authorization: Bearer <jwt>` on every `/api/*` route. Each route enforces the persona's per-screen
`view`/`manage` permission; list/detail queries for state-bearing entities apply the row-level data
scope (`all` / `own_region` / `own_state`).

- **Auth:** `POST /auth/login`, `GET /auth/me`, `GET /auth/can/{screen}`
- **Core:** `partners`, `candidates`, `cases` (+ decision/docs/notes/score), `documents`,
  `intake`, `communication`, `grievances`, `notifications`, `audit`, `analytics/{section}`, `reports`
- **Admin:** `users`, `roles`
- **Config/extended:** `partner-types`, `config/onboarding`, `gtm-coverage`, `dashboard`, `me/settings`

---

## 7. Common issues

| Symptom | Fix |
|--------|-----|
| `ORA-...` / connection refused on startup | Oracle isn't ready yet — wait for the container healthcheck, or check `DATABASE_URL`/creds |
| `401 Unauthorized` on `/api/*` | Missing/expired token — log in again and send `Authorization: Bearer <jwt>` |
| `403 Forbidden` | The persona lacks `view`/`manage` on that screen — expected; use a permitted role or grant it in `/api/roles` |
| Flyway "checksum mismatch" | A migration file changed after being applied — use a fresh DB (drop/recreate the schema) in dev |
| Port 8080 in use | set `SERVER_PORT=8081` |

---

## 8. Project layout

```
src/main/java/com/rcpl/platform/
  RcplApplication.java     entry point
  config/                  security (JWT+CORS), OpenAPI, onboarding config
  common/                  errors, JSON/date/id helpers
  auth/                    JWT, permissions, data-scope, @RequireScreen, login
  partner/ candidate/ caseflow/ document/ intake/ communication/
  grievance/ notification/ audit/ analytics/ report/ user/
  template/ settings/ gtm/ dashboard/     (config + extended features)
src/main/resources/
  application.yml
  db/migration/            V1 schema · V2 roles/perms · V3 extended · V4 extended reference data
```

Each feature package holds its own `Controller` (REST) → `Service` (logic) → entity + `Repository`
(JPA) → DTOs. The approvals workflow lives in `caseflow/service/CaseService.java`.
