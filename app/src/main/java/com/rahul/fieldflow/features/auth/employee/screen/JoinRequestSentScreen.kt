package com.rahul.fieldflow.features.auth.employee.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.features.auth.viewmodel.AuthState
import com.rahul.fieldflow.features.auth.viewmodel.AuthViewModel
import com.rahul.fieldflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinRequestSentScreen(
    onBackToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val isRejected = authState is AuthState.Rejected

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = { viewModel.logout(onBackToLogin) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout, 
                            contentDescription = "Logout", 
                            tint = Color.Red
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = if (isRejected) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (isRejected) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isRejected) Color.Red else Color(0xFF4CAF50),
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = if (isRejected) "Request Rejected" else "Request Sent",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isRejected) 
                    "Your request to join the workspace was rejected by the owner."
                else 
                    "Your request to join the workspace has been sent to the company owner.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Surface(
                        color = if (isRejected) Color(0xFFFFEBEE) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isRejected) "REJECTED" else "PENDING",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (isRejected) Color.Red else Color(0xFFEF6C00),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = if (isRejected) "What can I do?" else "Waiting for owner approval",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = if (isRejected)
                    "Please contact your workspace owner or try again with a different ID."
                else
                    "You'll be able to access the dashboard once the owner approves your request.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            PrimaryButton(
                text = if (isRejected) "Try Again" else "Refresh Status",
                onClick = { 
                    viewModel.refreshStatus() 
                },
                isLoading = authState is AuthState.Checking
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
