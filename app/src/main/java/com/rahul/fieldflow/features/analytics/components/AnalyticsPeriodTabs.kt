package com.rahul.fieldflow.features.analytics.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.features.analytics.model.AnalyticsPeriod
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@Composable
fun AnalyticsPeriodTabs(
    selectedPeriod: AnalyticsPeriod,
    onPeriodSelected: (AnalyticsPeriod) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedPeriod.ordinal,
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        divider = {},
        indicator = {}
    ) {
        AnalyticsPeriod.entries.forEach { period ->
            Tab(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                text = {
                    Text(
                        text = period.label,
                        color = if (selectedPeriod == period) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (selectedPeriod == period) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}
