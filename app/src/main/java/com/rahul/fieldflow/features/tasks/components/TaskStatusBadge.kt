package com.rahul.fieldflow.features.tasks.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.features.tasks.model.TaskStatus
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@Composable
fun TaskStatusBadge(status: TaskStatus) {
    val config = getStatusConfig(status)
    Surface(
        color = config.color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.label,
            color = config.color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

data class StatusUiConfig(val color: Color)

@Composable
fun getStatusConfig(status: TaskStatus): StatusUiConfig {
    return when (status) {
        TaskStatus.PENDING -> StatusUiConfig(Color(0xFFFFA000))
        TaskStatus.IN_PROGRESS -> StatusUiConfig(PrimaryBlue)
        TaskStatus.COMPLETED -> StatusUiConfig(Color(0xFF4CAF50))
        TaskStatus.OVERDUE -> StatusUiConfig(Color(0xFFF44336))
        TaskStatus.CANCELLED -> StatusUiConfig(Color(0xFF9E9E9E))
    }
}
