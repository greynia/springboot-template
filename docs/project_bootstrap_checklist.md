# 專案 Bootstrap Checklist

- [ ] 修改 repo 名稱、`groupId`、`artifactId`、Spring application name
- [ ] 替換 `com.example.project` base package
- [ ] 替換 demo JWT secret 與本地資料庫設定
- [ ] 替換 Docker Compose 使用的 `.env` 值
- [ ] 決定是否保留 `Task` demo module
- [ ] 替換 seed data 中的使用者名稱、email、密碼
- [ ] 移除 README 與 API 文件中的 template wording
- [ ] 確認 Flyway migration 可在乾淨資料庫上執行
- [ ] 確認 auth API 可正常使用：`login`、`refresh`、`me`、`change-password`
- [ ] 確認 refresh token rotation 與 password policy 符合專案需求
- [ ] 確認 demo task API 可正常使用
- [ ] 確認 actuator / observability baseline 可用：`health`、`info`、`metrics`、`X-Request-Id`
- [ ] 跑過 unit test 與 integration test
- [ ] 確認沒有把特定業務專案語意漏進 template
