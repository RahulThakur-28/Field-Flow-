package com.rahul.fieldflow.features.profile.owner.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.features.profile.components.ProfileSettingItem
import com.rahul.fieldflow.features.profile.owner.viewmodel.OwnerProfileViewModel
import com.rahul.fieldflow.ui.theme.FieldFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerNotificationPreferencesScreen(
    navController: NavController,
    viewModel: OwnerProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Preferences", fontWeight = FontWeight.Bold) },
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
                    trailingSwitch = uiState.pushNotificationsEnabled,
                    onSwitchChange = viewModel::togglePushNotifications
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                
                ProfileSettingItem(
                    title = "Email Notifications",
                    subtitle = "Receive weekly reports and alerts",
                    trailingSwitch = uiState.emailNotificationsEnabled,
                    onSwitchChange = viewModel::toggleEmailNotifications
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                ProfileSettingItem(
                    title = "Task Updates",
                    subtitle = "When a technician starts or completes a task",
                    trailingSwitch = uiState.taskUpdatesEnabled,
                    onSwitchChange = viewModel::toggleTaskUpdates
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                ProfileSettingItem(
                    title = "Team Activity",
                    subtitle = "Technician check-ins and movements",
                    trailingSwitch = uiState.teamActivityEnabled,
                    onSwitchChange = viewModel::toggleTeamActivity
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                ProfileSettingItem(
                    title = "Report Notifications",
                    subtitle = "When a new report is submitted",
                    trailingSwitch = uiState.reportNotificationsEnabled,
                    onSwitchChange = viewModel::toggleReportNotifications
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerNotificationPreferencesScreenPreview() {
    FieldFlowTheme {
        OwnerNotificationPreferencesScreen(navController = rememberNavController())
    }
}
