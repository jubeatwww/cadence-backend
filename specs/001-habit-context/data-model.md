# Data Model: Habit Context

## Schemas (PostgreSQL)

### Bounded Context: Habit Catalog

#### Table: `habit`
Aggregate Root.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `UUID` | PK | |
| `player_id` | `UUID` | FK -> player.id | Owner |
| `name` | `TEXT` | NOT NULL | |
| `description` | `TEXT` | | |
| `window_type` | `VARCHAR` | NOT NULL | DAILY, WEEKLY, MONTHLY... |
| `reset_mode` | `VARCHAR` | NOT NULL | WINDOW_BOUND, INFINITE |
| `is_active` | `BOOLEAN` | DEFAULT TRUE | |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | |

#### Table: `habit_step`
Entity within Habit Aggregate.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `UUID` | PK | |
| `habit_id` | `UUID` | FK -> habit.id | Aggregate Root Ref |
| `name` | `TEXT` | NOT NULL | |
| `sort_order` | `INT` | NOT NULL | Display order |
| `difficulty` | `VARCHAR` | NOT NULL | EASY, MEDIUM, HARD |
| `base_xp` | `INT` | NOT NULL | |

#### Table: `step_dependency`
Value Object (Relationship) within Habit Aggregate.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `step_id` | `UUID` | FK -> step.id | The step that has a dependency |
| `depends_on_step_id` | `UUID` | FK -> step.id | The prerequisite step |

#### Table: `habit_policy`
Polymorphic table for Policies (Quota, Cooldown, Reward).
*Alternatively, flattened columns on `habit` if simple, but separate table allows extensibility.*

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `habit_id` | `UUID` | FK -> habit.id | |
| `policy_type` | `VARCHAR` | NOT NULL | QUOTA, COOLDOWN, REWARD |
| `config_json` | `JSONB` | NOT NULL | Specific config (e.g. `{max_cycles: 5}`) |

---

### Bounded Context: Execution Tracking

#### Table: `execution_window`
Tracks time windows for players.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `UUID` | PK | |
| `player_id` | `UUID` | FK -> player.id | |
| `window_type` | `VARCHAR` | NOT NULL | |
| `start_at` | `TIMESTAMPTZ` | NOT NULL | |
| `end_at` | `TIMESTAMPTZ` | NOT NULL | |

#### Table: `step_completion`
Immutable Ledger of progress.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `UUID` | PK | |
| `player_id` | `UUID` | FK -> player.id | |
| `habit_id` | `UUID` | FK -> habit.id | |
| `step_id` | `UUID` | FK -> step.id | |
| `window_id` | `UUID` | FK -> execution_window.id | |
| `cycle_index` | `INT` | NOT NULL | 1-based index |
| `occurred_at` | `TIMESTAMPTZ` | NOT NULL | |
| `xp_gained` | `INT` | NOT NULL | Snapshot of XP earned |
| `status` | `VARCHAR` | NOT NULL | COMPLETED, SKIPPED |

#### Table: `habit_state`
Current state cache (CQRS Read Model / Optimization).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `habit_id` | `UUID` | PK, FK -> habit.id | |
| `current_cycle` | `INT` | DEFAULT 1 | For Infinite Mode |
| `cooldown_until` | `TIMESTAMPTZ` | | For Cooldown Policy |

---

### Bounded Context: Player

#### Table: `player`
Aggregate Root.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `UUID` | PK | |
| `username` | `TEXT` | UNIQUE | |

#### Table: `xp_ledger`
Entity within Player Aggregate.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `UUID` | PK | |
| `player_id` | `UUID` | FK -> player.id | |
| `amount` | `INT` | NOT NULL | Positive or Negative |
| `source_ref` | `TEXT` | | e.g. "completion:{id}" |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | |

## Entity - Aggregate Mapping

### Habit Aggregate (Kotlin)
```kotlin
data class Habit(
    @Id val id: UUID?,
    val name: String,
    val windowType: WindowType,
    val resetMode: ResetMode,
    @MappedCollection(idColumn = "habit_id")
    val steps: Set<Step>,
    // Policies mapped manually or via custom converter
    val policies: Set<Policy> 
)

data class Step(
    @Id val id: UUID?,
    val name: String,
    @MappedCollection(idColumn = "step_id")
    val dependencies: Set<DependencyRef> // Just holds UUIDs
)
```
