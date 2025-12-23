# Research: Habit Context Persistence & JSON Strategy

## Decision: Persistence Framework
**Decision**: Spring Data JDBC
**Rationale**: 
- **Simplicity**: The DDD "Aggregate" concept maps 1:1 with Spring Data JDBC's Root entity handling. It automatically handles cascading saves for the Aggregate Root (`Habit`) and its children (`Step`, `Dependency`, `Policy`).
- **Pragmatism**: Avoids the "Lazy Loading" and "Session" complexity of JPA/Hibernate, which often leads to N+1 queries or detached entity errors.
- **Control**: Allows explicit SQL control when needed for complex Graph queries (e.g., Recursive Common Table Expressions for dependency resolution) which are hard to do in JPA.

**Alternatives Considered**:
- **JPA (Hibernate)**: Rejected due to complexity mismatch. The Graph structure (steps pointing to steps) is tricky in JPA without complex bidirectional mappings.
- **JOOQ**: Good for type-safety, but adds build-time generation complexity. Spring Data JDBC is "good enough" for the write model.

## Decision: Graph Validation & Traversal
**Decision**: In-Memory Graph Algorithms (JGraphT or Custom DFS/BFS)
**Rationale**:
- **Volume**: A single habit rarely has > 100 steps. Loading the whole aggregate into memory to validate the graph is cheap and fast (microsecond scale).
- **Validation**: "Is DAG?", "Has Cycle?" are standard graph problems easier to solve in code than SQL.

**Alternatives Considered**:
- **Recursive SQL**: Good for querying "What depends on X", but harder to write validation logic (e.g. "detect cycle") in pure SQL portably.

## Decision: JSON Polymorphism for Policies
**Decision**: Jackson `@JsonTypeInfo`
**Rationale**:
- The `Habit` aggregate has different Policies (`Quota`, `Cooldown`, `Reward`).
- We need to serialize these for API responses.
- Jackson's standard polymorphic handling is robust and standard in Spring Boot.

## Decision: Time Handling
**Decision**: `java.time.Instant` stored as `TIMESTAMP WITH TIME ZONE` (PostgreSQL)
**Rationale**:
- Spec requires UTC. `Instant` is the safest type for absolute time points.
- Postgres `timestamptz` automatically normalizes to UTC storage.

## Decision: Domain Events Implementation
**Decision**: Spring `ApplicationEventPublisher` (In-Memory first)
**Rationale**:
- Start simple (Monolith).
- Events like `StepCompleted` need to update `XPLedger` (Player Context).
- Using Spring's internal event bus keeps it transactional (with `@TransactionalEventListener`) and simple.
- Can be upgraded to RabbitMQ/Kafka later if splitting microservices.
