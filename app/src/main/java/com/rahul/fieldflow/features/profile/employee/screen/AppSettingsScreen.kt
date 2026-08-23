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
fun AppSettingsScreen(navController: NavController) {
    var darkMode by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("English") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Settings", fontWeight = FontWeight.Bold) },
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
                    title = "Dark Mode",
                    subtitle = "Toggle dark theme",
                    trailingSwitch = darkMode,
                    onSwitchChange = { darkMode = it }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                
                ProfileSettingItem(
                    title = "Language",
                    subtitle = language,
                    onClick = { /* Handle language selection */ }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                ProfileSettingItem(
                    title = "App Version",
                    subtitle = "1.0.0 (Build 101)",
                    onClick = { }
                )
            }
        }
    }
}
