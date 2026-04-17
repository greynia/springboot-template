# springboot-template

這是一份務實可落地的 Spring Boot 後端模板，用來快速啟動商業系統。它內建清楚的 package 分層、JWT auth skeleton、Flyway migration、JPA persistence，以及中性的 `Task` demo module。

## Production Warning

目前這份 security 實作是 starter skeleton，不是 production-ready security package。

目前已包含：

- JWT login 與 `me`
- Spring Security filter chain
- 集中的 API error handling
- 可設定的 CORS whitelist

目前刻意還沒包含：

- refresh token flow
- key rotation 與 secret management 策略
- password policy 與 password reset / change flow
- 超出 `ADMIN` / `USER` 的完整 RBAC
- production 等級的 CORS policy review
- observability、audit、security hardening 預設值

## 版本基線

這份 template 的 backend 版本基線已對齊 `workflow-approval-system`。

- Spring Boot `3.5.13`
- Java `17`
- PostgreSQL `16`
- Flyway + `flyway-database-postgresql`
- JJWT `0.12.7`
- Testcontainers `1.21.4`

## 這個 Repo 目前包含什麼

- `backend/` Spring Boot application
- 清楚分層：`api / application / domain / infrastructure / common`
- exception hierarchy 與 centralized API error handling
- JWT auth skeleton：`login` 與 `me`
- OpenAPI / Swagger UI
- JPA + Flyway + PostgreSQL
- 中性的 `Task` demo aggregate
- unit test 與 integration test（MockMvc + Testcontainers）
- bootstrap 與 package rules 文件

## Quick Start

1. 複製 `backend/src/main/resources/application-local.properties.example` 為 `application-local.properties`
2. 如果你想用 Compose 啟動，先把 `.env.example` 複製成 `.env`
3. 修改 database 設定與 JWT secret
4. 啟動 PostgreSQL
5. 啟動 backend

```bash
cd backend
mvn spring-boot:run
```

## Demo 帳號

Flyway seed data 跑完後可使用：

- `admin@example.com / password123`
- `user1@example.com / password123`
- `user2@example.com / password123`

這些值只適合 local bootstrap，實際專案必須替換。

## Demo API Surface

- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/tasks`
- `GET /api/tasks`
- `GET /api/tasks/{id}`
- `PATCH /api/tasks/{id}/start`
- `PATCH /api/tasks/{id}/complete`
- `PATCH /api/tasks/{id}/cancel`
- `PATCH /api/tasks/{id}/assign`

Actuator / 文件端點：

- `GET /actuator/health`
- `GET /actuator/info`
- `GET /v3/api-docs`
- `GET /swagger-ui/index.html`

Swagger UI：

- `http://localhost:8080/swagger-ui/index.html`
- 如果你的本機 `8080` 已被占用，可改用 `BACKEND_PORT=8081`，然後使用 `http://localhost:8081/swagger-ui/index.html`

權限規則：

- 公開：
  `POST /api/auth/login`、`GET /actuator/health`、`GET /actuator/info`、`GET /v3/api-docs`、`GET /swagger-ui/index.html`
- 需要 JWT：
  `GET /api/auth/me`、所有 `/api/tasks/**`

## Run Tests

```bash
cd backend
mvn test
```

整合測試需要 Docker，因為它會使用 Testcontainers PostgreSQL。

這個 repo 已實際驗證過 Compose end-to-end：

```bash
cp .env.example .env
BACKEND_PORT=8081 docker compose up --build -d
```

如果你的本機 `8080` 已被占用，請改用其他 `BACKEND_PORT`。

`docker-compose.yml` 會優先讀 `.env`，如果某個變數未設定，才會回退到 template 內的 demo 預設值。

這個 repo 也已實際驗證過：

```bash
cd backend
mvn -Dmaven.repo.local=/tmp/m2repo test
```

若你的本機 `~/.m2` 可寫，直接用 `mvn test` 即可。`/tmp` repo override` 只在受限環境下有需要。

## Notes

- Docker Desktop + Testcontainers 已在這個 repo 驗證通過
- Docker Compose 啟動 `postgres` 與 `backend` 已驗證通過
- 已提供 `.env.example` 供本地 Compose 啟動使用，應先複製成 `.env` 再做團隊或專案客製化
- seed data 在固定 `id` insert 後會同步 PostgreSQL sequence，避免後續 demo 寫入撞主鍵
- 這份 template 從 Spring Boot `3.3.5` 對齊到 `3.5.13` 時，沒有出現需要重寫語法的 API break；主要工作在依賴與測試基礎設施相容性
- `application-docker.properties` 是 Compose 啟動時使用的 runtime profile
- GitHub Actions 目前會在 `main`、`develop` push 與 pull request 時自動跑 backend tests

## 文件

- [API 清單](docs/api_surface.md)
- [Package 規則](docs/package_rules.md)
- [測試策略](docs/testing_strategy.md)
- [專案 Bootstrap Checklist](docs/project_bootstrap_checklist.md)
- [VS Code 零到一建置教學](docs/vscode_zero_to_one_tutorial.md)
