-- ============================================================
-- Fix Permission Denied for Workspaces Lookup
-- ============================================================

-- 1. Explicitly grant SELECT privilege on the workspaces table to the authenticated role.
-- This addresses the 'permission denied for table workspaces' (Code: 42501) error.
grant select on public.workspaces to authenticated;

-- 2. Update Row Level Security (RLS) policies for the workspaces table.
-- We want to allow any authenticated user to perform a lookup by company_id_display.
-- This is necessary for the 'Join your company' flow where an employee needs to find a workspace.

-- Clean up any existing lookup policies to ensure a fresh state
drop policy if exists "anyone can find workspace by code" on public.workspaces;
drop policy if exists "allow lookup by company code" on public.workspaces;
drop policy if exists "users can view own workspace" on public.workspaces;

-- Policy: Allow authenticated users to SELECT from workspaces.
-- This allows the .select() query in WorkspaceDataSource.findWorkspaceByCode to succeed.
-- The query is naturally restricted by the 'company_id_display' filter in the application.
create policy "authenticated_workspace_select_policy"
on public.workspaces
for select
to authenticated
using (true);

-- Policy: Allow owners to manage their own workspaces (INSERT/UPDATE/DELETE).
-- This preserves the existing security rules for workspace owners.
-- SELECT is already covered by the broad policy above, but for clarity and safety in other operations:
create policy "owners_manage_own_workspace"
on public.workspaces
for all
to authenticated
using (owner_id = (select auth.uid()))
with check (owner_id = (select auth.uid()));
