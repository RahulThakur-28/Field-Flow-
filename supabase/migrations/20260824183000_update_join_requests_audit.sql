-- Add audit columns to join_requests
alter table public.join_requests
add column reviewed_at timestamptz,
add column reviewed_by uuid references public.profiles(id);

-- Update RPC to include audit info
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
        set
            status = 'approved',
            reviewed_at = now(),
            reviewed_by = (select auth.uid())
        where id = p_request_id;

        update public.profiles
        set workspace_id = v_workspace_id
        where id = v_employee_id;
    elsif p_action = 'reject' then
        update public.join_requests
        set
            status = 'rejected',
            reviewed_at = now(),
            reviewed_by = (select auth.uid())
        where id = p_request_id;
    else
        raise exception 'Invalid action: %', p_action;
    end if;
end;
$$;
