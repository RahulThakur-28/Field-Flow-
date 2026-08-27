package com.rahul.fieldflow.features.home.owner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@Composable
fun WorkflowIndicator(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val steps = listOf("Assign", "Travel", "Arrive", "Work", "Report")
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, step ->
            WorkflowStep(
                label = step,
                isCompleted = index < currentStep,
                isCurrent = index == currentStep,
                isFuture = index > currentStep,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WorkflowStep(
    label: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isFuture: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (isCompleted) Color(0xFF2E7D32) 
                    else if (isCurrent) PrimaryBlue 
                    else Color.White
                )
                .then(
                    if (isFuture || isCurrent) Modifier.border(1.5.dp, if (isCurrent) PrimaryBlue else Color(0xFFE0E0E0), CircleShape) 
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            } else if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
            color = if (isCurrent) PrimaryBlue else if (isCompleted) Color(0xFF2E7D32) else Color.Gray,
            maxLines = 1,
            fontSize = 9.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WorkflowIndicatorPreview() {
    WorkflowIndicator(currentStep = 2)
}
