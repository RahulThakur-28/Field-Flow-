package com.rahul.fieldflow.features.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.rahul.fieldflow.features.tasks.model.TaskPriority
import com.rahul.fieldflow.ui.theme.*

@Composable
fun TaskPriorityBadge(priority: TaskPriority) {
    val config = getPriorityConfig(priority)
    Surface(
        color = config.color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (MaterialTheme.colorScheme.surface == Color(0xFF161C2C)) config.color.copy(alpha = 0.9f) else config.color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = priority.label,
                style = MaterialTheme.typography.labelSmall,
                color = if (MaterialTheme.colorScheme.surface == Color(0xFF161C2C)) config.color.copy(alpha = 0.9f) else config.color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class PriorityUiConfig(val color: Color)

@Composable
fun getPriorityConfig(priority: TaskPriority): PriorityUiConfig {
    return when (priority) {
        TaskPriority.LOW -> PriorityUiConfig(SuccessGreen)
        TaskPriority.MEDIUM -> PriorityUiConfig(WarningOrange)
        TaskPriority.HIGH -> PriorityUiConfig(Color(0xFFEF6C00))
        TaskPriority.URGENT -> PriorityUiConfig(ErrorRed)
    }
}
