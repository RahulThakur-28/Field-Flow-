package com.rahul.fieldflow.features.tasks.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.Composable

@Composable
fun TaskScheduleCard(date: String, time: String) {
    DetailItem(Icons.Default.CalendarToday, "Scheduled Date", date)
}
