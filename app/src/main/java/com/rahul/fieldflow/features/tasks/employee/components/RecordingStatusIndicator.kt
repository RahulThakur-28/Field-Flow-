package com.rahul.fieldflow.features.tasks.employee.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.core.audio.ActiveRecordingState
import com.rahul.fieldflow.core.audio.RecordingStatus
import com.rahul.fieldflow.ui.theme.ErrorRed
import com.rahul.fieldflow.ui.theme.WarningOrange
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*

@Composable
fun RecordingStatusIndicator(
    state: ActiveRecordingState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.status == RecordingStatus.NOT_STARTED || state.status == RecordingStatus.COMPLETED) {
        return
    }

    val backgroundColor = when (state.status) {
        RecordingStatus.RECORDING -> ErrorRed.copy(alpha = 0.1f)
        RecordingStatus.INTERRUPTED -> WarningOrange.copy(alpha = 0.1f)
        RecordingStatus.FAILED -> ErrorRed.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (state.status) {
        RecordingStatus.RECORDING -> ErrorRed
        RecordingStatus.INTERRUPTED -> WarningOrange
        RecordingStatus.FAILED -> ErrorRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (state.status == RecordingStatus.RECORDING) {
                    PulseAnimation(color = contentColor)
                }
                
                Icon(
                    imageVector = when (state.status) {
                        RecordingStatus.RECORDING -> Icons.Default.Mic
                        RecordingStatus.INTERRUPTED -> Icons.Default.Pause
                        RecordingStatus.FAILED -> Icons.Default.Warning
                        else -> Icons.Default.Mic
                    },
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (state.status) {
                        RecordingStatus.RECORDING -> "Recording Active"
                        RecordingStatus.INTERRUPTED -> "Recording Interrupted"
                        RecordingStatus.FAILED -> "Recording Failed"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                
                if (state.status == RecordingStatus.RECORDING && state.startedAt != null) {
                    RecordingTimer(startedAt = state.startedAt, color = contentColor)
                } else if (state.status == RecordingStatus.INTERRUPTED) {
                    Text(
                        text = "Will resume automatically when focus regained",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                } else if (state.status == RecordingStatus.FAILED) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.error ?: "An unexpected error occurred",
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.8f),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = onRetry,
                            colors = ButtonDefaults.textButtonColors(contentColor = contentColor)
                        ) {
                            Text("Retry", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordingTimer(
    startedAt: OffsetDateTime,
    color: Color
) {
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    
    LaunchedEffect(startedAt) {
        while (true) {
            val now = OffsetDateTime.now()
            elapsedSeconds = Duration.between(startedAt, now).seconds
            kotlinx.coroutines.delay(1000)
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeString = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    Text(
        text = timeString,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = color
    )
}

@Composable
private fun PulseAnimation(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .background(color, CircleShape)
    )
}
