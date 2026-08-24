-- ============================================================
-- Atomic Task Creation RPC
-- ============================================================

CREATE OR REPLACE FUNCTION public.create_task_v1(
    p_title text,
    p_description text,
    p_priority public.task_priority,
    p_created_by uuid,
    p_due_date timestamptz,
    p_employee_id uuid,
    p_latitude double precision DEFAULT NULL,
    p_longitude double precision DEFAULT NULL,
    p_radius_meters integer DEFAULT 100
)
RETURNS uuid
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_catalog
AS $$
DECLARE
    v_task_id uuid;
BEGIN
    -- 1. Authorization Check: Ensure the caller is the owner
    -- This is inherently checked by RLS on INSERT into tasks,
    -- but we can be explicit if needed. auth.uid() must match p_created_by.
    IF p_created_by != auth.uid() THEN
        RAISE EXCEPTION 'Unauthorized: Creator ID must match authenticated user';
    END IF;

    -- 2. Verify workspace membership: Employee must belong to the same workspace as the owner
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
        due_date
    ) VALUES (
        p_title,
        p_description,
        p_priority,
        'assigned', -- Default to assigned since we are assigning it immediately
        p_created_by,
        p_due_date
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

    RETURN v_task_id;
END;
$$;

-- 6. Permissions
REVOKE EXECUTE ON FUNCTION public.create_task_v1(text, text, public.task_priority, uuid, timestamptz, uuid, double precision, double precision, integer) FROM public, anon, authenticated;
GRANT EXECUTE ON FUNCTION public.create_task_v1(text, text, public.task_priority, uuid, timestamptz, uuid, double precision, double precision, integer) TO authenticated;
