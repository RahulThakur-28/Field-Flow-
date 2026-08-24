-- ============================================================
-- FieldFlow - Phase 3.4.1
-- RLS Helper Functions
-- ============================================================

-- Create private schema for internal authorization helpers
create schema if not exists private;


-- ============================================================
-- 1. CHECK CURRENT USER IS OWNER
-- ============================================================

create or replace function private.is_owner()
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select exists (
        select 1
        from public.profiles
        where id = (select auth.uid())
          and role = 'owner'
          and is_active = true
    );
$$;


-- ============================================================
-- 2. CHECK CURRENT USER IS ACTIVE EMPLOYEE
-- ============================================================

create or replace function private.is_employee()
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select exists (
        select 1
        from public.profiles
        where id = (select auth.uid())
          and role = 'employee'
          and is_active = true
    );
$$;


-- ============================================================
-- 3. CHECK CURRENT USER OWNS A TASK
-- ============================================================

create or replace function private.is_task_owner(
    p_task_id uuid
)
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select exists (
        select 1
        from public.tasks
        where id = p_task_id
          and created_by = (select auth.uid())
          and is_deleted = false
    );
$$;


-- ============================================================
-- 4. CHECK CURRENT USER IS ASSIGNED TO A TASK
-- ============================================================

create or replace function private.is_task_employee(
    p_task_id uuid
)
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select exists (
        select 1
        from public.task_assignments
        where task_id = p_task_id
          and employee_id = (select auth.uid())
          and status <> 'cancelled'
    );
$$;


-- ============================================================
-- 5. CHECK CURRENT USER CAN ACCESS A TASK
-- ============================================================

create or replace function private.can_access_task(
    p_task_id uuid
)
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select
        (select private.is_task_owner(p_task_id))
        or
        (select private.is_task_employee(p_task_id));
$$;


-- ============================================================
-- 6. CHECK LOCATION SESSION ACCESS
-- ============================================================

create or replace function private.can_access_location_session(
    p_session_id uuid
)
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select exists (
        select 1
        from public.location_sessions ls
        where ls.id = p_session_id
          and (
              ls.employee_id = (select auth.uid())
              or
              exists (
                  select 1
                  from public.tasks t
                  where t.id = ls.task_id
                    and t.created_by = (select auth.uid())
                    and t.is_deleted = false
              )
          )
    );
$$;


-- ============================================================
-- 7. CHECK RECORDING SESSION ACCESS
-- ============================================================

create or replace function private.can_access_recording_session(
    p_recording_session_id uuid
)
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select exists (
        select 1
        from public.recording_sessions rs
        where rs.id = p_recording_session_id
          and (
              rs.employee_id = (select auth.uid())
              or
              exists (
                  select 1
                  from public.tasks t
                  where t.id = rs.task_id
                    and t.created_by = (select auth.uid())
                    and t.is_deleted = false
              )
          )
    );
$$;


-- ============================================================
-- 8. CHECK EMPLOYEE PROFILE IS RELATED TO OWNER
-- ============================================================

create or replace function private.can_access_profile(
    p_profile_id uuid
)
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select
        p_profile_id = (select auth.uid())
        or
        exists (
            select 1
            from public.tasks t
            join public.task_assignments ta
                on ta.task_id = t.id
            where t.created_by = (select auth.uid())
              and ta.employee_id = p_profile_id
              and ta.status <> 'cancelled'
              and t.is_deleted = false
        );
$$;


-- ============================================================
-- 9. WORKSPACE AUTHORIZATION HELPERS
-- ============================================================

create or replace function private.has_workspace()
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select exists (
        select 1
        from public.profiles
        where id = (select auth.uid())
          and workspace_id is not null
          and is_active = true
    );
$$;


create or replace function private.is_workspace_owner(
    p_workspace_id uuid
)
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select exists (
        select 1
        from public.workspaces w
        where w.id = p_workspace_id
          and w.owner_id = (select auth.uid())
    );
$$;


create or replace function private.is_same_workspace_employee(
    p_employee_id uuid
)
returns boolean
language sql
security definer
set search_path = ''
stable
as $$
    select exists (
        select 1
        from public.profiles owner_profile
        join public.profiles employee_profile
            on employee_profile.workspace_id = owner_profile.workspace_id
        where owner_profile.id = (select auth.uid())
          and owner_profile.role = 'owner'
          and owner_profile.is_active = true
          and employee_profile.id = p_employee_id
          and employee_profile.role = 'employee'
          and employee_profile.is_active = true
          and employee_profile.workspace_id is not null
    );
$$;



-- ============================================================
-- 9. FUNCTION PERMISSIONS
-- ============================================================

