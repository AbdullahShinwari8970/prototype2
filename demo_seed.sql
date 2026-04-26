-- ============================================================
-- DEMO SEED  —  "Physical Activity Tracking Study"
-- Researcher login: demo@research.com / Demo1234!
-- 3 participants | 2 surveys | 10 completed submissions
-- ============================================================

-- ─── Wipe all data ───────────────────────────────────────────
-- TRUNCATE TABLE responses, survey_tokens, enrollments,
   --             participants, questions, surveys, studies,
      --          researchers
-- RESTART IDENTITY CASCADE;

-- ↑ Run up to here first, then sign up at /signup, then run the rest below.

-- ─── STOP — sign up first ────────────────────────────────────
-- After the TRUNCATE above runs, go to the app and sign up at /signup.
-- Your researcher_id will be 1 (sequences were reset).
-- Then come back and run the rest of this script from here down.
-- ─────────────────────────────────────────────────────────────

-- ─── Study ───────────────────────────────────────────────────
INSERT INTO studies (study_name, researcher_id, status)
VALUES ('Physical Activity Tracking Study', 1, 'ACTIVE');
-- id = 1

-- ─── Surveys ─────────────────────────────────────────────────
INSERT INTO surveys (survey_name, schedule_type, send_hour, study_id)
VALUES
  ('Baseline Activity Survey', 'ONE_TIME', NULL, 1),   -- id = 1
  ('Daily Activity Log',       'DAILY',    9,    1);   -- id = 2

-- ─── Questions: Baseline (survey 1) ──────────────────────────
INSERT INTO questions (question_text, question_type, survey_id)
VALUES
  ('On average, how many days per week do you engage in physical activity?', 'TEXT', 1),  -- id = 1
  ('What is your primary mode of transport to work or university?',           'TEXT', 1),  -- id = 2
  ('Do you currently use a fitness tracking device? If yes, which one?',     'TEXT', 1);  -- id = 3

-- ─── Questions: Daily Log (survey 2) ─────────────────────────
INSERT INTO questions (question_text, question_type, survey_id)
VALUES
  ('Approximately how many steps did you take today?',                                  'TEXT', 2),  -- id = 4
  ('How many minutes were you sitting or sedentary today?',                             'TEXT', 2),  -- id = 5
  ('Did you do any structured exercise today? If yes, briefly describe what you did.',  'TEXT', 2);  -- id = 6

-- ─── Participants ─────────────────────────────────────────────
INSERT INTO participants (name, email)
VALUES
  ('Alice Murphy', 'alice.murphy@demo.com'),   -- id = 1
  ('Ben Clarke',   'ben.clarke@demo.com'),     -- id = 2
  ('Cara Nolan',   'cara.nolan@demo.com');     -- id = 3

-- ─── Enrollments ─────────────────────────────────────────────
INSERT INTO enrollments (study_id, participant_id, enrolled_at, status)
VALUES
  (1, 1, '2026-04-22 08:00:00', 'ACTIVE'),   -- id = 1  Alice
  (1, 2, '2026-04-22 08:00:00', 'ACTIVE'),   -- id = 2  Ben
  (1, 3, '2026-04-22 08:00:00', 'ACTIVE');   -- id = 3  Cara

-- ─── Survey Tokens: Baseline (ONE_TIME, survey 1) ────────────
-- All 3 completed on deploy day
INSERT INTO survey_tokens (token, enrollment_id, survey_id, status, prompted_at, expires_at, completed_at)
VALUES
  (gen_random_uuid()::text, 1, 1, 'COMPLETED', '2026-04-22 09:00:00', '2026-04-29 09:00:00', '2026-04-22 09:08:00'),  -- id = 1  Alice
  (gen_random_uuid()::text, 2, 1, 'COMPLETED', '2026-04-22 09:00:00', '2026-04-29 09:00:00', '2026-04-22 09:22:00'),  -- id = 2  Ben
  (gen_random_uuid()::text, 3, 1, 'COMPLETED', '2026-04-22 09:00:00', '2026-04-29 09:00:00', '2026-04-22 10:45:00');  -- id = 3  Cara

-- ─── Survey Tokens: Daily Log (DAILY, survey 2) ──────────────
--
--  Day 1 (2026-04-23): Alice ✓  Ben ✓  Cara ✓
--  Day 2 (2026-04-24): Alice ✓  Ben ✗  Cara ✓
--  Day 3 (2026-04-25): Alice ✓  Ben ✗  Cara ✗
--
--  Compliance rates:  Alice 3/3 (100%)  |  Ben 1/3 (33%)  |  Cara 2/3 (67%)

-- Day 1
INSERT INTO survey_tokens (token, enrollment_id, survey_id, status, prompted_at, expires_at, completed_at)
VALUES
  (gen_random_uuid()::text, 1, 2, 'COMPLETED', '2026-04-23 09:00:00', '2026-04-24 09:00:00', '2026-04-23 09:14:00'),  -- id = 4  Alice
  (gen_random_uuid()::text, 2, 2, 'COMPLETED', '2026-04-23 09:00:00', '2026-04-24 09:00:00', '2026-04-23 12:30:00'),  -- id = 5  Ben
  (gen_random_uuid()::text, 3, 2, 'COMPLETED', '2026-04-23 09:00:00', '2026-04-24 09:00:00', '2026-04-23 20:05:00');  -- id = 6  Cara

