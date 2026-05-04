# Daily App (Kotlin + Spring Boot)

A REST API to manage daily work using **Tasks**, **SubTasks**, **Blockers**, and **Notes**.

This project is built with:
- Kotlin
- Spring Boot (Web MVC + Data JPA)
- PostgreSQL
- Springdoc OpenAPI (Swagger UI)

## Features

- Task lifecycle management (`TODO`, `IN_PROGRESS`, `BLOCKED`, `DONE`, `CANCELED`)
- Subtasks scoped under tasks
- Blockers scoped under tasks (optionally linked to a subtask)
- Notes scoped under tasks (optionally linked to a subtask)
- Guardrails for ownership validation across nested resources
- Synchronizers/policies for blocker-driven task/subtask consistency

## Project Structure

- `src/main/kotlin/dev/artisra/dailyappkt/controllers` — REST controllers
- `src/main/kotlin/dev/artisra/dailyappkt/services` — business logic, policies, synchronizers
- `src/main/kotlin/dev/artisra/dailyappkt/repositories` — Spring Data repositories
- `src/main/kotlin/dev/artisra/dailyappkt/entities` — JPA entities
- `src/main/kotlin/dev/artisra/dailyappkt/models` — request/response DTOs and enums
- `src/main/resources/application.yml` — runtime configuration

## Requirements

- Java 24 (configured in `build.gradle.kts` toolchain)
- PostgreSQL running locally

## Configuration

Default datasource settings (from `application.yml`):

- URL: `jdbc:postgresql://localhost:5432/daily_app_db`
- Username: `postgres`
- Password: `password`
- Hibernate DDL mode: `validate`

> Because DDL mode is `validate`, the expected schema must already exist in your database.

## Run the Application

From the project root:

```bash
./gradlew bootRun
```

The API will be available at:

- `http://localhost:8080`

## Run Tests

Tests are classified into **Unit** and **Integration** tests using JUnit 5 tags.

### Run All Tests
```bash
./gradlew test
```

### Run Unit Tests
Unit tests use **MockK** to isolate services and verify business logic without database dependencies.
```bash
./gradlew test -Djunit.platform.include.tags=unit
```

### Run Integration Tests
Integration tests use an in-memory **H2** database and `@SpringBootTest` to verify end-to-end workflows and persistence.
```bash
./gradlew test -Djunit.platform.include.tags=integration
```

## Swagger / OpenAPI Documentation

Swagger support is provided by:

- `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3`

After starting the app, use:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

OpenAPI metadata is configured in:

- `src/main/kotlin/dev/artisra/dailyappkt/config/OpenApiConfig.kt`

## Main API Endpoints

### Tasks

- `GET /tasks`
- `GET /tasks/{id}`
- `POST /tasks`
- `PATCH /tasks/{id}`
- `DELETE /tasks/{id}`
- `POST /tasks/{id}/start`
- `POST /tasks/{id}/complete`
- `POST /tasks/{id}/reopen`
- `POST /tasks/{id}/cancel`

### SubTasks

- `GET /tasks/{taskId}/subtasks`
- `GET /tasks/{taskId}/subtasks/{id}`
- `POST /tasks/{taskId}/subtasks`
- `PUT /tasks/{taskId}/subtasks/{id}`
- `PATCH /tasks/{taskId}/subtasks/{id}`
- `DELETE /tasks/{taskId}/subtasks/{id}`
- `POST /tasks/{taskId}/subtasks/{id}/complete`
- `POST /tasks/{taskId}/subtasks/{id}/reopen`

### Blockers

- `GET /tasks/{taskId}/blockers`
- `GET /tasks/{taskId}/blockers/{blockerId}`
- `POST /tasks/{taskId}/blockers`
- `PUT /tasks/{taskId}/blockers/{blockerId}`
- `PATCH /tasks/{taskId}/blockers/{blockerId}`
- `DELETE /tasks/{taskId}/blockers/{blockerId}`
- `POST /tasks/{taskId}/blockers/{blockerId}/resolve`
- `POST /tasks/{taskId}/blockers/{blockerId}/reopen`

### Notes

- `GET /tasks/{taskId}/notes`
- `GET /tasks/{taskId}/notes/{noteId}`
- `POST /tasks/{taskId}/notes`
- `PUT /tasks/{taskId}/notes/{noteId}`
- `PATCH /tasks/{taskId}/notes/{noteId}`
- `DELETE /tasks/{taskId}/notes/{noteId}`

> Notes do not have command endpoints.

## Quick Start Checklist

1. Create `daily_app_db` in PostgreSQL.
2. Ensure DB credentials match `application.yml` (or override them).
3. Run `./gradlew bootRun`.
4. Open Swagger UI at `http://localhost:8080/swagger-ui/index.html`.