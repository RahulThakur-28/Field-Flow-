-- ============================================================
-- Final Fix for Checklist RLS and Schema Permissions
-- ============================================================

-- 1. Grant USAGE on private schema to authenticated users
-- Without this, policies calling private.is_task_owner will fail for 'authenticated' role.
GRANT USAGE ON SCHEMA private TO authenticated;
GRANT USAGE ON SCHEMA private TO anon;
GRANT USAGE ON SCHEMA private TO service_role;

-- 2. Ensure all internal functions have proper search paths and permissions
-- We qualification everything to be absolutely safe.
ALTER FUNCTION private.is_task_owner(uuid) SET search_path = public, auth, pg_catalog;
ALTER FUNCTION private.is_task_employee(uuid) SET search_path = public, auth, pg_catalog;

GRANT EXECUTE ON FUNCTION private.is_task_owner(uuid) TO authenticated;
GRANT EXECUTE ON FUNCTION private.is_task_employee(uuid) TO authenticated;

-- 3. Simplify Checklist RLS Policies
-- We move away from the helper function temporarily to ensure no schema/path issues.
-- This uses direct subqueries which are highly reliable in Supabase.

DROP POLICY IF EXISTS "Owners can manage checklist items of own tasks" ON public.task_checklist_items;
DROP POLICY IF EXISTS "Employees can view assigned checklist items" ON public.task_checklist_items;
DROP POLICY IF EXISTS "Employees can update assigned checklist item completion" ON public.task_checklist_items;

-- A. Owners: Can see/manage checklist items for any task they created
CREATE POLICY "Owners manage own checklist items"
ON public.task_checklist_items
FOR ALL
TO authenticated
USING (
    task_id IN (
        SELECT id FROM public.tasks
        WHERE created_by = auth.uid()
          AND is_deleted = false
    )
)
WITH CHECK (
    task_id IN (
        SELECT id FROM public.tasks
        WHERE created_by = auth.uid()
          AND is_deleted = false
    )
);

-- B. Employees: Can see checklist items for tasks assigned to them
CREATE POLICY "Employees view assigned checklist items"
ON public.task_checklist_items
FOR SELECT
TO authenticated
USING (
    task_id IN (
        SELECT task_id FROM public.task_assignments
        WHERE employee_id = auth.uid()
          AND status <> 'cancelled'
    )
);

-- C. Employees: Can update completion status for tasks assigned to them
CREATE POLICY "Employees update assigned checklist items"
ON public.task_checklist_items
FOR UPDATE
TO authenticated
USING (
    task_id IN (
        SELECT task_id FROM public.task_assignments
        WHERE employee_id = auth.uid()
          AND status <> 'cancelled'
    )
)
WITH CHECK (
    task_id IN (
        SELECT task_id FROM public.task_assignments
        WHERE employee_id = auth.uid()
          AND status <> 'cancelled'
    )
);

-- 4. Explicit Table Grants
GRANT SELECT, INSERT, UPDATE, DELETE ON public.task_checklist_items TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.task_checklist_items TO service_role;
