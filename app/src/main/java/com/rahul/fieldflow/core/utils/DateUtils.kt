package com.rahul.fieldflow.core.utils

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    fun formatMemberSince(isoDate: String?): String {
        if (isoDate == null) return "Member since: Aug 2026"
        return try {
            val dateTime = OffsetDateTime.parse(isoDate)
            val formatter = DateTimeFormatter.ofPattern("MMM yyyy")
            "Member since: ${dateTime.format(formatter)}"
        } catch (e: Exception) {
            "Member since: Aug 2026"
        }
    }

    fun formatDuration(seconds: Int): String {
        if (seconds <= 0) return "No recording"
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        
        return if (h > 0) {
            String.format(Locale.US, "%d:%02d:%02d", h, m, s)
        } else {
            String.format(Locale.US, "%d:%02d", m, s)
        }
    }
}
