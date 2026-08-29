package com.rahul.fieldflow.features.reports.components

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.domain.model.RecordingSession
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@Composable
fun RecordingSessionItem(
    session: RecordingSession,
    onGetUrl: suspend () -> String?
) {
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Session Started: ${session.startedAt.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "Duration: ${session.durationSeconds ?: 0}s | Status: ${session.status}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (isPlaying) {
                            mediaPlayer?.pause()
                            isPlaying = false
                        } else {
                            if (mediaPlayer == null) {
                                scope.launch {
                                    isLoading = true
                                    val url = onGetUrl()
                                    if (url != null) {
                                        mediaPlayer = MediaPlayer().apply {
                                            setDataSource(url)
                                            prepareAsync()
                                            setOnPreparedListener {
                                                start()
                                                isPlaying = true
                                                isLoading = false
                                            }
                                            setOnCompletionListener {
                                                isPlaying = false
                                                progress = 0f
                                            }
                                        }
                                    } else {
                                        isLoading = false
                                    }
                                }
                            } else {
                                mediaPlayer?.start()
                                isPlaying = true
                            }
                        }
                    },
                    enabled = !isLoading && session.storagePath != null
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = PrimaryBlue
                        )
                    }
                }

                Slider(
                    value = progress,
                    onValueChange = { /* Implement seek if needed */ },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                )
            }
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            mediaPlayer?.let {
                if (it.duration > 0) {
                    progress = it.currentPosition.toFloat() / it.duration
                }
            }
            delay(500)
        }
    }
}
