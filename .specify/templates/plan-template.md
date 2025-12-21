# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

[Extract from feature spec: primary requirement + technical approach from research]

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: [e.g., Python 3.11, Swift 5.9, Rust 1.75 or NEEDS CLARIFICATION]  
**Primary Dependencies**: [e.g., FastAPI, UIKit, LLVM or NEEDS CLARIFICATION]  
**Storage**: [if applicable, e.g., PostgreSQL, CoreData, files or N/A]  
**Testing**: [e.g., pytest, XCTest, cargo test or NEEDS CLARIFICATION]  
**Target Platform**: [e.g., Linux server, iOS 15+, WASM or NEEDS CLARIFICATION]
**Project Type**: [single/web/mobile - determines source structure]  
**Performance Goals**: [domain-specific, e.g., 1000 req/s, 10k lines/sec, 60 fps or NEEDS CLARIFICATION]  
**Constraints**: [domain-specific, e.g., <200ms p95, <100MB memory, offline-capable or NEEDS CLARIFICATION]  
**Scale/Scope**: [domain-specific, e.g., 10k users, 1M LOC, 50 screens or NEEDS CLARIFICATION]

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Validate alignment with `.specify/memory/constitution.md`:

**I. Domain-Driven Design**
- [ ] Ubiquitous language established for this feature (domain terms documented)
- [ ] Bounded context boundaries identified and respected
- [ ] Domain model reflects business concepts, not technical implementation

**II. Clean Architecture Layers**
- [ ] Domain layer planned with zero infrastructure dependencies
- [ ] Application layer defines interfaces (ports) for infrastructure needs
- [ ] Infrastructure adapters implement application interfaces
- [ ] Presentation layer remains thin, delegates to application services

**III. Testability-First Design**
- [ ] Domain entities/value objects designed as pure objects (unit testable)
- [ ] Use cases accept interfaces, enabling test doubles
- [ ] Test strategy defined: unit (domain) → use case (with fakes) → integration → contract

**IV. Aggregate Design**
- [ ] Aggregate roots identified with clear consistency boundaries
- [ ] Aggregates kept small (one transaction per aggregate instance)
- [ ] Cross-aggregate references use IDs only, not object references
- [ ] Eventual consistency via domain events for cross-aggregate scenarios

**V. CQRS**
- [ ] Commands (writes) separated from queries (reads)
- [ ] Commands modify state through domain logic, queries return DTOs
- [ ] Query optimization strategy documented if bypassing domain layer

**VI. Immutability & Value Objects**
- [ ] Value objects identified and designed as immutable
- [ ] Entity state changes occur through domain methods, not setters

**VII. Domain Events**
- [ ] Domain events identified for state changes affecting other aggregates/contexts
- [ ] Event names use past tense (e.g., `HabitCompletedEvent`)

**Violations Requiring Justification** (if any, document in "Complexity Tracking" section)

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
# [REMOVE IF UNUSED] Option 1: Single project (DEFAULT) - Clean Architecture Layers
src/
├── domain/                 # Domain Layer (innermost, zero dependencies)
│   ├── entities/          # Aggregates, entities with business identity
│   ├── valueobjects/      # Immutable value objects (Money, DateRange, etc.)
│   ├── services/          # Domain services (when logic doesn't fit in entities)
│   └── events/            # Domain events (HabitCompletedEvent, etc.)
├── application/           # Application Layer (use cases, orchestration)
│   ├── usecases/         # Use case implementations (commands/queries)
│   ├── interfaces/       # Ports (IRepository, IEventBus, etc.)
│   └── dto/              # Data transfer objects for queries
├── infrastructure/        # Infrastructure Layer (adapters, external dependencies)
│   ├── persistence/      # Repository implementations, DB context
│   ├── messaging/        # Event bus, message queue adapters
│   └── external/         # Third-party service integrations
└── presentation/          # Presentation Layer (CLI, API, UI)
    ├── cli/              # Command-line interface
    └── api/              # HTTP API controllers (if applicable)

tests/
├── unit/                 # Domain layer unit tests (fast, isolated)
├── usecases/            # Application layer tests (with test doubles/fakes)
├── integration/         # Infrastructure integration tests (real DB/services)
└── contract/            # API contract tests

# [REMOVE IF UNUSED] Option 2: Web application (when "frontend" + "backend" detected)
backend/
├── src/
│   ├── domain/          # Same as Option 1
│   ├── application/     # Same as Option 1
│   ├── infrastructure/  # Same as Option 1
│   └── presentation/
│       └── api/        # Web API controllers
└── tests/
    ├── unit/
    ├── usecases/
    ├── integration/
    └── contract/

frontend/
├── src/
│   ├── components/     # UI components (presentation layer)
│   ├── services/       # API client adapters
│   └── models/         # Frontend DTOs
└── tests/
    └── e2e/           # End-to-end tests

# [REMOVE IF UNUSED] Option 3: Mobile + API (when "iOS/Android" detected)
api/
└── [same as backend above - Clean Architecture layers]

ios/ or android/
├── Domain/            # Platform-specific domain (if separate from API domain)
├── Application/       # Use cases specific to mobile
├── Infrastructure/    # Local storage, platform APIs
└── Presentation/      # UI views, view models
└── Tests/
    ├── Unit/
    ├── Integration/
    └── UI/
```

**Structure Decision**: [Document the selected structure and reference the real
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
