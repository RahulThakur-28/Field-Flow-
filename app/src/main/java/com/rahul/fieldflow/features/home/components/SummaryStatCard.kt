package com.rahul.fieldflow.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.features.home.model.StatusBadgeType
import com.rahul.fieldflow.features.home.model.SummaryStatUiModel
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary

@Composable
fun SummaryStatCard(
    stat: SummaryStatUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val contentColor = when (stat.type) {
        StatusBadgeType.ACTIVE -> PrimaryBlue
        StatusBadgeType.DONE -> Color(0xFF2E7D32) // Deeper Green
        StatusBadgeType.PENDING -> Color(0xFFEF6C00) // Deeper Amber
        StatusBadgeType.LATE -> Color(0xFFC62828) // Deeper Red
        else -> TextDark
    }

    val backgroundColor = contentColor.copy(alpha = 0.1f)

    Card(
        modifier = modifier.height(84.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleLarge,
                color = if (MaterialTheme.colorScheme.surface == Color(0xFF161C2C)) contentColor.copy(alpha = 0.9f) else contentColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
