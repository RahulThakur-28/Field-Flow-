package com.rahul.fieldflow.features.reports.employee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.TaskReportContext
import com.rahul.fieldflow.domain.usecase.reports.GetEmployeeReportsUseCase
import com.rahul.fieldflow.features.reports.employee.state.EmployeeReportsUiState
import com.rahul.fieldflow.features.reports.model.*
import com.rahul.fieldflow.features.tasks.model.Employee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class EmployeeReportsViewModel @Inject constructor(
    private val getEmployeeReportsUseCase: GetEmployeeReportsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeReportsUiState())
    val uiState: StateFlow<EmployeeReportsUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getEmployeeReportsUseCase()
                .onSuccess { contexts ->
                    val reports = contexts.map { it.toUiModel() }
                    _uiState.update { 
                        it.copy(
                            reports = reports,
                            filteredReports = reports,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
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
        // Not used anymore as we navigate to TaskReportScreen
    }
}
