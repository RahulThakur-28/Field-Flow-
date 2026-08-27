package com.rahul.fieldflow.features.home.owner.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
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
import com.rahul.fieldflow.features.home.components.StatusBadge
import com.rahul.fieldflow.features.home.model.FieldVisitUiModel
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary

@Composable
fun LiveVisitCard(visit: FieldVisitUiModel, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = visit.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = visit.location, fontSize = 11.sp, color = TextSecondary)
                    }
                }
                StatusBadge(type = visit.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileAvatar(
                    initials = visit.employeeInitials,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = visit.employeeName, 
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold, 
                        color = TextDark
                    )
                    Text(
                        text = "Distance: ${visit.distance}", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = TextSecondary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${visit.completedTasks}/${visit.totalTasks} Tasks",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(64.dp)) {
                        LinearProgressIndicator(
                            progress = { visit.completedTasks.toFloat() / visit.totalTasks },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CircleShape),
                            color = PrimaryBlue,
                            trackColor = PrimaryBlue.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}
