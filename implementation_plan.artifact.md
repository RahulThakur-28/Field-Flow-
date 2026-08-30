# Employee Requests - Real Data Fix + Premium UI

This plan focuses on fixing the issue where employee details are not displayed in the "Owner -> Employee Requests" screen and polishing the UI for a premium experience.

## User Review Required
> [!IMPORTANT]
> I will update the Supabase query to use explicit aliases for joined tables. This ensures that the application can correctly parse the JSON response from Supabase and display real employee data (Name, Email, Phone).

## Proposed Changes

### Data Layer

#### [MODIFY] [JoinRequestDataSource.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/data/requests/JoinRequestDataSource.kt)
- Update `getPendingRequests` to use `profiles:profiles!join_requests_employee_id_fkey(...)` to ensure the key in JSON matches the DTO.
- Update `getMyRequests` to use `workspaces:workspaces(...)` for consistency.

---

### Features: Team / Employee Requests

#### [MODIFY] [EmployeeRequestsScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/team/screen/EmployeeRequestsScreen.kt)
- Redesign `RequestCard` for a premium look (elevated card, better typography, clear hierarchy).
- Improve status indicators and semantic colors.
- Add better empty state and error messages.
- Ensure all real employee data (Email, Phone, Role) is displayed prominently.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure compilation.

### Manual Verification
1. **Load Requests**: Open "Owner -> Team -> Employee Requests" and verify real employee data is shown.
2. **Accept Flow**: Tap "Accept" and verify the request disappears and the employee appears in the "Team" list.
3. **Reject Flow**: Tap "Reject" and verify the request disappears.
4. **Empty State**: Verify the empty state when no pending requests exist.
5. **Dark Theme**: Verify readability in Dark Mode.
