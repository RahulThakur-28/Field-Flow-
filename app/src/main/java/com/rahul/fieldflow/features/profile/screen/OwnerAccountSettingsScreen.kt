package com.rahul.fieldflow.features.profile.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.features.profile.components.ProfileSettingItem
import com.rahul.fieldflow.ui.theme.FieldFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerAccountSettingsScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Settings", fontWeight = FontWeight.Bold) },
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
                    title = "Account Information",
                    onClick = { /* Placeholder */ }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                
                ProfileSettingItem(
                    title = "Security",
                    onClick = { /* Placeholder */ }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                ProfileSettingItem(
                    title = "Change Password",
                    onClick = { /* Placeholder */ }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                ProfileSettingItem(
                    title = "Privacy",
                    onClick = { /* Placeholder */ }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerAccountSettingsScreenPreview() {
    FieldFlowTheme {
        OwnerAccountSettingsScreen(navController = rememberNavController())
    }
}
