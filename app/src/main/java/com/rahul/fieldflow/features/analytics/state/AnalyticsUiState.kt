package com.rahul.fieldflow.features.analytics.state

import com.rahul.fieldflow.features.analytics.model.*

data class AnalyticsUiState(
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.MONTH,
    val summaryStats: List<AnalyticsStat> = emptyList(),
    val tasksOverTime: List<BarChartData> = emptyList(),
    val completionTrend: List<LineChartData> = emptyList(),
    val taskBreakdown: List<DonutChartData> = emptyList(),
    val topPerformers: List<TopPerformer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
