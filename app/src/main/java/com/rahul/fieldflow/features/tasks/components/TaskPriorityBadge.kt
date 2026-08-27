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

@Composable
fun TaskPriorityBadge(priority: TaskPriority) {
    val config = getPriorityConfig(priority)
    Surface(
        color = config.color.copy(alpha = 0.1f),
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
                    .background(config.color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = priority.label,
                style = MaterialTheme.typography.labelSmall,
                color = config.color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class PriorityUiConfig(val color: Color)

@Composable
fun getPriorityConfig(priority: TaskPriority): PriorityUiConfig {
    return when (priority) {
        TaskPriority.LOW -> PriorityUiConfig(Color(0xFF2E7D32))
        TaskPriority.MEDIUM -> PriorityUiConfig(Color(0xFFF9A825))
        TaskPriority.HIGH -> PriorityUiConfig(Color(0xFFEF6C00))
        TaskPriority.URGENT -> PriorityUiConfig(Color(0xFFC62828))
    }
}
