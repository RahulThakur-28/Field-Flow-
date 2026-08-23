package com.rahul.fieldflow.features.reports.employee.state

import com.rahul.fieldflow.features.reports.model.Report

data class EmployeeReportsUiState(
    val reports: List<Report> = emptyList(),
    val filteredReports: List<Report> = emptyList(),
    val searchQuery: String = "",
    val selectedTab: Int = 0, // 0: All, 1: Pending, 2: Reviewed
    val isLoading: Boolean = false
)

data class EmployeeReportDetailsUiState(
    val report: Report? = null,
    val isLoading: Boolean = false
)
