-- ============================================================
-- FINAL FIX: Atomic Policy Cleanup and Hardened RLS
-- ============================================================

-- 1. Atomically drop ALL policies on the checklist table to remove any stale or RESTRICTIVE policies.
DO $$
DECLARE
    pol record;
BEGIN
    FOR pol IN
        SELECT policyname
        FROM pg_policies
        WHERE schemaname = 'public'
          AND tablename = 'task_checklist_items'
    LOOP
        EXECUTE format('DROP POLICY IF EXISTS %I ON public.task_checklist_items', pol.policyname);
    END LOOP;
END
$$;

-- 2. Ensure Schema and Function Permissions
GRANT USAGE ON SCHEMA private TO authenticated;
GRANT EXECUTE ON FUNCTION private.is_task_owner(uuid) TO authenticated;
GRANT EXECUTE ON FUNCTION private.is_task_employee(uuid) TO authenticated;

-- 3. Re-apply PERMISSIVE Policies using SECURITY DEFINER helpers.
-- This ensures that the check for "Is this the task owner?" runs as 'postgres'
-- and is NOT blocked by RLS or missing SELECT grants on the 'tasks' table itself.

-- A. Owners: Full access (SELECT, INSERT, UPDATE, DELETE)
CREATE POLICY "Owners_Full_Access_v2"
ON public.task_checklist_items
FOR ALL
TO authenticated
USING (private.is_task_owner(task_id))
WITH CHECK (private.is_task_owner(task_id));

-- B. Employees: View access
CREATE POLICY "Employees_View_Access_v2"
ON public.task_checklist_items
FOR SELECT
TO authenticated
USING (private.is_task_employee(task_id));

-- C. Employees: Update completion status
CREATE POLICY "Employees_Update_Completion_v2"
ON public.task_checklist_items
FOR UPDATE
TO authenticated
USING (private.is_task_employee(task_id))
WITH CHECK (private.is_task_employee(task_id));

-- 4. Final Grant Verification
GRANT SELECT, INSERT, UPDATE, DELETE ON public.task_checklist_items TO authenticated;
