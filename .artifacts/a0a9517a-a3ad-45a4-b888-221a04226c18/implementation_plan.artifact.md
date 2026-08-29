# Debug and Fix process-audio Transcript Upsert

The `process-audio` Edge Function is failing at the transcript UPSERT stage. Audit shows that the `public.transcripts.text` column is defined as `NOT NULL` without a default value, but the UPSERT payload in `process-audio/index.ts` does not include the `text` field when setting the status to `processing`.

## User Review Required

> [!IMPORTANT]
> I will be making the `public.transcripts.text` column nullable in the database. This allows creating a transcript record in the `processing` state before the actual text is available from the AI provider.

## Proposed Changes

### Database Migrations

#### [NEW] [20260829164500_make_transcript_text_nullable.sql](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/supabase/migrations/20260829164500_make_transcript_text_nullable.sql)
- Alter `public.transcripts` table to make `text` column nullable.

### Supabase Edge Functions

#### [MODIFY] [index.ts](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/supabase/functions/process-audio/index.ts)
- Add detailed production logging for UPSERT failure as requested.
- (Optional) Ensure the UPSERT is robust.

## Verification Plan

### Automated Tests
- No automated tests available for Edge Functions in this environment.

### Manual Verification
1. Deploy the new migration to production.
2. Redeploy `process-audio` edge function.
3. Trigger `process-audio` for an existing recording session.
4. Verify `public.transcripts` row exists with `status = 'completed'` and contains text.
5. Verify `generate-report` succeeds (triggered by `process-audio`).
6. Verify `public.task_reports` row exists for the task.
