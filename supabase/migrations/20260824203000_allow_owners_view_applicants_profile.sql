-- ============================================================
-- Allow owners to view profiles of employees requesting to join
-- ============================================================

-- This policy allows an owner to SELECT the profile details of an employee
-- who has a PENDING join request for a workspace owned by that owner.
-- This is required for the "Employee Requests" screen to display the applicant's name, email, etc.

create policy "owners_can_view_applicants"
on public.profiles
for select
to authenticated
using (
    exists (
        select 1
        from public.join_requests jr
        join public.workspaces w on w.id = jr.workspace_id
        where jr.employee_id = profiles.id
          and w.owner_id = (select auth.uid())
          and jr.status = 'pending'
    )
);
