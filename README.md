# springboot-template

Pragmatic Spring Boot backend template for starting business systems with a clean package structure, JWT auth skeleton, Flyway migrations, JPA persistence, and a neutral `Task` demo module.

## Version Baseline

This template is aligned with the backend version baseline used in `workflow-approval-system`.

- Spring Boot `3.5.13`
- Java `17`
- PostgreSQL `16`
- Flyway + `flyway-database-postgresql`
- JJWT `0.12.7`
- Testcontainers `1.21.4`

## What This Repo Includes

- `backend/` Spring Boot application
- Layered packages: `api / application / domain / infrastructure / common`
- Exception hierarchy and centralized API error handling
- JWT auth skeleton with `login` and `me`
- JPA + Flyway + PostgreSQL
- Neutral `Task` sample aggregate
- Unit tests and integration tests with MockMvc + Testcontainers
- Bootstrap and package rules docs

## Quick Start

1. Copy `backend/src/main/resources/application-local.properties.example` to `application-local.properties`.
2. Update database settings and JWT secret.
3. Start PostgreSQL locally.
4. Run the backend:

```bash
cd backend
mvn spring-boot:run
```

## Demo Credentials

Seed data provides demo users after Flyway runs:

- `admin@example.com / password123`
- `user1@example.com / password123`
- `user2@example.com / password123`

These values are only for local bootstrap and must be replaced in real projects.

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

## Run Tests

```bash
cd backend
mvn test
```

Integration tests require Docker because they use Testcontainers PostgreSQL.

Verified command in this repo:

```bash
cd backend
mvn -Dmaven.repo.local=/tmp/m2repo test
```

If your local `~/.m2` is writable, plain `mvn test` is enough. The `/tmp` repo override is only needed in restricted environments.

## Notes

- Docker Desktop + Testcontainers has been verified in this repo.
- Seed data resets PostgreSQL sequences after fixed-ID inserts so demo writes continue to work after Flyway bootstrap.
- Aligning this template from Spring Boot `3.3.5` to `3.5.13` did not require code-level syntax rewrites; the work was in dependency and test-infrastructure compatibility.

## Docs

- [Package Rules](docs/package_rules.md)
- [Testing Strategy](docs/testing_strategy.md)
- [Bootstrap Checklist](docs/project_bootstrap_checklist.md)
- [VS Code Zero-to-One Tutorial](docs/vscode_zero_to_one_tutorial.md)
