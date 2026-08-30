-- ============================================================
-- Fix Notifications Permissions
-- ============================================================

-- Explicitly grant permissions to the authenticated role for the notifications table
GRANT SELECT, INSERT, UPDATE, DELETE ON public.notifications TO authenticated, service_role;

-- Ensure RLS is still active but correctly configured
-- (The existing policy should be sufficient if SELECT/UPDATE are granted)
-- Policy: "users can view own notifications" on public.notifications for select using (user_id = auth.uid())
-- Policy: "users can update own notifications" on public.notifications for update using (user_id = auth.uid())
