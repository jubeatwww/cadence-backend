-- Test Seed Data
-- Based on: specs/Units Mock Data for PostgreSQL Seeding.md
-- This is for development/testing only

-- Player
INSERT INTO player (id, username)
VALUES ('11111111-1111-1111-1111-111111111111', 'test_player');

-- ============================================
-- Habit: 喝水 (Daily, Linear Chain)
-- ============================================
INSERT INTO habit (id, player_id, name, description, window_type, reset_mode)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111',
        '喝水', '每天喝水滿2000c.c.', 'DAILY', 'WINDOW_BOUND');

INSERT INTO habit_step (id, habit_id, name, sort_order, difficulty, base_xp)
VALUES ('a0000001-0000-0000-0000-000000000001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '喝水 200ml', 1, 'EASY', 3),
       ('a0000002-0000-0000-0000-000000000002', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '喝水 400ml', 2, 'EASY', 4),
       ('a0000003-0000-0000-0000-000000000003', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '喝水 600ml', 3, 'EASY', 5),
       ('a0000004-0000-0000-0000-000000000004', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '喝水 800ml', 4, 'EASY', 6),
       ('a0000005-0000-0000-0000-000000000005', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '喝水 1000ml', 5, 'MEDIUM', 7),
       ('a0000006-0000-0000-0000-000000000006', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '喝水 1200ml', 6, 'MEDIUM', 8),
       ('a0000007-0000-0000-0000-000000000007', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '喝水 1400ml', 7, 'MEDIUM', 9),
       ('a0000008-0000-0000-0000-000000000008', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '喝水 1600ml', 8, 'MEDIUM', 10),
       ('a0000009-0000-0000-0000-000000000009', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '喝水 1800ml', 9, 'HARD', 12),
       ('a0000010-0000-0000-0000-000000000010', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '喝水 2000ml', 10, 'HARD', 15);

-- 喝水 Dependencies (linear chain)
INSERT INTO step_dependency (step_id, depends_on_step_id)
VALUES ('a0000002-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000001'),
       ('a0000003-0000-0000-0000-000000000003', 'a0000002-0000-0000-0000-000000000002'),
       ('a0000004-0000-0000-0000-000000000004', 'a0000003-0000-0000-0000-000000000003'),
       ('a0000005-0000-0000-0000-000000000005', 'a0000004-0000-0000-0000-000000000004'),
       ('a0000006-0000-0000-0000-000000000006', 'a0000005-0000-0000-0000-000000000005'),
       ('a0000007-0000-0000-0000-000000000007', 'a0000006-0000-0000-0000-000000000006'),
       ('a0000008-0000-0000-0000-000000000008', 'a0000007-0000-0000-0000-000000000007'),
       ('a0000009-0000-0000-0000-000000000009', 'a0000008-0000-0000-0000-000000000008'),
       ('a0000010-0000-0000-0000-000000000010', 'a0000009-0000-0000-0000-000000000009');

-- ============================================
-- Habit: 日常 (Mixed Daily/Weekly, No Dependencies)
-- ============================================
INSERT INTO habit (id, player_id, name, description, window_type, reset_mode)
VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111',
        '日常', '日常生活任務', 'DAILY', 'WINDOW_BOUND');

INSERT INTO habit_step (id, habit_id, name, sort_order, difficulty, base_xp)
VALUES ('b0000001-0000-0000-0000-000000000001', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '冥想', 1, 'EASY', 15),
       ('b0000002-0000-0000-0000-000000000002', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '吃維他命', 2, 'EASY', 5),
       ('b0000003-0000-0000-0000-000000000003', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '陪貓玩', 3, 'EASY', 10),
       ('b0000004-0000-0000-0000-000000000004', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '貓梳毛', 4, 'EASY', 10),
       ('b0000005-0000-0000-0000-000000000005', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '滾健腹輪', 5, 'MEDIUM', 15);
-- No dependencies for 日常 - all tasks are parallel

-- ============================================
-- Habit: 有氧 (Weekly, Linear Chain)
-- ============================================
INSERT INTO habit (id, player_id, name, description, window_type, reset_mode)
VALUES ('cccccccc-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111',
        '有氧', '跑步機在客廳，每週有氧增加減脂效率', 'WEEKLY', 'WINDOW_BOUND');

INSERT INTO habit_step (id, habit_id, name, sort_order, difficulty, base_xp)
VALUES ('c0000001-0000-0000-0000-000000000001', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '有氧 #1', 1, 'EASY', 30),
       ('c0000002-0000-0000-0000-000000000002', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '有氧 #2', 2, 'EASY', 35),
       ('c0000003-0000-0000-0000-000000000003', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '有氧 #3', 3, 'EASY', 40),
       ('c0000004-0000-0000-0000-000000000004', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '有氧 #4', 4, 'EASY', 50),
       ('c0000005-0000-0000-0000-000000000005', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '有氧 #5', 5, 'MEDIUM', 60),
       ('c0000006-0000-0000-0000-000000000006', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '有氧 #6', 6, 'MEDIUM', 75);

-- 有氧 Dependencies (linear chain)
INSERT INTO step_dependency (step_id, depends_on_step_id)
VALUES ('c0000002-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000001'),
       ('c0000003-0000-0000-0000-000000000003', 'c0000002-0000-0000-0000-000000000002'),
       ('c0000004-0000-0000-0000-000000000004', 'c0000003-0000-0000-0000-000000000003'),
       ('c0000005-0000-0000-0000-000000000005', 'c0000004-0000-0000-0000-000000000004'),
       ('c0000006-0000-0000-0000-000000000006', 'c0000005-0000-0000-0000-000000000005');

-- ============================================
-- Habit: 重訓 (Weekly, Linear Chain)
-- ============================================
INSERT INTO habit (id, player_id, name, description, window_type, reset_mode)
VALUES ('dddddddd-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111',
        '重訓', '每週三練來每個部位都有練到', 'WEEKLY', 'WINDOW_BOUND');

INSERT INTO habit_step (id, habit_id, name, sort_order, difficulty, base_xp)
VALUES ('d0000001-0000-0000-0000-000000000001', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '重訓 #1', 1, 'EASY', 30),
       ('d0000002-0000-0000-0000-000000000002', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '重訓 #2', 2, 'EASY', 35),
       ('d0000003-0000-0000-0000-000000000003', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '重訓 #3', 3, 'MEDIUM', 45),
       ('d0000004-0000-0000-0000-000000000004', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '重訓 #4', 4, 'HARD', 60);

INSERT INTO step_dependency (step_id, depends_on_step_id)
VALUES ('d0000002-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000001'),
       ('d0000003-0000-0000-0000-000000000003', 'd0000002-0000-0000-0000-000000000002'),
       ('d0000004-0000-0000-0000-000000000004', 'd0000003-0000-0000-0000-000000000003');
