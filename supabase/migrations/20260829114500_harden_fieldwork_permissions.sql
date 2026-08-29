-- ============================================================
-- Harden Field Work Permissions and RLS
-- ============================================================

-- 1. Grant necessary privileges to 'authenticated' and 'service_role' for all fieldwork tables.
-- This ensures that the Data API can actually perform the operations allowed by RLS.

GRANT SELECT, INSERT, UPDATE, DELETE ON public.location_sessions TO authenticated, service_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.location_points TO authenticated, service_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.recording_sessions TO authenticated, service_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.transcripts TO authenticated, service_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.task_reports TO authenticated, service_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.activity_logs TO authenticated, service_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.task_images TO authenticated, service_role;

-- 2. Ensure execution permissions on the 'private' schema and security helpers are granted.
GRANT USAGE ON SCHEMA private TO authenticated, service_role;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA private TO authenticated, service_role;

-- 3. Re-verify/Re-apply RLS policies for critical tracking tables to ensure they use SECURITY DEFINER helpers.
-- This prevents the "permission denied" errors when an RLS policy on Table A tries to query Table B
-- which also has RLS enabled but the user hasn't been granted SELECT on Table B.

-- LOCATION SESSIONS
DROP POLICY IF EXISTS "employees can view own location sessions" ON public.location_sessions;
DROP POLICY IF EXISTS "owners can view employee location sessions" ON public.location_sessions;
DROP POLICY IF EXISTS "employees can create location sessions" ON public.location_sessions;
DROP POLICY IF EXISTS "employees can update own location sessions" ON public.location_sessions;

CREATE POLICY "location_sessions_select_policy" ON public.location_sessions
FOR SELECT TO authenticated USING (
    employee_id = auth.uid() OR private.is_task_owner(task_id)
);

CREATE POLICY "location_sessions_insert_policy" ON public.location_sessions
FOR INSERT TO authenticated WITH CHECK (
    employee_id = auth.uid() AND private.is_task_employee(task_id)
);

CREATE POLICY "location_sessions_update_policy" ON public.location_sessions
FOR UPDATE TO authenticated USING (
    employee_id = auth.uid()
) WITH CHECK (
    employee_id = auth.uid()
);

-- LOCATION POINTS
DROP POLICY IF EXISTS "users can view authorized location points" ON public.location_points;
DROP POLICY IF EXISTS "employees can insert own location points" ON public.location_points;

CREATE POLICY "location_points_select_policy" ON public.location_points
FOR SELECT TO authenticated USING (
    private.can_access_location_session(session_id)
);

CREATE POLICY "location_points_insert_policy" ON public.location_points
FOR INSERT TO authenticated WITH CHECK (
    EXISTS (
        SELECT 1 FROM public.location_sessions ls
        WHERE ls.id = session_id AND ls.employee_id = auth.uid()
    )
);

-- RECORDING SESSIONS
DROP POLICY IF EXISTS "users can view authorized recordings" ON public.recording_sessions;
DROP POLICY IF EXISTS "employees can create recording sessions" ON public.recording_sessions;
DROP POLICY IF EXISTS "employees can update own recording sessions" ON public.recording_sessions;

CREATE POLICY "recording_sessions_select_policy" ON public.recording_sessions
FOR SELECT TO authenticated USING (
    private.can_access_recording_session(id)
);

CREATE POLICY "recording_sessions_insert_policy" ON public.recording_sessions
FOR INSERT TO authenticated WITH CHECK (
    employee_id = auth.uid() AND private.is_task_employee(task_id)
);

CREATE POLICY "recording_sessions_update_policy" ON public.recording_sessions
FOR UPDATE TO authenticated USING (
    employee_id = auth.uid()
) WITH CHECK (
    employee_id = auth.uid()
);

-- ACTIVITY LOGS
DROP POLICY IF EXISTS "users can view authorized activity logs" ON public.activity_logs;
DROP POLICY IF EXISTS "authenticated users can create own activity logs" ON public.activity_logs;

CREATE POLICY "activity_logs_select_policy" ON public.activity_logs
FOR SELECT TO authenticated USING (
    user_id = auth.uid() OR (task_id IS NOT NULL AND private.can_access_task(task_id))
);

CREATE POLICY "activity_logs_insert_policy" ON public.activity_logs
FOR INSERT TO authenticated WITH CHECK (
    user_id = auth.uid()
);
