package com.rahul.fieldflow.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.ui.theme.TextDark

@Composable
fun NotificationButton(
    notificationCount: Int,
    onClick: () -> Unit = {}
) {
    Box {
        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = TextDark
            )
        }

        if (notificationCount > 0) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.TopEnd)
                    .offset(
                        x = (-8).dp,
                        y = 8.dp
                    )
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }
    }
}