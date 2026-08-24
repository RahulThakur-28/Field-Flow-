-- ============================================================
-- Fix Applicant Profile Access for Owners
-- ============================================================

-- 1. Create a secure SECURITY DEFINER helper function.
-- This function checks if the current user is the owner of a workspace
-- that has a pending join request from the specified employee.
-- By using SECURITY DEFINER, we bypass RLS for the internal lookup,
-- which prevents 'infinite recursion' errors.
CREATE OR REPLACE FUNCTION private.is_workspace_owner_for_applicant(requested_employee_id uuid)
RETURNS boolean
LANGUAGE sql
SECURITY DEFINER
SET search_path = public
STABLE
AS $$
    SELECT EXISTS (
        SELECT 1
        FROM public.join_requests jr
        JOIN public.workspaces w ON w.id = jr.workspace_id
        WHERE jr.employee_id = requested_employee_id
          AND w.owner_id = auth.uid()
          AND jr.status = 'pending'
    );
$$;

-- 2. Revoke execute from public to ensure it's primarily used via RLS.
REVOKE EXECUTE ON FUNCTION private.is_workspace_owner_for_applicant(uuid) FROM public, anon, authenticated;
GRANT EXECUTE ON FUNCTION private.is_workspace_owner_for_applicant(uuid) TO authenticated;

-- 3. Create a narrow SELECT policy for profiles.
-- This allows owners to read the name/email/phone of users requesting to join their company.
-- We use the helper function to avoid recursion on the profiles table.
CREATE POLICY "owners_view_pending_applicants"
ON public.profiles
FOR SELECT
TO authenticated
USING (
    private.is_workspace_owner_for_applicant(id)
);
