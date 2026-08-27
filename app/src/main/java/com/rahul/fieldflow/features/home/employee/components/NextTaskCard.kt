package com.rahul.fieldflow.features.home.employee.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
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
import com.rahul.fieldflow.features.home.components.StatusBadge
import com.rahul.fieldflow.features.home.model.NextTaskUiModel
import com.rahul.fieldflow.ui.theme.*

@Composable
fun NextTaskCard(task: NextTaskUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Next Task", 
                        style = MaterialTheme.typography.labelMedium, 
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.scheduledTime, 
                        style = MaterialTheme.typography.headlineSmall, 
                        color = TextDark, 
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                StatusBadge(type = task.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextDark,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn, 
                    contentDescription = null, 
                    modifier = Modifier.size(16.dp), 
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = task.location, 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    TaskInfoItem("Distance", task.distance)
                    TaskInfoItem("ETA", task.eta)
                    TaskInfoItem("Items", "${task.taskCount}")
                }
                
                Surface(
                    modifier = Modifier
                        .size(44.dp),
                    shape = CircleShape,
                    color = PrimaryBlue,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward, 
                            contentDescription = "Start", 
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFF0F2F5))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime, 
                        contentDescription = null, 
                        modifier = Modifier.size(14.dp), 
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Due by ${task.dueTime}", 
                        style = MaterialTheme.typography.labelMedium, 
                        color = TextSecondary
                    )
                }
                Text(
                    text = task.scheduleStatus, 
                    style = MaterialTheme.typography.labelMedium, 
                    color = Color(0xFF2E7D32), 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TaskInfoItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
    }
}
