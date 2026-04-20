# Supplier Module V1 Plan

## Summary

在 `springboot-template` 新增一個可直接對齊 `nextjs-template` 的 `Supplier` backend module，範圍為完整 CRUD，加上 `keyword / status / page / pageSize` 查詢。

此模組同時作為 template 的第一個典型 business CRUD 樣本，順手把共用 pagination / query filter pattern 定正。

已鎖定的產品決策：

- 範圍：完整 CRUD
- 分頁 contract：升級共用 `PageResponse`
- 刪除策略：`DELETE` 硬刪除
- 權限：list/detail 全登入可用；create/update/delete 限 `ADMIN`
- status：保留，但 v1 不提供編輯；create 預設 `ACTIVE`

## Key Changes

### 1. Supplier domain and persistence

- 新增 `Supplier` aggregate 的最小資料模型：
  `id`, `code`, `name`, `contactEmail`, `status`, `createdAt`, `updatedAt`
- `status` 只支援 `ACTIVE | INACTIVE`
- 新增 supplier table migration，`code` 必須唯一，`contactEmail` 非空
- 補 supplier seed data，至少 3 筆，包含 1 筆 `INACTIVE`，以對齊 `nextjs-template` mock 的展示與 filter 情境
- Repository 層支援：
  - 依 `id` 查詢
  - 依 `code` 唯一性檢查
  - list 查詢 with `keyword/status`
  - pageable 查詢

### 2. Public API contract

新增以下端點：

- `GET /api/suppliers`
- `GET /api/suppliers/{id}`
- `POST /api/suppliers`
- `PATCH /api/suppliers/{id}`
- `DELETE /api/suppliers/{id}`

API shape 直接對齊 `nextjs-template` 現有 supplier 前端：

- `GET /api/suppliers`
  - query params:
    - `keyword?: string`
    - `status?: ACTIVE|INACTIVE`
    - `page?: number`，預設 `1`
    - `pageSize?: number`，預設 `20`
  - response:
    - `items`
    - `currentPage`
    - `totalCount`
    - `pageSize`
    - `totalPages`
- `GET /api/suppliers/{id}`
  - 回傳單筆 supplier
- `POST /api/suppliers`
  - body: `code`, `name`, `contactEmail`
  - create 時 `status = ACTIVE`
  - 回 `201 Created`
- `PATCH /api/suppliers/{id}`
  - body: `code`, `name`, `contactEmail`
  - v1 不接受 `status`
- `DELETE /api/suppliers/{id}`
  - 成功回 `204 No Content`

錯誤規則：

- 找不到 supplier：`404 SUPPLIER_NOT_FOUND`
- `code` 重複：`400 SUPPLIER_CODE_DUPLICATED`
- 請求驗證失敗：沿用既有 validation error handler
- 無權限修改：沿用 `403` application exception 流程

### 3. Shared pagination / filter pattern

- 將 backend 共用 `PageResponse` 從目前的 `items + total` 升級為：
  `items`, `currentPage`, `totalCount`, `pageSize`, `totalPages`
- `Task` list 也同步改用新格式，避免 template 內出現兩種 list contract
- 補一個最小 pageable/query pattern：
  - controller 接 query params
  - application/query service 組裝 filter
  - repository 用 Spring Data pageable + sort
- supplier 預設排序：`id DESC`
- `keyword` 搜尋規則：匹配 `code` 或 `name`，採 contains/ignore-case

### 4. Application and security behavior

- `GET /api/suppliers`、`GET /api/suppliers/{id}`：任何 authenticated user 可呼叫
- `POST/PATCH/DELETE /api/suppliers/**`：只允許 `ADMIN`
- 不新增新的 RBAC abstraction；先沿用目前 template 的 `ADMIN` / `USER`
- 在 service 層處理：
  - create/update 時的唯一碼檢查
  - not found
  - admin gate
- `SupplierResponse` 欄位需與前端型別對齊：
  `id`, `code`, `name`, `contactEmail`, `status`, `createdAt`

## Test Plan

### Integration tests

- `ADMIN` 可建立 supplier，回 `201`
- `ADMIN` 可更新 supplier
- `ADMIN` 可刪除 supplier，回 `204`
- `USER` 呼叫 create/update/delete 會得到 `403`
- 任一登入使用者可查 list/detail
- list 支援：
  - 無 filter
  - `keyword`
  - `status`
  - `page/pageSize`
- list response 的 `currentPage / totalCount / pageSize / totalPages` 正確
- detail 查不存在 id 時回 `404 SUPPLIER_NOT_FOUND`
- create/update 遇到重複 `code` 回 `400 SUPPLIER_CODE_DUPLICATED`

### Regression tests

- 既有 auth integration tests 繼續通過
- 既有 task integration tests 更新為新 `PageResponse` 後仍通過
- request correlation / `X-Request-Id` 行為不被 supplier 新增 API 打破

## Assumptions and Defaults

- supplier v1 只做最小商業欄位，不加電話、地址、remark、audit owner
- `status` 僅作展示與 filter，不在 v1 提供變更 API
- `DELETE` 為真正刪除，不轉成 `INACTIVE`
- 前端 `nextjs-template` 不需先改 supplier 型別；backend 直接對齊它目前的 contract
- 這一輪可以順手更新 `docs/api_surface.md` 與 `TODO/checklist`，把 supplier module 和新 pagination contract 記錄進文件
