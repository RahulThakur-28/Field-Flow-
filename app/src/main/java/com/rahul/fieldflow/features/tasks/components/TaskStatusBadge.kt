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
    val color = when (status) {
        TaskStatus.PENDING -> Color(0xFFFFA000)
        TaskStatus.IN_PROGRESS -> PrimaryBlue
        TaskStatus.COMPLETED -> Color(0xFF4CAF50)
        TaskStatus.OVERDUE -> Color(0xFFF44336)
        TaskStatus.CANCELLED -> Color(0xFF9E9E9E)
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontWeight = FontWeight.Bold
        )
    }
}
