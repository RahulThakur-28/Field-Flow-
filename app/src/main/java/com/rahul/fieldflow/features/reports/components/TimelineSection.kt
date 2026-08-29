package com.rahul.fieldflow.features.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.domain.model.RecordingSession
import com.rahul.fieldflow.domain.model.Transcript
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import java.time.format.DateTimeFormatter

@Composable
fun TimelineSection(
    sessions: List<RecordingSession>,
    transcripts: List<Transcript>
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text(text = "Timeline", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        sessions.forEachIndexed { index, session ->
            // Transcript part
            val transcript = transcripts.find { it.recordingSessionId == session.id }
            if (transcript != null) {
                transcript.segments.forEach { segment ->
                    TranscriptSegmentItem(
                        time = session.startedAt.plusSeconds(segment.start.toLong()).format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        speaker = segment.speaker ?: "Employee",
                        text = segment.text
                    )
                }
            } else {
                Text(
                    text = "Session transcript unavailable (${session.status})",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            // Interruption part
            if (session.status == "interrupted" && index < sessions.size - 1) {
                InterruptionMarker(
                    time = session.endedAt?.format(DateTimeFormatter.ofPattern("HH:mm:ss")) ?: "N/A"
                )
            }
        }
    }
}

@Composable
private fun TranscriptSegmentItem(time: String, speaker: String, text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Box(modifier = Modifier.width(2.dp).height(24.dp).background(Color.LightGray))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = speaker, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun InterruptionMarker(time: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        color = Color.Red.copy(alpha = 0.05f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "RECORDING INTERRUPTED", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = "at $time", color = Color.Red.copy(alpha = 0.7f), fontSize = 10.sp)
            }
        }
    }
}
