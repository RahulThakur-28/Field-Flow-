-- ============================================================
-- Fix Profiles RLS Recursion and Workspace Visibility
-- ============================================================

-- 1. Create secure helper functions to bypass RLS recursion
-- These use SECURITY DEFINER to perform lookups with elevated privileges.
CREATE OR REPLACE FUNCTION private.get_workspace_id(p_user_id uuid)
RETURNS uuid LANGUAGE sql SECURITY DEFINER SET search_path = public, pg_catalog STABLE
AS $$ SELECT workspace_id FROM public.profiles WHERE id = p_user_id; $$;

CREATE OR REPLACE FUNCTION private.get_role(p_user_id uuid)
RETURNS text LANGUAGE sql SECURITY DEFINER SET search_path = public, pg_catalog STABLE
AS $$ SELECT role::text FROM public.profiles WHERE id = p_user_id; $$;

-- 2. Drop existing problematic policies on profiles
DROP POLICY IF EXISTS "owners can view employees in their workspace" ON public.profiles;
DROP POLICY IF EXISTS "users can view workspace members" ON public.profiles;
DROP POLICY IF EXISTS "profiles_select_own" ON public.profiles;
DROP POLICY IF EXISTS "profiles_select_workspace_members_as_owner" ON public.profiles;
DROP POLICY IF EXISTS "profiles_select_workspace_members_as_employee" ON public.profiles;
DROP POLICY IF EXISTS "owners_can_view_applicants" ON public.profiles;
DROP POLICY IF EXISTS "owners_view_pending_applicants" ON public.profiles;

-- 3. Create fresh, non-recursive policies

-- A. Users can always view their own profile
CREATE POLICY "profiles_select_self"
ON public.profiles FOR SELECT TO authenticated
USING (id = auth.uid());

-- B. Owners can view all profiles in their own workspace
-- Note: We use the helper to avoid querying public.profiles inside the USING clause directly.
CREATE POLICY "profiles_select_workspace_members_as_owner"
ON public.profiles FOR SELECT TO authenticated
USING (
    private.get_role(auth.uid()) = 'owner'
    AND workspace_id = private.get_workspace_id(auth.uid())
);

-- C. Employees can view other members in their workspace
CREATE POLICY "profiles_select_workspace_members_as_employee"
ON public.profiles FOR SELECT TO authenticated
USING (
    private.get_role(auth.uid()) = 'employee'
    AND workspace_id = private.get_workspace_id(auth.uid())
);

-- D. Owners can view profiles of applicants (pending join requests)
CREATE POLICY "profiles_select_applicants_as_owner"
ON public.profiles FOR SELECT TO authenticated
USING (
    private.get_role(auth.uid()) = 'owner'
    AND EXISTS (
        SELECT 1 FROM public.join_requests jr
        WHERE jr.employee_id = public.profiles.id
          AND jr.workspace_id = private.get_workspace_id(auth.uid())
          AND jr.status = 'pending'
    )
);

-- 4. Manage execution privileges
REVOKE EXECUTE ON FUNCTION private.get_workspace_id(uuid) FROM public, anon, authenticated;
REVOKE EXECUTE ON FUNCTION private.get_role(uuid) FROM public, anon, authenticated;
GRANT EXECUTE ON FUNCTION private.get_workspace_id(uuid) TO authenticated;
GRANT EXECUTE ON FUNCTION private.get_role(uuid) TO authenticated;
