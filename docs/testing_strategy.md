# 測試策略

## 範圍

這份 template 目前保留兩層測試：

- 單元測試：驗證 domain rule 與 application orchestration
- 整合測試：驗證 HTTP、security、persistence、Flyway、serialization

## 版本基線

- Spring Boot `3.5.13`
- PostgreSQL `16`
- Flyway + PostgreSQL database plugin
- Testcontainers `1.21.4`

## 基本覆蓋情境

- Auth login 成功與失敗
- Auth `me`
- Task create、list、detail
- Task 狀態轉移
- not found 與 validation failure
- 非 admin 指派 task 的 forbidden 情境

## 測試慣例

- Domain lifecycle rule 用一般 unit test
- Application service 用 Mockito
- API integration test 用 `MockMvc`
- persistence / Flyway 驗證用共用的 Testcontainers PostgreSQL
- integration test 直接跑真實 Flyway migration，不依賴 Hibernate 自動建 schema

## 整合測試注意事項

- 跑測試的 Maven / JVM 進程必須能連到 Docker。
- 整合測試共用同一個 PostgreSQL container，避免 Spring context cache 重用舊 datasource。
- 如果環境限制 `~/.m2`，可使用 `-Dmaven.repo.local=/tmp/m2repo`。
- 若 Docker Desktop 已安裝但 Testcontainers 仍失敗，先檢查 Docker context 與實際解析到的 Testcontainers 版本。

## 指令

```bash
mvn test
```
