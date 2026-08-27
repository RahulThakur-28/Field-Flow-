package com.rahul.fieldflow.features.auth.common.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationScreen(
    onVerifySuccess: () -> Unit,
    onChangeEmail: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val email = when (val state = authState) {
        is AuthState.Authenticated -> state.user.email
        is AuthState.EmailUnverified -> state.user.email
        else -> "your email"
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated || authState is AuthState.NoWorkspace) {
            onVerifySuccess()
        }
    }

    var timer by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(canResend) {
        if (!canResend) {
            timer = 60
            while (timer > 0) {
                delay(1000)
                timer--
            }
            canResend = true
        }
    }

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
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Verify your email",
                style = MaterialTheme.typography.headlineMedium,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "We sent a verification link to",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = TextDark,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Open your email and click the verification link to continue.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(64.dp))
            
            PrimaryButton(
                text = "Check Verification Status",
                onClick = { viewModel.refreshStatus() },
                isLoading = authState is AuthState.Checking
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (canResend) {
                TextButton(
                    onClick = {
                        viewModel.resendVerificationEmail(email)
                        canResend = false
                    }
                ) {
                    Text(
                        text = "Resend Email",
                        color = PrimaryBlue,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = "Resend available in ${timer}s",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TextButton(onClick = onChangeEmail) {
                Text(
                    text = "Wrong email? Change email",
                    color = PrimaryBlue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
