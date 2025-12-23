# Feature Specification: Habit Context Core Logic

**Feature Branch**: `001-habit-context`
**Created**: 2025-12-21
**Status**: Draft
**Input**: User description: "參考 @specs/habit_tracker_game_ddd_v2_2.md 建立habit context"

## User Scenarios & Testing

### User Story 1 - Habit Management (Priority: P1)

As a Player, I want to define Habits with steps and dependencies so that I can model my complex routines.

**Why this priority**: Core functionality to define the data structure.

**Independent Test**: Can create a Habit with multiple steps and dependencies, and validate the graph structure.

**Acceptance Scenarios**:

1. **Given** a new Habit, **When** adding steps A and B with dependency A->B, **Then** the system accepts the configuration.
2. **Given** a Habit with steps, **When** attempting to create a dependency loop (A->B->A) in a multi-branch graph, **Then** the system rejects it (validates DAG).
3. **Given** a Habit, **When** configured as "Window-bound" (e.g., Weekly), **Then** it accepts a Quota policy.
4. **Given** a Habit, **When** configured as "Infinite", **Then** it rejects a Quota policy.

---

### User Story 2 - Daily Task Generation (Priority: P1)

As a Player, I want to see a daily list of available tasks so that I know what to work on today.

**Why this priority**: The primary way users interact with the system daily.

**Independent Test**: Simulate the passage of time/windows and verify the correct steps become "Available" or "Locked".

**Acceptance Scenarios**:

1. **Given** a Habit with dependency A->B, **When** generating daily tasks for a new cycle, **Then** only step A is available.
2. **Given** step A is completed, **When** refreshing tasks, **Then** step B becomes available.
3. **Given** a "Weekly" Habit, **When** a new week starts, **Then** progress resets to the first step (cycle reset).
4. **Given** an "Infinite" Habit, **When** a new week starts, **Then** progress continues from the last incomplete step.

---

### User Story 3 - Task Execution & Rules (Priority: P1)

As a Player, I want to complete steps and earn XP so that I make progress and feel rewarded.

**Why this priority**: Records user action and enforces game rules (Cooldown, Quota).

**Independent Test**: Execute steps and verify state changes, XP accrual, and constraint enforcement.

**Acceptance Scenarios**:

1. **Given** an available step, **When** marked complete, **Then** XP is awarded and the step status is updated.
2. **Given** a Habit with Cooldown, **When** a step is completed, **Then** the habit becomes unavailable until the cooldown period passes.
3. **Given** a Window-bound Habit that reached its Quota, **When** attempting to start a new cycle in the same window, **Then** the system prevents it.
1. **Given** an Infinite Habit at the last step, **When** completed, **Then** it immediately restarts at step 1 with an incremented cycle count.

### Edge Cases

- **Editing Active Habits**: What happens if a user modifies the dependency graph of a habit that has active progress in the current window? (Assumption: System validates new graph against current progress or forces a reset).
- **Orphaned Steps**: What happens if a step is added but not connected to the start node? (System validation should reject graphs without a path from a start node).
- **Time Anomalies**: How does the system handle Daylight Saving Time transitions? (System uses UTC for all calculations as per requirements).
- **Concurrent Updates**: Two devices completing the same step simultaneously? (Idempotency keys on Completion record prevent double-counting).

## Requirements

### Functional Requirements (Domain Language)

- **FR-001**: System MUST validate Habit Step dependencies to ensure a valid graph (DAG for branching, or simple loop for single-path).
- **FR-002**: System MUST support "Window-bound" reset mode (resets at window boundary) and "Infinite" reset mode (continuous cycles).
- **FR-003**: System MUST enforce Cooldown periods (absolute time) between step completions if configured.
- **FR-004**: System MUST enforce Quota limits (max cycles per window) for Window-bound habits.
- **FR-005**: System MUST calculate XP rewards based on Step base XP, difficulty modifier, and cycle multiplier.
- **FR-006**: System MUST prevent modification of historical Completion records (immutable ledger).
- **FR-007**: System MUST generate a daily "Task Board" showing only steps whose dependencies are met and are not in Cooldown/Quota lockout.
- **FR-008**: System MUST treat all timestamps as UTC.

### Domain Model

**Bounded Context**: Habit Context (Habit Catalog & Execution Tracking)

**Ubiquitous Language**:
- **Habit**: A recurring activity/theme (e.g., "Workout").
- **Step**: A single unit of execution within a Habit (e.g., "Warm-up").
- **Dependency**: Rule stating Step A must be done before Step B.
- **Cycle**: One full iteration of a Habit from start to finish.
- **Window**: A time period (Daily/Weekly/Monthly) for tracking quotas and resets.
- **Reset Mode**: Strategy for progress reset (Window-bound vs. Infinite).
- **Cooldown**: Mandatory wait time after completion.
- **Quota**: Max allowed cycles per window.
- **Completion**: An immutable record of a finished Step.
- **MVA (Minimum Viable Action)**: A step with no unsatisfied dependencies.

**Aggregates**:

- **Habit (Root)**
  - **Entities**: Step, Dependency, Policies (Quota, Cooldown, Reward)
  - **Invariants**: 
    - Graph must be valid (At least one start node; No loops if branching exists).
    - Quota Policy only allowed for Window-bound mode.
  - **Identity**: UUID

- **Player (Root)**
  - **Entities**: XPLedger
  - **Invariants**: XP Total matches sum of ledger entries.
  - **Identity**: UUID

**Value Objects**:
- **WindowType**: Enum (Daily, Weekly, Biweekly, Monthly).
- **Difficulty**: Enum (Easy, Medium, Hard).
- **GrowthStrategy**: Defines how rewards scale with cycles (Linear/Exponential).

**Domain Events**:
- **StepCompleted**: (habit_id, step_id, xp_gained)
- **CycleCompleted**: (habit_id, cycle_index)
- **QuotaReached**: (habit_id, window_id)
- **CooldownStarted**: (habit_id, next_available_at)

## Success Criteria

### Measurable Outcomes

- **SC-001**: System validates complex dependency graphs (e.g., 50+ nodes) in under 1 second.
- **SC-002**: Daily Task Generation for 100 active habits completes in under 2 seconds.
- **SC-003**: 100% of invalid graphs (containing forbidden loops) are rejected by validation logic.
- **SC-004**: Correctly handles Cooldowns: User cannot complete a step 1 second before cooldown expires, but can 1 second after.