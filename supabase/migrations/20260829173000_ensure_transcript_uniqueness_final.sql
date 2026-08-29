-- ============================================================
-- Ensure Transcript Uniqueness Final Fix
-- ============================================================

-- 1. Clean up any existing duplicate transcripts (keep latest)
DELETE FROM public.transcripts a
USING public.transcripts b
WHERE a.id < b.id
  AND a.recording_session_id = b.recording_session_id;

-- 2. Add the unique constraint if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'unique_recording_session_transcript'
    ) THEN
        ALTER TABLE public.transcripts
        ADD CONSTRAINT unique_recording_session_transcript UNIQUE (recording_session_id);
    END IF;
END $$;

-- 3. Ensure permissions are set for service_role
GRANT ALL ON public.transcripts TO service_role;
