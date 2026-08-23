package com.rahul.fieldflow.features.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.features.profile.components.ProfileAvatar
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary

@Composable
fun FieldFlowHeader(
    date: String,
    userName: String,
    subtitle: String,
    initials: String,
    notificationCount: Int,
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Good morning,",
                style = MaterialTheme.typography.headlineSmall,
                color = TextDark
            )

            Text(
                text = "$userName 👋",
                style = MaterialTheme.typography.headlineSmall,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            NotificationButton(
                notificationCount = notificationCount,
                onClick = onNotificationClick
            )

            Spacer(modifier = Modifier.width(12.dp))

            ProfileAvatar(
                initials = initials,
                onClick = onProfileClick
            )
        }
    }
}