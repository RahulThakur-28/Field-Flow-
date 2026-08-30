package com.rahul.fieldflow.features.reports.components

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var currentPosition by remember { mutableIntStateOf(0) }
    var duration by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Session Started",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = session.startedAt.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm:ss")),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "${session.durationSeconds ?: 0}s",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
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
                                        try {
                                            mediaPlayer = MediaPlayer().apply {
                                                setDataSource(url)
                                                prepareAsync()
                                                setOnPreparedListener {
                                                    duration = it.duration
                                                    start()
                                                    isPlaying = true
                                                    isLoading = false
                                                }
                                                setOnCompletionListener {
                                                    isPlaying = false
                                                    progress = 0f
                                                    currentPosition = 0
                                                }
                                            }
                                        } catch (e: Exception) {
                                            android.util.Log.e("AUDIO_PLAYER", "Error playing audio", e)
                                            isLoading = false
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
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = PrimaryBlue,
                    enabled = !isLoading && session.storagePath != null
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Slider(
                        value = progress,
                        onValueChange = { newProgress ->
                            progress = newProgress
                            mediaPlayer?.let {
                                val seekTo = (newProgress * it.duration).toInt()
                                it.seekTo(seekTo)
                                currentPosition = seekTo
                            }
                        },
                        modifier = Modifier.height(20.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryBlue,
                            activeTrackColor = PrimaryBlue,
                            inactiveTrackColor = PrimaryBlue.copy(alpha = 0.2f)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(currentPosition),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatTime(duration),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            mediaPlayer?.let {
                if (it.duration > 0) {
                    currentPosition = it.currentPosition
                    progress = currentPosition.toFloat() / it.duration
                }
            }
            delay(200)
        }
    }
}

private fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
