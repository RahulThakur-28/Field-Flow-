# Implementation Plan: Owner Task Module

Implement a complete Owner Task Module including task list, creation, details, editing, and live tracking with mock data.

## User Review Required

> [!IMPORTANT]
> This module uses mock data for all operations (Create, Edit, List, Tracking). Real backend and GPS integration are out of scope for this task.

## Proposed Changes

### [Component Name] Tasks Feature

#### [NEW] [TaskUiModels.kt](file:///C:/Users/RAHUL%20THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/tasks/model/TaskUiModels.kt)
- Define `TaskUiModel`, `TaskStatus`, `TaskPriority`, `TaskChecklistItemUiModel`, and `TaskTimelineItemUiModel`.

#### [NEW] Shared Task Components
- `TaskCard.kt`, `TaskStatusBadge.kt`, `TaskPriorityBadge.kt`, `TaskSearchBar.kt`, `TaskFilterTabs.kt`, `TaskLocationCard.kt`, `TaskScheduleCard.kt`, `TaskChecklistCard.kt`, `TaskProgressTimeline.kt`, `TaskActionButtons.kt` in [features/tasks/components/](file:///C:/Users/RAHUL%20THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/tasks/components/).

#### [NEW] Owner Tasks Module
- **State**: `OwnerTasksUiState.kt`, `CreateTaskUiState.kt`, `OwnerTaskDetailsUiState.kt`, `EditTaskUiState.kt`.
- **ViewModels**: `OwnerTasksViewModel.kt`, `CreateTaskViewModel.kt`, `OwnerTaskDetailsViewModel.kt`, `EditTaskViewModel.kt`.
- **Screens**: `OwnerTasksScreen.kt`, `CreateTaskScreen.kt`, `OwnerTaskDetailsScreen.kt`, `EditTaskScreen.kt`, `OwnerLiveTrackingScreen.kt`.
- **Components**: `EmployeeSelector.kt`, `TaskForm.kt`.

#### [MODIFY] Navigation
- [AppRoutes.kt](file:///C:/Users/RAHUL%20THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/core/navigation/AppRoutes.kt): Update `Tasks` to support params if needed, and add `CreateTask`, `TaskDetails`, `EditTask`, `LiveTracking`.
- [AppNavGraph.kt](file:///C:/Users/RAHUL%20THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/core/navigation/AppNavGraph.kt): Add composables for all new screens.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify no compilation errors.

### Manual Verification
- Verify Compose Previews for all new screens.
- Navigate from Home -> Tasks -> Create Task.
- Navigate from Task List -> Task Details -> Edit Task / Track Live.
- Verify search and filter logic on the Task List.
- Verify form validation in Create/Edit Task.
