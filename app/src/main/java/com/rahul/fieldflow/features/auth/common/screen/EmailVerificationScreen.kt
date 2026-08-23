package com.rahul.fieldflow.features.auth.common.screen

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
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationScreen(
    onVerifySuccess: () -> Unit,
    onChangeEmail: () -> Unit
) {
    var timer by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(timer) {
        if (timer > 0) {
            delay(1000)
            timer--
        } else {
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
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Email,
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
                text = "user@example.com",
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
                text = "Open Email",
                onClick = { /* TODO: Open email app */ }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = {
                    if (canResend) {
                        timer = 60
                        canResend = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                enabled = canResend,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (canResend) PrimaryBlue else GrayLight)
            ) {
                Text(
                    text = if (canResend) "Resend Email" else "Resend in ${timer}s",
                    color = if (canResend) PrimaryBlue else TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TextButton(onClick = onChangeEmail) {
                Text(
                    text = "Wrong email? Change email",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // For demo purposes
            TextButton(
                onClick = onVerifySuccess,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "(Demo: Skip to Home)", color = TextSecondary.copy(alpha = 0.5f))
            }
        }
    }
}
