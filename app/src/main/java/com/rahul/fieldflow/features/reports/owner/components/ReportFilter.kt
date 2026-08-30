package com.rahul.fieldflow.features.reports.owner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.features.reports.owner.state.ReportFilter
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@Composable
fun ReportFilterTabs(
    selectedFilter: ReportFilter,
    onFilterSelected: (ReportFilter) -> Unit,
    modifier: Modifier = Modifier,
    allCount: Int = 0,
    needsReviewCount: Int = 0,
    reviewedCount: Int = 0
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ReportFilter.entries.forEach { filter ->
            val isSelected = selectedFilter == filter
            val semanticColor = when (filter) {
                ReportFilter.ALL -> PrimaryBlue
                ReportFilter.NEEDS_REVIEW -> Color(0xFFF44336) // Red
                ReportFilter.REVIEWED -> Color(0xFF4CAF50) // Green
            }
            
            val count = when (filter) {
                ReportFilter.ALL -> allCount
                ReportFilter.NEEDS_REVIEW -> needsReviewCount
                ReportFilter.REVIEWED -> reviewedCount
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onFilterSelected(filter) },
                color = if (isSelected) semanticColor.copy(alpha = 0.12f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, semanticColor.copy(alpha = 0.2f)) else null
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) semanticColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = filter.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) semanticColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
