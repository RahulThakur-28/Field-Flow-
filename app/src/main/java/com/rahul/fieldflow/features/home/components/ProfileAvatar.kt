package com.rahul.fieldflow.features.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@Composable
fun ProfileAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = PrimaryBlue.copy(alpha = 0.1f)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}