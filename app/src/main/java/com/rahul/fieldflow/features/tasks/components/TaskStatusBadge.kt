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
import com.rahul.fieldflow.ui.theme.*

@Composable
fun TaskStatusBadge(status: TaskStatus) {
    val config = getStatusConfig(status)
    Surface(
        color = config.color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.label,
            color = if (MaterialTheme.colorScheme.surface == Color(0xFF161C2C)) config.color.copy(alpha = 0.9f) else config.color,
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
        TaskStatus.PENDING -> StatusUiConfig(WarningOrange)
        TaskStatus.ASSIGNED -> StatusUiConfig(InfoBlue)
        TaskStatus.IN_PROGRESS -> StatusUiConfig(PrimaryBlue)
        TaskStatus.COMPLETED -> StatusUiConfig(SuccessGreen)
        TaskStatus.OVERDUE -> StatusUiConfig(ErrorRed)
        TaskStatus.CANCELLED -> StatusUiConfig(MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
