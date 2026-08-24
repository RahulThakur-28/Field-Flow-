package com.rahul.fieldflow.features.profile.employee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.auth.viewmodel.AuthViewModel
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.features.profile.components.*
import com.rahul.fieldflow.features.profile.employee.viewmodel.EmployeeProfileViewModel
import com.rahul.fieldflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeProfileScreen(
    navController: NavController,
    viewModel: EmployeeProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign out?") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    authViewModel.logout {
                        navController.navigate(AppRoutes.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }) {
                    Text("Sign Out", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("My Profile", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Spacer to balance the back button for centered title
                    Spacer(modifier = Modifier.width(48.dp))
                }
            )
        },
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.employeeItems,
                navController = navController
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            ProfileAvatar(
                                initials = uiState.initials,
                                modifier = Modifier.size(100.dp)
                            )
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = PrimaryBlue,
                                shadowElevation = 4.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = "Change Avatar",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = uiState.userName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Text(
                            text = "${uiState.role} · ${uiState.company}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatCard(value = "${uiState.completedTasks}", label = "Completed")
                            ProfileVerticalDivider()
                            ProfileStatCard(value = "${uiState.onTimePercentage}%", label = "On Time")
                            ProfileVerticalDivider()
                            ProfileStatCard(value = "${uiState.activeTasks}", label = "Active")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Contact Information
                ProfileInfoCard {
                    ProfileContactItem(icon = Icons.Default.Email, label = "Email", value = uiState.email)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))
                    ProfileContactItem(icon = Icons.Default.Phone, label = "Phone", value = uiState.phone)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        ProfileSettingItem(
                            title = "Change Password",
                            onClick = { navController.navigate(AppRoutes.ChangePassword) }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        ProfileSettingItem(
                            title = "Notification Settings",
                            onClick = { navController.navigate(AppRoutes.NotificationSettings) }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        ProfileSettingItem(
                            title = "App Settings",
                            onClick = { navController.navigate(AppRoutes.AppSettings) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(
                    onClick = { showSignOutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeProfileScreenPreview() {
    FieldFlowTheme {
        EmployeeProfileScreen(navController = rememberNavController())
    }
}
