package com.rahul.fieldflow.features.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.features.profile.components.ProfileAvatar
import com.rahul.fieldflow.features.tasks.model.Task
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter
import java.time.OffsetDateTime

@Composable
fun PremiumTaskCard(
    task: Task,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                TaskStatusBadge(task.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ID and Date
            val shortId = remember(task.id) { task.id.takeLast(4).uppercase() }
            val formattedDate = remember(task.scheduledDate) {
                task.scheduledDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            }
            Text(
                text = "ID: #$shortId · $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Employee and Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileAvatar(
                    initials = task.assignedTo.name.take(2).uppercase(),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${task.assignedTo.name} · ${task.location}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Priority, Deadline, and Checklist
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TaskPriorityBadge(task.priority)
                    Spacer(modifier = Modifier.width(12.dp))
                    DeadlineLabel(task.deadline ?: task.scheduledDate)
                }
                
                if (task.checklist.isNotEmpty()) {
                    val completed = task.checklist.count { it.isChecked }
                    Text(
                        text = "$completed/${task.checklist.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DeadlineLabel(deadline: OffsetDateTime) {
    val now = OffsetDateTime.now()
    val isToday = deadline.toLocalDate() == now.toLocalDate()
    val isTomorrow = deadline.toLocalDate() == now.plusDays(1).toLocalDate()
    
    val dateText = when {
        isToday -> "Today"
        isTomorrow -> "Tomorrow"
        else -> deadline.format(DateTimeFormatter.ofPattern("MMM dd"))
    }
    
    val timeText = deadline.format(DateTimeFormatter.ofPattern("hh:mm a"))
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.Schedule,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = TextSecondary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Due $timeText $dateText",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}
