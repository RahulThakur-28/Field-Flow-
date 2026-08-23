package com.rahul.fieldflow.features.analytics.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.analytics.model.*
import com.rahul.fieldflow.features.analytics.state.AnalyticsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnalyticsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate network delay

            val stats = listOf(
                AnalyticsStat("86", "Tasks Completed", "↑ +12% vs last month", true),
                AnalyticsStat("86%", "Completion Rate", "↑ +3% vs last month", true),
                AnalyticsStat("91%", "On-Time Rate", "↓ -2% vs last month", false),
                AnalyticsStat("2.4h", "Avg Duration", "↑ -0.3h vs last month", true)
            )

            val barData = listOf(
                BarChartData("Jan", 45f),
                BarChartData("Feb", 52f),
                BarChartData("Mar", 48f),
                BarChartData("Apr", 61f),
                BarChartData("May", 55f),
                BarChartData("Jun", 67f),
                BarChartData("Jul", 72f),
                BarChartData("Aug", 86f)
            )

            val lineData = listOf(
                LineChartData("Mar", 78f),
                LineChartData("Apr", 82f),
                LineChartData("May", 80f),
                LineChartData("Jun", 85f),
                LineChartData("Jul", 84f),
                LineChartData("Aug", 86f)
            )

            val breakdown = listOf(
                DonutChartData("Completed", 86, Color(0xFF4CAF50)),
                DonutChartData("In Progress", 12, Color(0xFF2196F3)),
                DonutChartData("Overdue", 3, Color(0xFFF44336)),
                DonutChartData("Cancelled", 2, Color(0xFF9E9E9E))
            )

            val performers = listOf(
                TopPerformer(1, "Priya Sharma", 38, 97),
                TopPerformer(2, "Rahul Thakur", 24, 92),
                TopPerformer(3, "Kavya Nair", 21, 89)
            )

            _uiState.update {
                it.copy(
                    summaryStats = stats,
                    tasksOverTime = barData,
                    completionTrend = lineData,
                    taskBreakdown = breakdown,
                    topPerformers = performers,
                    isLoading = false
                )
            }
        }
    }

    fun onPeriodSelected(period: AnalyticsPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
        loadAnalyticsData() // Reload data for the new period
    }
}
