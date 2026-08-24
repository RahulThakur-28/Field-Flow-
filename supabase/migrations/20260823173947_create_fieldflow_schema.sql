-- ============================================================
-- FieldFlow - Phase 2 Database Schema
-- Step 2.7: Final Initial Schema
-- ============================================================

-- ============================================================
-- 1. ENUMS
-- ============================================================

create type public.user_role as enum (
    'owner',
    'employee'
);

create type public.task_status as enum (
    'pending',
    'assigned',
    'in_progress',
    'completed',
    'cancelled'
);

create type public.task_priority as enum (
    'low',
    'medium',
    'high',
    'urgent'
);

create type public.assignment_status as enum (
    'assigned',
    'accepted',
    'in_progress',
    'completed',
    'cancelled'
);

create type public.tracking_status as enum (
    'active',
    'paused',
    'completed'
);

create type public.recording_status as enum (
    'recording',
    'interrupted',
    'completed',
    'failed'
);

create type public.transcription_status as enum (
    'pending',
    'processing',
    'completed',
    'failed'
);

create type public.activity_action as enum (
    'task_created',
    'task_assigned',
    'task_accepted',
    'task_started',
    'task_completed',
    'task_cancelled',
    'geofence_entered',
    'geofence_exited',
    'tracking_started',
    'tracking_paused',
    'tracking_resumed',
    'tracking_completed',
    'recording_started',
    'recording_interrupted',
    'recording_resumed',
    'recording_completed',
    'transcription_started',
    'transcription_completed',
    'transcription_failed',
    'image_uploaded'
);

create type public.notification_type as enum (
    'task_assigned',
    'task_updated',
    'task_completed',
    'geofence_event',
    'recording_event',
    'transcription_ready',
    'system'
);


-- ============================================================
-- 2. PROFILES
-- ============================================================

create table public.profiles (
    id uuid primary key references auth.users(id) on delete restrict,

    full_name text not null,
    email text not null,
    phone text,
    employee_code text unique,
    role public.user_role not null,

    avatar_url text,

    is_active boolean not null default true,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);


-- ============================================================
-- 3. TASKS
-- ============================================================

create table public.tasks (
    id uuid primary key default gen_random_uuid(),

    title text not null,
    description text,

    status public.task_status not null default 'pending',
    priority public.task_priority not null default 'medium',

    created_by uuid not null
        references public.profiles(id) on delete restrict,

    due_date timestamptz,
    completed_at timestamptz,

    is_deleted boolean not null default false,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),

    constraint tasks_completed_at_check
        check (
            completed_at is null
            or completed_at >= created_at
        )
);


-- ============================================================
-- 4. TASK ASSIGNMENTS
-- ============================================================

create table public.task_assignments (
    id uuid primary key default gen_random_uuid(),

    task_id uuid not null
        references public.tasks(id) on delete restrict,

    employee_id uuid not null
        references public.profiles(id) on delete restrict,

    assigned_by uuid not null
        references public.profiles(id) on delete restrict,

    assigned_at timestamptz not null default now(),

    status public.assignment_status not null default 'assigned',

    constraint unique_task_employee
        unique (task_id, employee_id)
);


-- ============================================================
-- 5. GEOFENCES
-- ============================================================

create table public.geofences (
    id uuid primary key default gen_random_uuid(),

    task_id uuid not null unique
        references public.tasks(id) on delete restrict,

    latitude double precision not null,
    longitude double precision not null,

    radius_meters integer not null,

    is_active boolean not null default true,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),

    constraint geofence_latitude_check
        check (latitude >= -90 and latitude <= 90),

    constraint geofence_longitude_check
        check (longitude >= -180 and longitude <= 180),

    constraint geofence_radius_check
        check (radius_meters > 0)
);


-- ============================================================
-- 6. LOCATION SESSIONS
-- ============================================================

create table public.location_sessions (
    id uuid primary key default gen_random_uuid(),

    task_id uuid not null
        references public.tasks(id) on delete restrict,

    employee_id uuid not null
        references public.profiles(id) on delete restrict,

    started_at timestamptz not null,
    ended_at timestamptz,

    status public.tracking_status not null,

    created_at timestamptz not null default now(),

    constraint location_session_time_check
        check (
            ended_at is null
            or ended_at >= started_at
        )
);


-- ============================================================
-- 7. LOCATION POINTS
-- ============================================================

create table public.location_points (
    id bigint generated always as identity primary key,

    session_id uuid not null
        references public.location_sessions(id) on delete cascade,

    latitude double precision not null,
    longitude double precision not null,

    accuracy real,
    altitude real,
    speed real,

    recorded_at timestamptz not null,

    constraint location_point_latitude_check
        check (latitude >= -90 and latitude <= 90),

    constraint location_point_longitude_check
        check (longitude >= -180 and longitude <= 180),

    constraint location_point_accuracy_check
        check (accuracy is null or accuracy >= 0),

    constraint location_point_speed_check
        check (speed is null or speed >= 0)
);


