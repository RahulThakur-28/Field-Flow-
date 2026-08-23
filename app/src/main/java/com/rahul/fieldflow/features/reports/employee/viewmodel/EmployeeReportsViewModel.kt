package com.rahul.fieldflow.features.reports.employee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.reports.model.ReportStatus
import com.rahul.fieldflow.features.reports.model.mockReports
import com.rahul.fieldflow.features.reports.employee.state.EmployeeReportDetailsUiState
import com.rahul.fieldflow.features.reports.employee.state.EmployeeReportsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeReportsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeReportsUiState())
    val uiState: StateFlow<EmployeeReportsUiState> = _uiState.asStateFlow()

    private val _detailsState = MutableStateFlow(EmployeeReportDetailsUiState())
    val detailsState: StateFlow<EmployeeReportDetailsUiState> = _detailsState.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate network
            
            // In a real app, filter by current employee ID
            val reports = mockReports.filter { it.employee.name == "Rahul Thakur" } 
            
            _uiState.update { 
                it.copy(
                    reports = reports,
                    filteredReports = reports,
                    isLoading = false
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
}
