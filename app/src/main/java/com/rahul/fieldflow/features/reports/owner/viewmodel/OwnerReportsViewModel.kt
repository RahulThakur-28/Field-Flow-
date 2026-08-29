package com.rahul.fieldflow.features.reports.owner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.usecase.reports.GetOwnerReportsUseCase
import com.rahul.fieldflow.domain.usecase.reports.MarkReportAsReviewedUseCase
import com.rahul.fieldflow.features.reports.model.*
import com.rahul.fieldflow.features.reports.owner.state.OwnerReportDetailsUiState
import com.rahul.fieldflow.features.reports.owner.state.OwnerReportsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerReportsViewModel @Inject constructor(
    private val getReportsUseCase: GetOwnerReportsUseCase,
    private val markReportAsReviewedUseCase: MarkReportAsReviewedUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(OwnerReportsUiState())
    val uiState: StateFlow<OwnerReportsUiState> = _uiState.asStateFlow()

    private val _detailsState = MutableStateFlow(OwnerReportDetailsUiState())
    val detailsState: StateFlow<OwnerReportDetailsUiState> = _detailsState.asStateFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            android.util.Log.d("REPORT_DEBUG", "OWNER_REPORTS_LOAD_START")
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getReportsUseCase()
                .onSuccess { contexts ->
                    android.util.Log.d("REPORT_DEBUG", "OWNER_REPORTS_LOAD_SUCCESS count=${contexts.size}")
                    val reports = contexts.map { 
                        android.util.Log.d("REPORT_DEBUG", "OWNER_REPORTS_MAPPING task_id=${it.task.id}")
                        it.toUiModel() 
                    }
                    _uiState.update { 
                        it.copy(
                            reports = reports,
                            isLoading = false,
                            needsReviewCount = reports.count { r -> r.status != ReportStatus.REVIEWED && r.status != ReportStatus.FAILED }
                        )
                    }
                    applyFilters()
                }
                .onFailure { error ->
                    android.util.Log.e("REPORT_DEBUG", "OWNER_REPORTS_LOAD_FAILED", error)
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = error.message ?: "Failed to load reports"
                        ) 
                    }
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
                    1 -> report.status != ReportStatus.REVIEWED && report.status != ReportStatus.FAILED
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
            // Now we use TaskReportScreen for details, but we can still find the report in our current list
            val report = _uiState.value.reports.find { it.id == reportId }
            _detailsState.update { it.copy(report = report, isLoading = false) }
        }
    }

    fun markAsReviewed(reportId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            android.util.Log.d("REPORT_DEBUG", "OWNER_REPORT_REVIEW_START reportId=$reportId")
            _detailsState.update { it.copy(isReviewing = true) }
            
            markReportAsReviewedUseCase(reportId)
                .onSuccess {
                    android.util.Log.d("REPORT_DEBUG", "OWNER_REPORT_REVIEW_SUCCESS")
                    _detailsState.update { it.copy(isReviewing = false) }
                    onSuccess()
                    loadReports() // Refresh list to update status and counts
                }
                .onFailure { error ->
                    android.util.Log.e("REPORT_DEBUG", "OWNER_REPORT_REVIEW_FAILED", error)
                    _detailsState.update { it.copy(isReviewing = false) }
                    // In a real app, show an error message
                }
        }
    }
}
