# Implementation Plan: Habit Context Core Logic

**Branch**: `001-habit-context` | **Date**: 2025-12-21 | **Spec**: [specs/001-habit-context/spec.md](spec.md)
**Input**: Feature specification from `/specs/001-habit-context/spec.md`

## Summary

Implement the core Habit Tracking domain logic using DDD principles. This includes the `Habit` and `Player` aggregates, graph validation logic for steps dependencies, and execution tracking (Completions, Windows). The system will handle two reset modes: "Window-bound" (resetting weekly/monthly) and "Infinite" (continuous loops), along with Quota and Cooldown policies.

## Technical Context

**Language/Version**: Kotlin 1.9+ (JVM 21)
**Primary Dependencies**: 
- Spring Boot 3.x (Web, Data JDBC, Security, Actuator)
- Jackson (JSON)
- Flyway (Migrations)
**Storage**: PostgreSQL (Production), H2/Testcontainers (Test)
**Testing**: JUnit 5, Mockk, Testcontainers
**Target Platform**: JVM (Linux server)
**Project Type**: Single Spring Boot Application (`src/main/kotlin`)
**Performance Goals**: Graph validation < 1s, Daily generation < 2s for 100 habits.
**Constraints**: Strict DDD, Immutable Ledger for XP/Completions, UTC timestamps.
**Scale/Scope**: Core domain logic, ~10-15 tables, critical business rules.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Good Taste**: "Bad programmers worry about the code. Good programmers worry about data structures." -> The spec focuses entirely on defining the correct Aggregate boundaries and Graph structures first.
- [x] **Never Break Userspace**: New feature, no existing userspace to break.
- [x] **Pragmatism**: Using Spring Data JDBC for simpler mapping (likely, or JPA if complex relationships need it - will decide in Research). 
- [x] **Simplicity**: "If you need more than 3 levels of indentation..." -> Core logic will be encapsulated in Domain Entities, keeping Service layer thin.

## Project Structure

### Documentation (this feature)

```text
specs/001-habit-context/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
└── tasks.md             # Phase 2 output
```

### Source Code (repository root)

```text
src/main/kotlin/com/cadence/cadence/
├── habit/                  # New Module
│   ├── domain/             # Aggregates, Value Objects, Policies
│   │   ├── model/
│   │   └── service/        # Domain Services (e.g. GraphValidator)
│   ├── application/        # Application Services (Use Cases)
│   ├── infrastructure/     # Persistence (Repositories)
│   └── web/                # REST Controllers (API)
└── player/                 # New Module
    ├── domain/
    ├── application/
    ├── infrastructure/
    └── web/
```

**Structure Decision**: Modular Monolith approach. `habit` and `player` will be separate packages (bounded contexts) co-located in the monolith. Interactions via Domain Services or Events.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | | |
