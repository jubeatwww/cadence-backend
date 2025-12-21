<!--
  Sync Impact Report:
  - Version: 0.0.0 → 1.0.0 (initial constitution creation)
  - Modified principles: N/A (initial creation)
  - Added sections: All core principles for DDD + Clean Architecture + Testability
  - Removed sections: N/A
  - Templates requiring updates:
    ✅ plan-template.md - Updated Constitution Check with all 7 principles + Clean Architecture folder structure
    ✅ spec-template.md - Added Domain Model section (Bounded Context, Ubiquitous Language, Aggregates, Value Objects, Domain Events)
    ✅ tasks-template.md - Reorganized to reflect Clean Architecture layers (Domain → Application → Infrastructure → Presentation) with TDD emphasis
  - Follow-up TODOs: None

  Template Changes Summary:
  1. plan-template.md:
     - Constitution Check section expanded with 7 principle checklist
     - Project structure updated to Clean Architecture layers (domain/, application/, infrastructure/, presentation/)
     - Test folder structure updated (unit/, usecases/, integration/, contract/)

  2. spec-template.md:
     - Added "Domain Model" mandatory section with:
       - Bounded Context identification
       - Ubiquitous Language glossary
       - Aggregates with invariants and identity
       - Value Objects
       - Domain Events
     - Functional Requirements section emphasizes domain language over technical terms

  3. tasks-template.md:
     - Task phases reorganized by Clean Architecture layers
     - Test-first TDD workflow explicitly documented
     - Path conventions updated to match layer structure
     - Layer dependency order specified (Domain → Application → Infrastructure → Presentation)
     - Parallel execution guidelines respect layer dependencies
-->

# Cadence Constitution

## Core Principles

### I. Domain-Driven Design (DDD)

**Ubiquitous Language MUST be established and maintained across all artifacts.**
- Every domain concept uses consistent terminology in code, tests, documentation, and conversations.
- The domain model (entities, value objects, aggregates) reflects the problem space, not technical implementation details.
- Bounded contexts are explicitly defined and respected; cross-context communication uses well-defined contracts.

**Rationale**: DDD ensures the codebase remains aligned with business requirements, making it easier to understand, modify, and test domain logic without being obscured by technical complexity.

### II. Clean Architecture Layers (NON-NEGOTIABLE)

**The system MUST enforce strict layered architecture with dependency inversion.**

Layers (innermost to outermost):
1. **Domain Layer** (entities, value objects, domain services, domain events)
   - Zero dependencies on outer layers
   - Pure business logic, framework-agnostic
   - No infrastructure concerns (no DB, no HTTP, no UI)

2. **Application Layer** (use cases, application services, commands/queries)
   - Orchestrates domain objects to fulfill use cases
   - Depends only on domain layer
   - Defines interfaces (ports) for infrastructure needs

3. **Infrastructure Layer** (repositories, external services, adapters)
   - Implements interfaces defined in application layer
   - Contains framework-specific code, DB access, external APIs
   - Depends on application and domain layers via interfaces

4. **Presentation Layer** (CLI, API controllers, UI)
   - Thin adapter layer converting external requests to application commands
   - Depends on application layer only

**Enforcement**:
- Dependencies MUST point inward only (domain ← application ← infrastructure ← presentation)
- Domain and application layers MUST NOT reference concrete infrastructure implementations
- Use dependency injection to wire implementations at composition root

**Rationale**: Clean Architecture enables testing domain logic without databases, testing use cases without HTTP, and replacing infrastructure components without touching business rules.

### III. Testability-First Design (NON-NEGOTIABLE)

**Every component MUST be designed for independent testability.**

Requirements:
- **Domain entities and value objects**: Pure functions/objects with no external dependencies → unit testable in isolation
- **Use cases/application services**: Accept domain objects and infrastructure interfaces → testable with test doubles (mocks/stubs)
- **Infrastructure adapters**: Implement well-defined interfaces → integration testable against real dependencies, replaceable with in-memory fakes for use case tests
- **Presentation layer**: Thin adapters with minimal logic → contract testable

**Test Strategy**:
1. **Unit Tests** (fast, numerous): Domain layer entities, value objects, domain services
2. **Use Case Tests** (fast, behavior-focused): Application services with in-memory fakes for repositories
3. **Integration Tests** (slower, fewer): Infrastructure components against real databases/services
4. **Contract Tests** (API boundaries): Verify external interfaces match specifications

**TDD Discipline**:
- Tests MUST be written before implementation for all domain and application logic
- Tests MUST fail first, then pass after implementation (Red-Green-Refactor)
- Implementation MUST NOT be started until test structure is approved

**Rationale**: Designing for testability forces good architecture (loose coupling, clear interfaces). Testing domain logic without infrastructure speeds up feedback loops and ensures business rules remain correct through refactoring.

### IV. Aggregate Design & Transactional Boundaries

**Aggregates MUST enforce consistency boundaries and be kept small.**

Rules:
- Each aggregate has one entity as the root; external references go through the root
- Aggregate boundaries define transactional consistency: one transaction modifies one aggregate instance
- Cross-aggregate consistency uses eventual consistency via domain events
- Aggregates MUST NOT directly reference other aggregates by object reference; use IDs only
- Keep aggregates small: prefer multiple small aggregates over large clusters

**Rationale**: Small aggregates reduce contention, improve testability (fewer dependencies), and enable scalability. Transactional boundaries prevent distributed transaction complexity.

