-- ============================================================
-- FieldFlow - Phase 4: Workspace + Join Request Flow
-- ============================================================

-- 1. ENUMS
create type public.request_status as enum (
    'pending',
    'approved',
    'rejected'
);

-- 2. TABLES
create table public.workspaces (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    owner_id uuid not null references public.profiles(id) on delete restrict,
    company_id_display text unique not null,
    created_at timestamptz not null default now()
);

create table public.join_requests (
    id uuid primary key default gen_random_uuid(),
    employee_id uuid not null references public.profiles(id) on delete cascade,
    workspace_id uuid not null references public.workspaces(id) on delete cascade,
    status public.request_status not null default 'pending',
    created_at timestamptz not null default now(),

    -- Prevent duplicate active pending requests
    constraint unique_pending_request unique (employee_id, workspace_id, status)
);

-- 3. ALTER PROFILES
alter table public.profiles
add column workspace_id uuid references public.workspaces(id) on delete set null;

-- 4. HELPER: GENERATE COMPANY ID
create or replace function public.generate_company_id()
returns text
language plpgsql
as $$
declare
    new_id text;
    is_unique boolean := false;
begin
    while not is_unique loop
        -- Generate 8 random digits
        new_id := floor(random() * 90000000 + 10000000)::text;
        -- Check for uniqueness in workspaces
        select not exists (select 1 from public.workspaces where company_id_display = new_id) into is_unique;
    end loop;
    return new_id;
end;
$$;

-- 5. AUTOMATIC WORKSPACE CREATION FOR OWNERS
create or replace function public.handle_owner_workspace()
returns trigger
language plpgsql
security definer
as $$
begin
    if new.role = 'owner' then
        insert into public.workspaces (name, owner_id, company_id_display)
        values (
            new.full_name || '''s Workspace',
            new.id,
            public.generate_company_id()
        )
        returning id into new.workspace_id;
    end if;
    return new;
end;
$$;

create trigger on_owner_profile_created
before insert on public.profiles
for each row
execute function public.handle_owner_workspace();

-- 6. SECURE APPROVAL RPC
create or replace function public.respond_to_join_request(
    p_request_id uuid,
    p_action text -- 'approve' or 'reject'
)
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
    v_workspace_id uuid;
    v_employee_id uuid;
    v_status public.request_status;
begin
    -- 1. Get request details and verify owner
    select workspace_id, employee_id into v_workspace_id, v_employee_id
    from public.join_requests
    where id = p_request_id;

    if not exists (
        select 1 from public.workspaces
        where id = v_workspace_id and owner_id = (select auth.uid())
    ) then
        raise exception 'Unauthorized: You do not own this workspace';
    end if;

    -- 2. Process action
    if p_action = 'approve' then
        update public.join_requests
        set status = 'approved'
        where id = p_request_id;

        update public.profiles
        set workspace_id = v_workspace_id
        where id = v_employee_id;
    elsif p_action = 'reject' then
        update public.join_requests
        set status = 'rejected'
        where id = p_request_id;
    else
        raise exception 'Invalid action: %', p_action;
    end if;
end;
$$;

-- 7. RLS POLICIES

-- Workspaces
alter table public.workspaces enable row level security;

create policy "anyone can find workspace by code"
on public.workspaces
for select
to authenticated
using (true); -- Filtered by company_id_display in client query

create policy "owners can manage own workspace"
on public.workspaces
for all
to authenticated
using (owner_id = (select auth.uid()));

-- Join Requests
alter table public.join_requests enable row level security;

create policy "employees can view own requests"
on public.join_requests
for select
to authenticated
using (employee_id = (select auth.uid()));

create policy "employees can create requests"
on public.join_requests
for insert
to authenticated
with check (
    employee_id = (select auth.uid())
    and (select role from public.profiles where id = (select auth.uid())) = 'employee'
);

create policy "owners can view workspace requests"
on public.join_requests
for select
to authenticated
using (
    exists (
        select 1 from public.workspaces
        where id = workspace_id and owner_id = (select auth.uid())
    )
);

-- Update Profiles RLS
create policy "users can view workspace members"
on public.profiles
for select
to authenticated
using (
    workspace_id = (
        select workspace_id from public.profiles where id = (select auth.uid())
    )
);