-- Day 2
INSERT INTO survey_tokens (token, enrollment_id, survey_id, status, prompted_at, expires_at, completed_at)
VALUES
  (gen_random_uuid()::text, 1, 2, 'COMPLETED', '2026-04-24 09:00:00', '2026-04-25 09:00:00', '2026-04-24 09:11:00'),  -- id = 7  Alice
  (gen_random_uuid()::text, 2, 2, 'EXPIRED',   '2026-04-24 09:00:00', '2026-04-25 09:00:00', NULL),                   -- id = 8  Ben  (missed)
  (gen_random_uuid()::text, 3, 2, 'COMPLETED', '2026-04-24 09:00:00', '2026-04-25 09:00:00', '2026-04-24 19:50:00');  -- id = 9  Cara

-- Day 3
INSERT INTO survey_tokens (token, enrollment_id, survey_id, status, prompted_at, expires_at, completed_at)
VALUES
  (gen_random_uuid()::text, 1, 2, 'COMPLETED', '2026-04-25 09:00:00', '2026-04-26 09:00:00', '2026-04-25 09:07:00'),  -- id = 10  Alice
  (gen_random_uuid()::text, 2, 2, 'EXPIRED',   '2026-04-25 09:00:00', '2026-04-26 09:00:00', NULL),                   -- id = 11  Ben  (missed)
  (gen_random_uuid()::text, 3, 2, 'PENDING',   '2026-04-26 09:00:00', '2026-04-27 09:00:00', NULL);                   -- id = 12  Cara (today, not yet answered)

-- ─── Responses: Baseline ─────────────────────────────────────

-- Alice (token 1, questions 1–3)
INSERT INTO responses (survey_token_id, question_id, answer_value, submitted_at) VALUES
  (1, 1, '5 to 6 days',                  '2026-04-22 09:08:00'),
  (1, 2, 'Cycling',                      '2026-04-22 09:08:00'),
  (1, 3, 'Yes — Garmin Forerunner 255',  '2026-04-22 09:08:00');

-- Ben (token 2, questions 1–3)
INSERT INTO responses (survey_token_id, question_id, answer_value, submitted_at) VALUES
  (2, 1, '3 to 4 days',            '2026-04-22 09:22:00'),
  (2, 2, 'Bus and walking',        '2026-04-22 09:22:00'),
  (2, 3, 'Yes — iPhone Health app','2026-04-22 09:22:00');

-- Cara (token 3, questions 1–3)
INSERT INTO responses (survey_token_id, question_id, answer_value, submitted_at) VALUES
  (3, 1, '1 to 2 days',  '2026-04-22 10:45:00'),
  (3, 2, 'Car',          '2026-04-22 10:45:00'),
  (3, 3, 'No',           '2026-04-22 10:45:00');

-- ─── Responses: Daily Log ─────────────────────────────────────

-- Alice Day 1 (token 4, questions 4–6)
INSERT INTO responses (survey_token_id, question_id, answer_value, submitted_at) VALUES
  (4, 4, '8200',                         '2026-04-23 09:14:00'),
  (4, 5, '240',                          '2026-04-23 09:14:00'),
  (4, 6, '30-minute run in the morning', '2026-04-23 09:14:00');

-- Ben Day 1 (token 5, questions 4–6)
INSERT INTO responses (survey_token_id, question_id, answer_value, submitted_at) VALUES
  (5, 4, '5100',                         '2026-04-23 12:30:00'),
  (5, 5, '380',                          '2026-04-23 12:30:00'),
  (5, 6, '20-minute walk during lunch',  '2026-04-23 12:30:00');

-- Cara Day 1 (token 6, questions 4–6)
INSERT INTO responses (survey_token_id, question_id, answer_value, submitted_at) VALUES
  (6, 4, '3800',                          '2026-04-23 20:05:00'),
  (6, 5, '450',                           '2026-04-23 20:05:00'),
  (6, 6, 'No structured exercise today',  '2026-04-23 20:05:00');

-- Alice Day 2 (token 7, questions 4–6)
INSERT INTO responses (survey_token_id, question_id, answer_value, submitted_at) VALUES
  (7, 4, '7800',                                '2026-04-24 09:11:00'),
  (7, 5, '260',                                 '2026-04-24 09:11:00'),
  (7, 6, '45-minute cycle to campus and back',  '2026-04-24 09:11:00');

-- Ben Day 2 (token 8) — EXPIRED, no responses

-- Cara Day 2 (token 9, questions 4–6)
INSERT INTO responses (survey_token_id, question_id, answer_value, submitted_at) VALUES
  (9, 4, '4100',                          '2026-04-24 19:50:00'),
  (9, 5, '420',                           '2026-04-24 19:50:00'),
  (9, 6, '15-minute walk in the evening', '2026-04-24 19:50:00');

-- Alice Day 3 (token 10, questions 4–6)
INSERT INTO responses (survey_token_id, question_id, answer_value, submitted_at) VALUES
  (10, 4, '8500',                                   '2026-04-25 09:07:00'),
  (10, 5, '210',                                    '2026-04-25 09:07:00'),
  (10, 6, '1-hour gym session — strength training', '2026-04-25 09:07:00');

-- Ben Day 3 (token 11)  — EXPIRED, no responses
-- Cara Day 3 (token 12) — PENDING (today), no responses yet
