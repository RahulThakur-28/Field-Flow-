package com.rahul.fieldflow.features.tasks.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable

@Composable
fun TaskLocationCard(location: String) {
    DetailItem(Icons.Default.LocationOn, "Location", location)
}
