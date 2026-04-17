# Package Rules

## Goal

Keep responsibilities explicit and avoid letting HTTP, persistence, and business rules bleed into each other.

## Rules

- `api`
  Accept HTTP requests, validate request DTOs, call application services, and return response DTOs.
- `application`
  Orchestrate use cases, enforce application-level authorization, coordinate repositories, and translate domain errors.
- `domain`
  Hold business rules, state transitions, and domain invariants without Spring or JPA dependencies.
- `infrastructure`
  Implement technical concerns such as JPA, Spring Security, JWT, and configuration.
- `common`
  Hold neutral shared enums or utilities that do not belong to a business module.

## Guardrails

- Controllers must not talk to repositories directly.
- Domain models must not carry JPA annotations.
- Infrastructure types must not define business policy.
- Do not introduce `Base*` abstractions unless at least two real modules need them.
