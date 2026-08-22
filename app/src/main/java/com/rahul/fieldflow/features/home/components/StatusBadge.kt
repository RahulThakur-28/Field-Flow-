package com.rahul.fieldflow.features.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.rahul.fieldflow.features.home.model.StatusBadgeType
import com.rahul.fieldflow.ui.theme.GrayLight
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.SecondaryIndigo
import com.rahul.fieldflow.ui.theme.TextSecondary

@Composable
fun StatusBadge(
    type: StatusBadgeType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (type) {

        StatusBadgeType.IN_PROGRESS -> Triple(
            Color(0xFFE3F2FD),
            PrimaryBlue,
            "In Progress"
        )

        StatusBadgeType.TRAVELING -> Triple(
            Color(0xFFF3E5F5),
            SecondaryIndigo,
            "Traveling"
        )

        StatusBadgeType.PENDING -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFFF9800),
            "Assigned"
        )

        StatusBadgeType.IDLE -> Triple(
            GrayLight.copy(alpha = 0.3f),
            TextSecondary,
            "Idle"
        )

        StatusBadgeType.DONE -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF4CAF50),
            "Completed"
        )

        else -> Triple(
            GrayLight.copy(alpha = 0.3f),
            TextSecondary,
            "Unknown"
        )
    }

    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}