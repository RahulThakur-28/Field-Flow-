package com.rahul.fieldflow.features.profile.owner.screen

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
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
import com.rahul.fieldflow.domain.model.AppTheme
import com.rahul.fieldflow.features.auth.viewmodel.AuthViewModel
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.features.profile.components.ProfileAvatar
import com.rahul.fieldflow.features.profile.components.ProfileContactItem
import com.rahul.fieldflow.features.profile.components.ProfileSettingItem
import com.rahul.fieldflow.features.profile.components.ProfileStatCard
import com.rahul.fieldflow.features.profile.components.ProfileVerticalDivider
import com.rahul.fieldflow.features.profile.owner.viewmodel.OwnerProfileViewModel
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerProfileScreen(
    navController: NavController,
    viewModel: OwnerProfileViewModel = hiltViewModel(),
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
                title = { Text("Profile", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.ownerItems,
                navController = navController
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                
                // Profile Header Card - Premium Blue
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            ProfileAvatar(
                                initials = uiState.initials,
                                modifier = Modifier.size(100.dp),
                                containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.onPrimary,
                                shadowElevation = 4.dp
                            ) {
                                IconButton(onClick = { navController.navigate(AppRoutes.OwnerEditProfile) }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary,
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
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Text(
                            text = "Owner • ${uiState.company}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${uiState.totalTasks}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = "Total Tasks",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${uiState.teamSize}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = "Team Size",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Contact Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProfileContactItem(icon = Icons.Default.Email, label = "Email", value = uiState.email)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ProfileContactItem(icon = Icons.Default.Phone, label = "Phone", value = uiState.phone ?: "Not added")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ProfileContactItem(icon = Icons.Default.Business, label = "Company", value = uiState.company.ifEmpty { "Company not available" })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        ProfileSettingItem(
                            title = "Edit Profile",
                            onClick = { navController.navigate(AppRoutes.OwnerEditProfile) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ProfileSettingItem(
                            title = "Notifications",
                            onClick = { navController.navigate(AppRoutes.Notifications) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ProfileSettingItem(
                            title = "Dark Theme",
                            trailingSwitch = uiState.appTheme == AppTheme.DARK,
                            onSwitchChange = { isDark ->
                                viewModel.setTheme(if (isDark) AppTheme.DARK else AppTheme.LIGHT)
                            }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ProfileSettingItem(
                            title = "Privacy Policy",
                            onClick = { navController.navigate(AppRoutes.PrivacyPolicy) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ProfileSettingItem(
                            title = "About Us",
                            onClick = { navController.navigate(AppRoutes.AboutUs) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ProfileSettingItem(
                            title = "Account Settings",
                            onClick = { navController.navigate(AppRoutes.OwnerAccountSettings) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(
                    onClick = { showSignOutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = com.rahul.fieldflow.ui.theme.ErrorRed)
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
fun OwnerProfileScreenPreview() {
    FieldFlowTheme {
        OwnerProfileScreen(navController = rememberNavController())
    }
}
