package com.rahul.fieldflow.features.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.domain.model.TaskReportContext
import com.rahul.fieldflow.features.tasks.components.TaskStatusBadge
import com.rahul.fieldflow.features.team.model.toUiStatus
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.WarningOrange
import java.time.format.DateTimeFormatter

@Composable
fun HomeReportCard(
    reportContext: TaskReportContext,
    onViewReport: () -> Unit
) {
    val task = reportContext.task
    val report = reportContext.aiReport

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TaskStatusBadge(task.status.toUiStatus())
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${task.assignedEmployee?.fullName ?: "Unknown"} • ${task.createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (report != null) {
                        Surface(
                            color = WarningOrange.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = WarningOrange
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI Report",
                                    color = WarningOrange,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Processing...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    if (reportContext.sessions.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(12.dp))
                        val duration = reportContext.sessions.sumOf { it.durationSeconds ?: 0 }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "%02d:%02d".format(duration / 60, duration % 60),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = "View Report →",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onViewReport() }
                )
            }
        }
    }
}
