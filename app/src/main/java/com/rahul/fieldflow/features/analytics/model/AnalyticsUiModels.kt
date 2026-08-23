package com.rahul.fieldflow.features.analytics.model

import androidx.compose.ui.graphics.Color

enum class AnalyticsPeriod(val label: String) {
    WEEK("Week"),
    MONTH("Month"),
    QUARTER("Quarter"),
    YEAR("Year")
}

data class AnalyticsStat(
    val value: String,
    val label: String,
    val comparisonText: String,
    val isPositive: Boolean
)

data class BarChartData(
    val label: String,
    val value: Float
)

data class LineChartData(
    val label: String,
    val value: Float
)

data class DonutChartData(
    val label: String,
    val value: Int,
    val color: Color
)

data class TopPerformer(
    val rank: Int,
    val name: String,
    val taskCount: Int,
    val performance: Int
)
