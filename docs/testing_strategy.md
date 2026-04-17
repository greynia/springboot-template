# Testing Strategy

## Scope

The template keeps two testing layers:

- Unit tests for domain rules and application orchestration
- Integration tests for HTTP, security, persistence, Flyway, and serialization

## Version Baseline

- Spring Boot `3.5.13`
- PostgreSQL `16`
- Flyway with PostgreSQL database plugin
- Testcontainers `1.21.4`

## Baseline Coverage

- Auth login success and failure
- Auth `me`
- Task create, list, detail
- Task state transition
- Not found and validation failures
- Forbidden access for non-admin assignment

## Conventions

- Use plain unit tests for domain lifecycle rules
- Use Mockito for application service tests
- Use `MockMvc` for API integration tests
- Use a shared Testcontainers PostgreSQL instance for real persistence and Flyway verification
- Let integration tests exercise real Flyway migrations instead of relying on Hibernate schema generation

## Integration Test Notes

- Docker must be available to the Maven/JVM process that runs the tests.
- The integration tests share one PostgreSQL container to avoid stale datasource reuse across cached Spring contexts.
- If your environment restricts `~/.m2`, run Maven with `-Dmaven.repo.local=/tmp/m2repo`.
- If Docker Desktop is installed but Testcontainers still fails, check the active Docker context and the resolved Testcontainers version first.

## Command

```bash
mvn test
```
