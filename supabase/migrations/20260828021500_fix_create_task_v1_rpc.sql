-- ============================================================
-- Fix create_task_v1 RPC signature mismatch
-- ============================================================

-- 1. Drop ALL possible previous versions to avoid ambiguity
-- Version from 20260824221500 (9 params)
DROP FUNCTION IF EXISTS public.create_task_v1(text, text, public.task_priority, uuid, timestamptz, uuid, double precision, double precision, integer);
-- Version from 20260824223000 / 20260825034000 (10 params)
DROP FUNCTION IF EXISTS public.create_task_v1(text, text, public.task_priority, uuid, timestamptz, uuid, text, double precision, double precision, integer);
-- Version from 20260828011000 (11 params)
DROP FUNCTION IF EXISTS public.create_task_v1(text, text, public.task_priority, uuid, timestamptz, uuid, text, double precision, double precision, integer, text[]);

-- 2. Create the unified function with robust types for PostgREST
-- Using text for priority to handle string input safely
CREATE OR REPLACE FUNCTION public.create_task_v1(
    p_title text,
    p_description text,
    p_priority text,
    p_created_by uuid,
    p_due_date timestamptz,
    p_employee_id uuid,
    p_location text DEFAULT NULL,
    p_latitude double precision DEFAULT NULL,
    p_longitude double precision DEFAULT NULL,
    p_radius_meters integer DEFAULT 100,
    p_checklist_items text[] DEFAULT '{}'
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
    v_priority public.task_priority;
BEGIN
    -- Cast text priority to enum safely
    v_priority := p_priority::public.task_priority;

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

    -- 3. Create the Task
    INSERT INTO public.tasks (
        title,
        description,
        priority,
        status,
        created_by,
        due_date,
        location,
        latitude,
        longitude,
        radius_meters
    ) VALUES (
        p_title,
        p_description,
        v_priority,
        'assigned',
        p_created_by,
        p_due_date,
        p_location,
        p_latitude,
        p_longitude,
        p_radius_meters
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

    -- 5. Create Geofence if location provided
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

    -- 6. Create Checklist Items
    IF p_checklist_items IS NOT NULL THEN
        FOREACH v_item_text IN ARRAY p_checklist_items
        LOOP
            INSERT INTO public.task_checklist_items (
                task_id,
                item_text,
                position
            ) VALUES (
                v_task_id,
                v_item_text,
                v_index
            );
            v_index := v_index + 1;
        END LOOP;
    END IF;

    RETURN v_task_id;
END;
$$;

-- 3. Grant Permissions
REVOKE EXECUTE ON FUNCTION public.create_task_v1(text, text, text, uuid, timestamptz, uuid, text, double precision, double precision, integer, text[]) FROM public, anon, authenticated;
GRANT EXECUTE ON FUNCTION public.create_task_v1(text, text, text, uuid, timestamptz, uuid, text, double precision, double precision, integer, text[]) TO authenticated;
