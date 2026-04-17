# VS Code 零到一建置教學

## 目標

這份文件說明如何在 VS Code 環境下，從零開始建置、啟動、測試、並延伸這份 Spring Boot template。

## 工具版本

建議版本基線如下：

- VS Code：最新版 stable
- Java SDK：`17`
- Maven：`3.9.12` 或更新版本
- Docker Desktop：最新版 stable
- PostgreSQL：`16`，若你要在沒有 Docker 的情況下本地啟 DB

## VS Code 外掛

請在 VS Code 安裝以下外掛：

- `vscjava.vscode-java-pack`
  Java 核心開發包，包含 Java 語言支援、debugger、test runner、Maven 支援、Java project 管理等。
- `vmware.vscode-boot-dev-pack`
  Spring Boot 開發包，包含 Spring Boot Tools、Spring Initializr Java Support、Spring Boot Dashboard。
- `vscjava.vscode-maven`
  Maven explorer 與 Maven 指令整合。
- `redhat.vscode-yaml`
  強化 YAML 編輯體驗，適合 Docker Compose 與 Spring 設定。
- `redhat.vscode-xml`
  強化 `pom.xml` 編輯體驗。
- `ms-azuretools.vscode-docker`
  提供 Dockerfile 與 Compose 編輯支援。

這個 repo 也已包含 [.vscode/extensions.json](/Users/greynia/Desktop/Personal/springboot-template/.vscode/extensions.json:1)，VS Code 會自動提示建議安裝。

## 安裝 Java 17

安裝 JDK 17，例如 Eclipse Temurin 17。

安裝完成後，請確認：

```bash
java -version
javac -version
```

預期 major version 為 `17`。

## 安裝 Maven

安裝 Maven `3.9.12` 或更新版本。

確認：

```bash
mvn -v
```

預期：

- Java version 指向 JDK `17`
- Maven version 為 `3.9.x` 或更新

## 安裝 Docker Desktop

Docker Desktop 主要用於：

- 整合測試時的 Testcontainers
- 本地用 Compose 啟動整包環境

確認：

```bash
docker version
docker ps
```

## 在 VS Code 開啟本 Repo

1. 開啟 VS Code
2. 選擇 `File -> Open Folder...`
3. 選取 repo 根目錄 `springboot-template`
4. 等待 Java project 匯入完成

如果 VS Code 要你指定 JDK，請指向你的 JDK 17。

## 專案結構

- `backend/`
  Spring Boot application
- `docs/`
  package rules、testing strategy、bootstrap checklist、tutorial
- `docker-compose.yml`
  本地 PostgreSQL + backend 啟動用 deployment stack

## pom.xml 內的核心依賴

目前 [backend/pom.xml](/Users/greynia/Desktop/Personal/springboot-template/backend/pom.xml:1) 已包含：

- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-actuator`
- `flyway-core`
- `flyway-database-postgresql`
- `springdoc-openapi-starter-webmvc-ui`
- `postgresql`
- `jjwt-api`
- `jjwt-impl`
- `jjwt-jackson`
- `spring-boot-starter-test`
- `spring-security-test`
- `spring-boot-testcontainers`
- `testcontainers-junit-jupiter`
- `testcontainers-postgresql`

除非專案有特殊需求，例如 OpenAPI 以外的功能、cache、mail、object storage，不然通常不需要在 v1 backend 起步時額外加更多依賴。

## 本地啟動專案

### 方案 A：DB 用 Docker，app 用 VS Code 或 terminal 啟動

先只啟 PostgreSQL：

```bash
docker compose up -d postgres
```

再啟 backend：

```bash
cd backend
mvn spring-boot:run
```

API 預設位置：

- `http://localhost:8080`

### 方案 B：用 Docker Compose 啟整包

先建立本地 Compose env 檔：

```bash
cp .env.example .env
```

再執行：

```bash
docker compose up --build
```

會啟動：

- PostgreSQL：`5432`
- Backend：`8080`

如果本機的 `8080` 已被占用：

```bash
BACKEND_PORT=8081 docker compose up --build
```

此時 backend 會在 `http://localhost:8081`。

### Docker Profile

Compose 啟動時會使用 [application-docker.properties](/Users/greynia/Desktop/Personal/springboot-template/backend/src/main/resources/application-docker.properties:1)。

它的用途是：

- 提供容器環境下的 datasource 預設值
- 將 Docker runtime 設定與本機手動設定分開
- 放置 Docker 專用的 health / readiness 設定

### Compose 環境變數

Compose 會讀 `.env`。

請從 [`.env.example`](/Users/greynia/Desktop/Personal/springboot-template/.env.example:1) 開始：

- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `POSTGRES_PORT`
- `BACKEND_PORT`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`

這些預設值適合 local template bootstrap，但實際專案必須替換，不能當 production secret 管理方式。

## 跑測試

terminal 指令：

```bash
cd backend
mvn test
```

如果你的環境限制 `~/.m2`：

```bash
cd backend
mvn -Dmaven.repo.local=/tmp/m2repo test
```

## 在 VS Code 跑測試

1. 開啟 `Testing` 面板
2. 等待 Java tests 被 discover
3. 執行單一測試、單一 class，或全部測試

若要跑 integration test，Docker 必須先啟動。

## Demo 登入帳號

Flyway seed data 跑完後可使用：

- `admin@example.com / password123`
- `user1@example.com / password123`
- `user2@example.com / password123`

## API 端點

公開端點：

- `POST /api/auth/login`
- `GET /actuator/health`
- `GET /actuator/info`
- `GET /v3/api-docs`
- `GET /swagger-ui/index.html`

需要 JWT 的端點：

- `GET /api/auth/me`
- `POST /api/tasks`
- `GET /api/tasks`
- `GET /api/tasks/{id}`
- `PATCH /api/tasks/{id}/start`
- `PATCH /api/tasks/{id}/complete`
- `PATCH /api/tasks/{id}/cancel`
- `PATCH /api/tasks/{id}/assign`

完整 API 清單可參考 [API 清單](docs/api_surface.md)。

## 如何新增新模組

請以既有 `Task` module 當範例。

### 第一步：定義 domain

在以下路徑建立檔案：

- `domain/<module>/model`
- `domain/<module>/service`
- `domain/<module>/exception`

這裡放不變條件與狀態轉移規則。

### 第二步：定義 application service

在以下路徑建立檔案：

- `application/<module>/`

常見 class：

- `<Module>ApplicationService`
- `<Module>QueryService`
- `<Module>Mapper`

這裡負責 use case orchestration。

### 第三步：定義 API

在以下路徑建立檔案：

- `api/controller`
- `api/dto/<module>`

加入 request DTO、response DTO、controller endpoint。

### 第四步：定義 persistence

在以下路徑建立檔案：

- `infrastructure/persistence/jpa/entity`
- `infrastructure/persistence/jpa/repository`

加入 entity mapping 與 repository interface。

### 第五步：新增 migration

在以下路徑新增 Flyway SQL：

- `backend/src/main/resources/db/migration`

命名範例：

- `V3__add_projects.sql`
- `V4__seed_projects.sql`

### 第六步：補測試

至少要補：

- domain/application unit test
- API integration test with MockMvc

## 如何在 VS Code 新增 Java 檔案

建議方式：

1. 開啟 `JAVA PROJECTS` 面板
2. 展開 `backend/src/main/java`
3. 在目標 package 上按右鍵
4. 選擇 `New Java Class`、`New Java Record` 或 `New Package`

也可以手動：

1. 在正確資料夾建立 `.java` 檔
2. 讓 VS Code 自動推斷 package declaration

## 建議的第一天流程

1. 安裝 JDK 17、Maven、Docker Desktop、VS Code 外掛
2. 用 VS Code 開啟這個 repo
3. 執行 `docker compose up -d postgres`
4. 執行 `cd backend && mvn test`
5. 執行 `cd backend && mvn spring-boot:run`
6. 測試 `POST /api/auth/login`
7. 以 `Task` module 當作第一個業務模組的參考樣板

## Actuator 端點

目前 template 已暴露：

- `GET /actuator/health`
- `GET /actuator/info`

用途：

- 本地 smoke check
- Docker / container health 驗證
- 未來接 liveness / readiness

## GitHub Actions Workflow

本 repo 已包含 [.github/workflows/backend-test.yml](/Users/greynia/Desktop/Personal/springboot-template/.github/workflows/backend-test.yml:1)。

目前會：

- 在 `main`、`develop` push 時執行
- 在每個 pull request 執行
- 自動安裝 Java `17`
- 執行 `cd backend && mvn test`

## 常見問題

- Maven 用到錯的 JDK：
  檢查 `mvn -v` 是否真的指向 Java 17。
- VS Code 無法正確匯入專案：
  安裝完 Java extensions 後重新 reload window，並確認你開的是 repo 根目錄，不是單一檔案。
- Integration test 一開始就失敗：
  檢查 `docker ps`，確認 Docker Desktop 已正常啟動。
- Backend 無法連 DB：
  檢查 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`。
- Docker Compose backend 因為 `8080` 被占用而無法啟動：
  使用 `BACKEND_PORT=8081` 或其他可用 port 啟動。
