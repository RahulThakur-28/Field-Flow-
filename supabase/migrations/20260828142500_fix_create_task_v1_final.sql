-- ============================================================
-- 1. CLEANUP TASKS TABLE (REVERT COORDS ADDITION)
-- ============================================================

-- The user stated that tasks table does NOT contain these columns
-- and we should not add them. They belong in geofences.
ALTER TABLE public.tasks DROP COLUMN IF EXISTS latitude;
ALTER TABLE public.tasks DROP COLUMN IF EXISTS longitude;
ALTER TABLE public.tasks DROP COLUMN IF EXISTS radius_meters;

-- ============================================================
-- 2. ENSURE CHECKLIST TABLE EXISTS
-- ============================================================

CREATE TABLE IF NOT EXISTS public.task_checklist_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES public.tasks(id) ON DELETE CASCADE,
    item_text TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT false,
    position INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Ensure index exists
CREATE INDEX IF NOT EXISTS idx_task_checklist_items_task_id ON public.task_checklist_items(task_id);

-- ============================================================
-- 3. FINAL CORRECTED RPC
-- ============================================================

-- Drop all previous overloads to ensure a clean state
DROP FUNCTION IF EXISTS public.create_task_v1(text, text, public.task_priority, uuid, timestamptz, uuid, double precision, double precision, integer);
DROP FUNCTION IF EXISTS public.create_task_v1(text, text, public.task_priority, uuid, timestamptz, uuid, text, double precision, double precision, integer);
DROP FUNCTION IF EXISTS public.create_task_v1(text, text, public.task_priority, uuid, timestamptz, uuid, text, double precision, double precision, integer, text[]);
DROP FUNCTION IF EXISTS public.create_task_v1(text, text, text, uuid, timestamptz, uuid, text, double precision, double precision, integer, text[]);

CREATE OR REPLACE FUNCTION public.create_task_v1(
    p_title text,
    p_description text,
    p_priority public.task_priority,
    p_created_by uuid,
    p_location text,
    p_due_date timestamptz,
    p_employee_id uuid,
    p_latitude double precision DEFAULT NULL,
    p_longitude double precision DEFAULT NULL,
    p_radius_meters integer DEFAULT 100,
    p_checklist_items text[] DEFAULT ARRAY[]::text[]
)
RETURNS uuid
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_catalog
AS $$
DECLARE
    v_task_id uuid;
    v_item_text text;
    v_index integer := 0;
BEGIN
    -- 1. Authorization Check
    IF p_created_by != auth.uid() THEN
        RAISE EXCEPTION 'Unauthorized: Creator ID must match authenticated user';
    END IF;

    -- 2. Verify workspace membership
    IF NOT EXISTS (
        SELECT 1
        FROM public.profiles owner_p
        JOIN public.profiles employee_p ON employee_p.workspace_id = owner_p.workspace_id
        WHERE owner_p.id = p_created_by
          AND employee_p.id = p_employee_id
    ) THEN
        RAISE EXCEPTION 'Unauthorized: Employee does not belong to your workspace';
    END IF;

    -- 3. Create the Task (Using ONLY valid columns)
    INSERT INTO public.tasks (
        title,
        description,
        priority,
        status,
        created_by,
        due_date,
        location
    ) VALUES (
        p_title,
        p_description,
        p_priority,
        'assigned',
        p_created_by,
        p_due_date,
        p_location
    ) RETURNING id INTO v_task_id;

    -- 4. Create the Assignment
    INSERT INTO public.task_assignments (
        task_id,
        employee_id,
        assigned_by,
        status
    ) VALUES (
        v_task_id,
        p_employee_id,
        p_created_by,
        'assigned'
    );

    -- 5. Create Geofence if coordinates provided
    IF p_latitude IS NOT NULL AND p_longitude IS NOT NULL THEN
        INSERT INTO public.geofences (
            task_id,
            latitude,
            longitude,
            radius_meters
        ) VALUES (
            v_task_id,
            p_latitude,
            p_longitude,
            p_radius_meters
        );
    END IF;

    -- 6. Insert Checklist Items
    IF p_checklist_items IS NOT NULL AND array_length(p_checklist_items, 1) > 0 THEN
        FOREACH v_item_text IN ARRAY p_checklist_items
        LOOP
            INSERT INTO public.task_checklist_items (
                task_id,
                item_text,
                is_completed,
                position
            ) VALUES (
                v_task_id,
                v_item_text,
                false,
                v_index
            );
            v_index := v_index + 1;
        END LOOP;
    END IF;

    RETURN v_task_id;
END;
$$;

-- 4. Set Permissions
REVOKE EXECUTE ON FUNCTION public.create_task_v1(text, text, public.task_priority, uuid, text, timestamptz, uuid, double precision, double precision, integer, text[]) FROM public, anon, authenticated;
GRANT EXECUTE ON FUNCTION public.create_task_v1(text, text, public.task_priority, uuid, text, timestamptz, uuid, double precision, double precision, integer, text[]) TO authenticated;
