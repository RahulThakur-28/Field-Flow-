-- ============================================================
-- RLS Policies for Task Checklist Items
-- ============================================================

ALTER TABLE public.task_checklist_items ENABLE ROW LEVEL SECURITY;

-- 1. Owners can do everything with checklist items of their own tasks
CREATE POLICY "Owners can manage checklist items of own tasks"
ON public.task_checklist_items
FOR ALL
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM public.tasks
        WHERE tasks.id = task_checklist_items.task_id
          AND tasks.created_by = auth.uid()
    )
)
WITH CHECK (
    EXISTS (
        SELECT 1 FROM public.tasks
        WHERE tasks.id = task_checklist_items.task_id
          AND tasks.created_by = auth.uid()
    )
);

-- 2. Employees can view checklist items of assigned tasks
CREATE POLICY "Employees can view assigned checklist items"
ON public.task_checklist_items
FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM public.task_assignments
        WHERE task_assignments.task_id = task_checklist_items.task_id
          AND task_assignments.employee_id = auth.uid()
    )
);

-- 3. Employees can update completion status of assigned checklist items
CREATE POLICY "Employees can update assigned checklist item completion"
ON public.task_checklist_items
FOR UPDATE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM public.task_assignments
        WHERE task_assignments.task_id = task_checklist_items.task_id
          AND task_assignments.employee_id = auth.uid()
    )
)
WITH CHECK (
    EXISTS (
        SELECT 1 FROM public.task_assignments
        WHERE task_assignments.task_id = task_checklist_items.task_id
          AND task_assignments.employee_id = auth.uid()
    )
);
