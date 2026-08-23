package com.rahul.fieldflow.features.reports.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.reports.model.ReportStatus
import com.rahul.fieldflow.features.reports.model.mockReports
import com.rahul.fieldflow.features.reports.state.ReportDetailsUiState
import com.rahul.fieldflow.features.reports.state.ReportsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReportsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _detailsState = MutableStateFlow(ReportDetailsUiState())
    val detailsState: StateFlow<ReportDetailsUiState> = _detailsState.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate network
            val reports = mockReports
            _uiState.update { 
                it.copy(
                    reports = reports,
                    filteredReports = reports,
                    isLoading = false,
                    needsReviewCount = reports.count { r -> r.status == ReportStatus.NEEDS_REVIEW }
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(searchQuery = query)
        }
        applyFilters()
    }

    fun onTabSelected(index: Int) {
        _uiState.update { state ->
            state.copy(selectedTab = index)
        }
        applyFilters()
    }

    private fun applyFilters() {
        _uiState.update { state ->
            val filtered = state.reports.filter { report ->
                val matchesSearch = report.title.contains(state.searchQuery, ignoreCase = true) ||
                        report.employee.name.contains(state.searchQuery, ignoreCase = true) ||
                        report.location.contains(state.searchQuery, ignoreCase = true)
                
                val matchesTab = when (state.selectedTab) {
                    1 -> report.status == ReportStatus.NEEDS_REVIEW
                    2 -> report.status == ReportStatus.REVIEWED
                    else -> true
                }
                
                matchesSearch && matchesTab
            }
            state.copy(filteredReports = filtered)
        }
    }

    fun loadReportDetails(reportId: String) {
        viewModelScope.launch {
            _detailsState.update { it.copy(isLoading = true) }
            delay(800)
            val report = mockReports.find { it.id == reportId }
            _detailsState.update { it.copy(report = report, isLoading = false) }
        }
    }

    fun markAsReviewed(reportId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _detailsState.update { it.copy(isReviewing = true) }
            delay(1500)
            // In a real app, update repository
            _detailsState.update { it.copy(isReviewing = false) }
            onSuccess()
            loadReports() // Refresh list
        }
    }
}
