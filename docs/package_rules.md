# Package 規則

## 目標

讓分層責任保持清楚，避免 HTTP、persistence、business rule 彼此滲透。

## 規則

- `api`
  負責接 HTTP request、做 DTO validation、呼叫 application service、回傳 response DTO。
- `application`
  負責 use case orchestration、application 層級授權、協調 repository、轉譯 domain error。
- `domain`
  負責 business rule、狀態轉移、領域不變條件，不依賴 Spring 或 JPA。
- `infrastructure`
  負責技術細節，例如 JPA、Spring Security、JWT、設定與整合。
- `common`
  放中性的共用 enum 或 utility，不放特定業務模組語意。

## Guardrails

- Controller 不可直接呼叫 repository。
- Domain model 不可帶 JPA annotation。
- Infrastructure type 不可定義業務政策。
- 除非至少有兩個真實模組需要，否則不要引入 `Base*` 抽象。
