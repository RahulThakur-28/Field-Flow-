package com.rahul.fieldflow.features.auth.employee.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.ui.theme.*

import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.rahul.fieldflow.features.auth.viewmodel.AuthState

@Composable
fun JoinRequestSentScreen(
    onBackToLogin: () -> Unit,
    viewModel: com.rahul.fieldflow.features.auth.viewmodel.AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val isRejected = authState is AuthState.Rejected

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                color = if (isRejected) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (isRejected) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isRejected) Color.Red else Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = if (isRejected) "Request Rejected" else "Request Sent",
                style = MaterialTheme.typography.headlineMedium,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isRejected) 
                    "Your request to join the workspace was rejected by the owner."
                else 
                    "Your request to join the workspace has been sent to the company owner.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Status:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextDark,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Surface(
                        color = if (isRejected) Color(0xFFFFEBEE) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isRejected) "REJECTED" else "PENDING",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = if (isRejected) Color.Red else Color(0xFFFF9800),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = if (isRejected) "What can I do?" else "Waiting for owner approval",
                style = MaterialTheme.typography.titleMedium,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isRejected)
                    "Please contact your workspace owner or try again with a different ID."
                else
                    "You'll be able to access the dashboard once the owner approves your request.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            PrimaryButton(
                text = if (isRejected) "Try Again" else "Refresh Status",
                onClick = { 
                    viewModel.refreshStatus() 
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onBackToLogin
            ) {
                Text(
                    text = "Back to Login",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
