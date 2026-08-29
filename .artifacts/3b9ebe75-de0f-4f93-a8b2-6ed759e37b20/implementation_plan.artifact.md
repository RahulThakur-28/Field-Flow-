# Redesign and Backend Integration for Owner → Team Section

This plan covers the redesign of the Owner Team list and Employee Details screens, integrating them with real Supabase data and removing all mock/fake statistics.

## User Review Required

> [!IMPORTANT]
> The redesign will remove performance scores and avg on-time metrics as they were mock-based. The focus will shift to real task counts and current activity.

> [!NOTE]
> Navigation to `TaskReportScreen` will be verified to use `taskId` correctly to avoid previous bugs.

## Proposed Changes

### Domain & Data Layer

#### [MODIFY] [TaskDataSource.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/data/tasks/TaskDataSource.kt)
- Add `getTaskSummaryForEmployees(employeeIds: List<String>)` to fetch total and completed task counts for a list of employees.
- Add `getActiveAssignmentsForEmployees(employeeIds: List<String>)` to fetch current active tasks for employees.

#### [MODIFY] [TaskRepository.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/domain/repository/TaskRepository.kt)
- Add methods for employee task summaries and active assignments.

#### [MODIFY] [TaskRepositoryImpl.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/data/tasks/TaskRepositoryImpl.kt)
- Implement new repository methods.

#### [NEW] [GetTeamWithStatsUseCase.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/domain/usecase/team/GetTeamWithStatsUseCase.kt)
- Orchestrates fetching workspace employees, their task counts, and current assignments.

#### [NEW] [GetEmployeeDetailsUseCase.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/domain/usecase/team/GetEmployeeDetailsUseCase.kt)
- Fetches full profile, current task, and past tasks (with report status) for a specific employee.

---

### UI Layer

#### [MODIFY] [TeamUiModels.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/team/model/TeamUiModels.kt)
- Redefine `EmployeeTeamUiModel` to match the new design requirements.
- Remove mock data.

#### [MODIFY] [TeamViewModel.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/team/viewmodel/TeamViewModel.kt)
- Switch to using `GetTeamWithStatsUseCase` and `GetEmployeeDetailsUseCase`.
- Remove all mock data references.

#### [MODIFY] [OwnerTeamScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/team/screen/OwnerTeamScreen.kt)
- Redesign the top bar, search, and member list.
- Remove old performance/summary cards.

#### [MODIFY] [TeamMemberCard.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/team/components/TeamMemberCard.kt)
- Update to new card design including avatar, role, status, email, phone, and task counts.

#### [MODIFY] [EmployeeDetailsScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/team/screen/EmployeeDetailsScreen.kt)
- Redesign according to the reference: Profile Card, Current Task section, and Past Tasks & Reports section.

---

### Cleanup

#### [DELETE] [EmployeePerformanceCard.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/team/components/EmployeePerformanceCard.kt)
- No longer needed as performance stats are removed.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure no compilation errors.

### Manual Verification
1. Open Owner Team screen.
2. Verify employee list loads real data from Supabase.
3. Search for an employee and verify filtering.
4. Tap an employee card to open Details.
5. Verify Profile information is correct.
6. Verify "Current Task" shows real assignment if one exists.
7. Verify "Past Tasks & Reports" shows list of completed tasks.
8. Tap "View Report" on a past task and verify `TaskReportScreen` opens with the correct `taskId`.
9. Ensure no mock data (e.g., "Rahul Thakur" or "+91 XXXXX XXXXX") is shown unless it's in the actual DB.
