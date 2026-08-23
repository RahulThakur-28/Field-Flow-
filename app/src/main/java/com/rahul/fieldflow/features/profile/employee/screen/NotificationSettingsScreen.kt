package com.rahul.fieldflow.features.profile.employee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rahul.fieldflow.features.profile.components.ProfileSettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(navController: NavController) {
    var pushEnabled by remember { mutableStateOf(true) }
    var emailEnabled by remember { mutableStateOf(true) }
    var taskUpdates by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ProfileSettingItem(
                    title = "Push Notifications",
                    subtitle = "Receive alerts on your device",
                    trailingSwitch = pushEnabled,
                    onSwitchChange = { pushEnabled = it }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                
                ProfileSettingItem(
                    title = "Email Notifications",
                    subtitle = "Receive updates via email",
                    trailingSwitch = emailEnabled,
                    onSwitchChange = { emailEnabled = it }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                ProfileSettingItem(
                    title = "Task Updates",
                    subtitle = "When a new task is assigned or modified",
                    trailingSwitch = taskUpdates,
                    onSwitchChange = { taskUpdates = it }
                )
            }
        }
    }
}
