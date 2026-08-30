package com.rahul.fieldflow.features.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.rahul.fieldflow.features.home.model.StatusBadgeType
import com.rahul.fieldflow.ui.theme.*

@Composable
fun StatusBadge(
    type: StatusBadgeType,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface == SurfaceDark

    val (backgroundColor, textColor, label) = when (type) {
        StatusBadgeType.IN_PROGRESS -> Triple(
            if (isDark) InfoDark else Color(0xFFE3F2FD),
            if (isDark) InfoBlue else PrimaryBlue,
            "In Progress"
        )

        StatusBadgeType.TRAVELING -> Triple(
            if (isDark) Color(0xFF2A1B3D) else Color(0xFFF3E5F5),
            SecondaryIndigo,
            "Traveling"
        )

        StatusBadgeType.PENDING -> Triple(
            if (isDark) WarningDark else Color(0xFFFFF3E0),
            WarningOrange,
            "Assigned"
        )

        StatusBadgeType.IDLE -> Triple(
            if (isDark) MaterialTheme.colorScheme.surfaceVariant else GrayLight.copy(alpha = 0.3f),
            if (isDark) TextMuted else TextSecondary,
            "Idle"
        )

        StatusBadgeType.DONE -> Triple(
            if (isDark) SuccessDark else Color(0xFFE8F5E9),
            SuccessGreen,
            "Completed"
        )

        else -> Triple(
            if (isDark) MaterialTheme.colorScheme.surfaceVariant else GrayLight.copy(alpha = 0.3f),
            if (isDark) TextMuted else TextSecondary,
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