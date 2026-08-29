package com.rahul.fieldflow.features.home.owner.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.features.home.components.NotificationButton
import com.rahul.fieldflow.features.profile.components.ProfileAvatar
import com.rahul.fieldflow.ui.theme.TextDark
import java.time.LocalTime

@Composable
fun HomeGreetingHeader(
    userName: String,
    initials: String,
    unreadNotificationsCount: Int,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTime = LocalTime.now()
    val greeting = when (currentTime.hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    
    val icon = when (currentTime.hour) {
        in 6..17 -> Icons.Default.WbSunny
        else -> Icons.Default.DarkMode
    }
    
    val iconColor = when (currentTime.hour) {
        in 6..17 -> Color(0xFFFFA000)
        else -> Color(0xFF5C6BC0)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$greeting,",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextDark.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "$userName 👋",
                style = MaterialTheme.typography.headlineSmall,
                color = TextDark,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NotificationButton(
                notificationCount = unreadNotificationsCount,
                onClick = onNotificationClick
            )

            ProfileAvatar(
                initials = initials,
                onClick = onProfileClick,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