### V. CQRS (Command Query Responsibility Segregation)

**Write models (commands) and read models (queries) MUST be separated.**

Commands:
- Modify aggregate state through domain logic
- Return success/failure status or domain events
- Processed by application services that enforce business rules

Queries:
- Read-only operations returning data transfer objects (DTOs)
- May bypass domain layer and query infrastructure directly for efficiency
- Never modify state

**Rationale**: CQRS simplifies domain logic (writes focus on rules, reads focus on performance), enables independent optimization of read/write paths, and supports event sourcing if needed.

### VI. Immutability & Value Objects

**Domain models MUST prefer immutability wherever business rules allow.**

Requirements:
- Value objects MUST be immutable: once created, their state cannot change
- Entities may have mutable state, but changes occur through domain methods (not property setters)
- Use value objects for domain concepts without identity (Money, DateRange, Email, etc.)

**Rationale**: Immutability eliminates entire classes of bugs (unintended mutations, concurrency issues), makes testing predictable (no hidden state changes), and clarifies intent (new object = new state).

### VII. Explicit Domain Events

**State changes with cross-aggregate or cross-context implications MUST emit domain events.**

Requirements:
- Domain events are immutable records of facts (e.g., `HabitCompletedEvent`, `CycleResetEvent`)
- Events are raised by aggregates during state transitions
- Application services publish events to infrastructure event bus
- Other aggregates or bounded contexts subscribe to events for eventual consistency

**Rationale**: Domain events decouple aggregates, enable audit trails, support event sourcing, and make side effects explicit and testable.

## Architecture Constraints

### Dependency Rules

**Infrastructure MUST NOT leak into domain or application layers.**

Prohibited in domain/application layers:
- Database libraries (Entity Framework, Dapper, etc.)
- HTTP/API frameworks (ASP.NET, Express, etc.)
- External service SDKs (third-party APIs)
- UI frameworks (React, MAUI, etc.)

Allowed in domain/application layers:
- Standard library types (collections, dates, primitives)
- Domain-specific abstractions (IRepository, IEventBus as interfaces)
- Dependency injection framework attributes (if absolutely necessary, prefer constructor injection without attributes)

**Rationale**: Infrastructure independence enables testing without databases/APIs, porting to different platforms, and replacing infrastructure without rewriting business logic.

### Testing Requirements

**All code MUST meet minimum test coverage standards per layer.**

Minimum coverage (by layer):
- Domain layer: 95%+ statement coverage (nearly 100%, domain logic is critical)
- Application layer: 90%+ (use case orchestration must be verified)
- Infrastructure layer: 70%+ (integration tests, not all error paths may be testable)
- Presentation layer: Contract tests for all public interfaces (coverage less critical, keep thin)

**Test execution requirements**:
- Unit tests MUST run in <5 seconds for entire suite (fast feedback)
- Integration tests MAY be slower but MUST be isolated (use test containers/in-memory DBs)
- CI pipeline MUST run all tests before merge

**Rationale**: High coverage in domain/application layers ensures business logic correctness. Fast tests encourage frequent execution during development.

## Development Workflow

### Feature Implementation Flow

**Every feature MUST follow the workflow: Spec → Plan (Design) → Tasks → TDD Implementation.**

Steps:
1. **Specification** (`spec.md`): Define user scenarios, functional requirements, success criteria (domain language)
2. **Planning** (`plan.md`, `data-model.md`, `contracts/`): Design aggregates, entities, value objects, use cases, bounded context interactions
3. **Task Breakdown** (`tasks.md`): Organize tasks by user story with explicit test-first approach
4. **TDD Implementation**:
   - Write failing tests (domain → application → integration)
   - Implement minimal code to pass tests
   - Refactor while keeping tests green
   - Commit after each passing test or logical unit

### Code Review Gates

**All changes MUST pass architectural validation before merge.**

Review checklist:
- [ ] Domain layer has zero infrastructure dependencies (static analysis enforced)
- [ ] Use cases accept interfaces, not concrete implementations
- [ ] Aggregates enforce invariants through domain methods
- [ ] Tests exist and cover new domain logic (TDD evidence: test commits before implementation commits)
- [ ] CQRS separation maintained (commands vs queries)
- [ ] Domain events emitted for cross-aggregate changes
- [ ] No primitive obsession (value objects used where appropriate)

**Rationale**: Architectural discipline degrades without enforcement. Reviews catch violations early before they spread.

## Governance

**Constitution Compliance is Mandatory**

- All design decisions MUST be justified against these principles
- Violations require explicit documentation in `plan.md` under "Complexity Tracking" with rationale
- Amendments to this constitution require:
  1. Documented proposal with impact analysis
  2. Team review and approval
  3. Version increment (MAJOR for backward-incompatible changes, MINOR for new principles, PATCH for clarifications)
  4. Propagation of changes to all dependent templates (plan, spec, tasks)

**Versioning Policy**:
- MAJOR (X.0.0): Principle removal or redefinition that invalidates prior designs
- MINOR (X.Y.0): New principle added or existing principle significantly expanded
- PATCH (X.Y.Z): Clarifications, wording improvements, typo fixes

**Compliance Review**:
- Every feature plan MUST include "Constitution Check" section validating alignment
- CI pipeline SHOULD enforce dependency rules via architecture tests (e.g., NetArchTest, ArchUnit)

**Version**: 1.0.0 | **Ratified**: 2025-12-21 | **Last Amended**: 2025-12-21