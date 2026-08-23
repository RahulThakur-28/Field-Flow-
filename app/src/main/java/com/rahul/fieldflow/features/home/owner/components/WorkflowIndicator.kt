package com.rahul.fieldflow.features.home.owner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
            .padding(vertical = 16.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, step ->
            WorkflowStep(
                label = step,
                isCompleted = index < currentStep,
                isCurrent = index == currentStep,
                isFuture = index > currentStep
            )
            
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .height(2.dp)
                        .weight(1f)
                        .background(if (index < currentStep) PrimaryBlue else Color(0xFFE0E0E0))
                )
            }
        }
    }
}

@Composable
private fun WorkflowStep(
    label: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isFuture: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isCompleted || isCurrent) PrimaryBlue else Color.Transparent)
                .then(
                    if (isFuture) Modifier.border(1.5.dp, Color(0xFFE0E0E0), CircleShape) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            } else if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
            // Future steps are just an empty circle (the border)
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
            color = if (isCurrent || isCompleted) PrimaryBlue else Color.Gray,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WorkflowIndicatorPreview() {
    WorkflowIndicator(currentStep = 2)
}