revoke execute on function private.is_owner() from public, anon, authenticated;
revoke execute on function private.is_employee() from public, anon, authenticated;
revoke execute on function private.is_task_owner(uuid) from public, anon, authenticated;
revoke execute on function private.is_task_employee(uuid) from public, anon, authenticated;
revoke execute on function private.can_access_task(uuid) from public, anon, authenticated;
revoke execute on function private.can_access_location_session(uuid) from public, anon, authenticated;
revoke execute on function private.can_access_recording_session(uuid) from public, anon, authenticated;
revoke execute on function private.can_access_profile(uuid) from public, anon, authenticated;

revoke execute on function private.has_workspace()
from public, anon, authenticated;

revoke execute on function private.is_workspace_owner(uuid)
from public, anon, authenticated;

revoke execute on function private.is_same_workspace_employee(uuid)
from public, anon, authenticated;



-- ============================================================
-- FieldFlow - Phase 3.4.2
-- Row Level Security Policies
-- ============================================================


-- ============================================================
-- 1. ENABLE RLS
-- ============================================================

alter table public.profiles enable row level security;
alter table public.tasks enable row level security;
alter table public.task_assignments enable row level security;
alter table public.geofences enable row level security;
alter table public.location_sessions enable row level security;
alter table public.location_points enable row level security;
alter table public.recording_sessions enable row level security;
alter table public.transcripts enable row level security;
alter table public.task_images enable row level security;
alter table public.activity_logs enable row level security;
alter table public.notifications enable row level security;


-- ============================================================
-- 2. PROFILES
-- ============================================================

drop policy if exists "owners can view their employees"
on public.profiles;

create policy "owners can view employees in their workspace"
on public.profiles
for select
to authenticated
using (
    id = (select auth.uid())
    or
    (
        role = 'employee'
        and workspace_id is not null
        and exists (
            select 1
            from public.profiles owner_profile
            where owner_profile.id = (select auth.uid())
              and owner_profile.role = 'owner'
              and owner_profile.is_active = true
              and owner_profile.workspace_id = profiles.workspace_id
        )
    )
);
-- ============================================================
-- 3. TASKS
-- ============================================================

create policy "owners can view own tasks"
on public.tasks
for select
to authenticated
using (
    (select private.is_task_owner(id))
);

create policy "employees can view assigned tasks"
on public.tasks
for select
to authenticated
using (
    (select private.is_task_employee(id))
);


drop policy if exists "owners can create tasks"
on public.tasks;

create policy "workspace owners can create tasks"
on public.tasks
for insert
to authenticated
with check (
    (select private.is_owner())
    and
    (select private.has_workspace())
    and
    created_by = (select auth.uid())
);


create policy "owners can update own tasks"
on public.tasks
for update
to authenticated
using (
    (select private.is_task_owner(id))
)
with check (
    (select private.is_task_owner(id))
);


-- ============================================================
-- 4. TASK ASSIGNMENTS
-- ============================================================

create policy "owners can view task assignments"
on public.task_assignments
for select
to authenticated
using (
    (select private.is_task_owner(task_id))
    or
    employee_id = (select auth.uid())
);


drop policy if exists "owners can create assignments"
on public.task_assignments;

create policy "owners can create same workspace assignments"
on public.task_assignments
for insert
to authenticated
with check (
    (select private.is_task_owner(task_id))
    and
    (select private.is_same_workspace_employee(employee_id))
    and
    assigned_by = (select auth.uid())
);

drop policy if exists "owners can update assignments"
on public.task_assignments;

create policy "owners can update same workspace assignments"
on public.task_assignments
for update
to authenticated
using (
    (select private.is_task_owner(task_id))
)
with check (
    (select private.is_task_owner(task_id))
    and
    (select private.is_same_workspace_employee(employee_id))
);

-- ============================================================
-- 5. GEOFENCES
-- ============================================================

create policy "owners can view task geofences"
on public.geofences
for select
to authenticated
using (
    (select private.is_task_owner(task_id))
);

create policy "employees can view assigned geofences"
on public.geofences
for select
to authenticated
using (
    (select private.is_task_employee(task_id))
);


create policy "owners can create geofences"
on public.geofences
for insert
to authenticated
with check (
    (select private.is_task_owner(task_id))
);


create policy "owners can update geofences"
on public.geofences
for update
to authenticated
using (
    (select private.is_task_owner(task_id))
)
with check (
    (select private.is_task_owner(task_id))
);


-- ============================================================
-- 6. LOCATION SESSIONS
-- ============================================================

create policy "employees can view own location sessions"
on public.location_sessions
for select
to authenticated
using (
    employee_id = (select auth.uid())
);


