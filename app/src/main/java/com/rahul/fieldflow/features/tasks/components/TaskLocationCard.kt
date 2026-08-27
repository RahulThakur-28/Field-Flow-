package com.rahul.fieldflow.features.tasks.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.ui.theme.TextSecondary

@Composable
fun TaskLocationCard(
    location: String,
    lat: Double? = null,
    lng: Double? = null,
    radius: Int? = null
) {
    Column {
        DetailItem(Icons.Default.LocationOn, "Location", location)
        if (lat != null && lng != null) {
            Text(
                text = "Coordinates: ${String.format("%.4f", lat)}, ${String.format("%.4f", lng)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(start = 48.dp)
            )
        }
        if (radius != null) {
            Text(
                text = "Geofence: ${radius}m",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(start = 48.dp)
            )
        }
    }
}
