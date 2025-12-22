# Tasks: Habit Context Core Logic

**Input**: Design documents from `/specs/001-habit-context/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are OPTIONAL but recommended for critical logic (Graph Validation).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Create module structure for habit and player contexts in `src/main/kotlin/com/cadence/cadence/`
- [X] T002 [P] Add Spring Data JDBC and Flyway dependencies to `build.gradle.kts` (if not present)
- [X] T003 [P] Configure Gson for JSON polymorphism (Policies) in `src/main/kotlin/com/cadence/cadence/config/JsonConfig.kt`
- [X] T004 Configure PostgreSQL connection in `src/main/resources/application.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 Create Flyway migration V1 for Player and Habit tables in `src/main/resources/db/migration/V1__init_habit_context.sql`
- [X] T006 [P] Create `Player` domain + entity in `player/domain/` and `player/infrastructure/persistence/`
- [X] T007 [P] Create `XPLedger` domain + entity in `player/domain/` and `player/infrastructure/persistence/`
- [X] T008 [P] Create `Habit` domain + entity in `habit/domain/` and `habit/infrastructure/persistence/`
- [X] T009 [P] Create `Step` and `Dependency` domain + entity in `habit/domain/` and `habit/infrastructure/persistence/`
- [X] T010 [P] Create `Policy` polymorphic classes in `habit/domain/` and `habit/infrastructure/persistence/`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Habit Management (Priority: P1) 🎯 MVP

**Goal**: Define Habits with steps and dependencies so that players can model routines.

**Independent Test**: Create a habit via API and verify it is persisted with correct graph structure.

### Implementation for User Story 1

- [X] T011 [US1] Implement `GraphValidator` domain service (cycle detection) in `habit/domain/service/GraphValidator.kt`
- [X] T012 [US1] Unit test for `GraphValidator` (DAG/Cycle scenarios) in `habit/domain/service/GraphValidatorTest.kt`
- [X] T013 [US1] Create `HabitRepository` interface in `habit/domain/HabitRepository.kt` + JDBC impl
- [X] T014 [US1] Implement `CreateHabitUseCase` application service in `habit/application/CreateHabitUseCase.kt`
- [X] T015 [US1] Implement `HabitController` with POST /habits endpoint in `habit/web/HabitController.kt`
- [X] T016 [US1] Implement `HabitController` GET endpoints in `habit/web/HabitController.kt`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Daily Task Generation (Priority: P1)

**Goal**: Show a daily list of available tasks based on dependencies and window resets.

**Independent Test**: Query /tasks/daily and verify locked/available steps based on mock time.

### Implementation for User Story 2

- [ ] T017 [US2] Create `Window` and `HabitState` entities in `src/main/kotlin/com/cadence/cadence/habit/domain/ExecutionEntities.kt` (if not in T008)
- [ ] T018 [US2] Create Flyway migration V2 for Execution tables (if not in V1) in `src/main/resources/db/migration/V2__execution_tracking.sql`
- [ ] T019 [US2] Implement `TaskGenerationService` to calculate available steps in `src/main/kotlin/com/cadence/cadence/habit/domain/service/TaskGenerationService.kt`
- [ ] T020 [US2] Implement `DailyTaskUseCase` in `src/main/kotlin/com/cadence/cadence/habit/application/DailyTaskUseCase.kt`
- [ ] T021 [US2] Create `TaskController` with GET /tasks/daily endpoint in `src/main/kotlin/com/cadence/cadence/habit/web/TaskController.kt`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Task Execution & Rules (Priority: P1)

**Goal**: Complete steps, earn XP, and enforce Cooldown/Quota rules.

**Independent Test**: Complete a step, verify XP ledger update and Cooldown enforcement.

### Implementation for User Story 3

- [ ] T022 [US3] Create `StepCompletion` entity in `src/main/kotlin/com/cadence/cadence/habit/domain/Completion.kt`
- [ ] T023 [US3] Implement `RuleEngine` domain service (Quota, Cooldown) in `src/main/kotlin/com/cadence/cadence/habit/domain/service/RuleEngine.kt`
- [ ] T024 [US3] Implement `XPService` for calculating rewards in `src/main/kotlin/com/cadence/cadence/player/application/XPService.kt`
- [ ] T025 [US3] Implement `StepCompletionUseCase` (transactional: save completion, update ledger) in `src/main/kotlin/com/cadence/cadence/habit/application/StepCompletionUseCase.kt`
- [ ] T026 [US3] Create `CompletionController` with POST /completions endpoint in `src/main/kotlin/com/cadence/cadence/habit/web/CompletionController.kt`

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T027 [P] Add API documentation (Swagger/OpenAPI) configuration
- [ ] T028 [P] Add Integration Tests for full flows in `src/test/kotlin/com/cadence/cadence/habit/integration/HabitFlowTest.kt`
- [ ] T029 Code cleanup and consistency check

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P1)**: Can start after Foundational (Phase 2) - Depends on Graph structures from US1 (logically), but can mock them.
- **User Story 3 (P1)**: Can start after Foundational (Phase 2) - Depends on Tasks being available (US2), but can be tested with direct API calls.

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories
