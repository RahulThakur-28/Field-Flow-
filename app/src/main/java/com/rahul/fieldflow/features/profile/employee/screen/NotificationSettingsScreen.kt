package com.rahul.fieldflow.features.profile.employee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rahul.fieldflow.features.profile.components.ProfileSettingItem
import com.rahul.fieldflow.features.profile.employee.viewmodel.EmployeeProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    viewModel: EmployeeProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Text(
                    text = "PREFERENCES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column {
                        ProfileSettingItem(
                            title = "Push Notifications",
                            subtitle = "Receive alerts on your device",
                            trailingSwitch = uiState.pushNotificationsEnabled,
                            onSwitchChange = viewModel::togglePushNotifications
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp), 
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                        
                        ProfileSettingItem(
                            title = "Email Notifications",
                            subtitle = "Receive updates via email",
                            trailingSwitch = uiState.emailNotificationsEnabled,
                            onSwitchChange = viewModel::toggleEmailNotifications
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp), 
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )

                        ProfileSettingItem(
                            title = "Task Updates",
                            subtitle = "When a new task is assigned or modified",
                            trailingSwitch = uiState.taskUpdatesEnabled,
                            onSwitchChange = viewModel::toggleTaskUpdates
                        )
                    }
                }
            }
        }
    }
}
