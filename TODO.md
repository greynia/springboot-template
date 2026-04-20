# TODO

## 第一優先

- [x] 補上 OpenAPI / Swagger UI
- [x] 補上可設定的 CORS 白名單
- [x] 在 README 明確標示目前 security 只是 skeleton，不是 production-ready
- [x] 實跑 `docker compose up --build` 並驗證 backend container 可正常啟動

## 第二優先

- [x] 補 refresh token skeleton
- [x] 補 password policy 與修改密碼流程 skeleton
- [x] 補 profile 專用設定，例如 `application-docker.properties`
- [ ] 補常用 entity 的 audit field 策略

## 第三優先

- [x] 補 Actuator 與 health/readiness endpoint
- [x] 補最小 observability 預設與 request correlation 策略
- [ ] 補超出 `ADMIN` / `USER` 的最小 RBAC 延伸指引
- [x] 補 pagination 與 query filter pattern 範例
- [x] 補 `mvn test` 的 CI workflow
