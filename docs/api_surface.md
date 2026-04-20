# API 清單

## 公開端點

以下端點不需要 JWT 驗證。

- `POST /api/auth/login`
  使用 demo 帳號登入並取得 access token 與 refresh token。
- `POST /api/auth/refresh`
  用 refresh token 換新的一組 access token / refresh token。
- `GET /actuator/health`
  提供基本健康檢查狀態，可用於 liveness / readiness 檢查。
- `GET /actuator/info`
  提供基本應用資訊。
- `GET /actuator/metrics`
  提供基本 metrics 資訊。
- `GET /v3/api-docs`
  OpenAPI JSON 文件。
- `GET /swagger-ui/index.html`
  Swagger UI 頁面。

## 需要登入的端點

以下端點需要帶上 `Authorization: Bearer <token>`。

- `GET /api/auth/me`
  取得目前登入使用者資訊。
- `POST /api/auth/logout`
  登出目前使用者，並撤銷其所有有效 refresh token。
- `POST /api/auth/change-password`
  修改目前登入使用者密碼，並撤銷既有 refresh token。
- `POST /api/tasks`
  建立 task。
- `GET /api/tasks`
  取得 task 清單。分頁欄位包含 `currentPage`、`totalCount`、`pageSize`、`totalPages`。
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
- `GET /api/suppliers`
  取得 supplier 清單，支援 `keyword`、`status`、`page`、`pageSize`。
- `GET /api/suppliers/{id}`
  取得單筆 supplier 詳細資料。
- `POST /api/suppliers`
  建立 supplier。admin-only。
- `PATCH /api/suppliers/{id}`
  更新 supplier。admin-only。
- `DELETE /api/suppliers/{id}`
  刪除 supplier。admin-only。

## Demo 操作流程

1. 呼叫 `POST /api/auth/login`
2. 複製回傳的 `accessToken`
3. 在後續 request 加上 `Authorization: Bearer <token>`
4. 呼叫 `GET /api/auth/me`、任一個 `tasks` 端點或 `suppliers` 端點

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

## Supplier List Request 範例

```http
GET /api/suppliers?keyword=sup&status=ACTIVE&page=1&pageSize=20
Authorization: Bearer <token>
```
