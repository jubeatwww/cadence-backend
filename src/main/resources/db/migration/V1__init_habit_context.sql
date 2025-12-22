-- Habit Context Core Tables
-- Version: 1
-- Description: Initial schema for Player and Habit aggregates

-- Player Aggregate
CREATE TABLE player
(
    id         UUID PRIMARY KEY,
    username   TEXT        NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE xp_ledger
(
    id         UUID PRIMARY KEY,
    player_id  UUID        NOT NULL REFERENCES player (id) ON DELETE CASCADE,
    amount     INT         NOT NULL,
    source_ref TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_xp_ledger_player ON xp_ledger (player_id);

-- Habit Aggregate
CREATE TABLE habit
(
    id          UUID PRIMARY KEY,
    player_id   UUID        NOT NULL REFERENCES player (id) ON DELETE CASCADE,
    name        TEXT        NOT NULL,
    description TEXT,
    window_type VARCHAR(20) NOT NULL,
    reset_mode  VARCHAR(20) NOT NULL,
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_habit_player ON habit (player_id);

CREATE TABLE habit_step
(
    id         UUID PRIMARY KEY,
    habit_id   UUID        NOT NULL REFERENCES habit (id) ON DELETE CASCADE,
    name       TEXT        NOT NULL,
    sort_order INT         NOT NULL,
    difficulty VARCHAR(10) NOT NULL,
    base_xp    INT         NOT NULL
);

CREATE INDEX idx_habit_step_habit ON habit_step (habit_id);

CREATE TABLE step_dependency
(
    step_id            UUID NOT NULL REFERENCES habit_step (id) ON DELETE CASCADE,
    depends_on_step_id UUID NOT NULL REFERENCES habit_step (id) ON DELETE CASCADE,
    PRIMARY KEY (step_id, depends_on_step_id),
    CHECK (step_id != depends_on_step_id)
);

CREATE TABLE habit_policy
(
    id          UUID PRIMARY KEY,
    habit_id    UUID        NOT NULL REFERENCES habit (id) ON DELETE CASCADE,
    policy_type VARCHAR(20) NOT NULL,
    config_json JSONB       NOT NULL
);

CREATE INDEX idx_habit_policy_habit ON habit_policy (habit_id);

-- Habit State (for caching current cycle/cooldown)
CREATE TABLE habit_state
(
    habit_id       UUID PRIMARY KEY REFERENCES habit (id) ON DELETE CASCADE,
    current_cycle  INT NOT NULL DEFAULT 1,
    cooldown_until TIMESTAMPTZ
);
