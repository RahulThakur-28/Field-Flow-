-- ============================================================
-- Fix Recording Sessions Started At Default
-- ============================================================

-- Ensure recording_sessions.started_at has a default value of now()
-- to improve reliability and prevent dependency on client clock.

ALTER TABLE public.recording_sessions
ALTER COLUMN started_at SET DEFAULT now();
