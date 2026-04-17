# VS Code Zero-to-One Tutorial

## Goal

This document shows how to set up, run, test, and extend this Spring Boot template from scratch in a VS Code environment.

## Tool Versions

Recommended baseline for this repo:

- VS Code: latest stable
- Java SDK: `17`
- Maven: `3.9.12` or later
- Docker Desktop: latest stable
- PostgreSQL: `16` if you run DB locally without Docker

## VS Code Extensions

Install these extensions in VS Code:

- `vscjava.vscode-java-pack`
  Core Java support bundle. Includes Java language support, debugger, test runner, Maven support, and Java project management.
- `vmware.vscode-boot-dev-pack`
  Spring Boot development bundle. Includes Spring Boot tools, Spring Initializr support, and Spring Boot Dashboard.
- `vscjava.vscode-maven`
  Maven explorer and Maven command integration.
- `redhat.vscode-yaml`
  Better editing for YAML files such as Docker Compose and Spring configuration.
- `redhat.vscode-xml`
  Better editing for `pom.xml`.
- `ms-azuretools.vscode-docker`
  Dockerfile and Compose authoring support.

This repo also includes [.vscode/extensions.json](/Users/greynia/Desktop/Personal/springboot-template/.vscode/extensions.json:1) so VS Code can suggest them automatically.

## Install Java 17

Install a JDK 17 distribution such as Eclipse Temurin 17.

After installation, verify:

```bash
java -version
javac -version
```

Expected major version: `17`.

## Install Maven

Install Maven `3.9.12` or later.

Verify:

```bash
mvn -v
```

Expected:

- Java version points to JDK `17`
- Maven version is `3.9.x` or newer

## Install Docker Desktop

Docker Desktop is needed for:

- Integration tests with Testcontainers
- Local Compose-based startup

Verify:

```bash
docker version
docker ps
```

## Open This Repo In VS Code

1. Open VS Code.
2. Choose `File -> Open Folder...`.
3. Select the repo root `springboot-template`.
4. Wait for Java project import to finish.

If VS Code asks for a JDK, point it to your JDK 17 installation.

## Project Structure

- `backend/`
  Spring Boot application
- `docs/`
  package rules, testing strategy, bootstrap checklist, tutorial
- `docker-compose.yml`
  local deployment stack for PostgreSQL + backend

## Backend Dependencies In pom.xml

Core dependencies already included in [backend/pom.xml](/Users/greynia/Desktop/Personal/springboot-template/backend/pom.xml:1):

- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `flyway-core`
- `flyway-database-postgresql`
- `postgresql`
- `jjwt-api`
- `jjwt-impl`
- `jjwt-jackson`
- `spring-boot-starter-test`
- `spring-security-test`
- `spring-boot-testcontainers`
- `testcontainers-junit-jupiter`
- `testcontainers-postgresql`

You usually do not need to add more dependencies before starting v1 backend work unless the project has a specific need such as OpenAPI, cache, mail, or object storage.

## Run The Project Locally

### Option A: Run DB with Docker, run app from VS Code or terminal

Start PostgreSQL only:

```bash
docker compose up -d postgres
```

Run backend:

```bash
cd backend
mvn spring-boot:run
```

API will be available at:

- `http://localhost:8080`

### Option B: Run everything with Docker Compose

```bash
docker compose up --build
```

This starts:

- PostgreSQL on `5432`
- Backend on `8080`

## Run Tests

From the terminal:

```bash
cd backend
mvn test
```

If your environment restricts `~/.m2`, use:

```bash
cd backend
mvn -Dmaven.repo.local=/tmp/m2repo test
```

## Run Tests In VS Code

1. Open the `Testing` panel.
2. Wait for Java tests to be discovered.
3. Run a single test, a class, or all tests.

For integration tests, Docker must be running first.

## Demo Login Accounts

After Flyway seed data runs:

- `admin@example.com / password123`
- `user1@example.com / password123`
- `user2@example.com / password123`

## How To Create A New Module

Use the existing `Task` module as the reference pattern.

### Step 1: define domain

Create files under:

- `domain/<module>/model`
- `domain/<module>/service`
- `domain/<module>/exception`

Put invariants and state transitions here.

### Step 2: define application service

Create files under:

- `application/<module>/`

Typical classes:

- `<Module>ApplicationService`
- `<Module>QueryService`
- `<Module>Mapper`

Put use case orchestration here.

### Step 3: define API

Create files under:

- `api/controller`
- `api/dto/<module>`

Add request DTOs, response DTOs, and controller endpoints.

### Step 4: define persistence

Create files under:

- `infrastructure/persistence/jpa/entity`
- `infrastructure/persistence/jpa/repository`

Add entity mapping and repository interfaces.

### Step 5: add migration

Create a new Flyway SQL file under:

- `backend/src/main/resources/db/migration`

Example naming:

- `V3__add_projects.sql`
- `V4__seed_projects.sql`

### Step 6: add tests

Add:

- domain/application unit test
- API integration test with MockMvc

## How To Create A New Java File In VS Code

Recommended way:

1. Open the `JAVA PROJECTS` panel.
2. Expand `backend/src/main/java`.
3. Right-click the target package.
4. Choose `New Java Class`, `New Java Record`, or `New Package`.

Alternative:

1. Create a `.java` file manually in the correct folder.
2. Let VS Code infer the package declaration.

## Recommended First-Day Workflow

1. Install JDK 17, Maven, Docker Desktop, and VS Code extensions.
2. Open this repo in VS Code.
3. Run `docker compose up -d postgres`.
4. Run `cd backend && mvn test`.
5. Run `cd backend && mvn spring-boot:run`.
6. Test `POST /api/auth/login`.
7. Use the `Task` module as the model for your first business module.

## Common Problems

- Maven uses the wrong JDK:
  Check `mvn -v` and make sure it points to Java 17.
- VS Code cannot import the project:
  Reload the window after installing the Java extensions and make sure the repo root is opened, not a single file.
- Integration tests fail immediately:
  Check `docker ps` and confirm Docker Desktop is running.
- Backend cannot connect to DB:
  Confirm `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
