# Owner Reports Module - Complete Fix + Premium UI + Real Backend

This plan focuses on auditing and fixing the owner report status flow, ensuring reliable backend synchronization, and polishing the UI for a premium experience.

## User Review Required
> [!IMPORTANT]
> The current filtering logic for "Needs Review" in the ViewModel is too broad. I will narrow it down to strictly check for the `needs_review` status from the backend.
> [!IMPORTANT]
> I will change the Owner Reports query to filter by `workspace_id` instead of `tasks.created_by` to ensure owners see reports for their entire team.

## Proposed Changes

### Domain & Data Models

#### [MODIFY] [TaskReport.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/domain/model/TaskReport.kt)
- Update `KeyFinding` and `ActionItem` to be `@Serializable`.

#### [MODIFY] [ReportDataSource.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/data/reports/ReportDataSource.kt)
- Update `TaskReportWithDetailsDto` and `TaskReportDto` to use serializable lists for findings and action items instead of `JsonArray`. This fixes the serialization error reported.
- Modify `getOwnerReports` to filter by `workspace_id`.
- Enhance `updateReportStatus` with verification.
- Add diagnostic logging.

#### [MODIFY] [ReportRepositoryImpl.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/data/reports/ReportRepositoryImpl.kt)
- Simplify mapping logic to use the new serializable models.

---

### Features: Reports

#### [MODIFY] [ReportUiModels.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/reports/model/ReportUiModels.kt)
- Ensure `ReportStatus` exactly matches backend strings.
- Refine `toUiModel` mapping.

#### [MODIFY] [OwnerReportsViewModel.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/reports/owner/viewmodel/OwnerReportsViewModel.kt)
- Implement pull-to-refresh (`onRefresh`).
- Correct filter logic for "Needs Review" and "Reviewed".
- Add comprehensive logging.

#### [MODIFY] [OwnerReportsScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/reports/owner/screen/OwnerReportsScreen.kt)
- Add `PullRefreshIndicator`.
- Use premium colors for filters (Red for Needs Review, Green for Reviewed, Blue for All).
- Improve header and counts display.

#### [MODIFY] [ReportCard.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/reports/components/ReportCard.kt)
- Redesign for a premium look.
- Support dark theme surfaces.

#### [MODIFY] [TaskReportScreen.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/reports/screen/TaskReportScreen.kt)
- UI polish for better readability and hierarchy.
- Integrate a real audio player for recordings.

---

### UI Components & Theme

#### [MODIFY] [AiReportSection.kt](file:///C:/Users/RAHUL THAKUR/AndroidStudioProjects/FieldFlow/app/src/main/java/com/rahul/fieldflow/features/reports/components/AiReportSection.kt)
- Polish AI report cards and action item priority colors.

---

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure compilation.

### Manual Verification
1. **Load Reports**: Verify reports load from Supabase for the entire workspace.
2. **Filter Test**: Select "Needs Review" and verify only `needs_review` reports appear.
3. **Mark Reviewed**:
   - Tap "Mark Reviewed".
   - Verify loading state.
   - Verify backend update using `reportId`.
   - Verify automatic reload and filter update.
   - Verify counts update correctly.
4. **Search + Filter**: Combine search queries with different filters.
5. **Pull to Refresh**: Verify latest data is fetched.
6. **Task Report Detail**:
   - Verify real AI content.
   - Test audio player with a real URL.
   - Verify transcript visibility.
7. **Theme Test**: Switch between Light and Dark themes on all screens.
