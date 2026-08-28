-- ============================================================
-- Harden Security Helpers and Checklist RLS
-- ============================================================

-- 1. Redefine security helpers with explicit search_path and robust logic
-- We include 'auth' to ensure uid() is resolvable and 'public' for tables.
-- We use SECURITY DEFINER so they run as the creator (postgres) and bypass RLS on 'tasks'.

CREATE OR REPLACE FUNCTION private.is_task_owner(p_task_id uuid)
RETURNS boolean
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, auth, pg_catalog
STABLE
AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1
        FROM public.tasks
        WHERE id = p_task_id
          AND created_by = auth.uid()
          AND is_deleted = false
    );
END;
$$;

CREATE OR REPLACE FUNCTION private.is_task_employee(p_task_id uuid)
RETURNS boolean
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, auth, pg_catalog
STABLE
AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1
        FROM public.task_assignments
        WHERE task_id = p_task_id
          AND employee_id = auth.uid()
          AND status <> 'cancelled'
    );
END;
$$;

-- 2. Ensure authenticated role can execute these helpers
GRANT EXECUTE ON FUNCTION private.is_task_owner(uuid) TO authenticated;
GRANT EXECUTE ON FUNCTION private.is_task_employee(uuid) TO authenticated;

-- 3. Ensure authenticated role has full table access (subject to RLS)
GRANT SELECT, INSERT, UPDATE, DELETE ON public.task_checklist_items TO authenticated;

-- 4. Re-apply RLS Policies for task_checklist_items using the hardened helpers
DROP POLICY IF EXISTS "Owners can manage checklist items of own tasks" ON public.task_checklist_items;
DROP POLICY IF EXISTS "Employees can view assigned checklist items" ON public.task_checklist_items;
DROP POLICY IF EXISTS "Employees can update assigned checklist item completion" ON public.task_checklist_items;

-- A. Owners: Full control over checklist items for tasks they created
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

-- B. Employees: View checklist items for tasks assigned to them
CREATE POLICY "Employees can view assigned checklist items"
ON public.task_checklist_items
FOR SELECT
TO authenticated
USING (
    private.is_task_employee(task_id)
);

-- C. Employees: Update completion status for tasks assigned to them
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
