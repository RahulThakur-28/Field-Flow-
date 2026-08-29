-- ============================================================
-- Fix Recording RLS Recursion
-- ============================================================

-- The previous policy on recording_sessions triggered a function that
-- queried recording_sessions, causing infinite recursion.

DROP POLICY IF EXISTS "recording_sessions_select_policy" ON public.recording_sessions;
DROP POLICY IF EXISTS "recording_sessions_insert_policy" ON public.recording_sessions;
DROP POLICY IF EXISTS "recording_sessions_update_policy" ON public.recording_sessions;

-- 1. SELECT: Use columns directly to avoid recursion
CREATE POLICY "recording_sessions_select_v2" ON public.recording_sessions
FOR SELECT TO authenticated USING (
    employee_id = auth.uid() OR private.is_task_owner(task_id)
);

-- 2. INSERT: Verified non-recursive
CREATE POLICY "recording_sessions_insert_v2" ON public.recording_sessions
FOR INSERT TO authenticated WITH CHECK (
    employee_id = auth.uid() AND private.is_task_employee(task_id)
);

-- 3. UPDATE: Use columns directly
CREATE POLICY "recording_sessions_update_v2" ON public.recording_sessions
FOR UPDATE TO authenticated USING (
    employee_id = auth.uid()
) WITH CHECK (
    employee_id = auth.uid()
);

-- Also fix TRANSCRIPTS recursion if any
-- Transcripts previously used private.can_access_recording_session(recording_session_id)
-- which queries recording_sessions. This is OK if recording_sessions policies are non-recursive.
-- However, for performance and safety, let's use is_task_employee/owner.

DROP POLICY IF EXISTS "users can view authorized transcripts" ON public.transcripts;
DROP POLICY IF EXISTS "employees can create transcripts" ON public.transcripts;

CREATE POLICY "transcripts_select_v2" ON public.transcripts
FOR SELECT TO authenticated USING (
    EXISTS (
        SELECT 1 FROM public.recording_sessions rs
        WHERE rs.id = recording_session_id
          AND (rs.employee_id = auth.uid() OR private.is_task_owner(rs.task_id))
    )
);

CREATE POLICY "transcripts_insert_v2" ON public.transcripts
FOR INSERT TO authenticated WITH CHECK (
    EXISTS (
        SELECT 1 FROM public.recording_sessions rs
        WHERE rs.id = recording_session_id
          AND rs.employee_id = auth.uid()
    )
);
