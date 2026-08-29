package com.rahul.fieldflow.features.reports.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.domain.model.TaskReport
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@Composable
fun AiReportSection(report: TaskReport) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "AI Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = report.summary ?: "No summary available.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (report.keyFindings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFE65100))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Key Findings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            report.keyFindings.forEach { finding ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = finding.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(text = finding.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (report.actionItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Assignment, contentDescription = null, tint = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Action Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            report.actionItems.forEach { item ->
                ListItem(
                    headlineContent = { Text(item.title, fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text(item.description) },
                    trailingContent = { 
                        Badge(containerColor = getPriorityColor(item.priority)) {
                            Text(item.priority.uppercase())
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun getPriorityColor(priority: String): Color {
    return when (priority.lowercase()) {
        "urgent" -> Color.Red
        "high" -> Color(0xFFE65100)
        "medium" -> Color(0xFFFFA000)
        "low" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }
}
