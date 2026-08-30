# Implementation Plan - Employee Module Bottom Navigation Fix

This plan addresses the crashes and navigation inconsistencies reported in the Employee module.

## Root Causes Identified

1.  **Redirection Loop**: `AppNavGraph.kt` had logic that forced a redirect to the Home screen if the user was on any other screen while authenticated. This caused a loop or a jump back to Home whenever a bottom navigation tab was clicked.
2.  **Incorrect ViewModel Initialization**: Several screens were using `viewModel()` instead of `hiltViewModel()` for Hilt-annotated ViewModels, causing `RuntimeException` because dependencies couldn't be injected.
3.  **Hardcoded Colors**: Some Employee screens had hardcoded light background colors, leading to visual inconsistencies in dark mode.

## Proposed Changes

### Navigation Core

#### [MODIFY] [AppNavGraph.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/core/navigation/AppNavGraph.kt)
- Refined the auto-redirection logic to ONLY trigger when the user is at an "Auth" destination (Splash, Login, etc.). This prevents the app from force-navigating back to Home when the user is already inside the application.

### Screen & ViewModel Fixes

#### [MODIFY] [EmployeeProfileScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/profile/employee/screen/EmployeeProfileScreen.kt)
- Changed `viewModel()` to `hiltViewModel()`.
- Replaced `BackgroundLight` with `MaterialTheme.colorScheme.background`.
- Updated `TopAppBar` colors to be theme-aware.

#### [MODIFY] [OwnerNotificationPreferencesScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/profile/owner/screen/OwnerNotificationPreferencesScreen.kt)
- Changed `viewModel()` to `hiltViewModel()`.

#### [MODIFY] [OwnerAnalyticsScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/analytics/screen/OwnerAnalyticsScreen.kt)
- Changed `viewModel()` to `hiltViewModel()`.

#### [MODIFY] [EmployeeHomeScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/home/employee/screen/EmployeeHomeScreen.kt)
- Removed hardcoded background and gradient colors.
- Updated text colors to use `onSurfaceVariant`.

#### [MODIFY] [EmployeeReportsScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/reports/employee/screen/EmployeeReportsScreen.kt)
- Replaced `BackgroundLight` with `MaterialTheme.colorScheme.background`.
- Updated `TopAppBar` colors.

#### [MODIFY] [AboutUsScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/profile/owner/screen/AboutUsScreen.kt)
- Cleaned up remaining hardcoded colors and updated to theme-aware components.

#### [MODIFY] [PrivacyPolicyScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/profile/owner/screen/PrivacyPolicyScreen.kt)
- Cleaned up remaining hardcoded colors and updated to theme-aware components.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify the build.

### Manual Verification
1.  **Tab Switching**: Log in as an Employee and click through Home -> Tasks -> Reports -> Profile. Verify no crashes and the screen stays on the selected tab.
2.  **Sub-navigation**: Navigate to Task Details from Home/Tasks and Task Report from Reports. Verify back navigation works.
3.  **Dark Theme**: Toggle Dark Theme from the Profile screen and verify all Employee tabs render correctly with dark backgrounds and readable text.
4.  **Fresh Login**: Verify the first-time redirection from Splash/Login to EmployeeHome works correctly.
