-- ============================================================
-- Fix Transcripts Uniqueness for Upsert Idempotency
-- ============================================================

-- The Edge Function uses .upsert(..., { onConflict: 'recording_session_id' })
-- This REQUIRES a unique constraint or primary key on the conflict column.

-- 1. Clean up any existing duplicate transcripts per session (keep newest)
DELETE FROM public.transcripts a
USING public.transcripts b
WHERE a.id < b.id
  AND a.recording_session_id = b.recording_session_id;

-- 2. Add the unique constraint
ALTER TABLE public.transcripts
ADD CONSTRAINT unique_recording_session_transcript UNIQUE (recording_session_id);

-- 3. Verify status enum (already exists from schema, but ensuring compatibility)
-- transcription_status: pending, processing, completed, failed

-- 4. Granting explicit permissions again just in case for service_role
GRANT ALL ON public.transcripts TO service_role;
