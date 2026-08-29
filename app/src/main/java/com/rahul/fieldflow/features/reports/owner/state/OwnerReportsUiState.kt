package com.rahul.fieldflow.features.reports.owner.state

import com.rahul.fieldflow.features.reports.model.Report

data class OwnerReportsUiState(
    val reports: List<Report> = emptyList(),
    val filteredReports: List<Report> = emptyList(),
    val searchQuery: String = "",
    val selectedTab: Int = 0, // 0: All, 1: Needs Review, 2: Reviewed
    val isLoading: Boolean = false,
    val needsReviewCount: Int = 0,
    val error: String? = null
)

data class OwnerReportDetailsUiState(
    val report: Report? = null,
    val isLoading: Boolean = false,
    val isReviewing: Boolean = false
)
