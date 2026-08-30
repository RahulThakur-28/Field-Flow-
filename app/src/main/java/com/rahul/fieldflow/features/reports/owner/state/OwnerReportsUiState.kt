package com.rahul.fieldflow.features.reports.owner.state

import com.rahul.fieldflow.features.reports.model.Report

enum class ReportFilter(val label: String) {
    ALL("All"),
    NEEDS_REVIEW("Needs Review"),
    REVIEWED("Reviewed")
}

data class OwnerReportsUiState(
    val reports: List<Report> = emptyList(),
    val filteredReports: List<Report> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: ReportFilter = ReportFilter.ALL,
    val isLoading: Boolean = false,
    val needsReviewCount: Int = 0,
    val reviewedCount: Int = 0,
    val error: String? = null
)

data class OwnerReportDetailsUiState(
    val report: Report? = null,
    val isLoading: Boolean = false,
    val isReviewing: Boolean = false
)
