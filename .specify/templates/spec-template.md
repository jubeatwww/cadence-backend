# Feature Specification: [FEATURE NAME]

**Feature Branch**: `[###-feature-name]`  
**Created**: [DATE]  
**Status**: Draft  
**Input**: User description: "$ARGUMENTS"

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.
  
  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - [Brief Title] (Priority: P1)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently - e.g., "Can be fully tested by [specific action] and delivers [specific value]"]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]
2. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

### User Story 2 - [Brief Title] (Priority: P2)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

### User Story 3 - [Brief Title] (Priority: P3)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

[Add more user stories as needed, each with an assigned priority]

### Edge Cases

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right edge cases.
-->

- What happens when [boundary condition]?
- How does system handle [error scenario]?

## Requirements *(mandatory)*

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right functional requirements using DOMAIN LANGUAGE.

  IMPORTANT (DDD Principle): Use business/domain terminology, NOT technical terms.
  - Good: "Customer can place an order", "Habit can be marked complete"
  - Bad: "User can POST to /api/orders", "Record can be updated in database"
-->

### Functional Requirements (Domain Language)

- **FR-001**: [Domain Entity] MUST [business capability in domain terms, e.g., "Customer can place an order"]
- **FR-002**: [Domain Entity] MUST [business rule, e.g., "Order total cannot exceed customer credit limit"]
- **FR-003**: [Actor] MUST be able to [domain action, e.g., "cancel pending orders"]
- **FR-004**: System MUST [domain invariant, e.g., "prevent double-booking of time slots"]
- **FR-005**: [Domain Event] MUST trigger [consequence, e.g., "HabitCompleted updates streak counter"]

*Example of marking unclear requirements:*

- **FR-006**: System MUST authenticate users via [NEEDS CLARIFICATION: auth method not specified - email/password, SSO, OAuth?]
- **FR-007**: System MUST retain user data for [NEEDS CLARIFICATION: retention period not specified]

### Domain Model *(mandatory for DDD compliance)*

**Bounded Context**: [Name of the bounded context this feature belongs to]

**Ubiquitous Language** (key domain terms):
- **[Term 1]**: [Business definition, e.g., "Habit - A recurring activity tracked for completion"]
- **[Term 2]**: [Business definition, e.g., "Cycle - One complete iteration through habit steps"]
- **[Term 3]**: [Business definition, e.g., "Window - Time period for window-bound habits"]

**Aggregates** (consistency boundaries):
- **[Aggregate 1]**: [Aggregate root entity + owned entities, e.g., "Habit (root) → HabitSteps"]
  - **Invariants**: [Business rules enforced, e.g., "At least one step with zero dependencies"]
  - **Identity**: [How uniquely identified, e.g., "HabitId (GUID)"]

- **[Aggregate 2]**: [Another aggregate root]
  - **Invariants**: [Business rules]
  - **Identity**: [Identifier]

**Value Objects** (immutable concepts without identity):
- **[ValueObject 1]**: [What it represents, e.g., "TimeWindow (start, end, type)"]
- **[ValueObject 2]**: [What it represents, e.g., "CooldownPeriod (duration, unit)"]

**Domain Events** (state change facts):
- **[Event 1]**: Raised when [trigger, e.g., "HabitCompletedEvent - raised when user completes a habit step"]
- **[Event 2]**: Raised when [trigger, e.g., "CycleResetEvent - raised when new window starts for window-bound habit"]

## Success Criteria *(mandatory)*

<!--
  ACTION REQUIRED: Define measurable success criteria.
  These must be technology-agnostic and measurable.
-->

### Measurable Outcomes

- **SC-001**: [Measurable metric, e.g., "Users can complete account creation in under 2 minutes"]
- **SC-002**: [Measurable metric, e.g., "System handles 1000 concurrent users without degradation"]
- **SC-003**: [User satisfaction metric, e.g., "90% of users successfully complete primary task on first attempt"]
- **SC-004**: [Business metric, e.g., "Reduce support tickets related to [X] by 50%"]
