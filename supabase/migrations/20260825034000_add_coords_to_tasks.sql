-- ============================================================
-- Add Latitude, Longitude, and Radius to Tasks table
-- ============================================================

ALTER TABLE public.tasks
ADD COLUMN latitude double precision,
ADD COLUMN longitude double precision,
ADD COLUMN radius_meters integer DEFAULT 100;

-- Update the RPC to also store these in the tasks table for easier retrieval
CREATE OR REPLACE FUNCTION public.create_task_v1(
    p_title text,
    p_description text,
    p_priority public.task_priority,
    p_created_by uuid,
    p_due_date timestamptz,
    p_employee_id uuid,
    p_location text DEFAULT NULL,
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

    -- 3. Create the Task with location data
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
        p_priority,
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

    -- 5. Create Geofence (for actual tracking/triggers)
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
