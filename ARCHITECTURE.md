# Architecture Document - Daily App

This document provides a high-level overview of the architectural design, components, and business rules of the Daily App (Kotlin + Spring Boot).

## 1. Architectural Overview

The application follows a standard N-tier architecture, leveraging Spring Boot's ecosystem for dependency injection, persistence, and web interfaces.

### Layers:
- **Presentation Layer (Controllers):** REST API endpoints that handle HTTP requests and return DTO responses.
- **Service Layer (Business Logic):** Orchestrates business operations, enforces policies, and coordinates state synchronization.
- **Persistence Layer (Repositories):** Spring Data JPA repositories for database interaction.
- **Domain Layer (Entities):** JPA entities representing the core data models.

---

## 2. Core Components

### 2.1 Controllers
Located in `dev.artisra.dailyappkt.controllers`.
- **TaskController:** Manages the lifecycle and state transitions of Tasks (`/tasks`).
- **SubTaskController:** Manages SubTasks nested under a Task context (`/tasks/{taskId}/subtasks`).
- **BlockerController:** Manages Blockers and their resolution status (`/tasks/{taskId}/blockers`).
- **NoteController:** Manages informative notes linked to tasks/subtasks (`/tasks/{taskId}/notes`).

### 2.2 Services
Located in `dev.artisra.dailyappkt.services`. We use specialized service types to maintain a clean separation of concerns:

- **Primary Services (`TaskService`, `SubTaskService`, etc.):** Handle standard CRUD and orchestration.
- **Policies (`TaskPolicyService`, `SubTaskPolicyService`):** Dedicated to validation rules and state transition guards (e.g., "cannot complete a task with open blockers").
- **Synchronizers (`TaskSynchronizerService`, `SubTaskSynchronizerService`):** Reactively update related resource states when a change occurs (e.g., setting a task to `BLOCKED` when a blocker is added).
- **Guards (`TaskOwnershipGuardService`):** Centralized validation for resource ownership and existence across nested API paths.

### 2.3 Repositories
Located in `dev.artisra.dailyappkt.repositories`.
- Standard Spring Data `JpaRepository` interfaces providing abstracted CRUD and custom query methods like `findByTaskId`.

---

## 3. Data Schema

The database consists of four primary tables managed by JPA and validated against the following schema:

### 3.1 ER Diagram (Conceptual)
```
Task (1) <--- (N) SubTask
Task (1) <--- (N) Blocker
Task (1) <--- (N) Note
SubTask (1) <--- (0..N) Blocker (Optional link)
SubTask (1) <--- (0..N) Note (Optional link)
```

### 3.2 Tables
- **`tasks`**: Stores the main task details and its `status` (`TODO`, `IN_PROGRESS`, `BLOCKED`, `DONE`, `CANCELED`).
- **`sub_tasks`**: Binary completion status (`is_completed`) linked to a parent task.
- **`blockers`**: Tracks reasons for blocks. Can be linked to both a Task and a SubTask.
- **`task_notes`**: Textual content categorized by type (`GENERAL`, `LEARNING`, `LINKS`, `IDEAS`, `DECISION`, `ISSUES`).

---

## 4. Business Rules & Logic Flow

### 4.1 State Machine (Task Status)
The status transition is governed by `TaskPolicyService`:
- `TODO` -> `IN_PROGRESS` (via `/start`)
- `IN_PROGRESS` -> `DONE` (via `/complete`) - **Blocked if** subtasks are incomplete or blockers are unresolved.
- Any -> `CANCELED` (via `/cancel`)
- `DONE`/`CANCELED` -> `TODO` (via `/reopen`)

### 4.2 Reactive Synchronization
The `Synchronizer` services ensure data consistency:
- **Blocker Added/Reopened:** Immediately moves the parent Task to `BLOCKED` status and sets the related SubTask to `is_completed = false`.
- **Last Blocker Resolved:** Automatically moves the Task status from `BLOCKED` back to `IN_PROGRESS`.
- **SubTask Reopened:** Ensures the parent Task is moved back to `IN_PROGRESS` if it was previously `DONE`.

### 4.3 Ownership Guarding
To prevent API manipulation where a user might try to update a subtask using the wrong `taskId`, the `TaskOwnershipGuardService` is called at the beginning of most nested operations to verify the relationship between resources.

---

## 5. Technology Stack
- **Language:** Kotlin 2.1+
- **Framework:** Spring Boot 3.4+
- **Database:** PostgreSQL (Production/Dev), H2 (Testing)
- **Documentation:** Springdoc OpenAPI (Swagger UI)
- **Testing:** JUnit 5, MockK (Unit), MockMvc + SpringBootTest (Integration)
