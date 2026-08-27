package com.rahul.fieldflow.features.home.owner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.features.home.model.ActivityItemUiModel
import com.rahul.fieldflow.ui.theme.GrayLight
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary

@Composable
fun ActivityTimeline(activities: List<ActivityItemUiModel>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        activities.forEachIndexed { index, activity ->
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.8f))
                    )
                    if (index < activities.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(PrimaryBlue.copy(alpha = 0.15f))
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text(
                        text = activity.title, 
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = TextDark,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                    Text(
                        text = activity.time, 
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
