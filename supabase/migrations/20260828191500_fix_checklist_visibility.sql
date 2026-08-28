-- ============================================================
-- Fix Checklist Visibility for Authenticated Users
-- ============================================================

-- 1. Explicitly grant permissions to the authenticated role
-- This is often missed when creating tables via SQL.
GRANT SELECT, INSERT, UPDATE, DELETE ON public.task_checklist_items TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.task_checklist_items TO service_role;

-- 2. Ensure helper functions are executable by the authenticated role
-- These are used in RLS policies.
GRANT EXECUTE ON FUNCTION private.is_task_owner(uuid) TO authenticated;
GRANT EXECUTE ON FUNCTION private.is_task_employee(uuid) TO authenticated;

-- 3. Refine RLS Policies to use SECURITY DEFINER helpers
-- This avoids recursive RLS checks on the tasks table during subqueries.

DROP POLICY IF EXISTS "Owners can manage checklist items of own tasks" ON public.task_checklist_items;
DROP POLICY IF EXISTS "Employees can view assigned checklist items" ON public.task_checklist_items;
DROP POLICY IF EXISTS "Employees can update assigned checklist item completion" ON public.task_checklist_items;

-- A. Owners: Full access to checklist items for their own tasks
CREATE POLICY "Owners can manage checklist items of own tasks"
ON public.task_checklist_items
FOR ALL
TO authenticated
USING (
    private.is_task_owner(task_id)
)
WITH CHECK (
    private.is_task_owner(task_id)
);

-- B. Employees: View checklist items for assigned tasks
CREATE POLICY "Employees can view assigned checklist items"
ON public.task_checklist_items
FOR SELECT
TO authenticated
USING (
    private.is_task_employee(task_id)
);

-- C. Employees: Update completion status for assigned checklist items
CREATE POLICY "Employees can update assigned checklist item completion"
ON public.task_checklist_items
FOR UPDATE
TO authenticated
USING (
    private.is_task_employee(task_id)
)
WITH CHECK (
    private.is_task_employee(task_id)
);
