package com.rahul.fieldflow.features.team.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.features.profile.components.ProfileAvatar
import com.rahul.fieldflow.features.tasks.components.TaskStatusBadge
import com.rahul.fieldflow.features.team.model.EmployeeTeamUiModel
import com.rahul.fieldflow.features.team.model.toUiStatus
import com.rahul.fieldflow.core.utils.DateUtils
import com.rahul.fieldflow.ui.theme.*

@Composable
fun TeamMemberCard(
    member: EmployeeTeamUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileAvatar(
                    initials = member.profile.fullName.take(1) + (member.profile.fullName.split(" ").getOrNull(1)?.take(1) ?: ""),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.profile.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = member.profile.role.name.lowercase().replaceFirstChar { it.uppercase() } + " Employee",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                StatusIndicator(status = member.status)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contact Info
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ContactInfoRow(icon = Icons.Default.Email, text = member.profile.email)
                ContactInfoRow(icon = Icons.Default.Phone, text = member.profile.phone ?: "No phone number")
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = DateUtils.formatMemberSince(member.profile.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PerformanceStat(
                    label = "Total Tasks", 
                    value = "${member.totalTasks}", 
                    modifier = Modifier.weight(1f)
                )
                PerformanceStat(
                    label = "Completed", 
                    value = "${member.completedTasks}", 
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.End
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Current Task
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Current Task",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (member.currentTask != null) {
                            Text(
                                text = member.currentTask.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TaskStatusBadge(status = member.currentTask.status.toUiStatus())
                        } else {
                            Text(
                                text = "No active tasks",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                    
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatusIndicator(status: String) {
    val isActive = status.equals("Active", ignoreCase = true)
    val color = if (isActive) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = status,
            color = if (MaterialTheme.colorScheme.surface == Color(0xFF161C2C) && isActive) color.copy(alpha = 0.9f) else color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun PerformanceStat(
    label: String, 
    value: String, 
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = alignment) {
        Text(
            text = label, 
            style = MaterialTheme.typography.labelSmall, 
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value, 
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold, 
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
