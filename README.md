# Smart Skin Health Analysis & Routine Generator System

A JavaFX desktop application for personalized skin health tracking, routine generation, ingredient conflict detection, appointment booking, expert validation, and administrative management.

## Group Members

- Aisha Anjum Abbasi - 24I-3171 - UC1 to UC5
- Shehryar Amin - 22I-2649 - UC6 to UC10
- Alisha Iqbal - 24I-3147 - UC11 to UC15

## Prerequisites

- JDK 17
- MySQL 8
- Maven

## Setup Steps

1. Run `database.sql` in MySQL.
2. Update `src/resources/config.properties` with your MySQL credentials.
3. Run `DatabaseSeeder.java` once.
4. Run the app with `mvn javafx:run` or run `Main.java` directly.

## Architecture (Layered 3-Tier)

```text
Presentation Layer (JavaFX UI)
  -> Controllers in src/ui
  -> FXML Views in src/resources/fxml

Business Logic Layer (Services)
  -> Classes in src/service
  -> Contains validation, workflow, rules, orchestration

Data Access Layer (Repositories)
  -> Classes in src/repository
  -> JDBC-only database communication

Database Layer (MySQL)
  -> Tables defined in database.sql
```

## Design Patterns

| Pattern | Class | Location |
|---|---|---|
| Singleton | DBConnection, AdminService | src/repository, src/service |
| Strategy | RoutineStrategy + concrete strategies | src/service |
| Factory | RoutineStrategyFactory | src/service |
| Observer-style event handling | NotificationService, ProgressTrackingService | src/service |
| Creator | AuthService, RoutineGeneratorService | src/service |

## Work Division

| Member | Assigned UCs |
|---|---|
| Aisha Anjum Abbasi (24I-3171) | UC1, UC2, UC3, UC4, UC5 |
| Shehryar Amin (22I-2649) | UC6, UC7, UC8, UC9, UC10 |
| Alisha Iqbal (24I-3147) | UC11, UC12, UC13, UC14, UC15 |
