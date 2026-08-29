package com.rahul.fieldflow.features.tasks.employee.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.features.tasks.model.TaskStatus
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@Composable
fun TaskJourneyStatus(currentStatus: TaskStatus) {
    val stages = listOf(
        TaskStatus.ASSIGNED to "Assigned",
        TaskStatus.IN_PROGRESS to "In Progress",
        TaskStatus.COMPLETED to "Completed"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // ... (rest of the component)
        Text(
            text = "Journey Status",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        stages.forEachIndexed { index, (status, label) ->
            val isCompleted = stages.indexOfFirst { it.first == currentStatus } >= index
            val isActive = currentStatus == status
            
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isCompleted) PrimaryBlue else Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted && !isActive) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (isActive) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                    if (index < stages.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(if (isCompleted && stages.indexOfFirst { it.first == currentStatus } > index) PrimaryBlue else Color.LightGray)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCompleted) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}
