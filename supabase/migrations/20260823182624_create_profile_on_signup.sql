-- ============================================================
-- FieldFlow - Phase 3.2
-- Create profile automatically after Supabase Auth signup
-- ============================================================

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
    insert into public.profiles (
        id,
        full_name,
        email,
        phone,
        role
    )
    values (
        new.id,
        coalesce(
            new.raw_user_meta_data ->> 'full_name',
            split_part(coalesce(new.email, ''), '@', 1)
        ),
        new.email,
        new.raw_user_meta_data ->> 'phone',
        'employee'
    );

    return new;
end;
$$;


create trigger on_auth_user_created
after insert on auth.users
for each row
execute function public.handle_new_user();