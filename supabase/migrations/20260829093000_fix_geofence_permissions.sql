-- ============================================================
-- Fix Permission Denied for geofences table
-- ============================================================

-- 1. Explicitly grant SELECT privilege on core tables to the authenticated role.
-- This ensures that joined queries in TaskDataSource can execute.
-- RLS will still restrict which rows are visible.

GRANT SELECT ON public.geofences TO authenticated;
GRANT SELECT ON public.tasks TO authenticated;
GRANT SELECT ON public.task_assignments TO authenticated;
GRANT SELECT ON public.profiles TO authenticated;

-- Also ensure service_role has full access for background processing (Edge Functions)
GRANT ALL ON public.geofences TO service_role;
GRANT ALL ON public.tasks TO service_role;
GRANT ALL ON public.task_assignments TO service_role;
GRANT ALL ON public.profiles TO service_role;

-- 2. Verify RLS policies for geofences
-- We ensure the policies use the hardened SECURITY DEFINER helpers
-- to avoid RLS recursion or permission issues on the 'tasks' table.

DROP POLICY IF EXISTS "owners can view task geofences" ON public.geofences;
DROP POLICY IF EXISTS "employees can view assigned geofences" ON public.geofences;

-- A. Owners: Can view geofences for tasks they created
CREATE POLICY "owners_view_geofences_v1"
ON public.geofences
FOR SELECT
TO authenticated
USING (
    private.is_task_owner(task_id)
);

-- B. Employees: Can view geofences for tasks assigned to them
CREATE POLICY "employees_view_geofences_v1"
ON public.geofences
FOR SELECT
TO authenticated
USING (
    private.is_task_employee(task_id)
);