create policy "owners can view employee location sessions"
on public.location_sessions
for select
to authenticated
using (
    (select private.is_task_owner(task_id))
);


create policy "employees can create location sessions"
on public.location_sessions
for insert
to authenticated
with check (
    employee_id = (select auth.uid())
    and
    (select private.is_task_employee(task_id))
);


create policy "employees can update own location sessions"
on public.location_sessions
for update
to authenticated
using (
    employee_id = (select auth.uid())
)
with check (
    employee_id = (select auth.uid())
);


-- ============================================================
-- 7. LOCATION POINTS
-- ============================================================

create policy "users can view authorized location points"
on public.location_points
for select
to authenticated
using (
    (select private.can_access_location_session(session_id))
);


create policy "employees can insert own location points"
on public.location_points
for insert
to authenticated
with check (
    exists (
        select 1
        from public.location_sessions ls
        where ls.id = session_id
          and ls.employee_id = (select auth.uid())
    )
);


-- ============================================================
-- 8. RECORDING SESSIONS
-- ============================================================

create policy "users can view authorized recordings"
on public.recording_sessions
for select
to authenticated
using (
    (select private.can_access_recording_session(id))
);


create policy "employees can create recording sessions"
on public.recording_sessions
for insert
to authenticated
with check (
    employee_id = (select auth.uid())
    and
    (select private.is_task_employee(task_id))
);


create policy "employees can update own recording sessions"
on public.recording_sessions
for update
to authenticated
using (
    employee_id = (select auth.uid())
)
with check (
    employee_id = (select auth.uid())
);


-- ============================================================
-- 9. TRANSCRIPTS
-- ============================================================

create policy "users can view authorized transcripts"
on public.transcripts
for select
to authenticated
using (
    (select private.can_access_recording_session(recording_session_id))
);


create policy "employees can create transcripts"
on public.transcripts
for insert
to authenticated
with check (
    (select private.can_access_recording_session(recording_session_id))
);


-- ============================================================
-- 10. TASK IMAGES
-- ============================================================

create policy "users can view authorized task images"
on public.task_images
for select
to authenticated
using (
    (select private.can_access_task(task_id))
);


create policy "employees can upload task images"
on public.task_images
for insert
to authenticated
with check (
    employee_id = (select auth.uid())
    and
    (select private.is_task_employee(task_id))
);


-- ============================================================
-- 11. ACTIVITY LOGS
-- ============================================================

create policy "users can view authorized activity logs"
on public.activity_logs
for select
to authenticated
using (
    user_id = (select auth.uid())
    or
    (
        task_id is not null
        and
        (select private.can_access_task(task_id))
    )
);


create policy "authenticated users can create own activity logs"
on public.activity_logs
for insert
to authenticated
with check (
    user_id = (select auth.uid())
);


-- ============================================================
-- 12. NOTIFICATIONS
-- ============================================================

create policy "users can view own notifications"
on public.notifications
for select
to authenticated
using (
    user_id = (select auth.uid())
);


create policy "users can update own notifications"
on public.notifications
for update
to authenticated
using (
    user_id = (select auth.uid())
)
with check (
    user_id = (select auth.uid())
);


-- ============================================================
-- FieldFlow - Phase 3.4.3
-- Secure Employee Task Status Updates
-- ============================================================

-- ============================================================
-- 1. START TASK
-- ============================================================

create or replace function public.start_task(
    p_task_id uuid
)
returns public.tasks
language plpgsql
security definer
set search_path = ''
as $$
declare
    v_task public.tasks;
begin

    if not exists (
        select 1
        from public.task_assignments ta
        where ta.task_id = p_task_id
          and ta.employee_id = (select auth.uid())
          and ta.status <> 'cancelled'
    ) then
        raise exception 'You are not assigned to this task';
    end if;

    update public.tasks
    set
        status = 'in_progress',
        updated_at = now()
    where id = p_task_id
      and status in ('assigned', 'pending')
      and is_deleted = false
    returning * into v_task;

    if v_task.id is null then
        raise exception 'Task cannot be started from its current state';
    end if;

    update public.task_assignments
    set status = 'in_progress'
    where task_id = p_task_id
      and employee_id = (select auth.uid());

    return v_task;
end;
$$;


-- ============================================================
-- 2. COMPLETE TASK
-- ============================================================

create or replace function public.complete_task(
    p_task_id uuid
)
returns public.tasks
language plpgsql
security definer
set search_path = ''
as $$
declare
    v_task public.tasks;
