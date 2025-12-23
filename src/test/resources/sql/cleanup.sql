-- Cleanup all test data
-- Order matters due to foreign key constraints

DELETE FROM step_dependency;
DELETE FROM habit_policy;
DELETE FROM habit_state;
DELETE FROM habit_step;
DELETE FROM habit;
DELETE FROM xp_ledger;
DELETE FROM player;