-- ============================================================
-- 8. RECORDING SESSIONS
-- ============================================================

create table public.recording_sessions (
    id uuid primary key default gen_random_uuid(),

    task_id uuid not null
        references public.tasks(id) on delete restrict,

    employee_id uuid not null
        references public.profiles(id) on delete restrict,

    started_at timestamptz not null,
    ended_at timestamptz,

    status public.recording_status not null,

    storage_path text,

    duration_seconds integer,

    created_at timestamptz not null default now(),

    constraint recording_session_time_check
        check (
            ended_at is null
            or ended_at >= started_at
        ),

    constraint recording_duration_check
        check (
            duration_seconds is null
            or duration_seconds >= 0
        )
);


-- ============================================================
-- 9. TRANSCRIPTS
-- ============================================================

create table public.transcripts (
    id uuid primary key default gen_random_uuid(),

    recording_session_id uuid not null
        references public.recording_sessions(id) on delete restrict,

    text text not null,

    language text,

    status public.transcription_status not null default 'pending',

    started_at timestamptz,
    completed_at timestamptz,

    created_at timestamptz not null default now(),

    constraint transcript_time_check
        check (
            completed_at is null
            or started_at is null
            or completed_at >= started_at
        )
);


-- ============================================================
-- 10. TASK IMAGES
-- ============================================================

create table public.task_images (
    id uuid primary key default gen_random_uuid(),

    task_id uuid not null
        references public.tasks(id) on delete restrict,

    employee_id uuid not null
        references public.profiles(id) on delete restrict,

    storage_path text not null,

    latitude double precision not null,
    longitude double precision not null,

    captured_at timestamptz not null,

    created_at timestamptz not null default now(),

    constraint task_image_latitude_check
        check (latitude >= -90 and latitude <= 90),

    constraint task_image_longitude_check
        check (longitude >= -180 and longitude <= 180)
);


-- ============================================================
-- 11. ACTIVITY LOGS
-- ============================================================

create table public.activity_logs (
    id uuid primary key default gen_random_uuid(),

    user_id uuid not null
        references public.profiles(id) on delete restrict,

    task_id uuid
        references public.tasks(id) on delete restrict,

    action public.activity_action not null,

    metadata jsonb,

    created_at timestamptz not null default now()
);


-- ============================================================
-- 12. NOTIFICATIONS
-- ============================================================

create table public.notifications (
    id uuid primary key default gen_random_uuid(),

    user_id uuid not null
        references public.profiles(id) on delete restrict,

    title text not null,
    message text not null,

    type public.notification_type not null,

    is_read boolean not null default false,

    created_at timestamptz not null default now()
);


-- ============================================================
-- 13. INDEXES
-- ============================================================

create index idx_profiles_role
    on public.profiles(role);

create index idx_profiles_employee_code
    on public.profiles(employee_code);

create index idx_tasks_created_by
    on public.tasks(created_by);

create index idx_tasks_status
    on public.tasks(status);

create index idx_tasks_due_date
    on public.tasks(due_date);

create index idx_task_assignments_task_id
    on public.task_assignments(task_id);

create index idx_task_assignments_employee_id
    on public.task_assignments(employee_id);

create index idx_location_sessions_task_id
    on public.location_sessions(task_id);

create index idx_location_sessions_employee_id
    on public.location_sessions(employee_id);

create index idx_location_sessions_started_at
    on public.location_sessions(started_at);

create index idx_location_points_session_recorded
    on public.location_points(session_id, recorded_at);

create index idx_recording_sessions_task_id
    on public.recording_sessions(task_id);

create index idx_recording_sessions_employee_id
    on public.recording_sessions(employee_id);

create index idx_recording_sessions_started_at
    on public.recording_sessions(started_at);

create index idx_transcripts_recording_session_id
    on public.transcripts(recording_session_id);

create index idx_task_images_task_id
    on public.task_images(task_id);

create index idx_task_images_employee_id
    on public.task_images(employee_id);

create index idx_task_images_captured_at
    on public.task_images(captured_at);

create index idx_activity_logs_task_id
    on public.activity_logs(task_id);

create index idx_activity_logs_user_id
    on public.activity_logs(user_id);

create index idx_activity_logs_created_at
    on public.activity_logs(created_at);

create index idx_notifications_user_id
    on public.notifications(user_id);

create index idx_notifications_unread
    on public.notifications(user_id, is_read);


-- ============================================================
-- 14. UPDATED_AT FUNCTION
-- ============================================================

create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;


-- ============================================================
-- 15. UPDATED_AT TRIGGERS
-- ============================================================

create trigger set_profiles_updated_at
before update on public.profiles
for each row
execute function public.set_updated_at();

create trigger set_tasks_updated_at
before update on public.tasks
for each row
execute function public.set_updated_at();

create trigger set_geofences_updated_at
before update on public.geofences
for each row
execute function public.set_updated_at();