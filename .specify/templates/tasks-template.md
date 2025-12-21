---

description: "Task list template for feature implementation"
---

# Tasks: [FEATURE NAME]

**Input**: Design documents from `/specs/[###-feature-name]/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: The examples below include test tasks. Tests are OPTIONAL - only include them if explicitly requested in the feature specification.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions (Clean Architecture)

- **Single project**:
  - Domain: `src/domain/{entities,valueobjects,services,events}/`
  - Application: `src/application/{usecases,interfaces,dto}/`
  - Infrastructure: `src/infrastructure/{persistence,messaging,external}/`
  - Presentation: `src/presentation/{cli,api}/`
  - Tests: `tests/{unit,usecases,integration,contract}/`
- **Web app**: `backend/src/{domain,application,infrastructure,presentation/api}/`, `frontend/src/`
- **Mobile**: `api/src/{domain,application,infrastructure,presentation}/`, `ios/{Domain,Application,Infrastructure,Presentation}/`
- Paths shown below assume single project - adjust based on plan.md structure

<!-- 
  ============================================================================
  IMPORTANT: The tasks below are SAMPLE TASKS for illustration purposes only.
  
  The /speckit.tasks command MUST replace these with actual tasks based on:
  - User stories from spec.md (with their priorities P1, P2, P3...)
  - Feature requirements from plan.md
  - Entities from data-model.md
  - Endpoints from contracts/
  
  Tasks MUST be organized by user story so each story can be:
  - Implemented independently
  - Tested independently
  - Delivered as an MVP increment
  
  DO NOT keep these sample tasks in the generated tasks.md file.
  ============================================================================
-->

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create project structure per implementation plan
- [ ] T002 Initialize [language] project with [framework] dependencies
- [ ] T003 [P] Configure linting and formatting tools

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

Examples of foundational tasks (adjust based on your project):

- [ ] T004 Setup database schema and migrations framework
- [ ] T005 [P] Implement authentication/authorization framework
- [ ] T006 [P] Setup API routing and middleware structure
- [ ] T007 Create base models/entities that all stories depend on
- [ ] T008 Configure error handling and logging infrastructure
- [ ] T009 Setup environment configuration management

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) 🎯 MVP

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 1 (TDD - Write FIRST, must FAIL before implementation) ⚠️

> **CRITICAL: Tests written → Tests fail → Implementation → Tests pass**

**Domain Layer Tests** (unit tests - fast, isolated):
- [ ] T010 [P] [US1] Unit test for [Entity1] invariants in tests/unit/domain/entities/test_[entity1].py
- [ ] T011 [P] [US1] Unit test for [ValueObject1] immutability in tests/unit/domain/valueobjects/test_[valueobject1].py
- [ ] T012 [P] [US1] Unit test for [DomainEvent1] in tests/unit/domain/events/test_[event1].py

**Application Layer Tests** (use case tests with fakes):
- [ ] T013 [US1] Use case test for [Command/Query] in tests/usecases/test_[usecase].py (uses in-memory repository fake)

**Infrastructure Layer Tests** (integration tests - real dependencies):
- [ ] T014 [P] [US1] Repository integration test in tests/integration/persistence/test_[repository].py

**Presentation Layer Tests** (contract tests):
- [ ] T015 [P] [US1] Contract test for [endpoint] in tests/contract/test_[name].py

### Implementation for User Story 1 (only after tests written and failing)

**Domain Layer Implementation**:
- [ ] T016 [P] [US1] Create [Entity1] aggregate root in src/domain/entities/[entity1].py
- [ ] T017 [P] [US1] Create [ValueObject1] in src/domain/valueobjects/[valueobject1].py
- [ ] T018 [P] [US1] Create [DomainEvent1] in src/domain/events/[event1].py
- [ ] T019 [US1] Implement [DomainService] (if needed) in src/domain/services/[service].py

**Application Layer Implementation**:
- [ ] T020 [US1] Define [IRepository] interface in src/application/interfaces/[irepository].py
- [ ] T021 [US1] Implement [Command/Query UseCase] in src/application/usecases/[usecase].py (depends on T016-T019)
- [ ] T022 [P] [US1] Create [DTO] for queries in src/application/dto/[dto].py

**Infrastructure Layer Implementation**:
- [ ] T023 [US1] Implement [Repository] in src/infrastructure/persistence/[repository].py (implements IRepository from T020)
- [ ] T024 [US1] Setup DB schema/migration for [Entity1] (if applicable)

**Presentation Layer Implementation**:
- [ ] T025 [US1] Implement [CLI command/API endpoint] in src/presentation/{cli,api}/[handler].py
- [ ] T026 [US1] Wire dependencies (DI configuration) for user story 1

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2 (TDD - Write FIRST, must FAIL before implementation) ⚠️

**Domain Layer Tests**:
- [ ] T027 [P] [US2] Unit test for [Entity2] in tests/unit/domain/entities/test_[entity2].py
- [ ] T028 [P] [US2] Unit test for [ValueObject2] in tests/unit/domain/valueobjects/test_[valueobject2].py

**Application Layer Tests**:
- [ ] T029 [US2] Use case test for [UseCase2] in tests/usecases/test_[usecase2].py

**Infrastructure Layer Tests**:
- [ ] T030 [P] [US2] Repository integration test in tests/integration/persistence/test_[repository2].py

**Presentation Layer Tests**:
- [ ] T031 [P] [US2] Contract test for [endpoint] in tests/contract/test_[name2].py

### Implementation for User Story 2 (only after tests written and failing)

**Domain Layer**:
- [ ] T032 [P] [US2] Create [Entity2] aggregate in src/domain/entities/[entity2].py
- [ ] T033 [P] [US2] Create [ValueObject2] in src/domain/valueobjects/[valueobject2].py

**Application Layer**:
- [ ] T034 [US2] Define [IRepository2] interface in src/application/interfaces/[irepository2].py
- [ ] T035 [US2] Implement [UseCase2] in src/application/usecases/[usecase2].py

**Infrastructure Layer**:
- [ ] T036 [US2] Implement [Repository2] in src/infrastructure/persistence/[repository2].py

**Presentation Layer**:
- [ ] T037 [US2] Implement [endpoint/handler] in src/presentation/{cli,api}/[handler2].py
- [ ] T038 [US2] Wire dependencies for user story 2

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3 (TDD - Write FIRST, must FAIL before implementation) ⚠️

**Domain Layer Tests**:
- [ ] T039 [P] [US3] Unit test for [Entity3] in tests/unit/domain/entities/test_[entity3].py

**Application Layer Tests**:
- [ ] T040 [US3] Use case test for [UseCase3] in tests/usecases/test_[usecase3].py

**Infrastructure Layer Tests**:
- [ ] T041 [P] [US3] Repository integration test in tests/integration/persistence/test_[repository3].py

**Presentation Layer Tests**:
- [ ] T042 [P] [US3] Contract test for [endpoint] in tests/contract/test_[name3].py

### Implementation for User Story 3 (only after tests written and failing)

**Domain Layer**:
- [ ] T043 [P] [US3] Create [Entity3] aggregate in src/domain/entities/[entity3].py

**Application Layer**:
- [ ] T044 [US3] Define [IRepository3] interface in src/application/interfaces/[irepository3].py
- [ ] T045 [US3] Implement [UseCase3] in src/application/usecases/[usecase3].py

**Infrastructure Layer**:
- [ ] T046 [US3] Implement [Repository3] in src/infrastructure/persistence/[repository3].py

**Presentation Layer**:
- [ ] T047 [US3] Implement [endpoint/handler] in src/presentation/{cli,api}/[handler3].py
- [ ] T048 [US3] Wire dependencies for user story 3

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX [P] Additional unit tests (if requested) in tests/unit/
- [ ] TXXX Security hardening
- [ ] TXXX Run quickstart.md validation

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
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story (Clean Architecture Layer Dependencies)

**Test-First (TDD) Flow**:
1. Write ALL tests first (domain unit → use case → integration → contract)
2. Verify tests FAIL (red)
3. Implement following layer order below
4. Verify tests PASS (green)
5. Refactor while keeping tests green

**Implementation Layer Order** (dependency inversion - inner to outer):
1. **Domain Layer** (no dependencies): Entities, value objects, domain events, domain services
2. **Application Layer** (depends on domain): Interfaces (ports), use cases, DTOs
3. **Infrastructure Layer** (depends on application interfaces): Repositories, adapters
4. **Presentation Layer** (depends on application): CLI/API handlers, DI wiring

**Within-Story Parallelization**:
- All domain layer tests can run in parallel [P]
- All domain layer implementations can run in parallel [P] (different entities/value objects)
- Application interfaces can be defined in parallel with domain implementation
- Infrastructure and presentation can proceed after application layer is defined

**Cross-Story Rule**:
- Story complete (all layers + tests green) before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (if tests requested):
Task: "Contract test for [endpoint] in tests/contract/test_[name].py"
Task: "Integration test for [user journey] in tests/integration/test_[name].py"

# Launch all models for User Story 1 together:
Task: "Create [Entity1] model in src/models/[entity1].py"
Task: "Create [Entity2] model in src/models/[entity2].py"
```

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

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
