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
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hello, ",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextDark.copy(alpha = 0.7f)
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " 👋",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NotificationButton(
                notificationCount = notificationCount,
                onClick = onNotificationClick
            )

            ProfileAvatar(
                initials = initials,
                onClick = onProfileClick,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}