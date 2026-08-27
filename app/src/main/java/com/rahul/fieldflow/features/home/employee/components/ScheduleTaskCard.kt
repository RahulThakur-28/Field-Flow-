package com.rahul.fieldflow.features.home.employee.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.features.home.components.StatusBadge
import com.rahul.fieldflow.features.home.model.ScheduleTaskUiModel
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary

@Composable
fun ScheduleTaskCard(task: ScheduleTaskUiModel, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8F9FB)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = task.time.take(2), // Just the hour as a visual hint
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title, 
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold, 
                    color = TextDark,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "${task.time} • ${task.location}", 
                    style = MaterialTheme.typography.labelSmall, 
                    color = TextSecondary
                )
            }
            
            StatusBadge(type = task.status)
        }
    }
}
