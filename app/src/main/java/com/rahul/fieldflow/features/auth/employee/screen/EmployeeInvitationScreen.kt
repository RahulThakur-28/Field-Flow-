package com.rahul.fieldflow.features.auth.employee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.ui.theme.*

@Composable
fun EmployeeInvitationScreen(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
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
            Text(
                text = "You're invited! 🎉",
                style = MaterialTheme.typography.headlineMedium,
                color = TextDark,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "ABC Services has invited you to join their FieldFlow workspace.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = BackgroundLight
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "ABC Services",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextDark,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Invited by: ", color = TextSecondary, fontSize = 14.sp)
                        Text(text = "Rahul Thakur", color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(64.dp))
            
            PrimaryButton(
                text = "Accept Invitation",
                onClick = onAccept
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = onDecline,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(
                    text = "Decline",
                    color = Color.Red.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
