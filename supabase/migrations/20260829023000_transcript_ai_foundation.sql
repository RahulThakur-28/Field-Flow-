-- ============================================================
-- Phase 4A: Transcript + AI Reporting Database Foundation
-- ============================================================

-- 1. Modify public.transcripts to support timestamped segments and retries
ALTER TABLE public.transcripts
ADD COLUMN IF NOT EXISTS segments JSONB,
ADD COLUMN IF NOT EXISTS error_message TEXT,
ADD COLUMN IF NOT EXISTS retry_count INT NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS provider TEXT;

-- 2. Create public.task_reports table
CREATE TABLE IF NOT EXISTS public.task_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES public.tasks(id) ON DELETE CASCADE,
    summary TEXT,
    key_findings JSONB,
    action_items JSONB,
    status public.transcription_status NOT NULL DEFAULT 'pending',
    version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- Constraint: One active report per task for the initial phase
    CONSTRAINT unique_task_report UNIQUE (task_id)
);

-- 3. Indexes for performance
CREATE INDEX IF NOT EXISTS idx_task_reports_task_id ON public.task_reports(task_id);

-- 4. Automatic updated_at trigger (Reusing existing public.set_updated_at)
CREATE TRIGGER set_task_reports_updated_at
BEFORE UPDATE ON public.task_reports
FOR EACH ROW
EXECUTE FUNCTION public.set_updated_at();

-- 5. Row Level Security for task_reports
ALTER TABLE public.task_reports ENABLE ROW LEVEL SECURITY;

-- A. Owners: Can view reports for tasks they created
CREATE POLICY "Owners can view reports of own tasks"
ON public.task_reports
FOR SELECT
TO authenticated
USING (
    (SELECT private.is_task_owner(task_id))
);

-- B. Employees: Can view reports for tasks assigned to them
CREATE POLICY "Employees can view assigned task reports"
ON public.task_reports
FOR SELECT
TO authenticated
USING (
    (SELECT private.is_task_employee(task_id))
);

-- Note: No INSERT/UPDATE/DELETE policies for authenticated role yet.
-- AI/Backend processing (Edge Functions) will use service_role to manage reports.
-- Employees are explicitly restricted from modifying AI data by the absence of policies.

-- 6. Verify Transcripts RLS
-- Existing policies use private.can_access_recording_session(recording_session_id),
-- which remains valid for the new columns.

-- 7. Grant permissions
GRANT SELECT ON public.task_reports TO authenticated;
GRANT ALL ON public.task_reports TO service_role;
