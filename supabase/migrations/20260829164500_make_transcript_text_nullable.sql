-- ============================================================
-- Make Transcripts Text Nullable
-- ============================================================

-- The Edge Function starts transcription by upserting a row with 'processing' status.
-- At this stage, the 'text' is not yet available, so it must be nullable.

ALTER TABLE public.transcripts
ALTER COLUMN text DROP NOT NULL;

-- Log the change
COMMENT ON COLUMN public.transcripts.text IS 'Transcription text. Nullable to support initial processing state.';
