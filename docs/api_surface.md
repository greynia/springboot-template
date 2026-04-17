# API 清單

## 公開端點

以下端點不需要 JWT 驗證。

- `POST /api/auth/login`
  使用 demo 帳號登入並取得 JWT access token。
- `GET /actuator/health`
  提供基本健康檢查狀態，可用於 liveness / readiness 檢查。
- `GET /actuator/info`
  提供基本應用資訊。
- `GET /v3/api-docs`
  OpenAPI JSON 文件。
- `GET /swagger-ui/index.html`
  Swagger UI 頁面。

## 需要登入的端點

以下端點需要帶上 `Authorization: Bearer <token>`。

- `GET /api/auth/me`
  取得目前登入使用者資訊。
- `POST /api/tasks`
  建立 task。
- `GET /api/tasks`
  取得 task 清單。
- `GET /api/tasks/{id}`
  取得單筆 task 詳細資料。
- `PATCH /api/tasks/{id}/start`
  將 task 狀態切換為開始進行。
- `PATCH /api/tasks/{id}/complete`
  將 task 狀態切換為完成。
- `PATCH /api/tasks/{id}/cancel`
  將 task 狀態切換為取消。
- `PATCH /api/tasks/{id}/assign`
  指派 task。此模板預設設計為 admin-only 流程示範。

## Demo 操作流程

1. 呼叫 `POST /api/auth/login`
2. 複製回傳的 `accessToken`
3. 在後續 request 加上 `Authorization: Bearer <token>`
4. 呼叫 `GET /api/auth/me` 或任一個 `tasks` 相關端點

## Login Request 範例

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "password123"
}
```

## 已登入 Request 範例

```http
GET /api/tasks
Authorization: Bearer <token>
```