begin

    if not exists (
        select 1
        from public.task_assignments ta
        where ta.task_id = p_task_id
          and ta.employee_id = (select auth.uid())
          and ta.status <> 'cancelled'
    ) then
        raise exception 'You are not assigned to this task';
    end if;

    update public.tasks
    set
        status = 'completed',
        completed_at = now(),
        updated_at = now()
    where id = p_task_id
      and status = 'in_progress'
      and is_deleted = false
    returning * into v_task;

    if v_task.id is null then
        raise exception 'Task must be in progress before completion';
    end if;

    update public.task_assignments
    set status = 'completed'
    where task_id = p_task_id
      and employee_id = (select auth.uid());

    return v_task;
end;
$$;


-- ============================================================
-- 3. RPC PERMISSIONS
-- ============================================================

revoke execute on function public.start_task(uuid)
from public, anon;

grant execute on function public.start_task(uuid)
to authenticated;


revoke execute on function public.complete_task(uuid)
from public, anon;

grant execute on function public.complete_task(uuid)
to authenticated;






-- ============================================================
-- FieldFlow - Phase 3.4.4
-- Supabase Storage RLS
-- ============================================================


-- ============================================================
-- 1. TASK IMAGES - INSERT
-- ============================================================

create policy "employees can upload assigned task images"
on storage.objects
for insert
to authenticated
with check (
    bucket_id = 'task-images'
    and (storage.foldername(name))[1]::uuid is not null
    and (storage.foldername(name))[2]::uuid = (select auth.uid())
    and (select private.is_task_employee(
        (storage.foldername(name))[1]::uuid
    ))
);


-- ============================================================
-- 2. TASK IMAGES - VIEW
-- ============================================================

create policy "authorized users can view task images"
on storage.objects
for select
to authenticated
using (
    bucket_id = 'task-images'
    and (
        (
            (select private.is_task_owner(
                (storage.foldername(name))[1]::uuid
            ))
        )
        or
        (
            (storage.foldername(name))[2]::uuid = (select auth.uid())
            and
            (select private.is_task_employee(
                (storage.foldername(name))[1]::uuid
            ))
        )
    )
);


-- ============================================================
-- 3. TASK IMAGES - DELETE
-- ============================================================

create policy "employees can delete own task images"
on storage.objects
for delete
to authenticated
using (
    bucket_id = 'task-images'
    and (storage.foldername(name))[2]::uuid = (select auth.uid())
    and (select private.is_task_employee(
        (storage.foldername(name))[1]::uuid
    ))
);


-- ============================================================
-- 4. RECORDINGS - INSERT
-- ============================================================

create policy "employees can upload recordings"
on storage.objects
for insert
to authenticated
with check (
    bucket_id = 'recordings'
    and (storage.foldername(name))[1]::uuid is not null
    and (storage.foldername(name))[2]::uuid = (select auth.uid())
    and (select private.is_task_employee(
        (storage.foldername(name))[1]::uuid
    ))
);


-- ============================================================
-- 5. RECORDINGS - VIEW
-- ============================================================

create policy "authorized users can view recordings"
on storage.objects
for select
to authenticated
using (
    bucket_id = 'recordings'
    and (
        (
            (select private.is_task_owner(
                (storage.foldername(name))[1]::uuid
            ))
        )
        or
        (
            (storage.foldername(name))[2]::uuid = (select auth.uid())
            and
            (select private.is_task_employee(
                (storage.foldername(name))[1]::uuid
            ))
        )
    )
);


-- ============================================================
-- 6. RECORDINGS - DELETE
-- ============================================================

create policy "employees can delete own recordings"
on storage.objects
for delete
to authenticated
using (
    bucket_id = 'recordings'
    and (storage.foldername(name))[2]::uuid = (select auth.uid())
    and (select private.is_task_employee(
        (storage.foldername(name))[1]::uuid
    ))
);




-- ============================================================
-- FieldFlow - Phase 3.5
-- Workspace + Join Request RLS
-- ============================================================

-- ============================================================
-- WORKSPACES
-- ============================================================

alter table public.workspaces enable row level security;

create policy "users can view own workspace"
on public.workspaces
for select
to authenticated
using (
    owner_id = (select auth.uid())
    or
    id = (
        select workspace_id
        from public.profiles
        where id = (select auth.uid())
    )
);


-- ============================================================
-- JOIN REQUESTS
-- ============================================================

alter table public.join_requests enable row level security;

create policy "employees can view own join requests"
on public.join_requests
for select
to authenticated
using (
    employee_id = (select auth.uid())
);


create policy "owners can view workspace join requests"
on public.join_requests
for select
to authenticated
using (
    exists (
        select 1
        from public.workspaces w
        where w.id = join_requests.workspace_id
          and w.owner_id = (select auth.uid())
    )
);