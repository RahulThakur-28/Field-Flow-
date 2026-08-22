package com.rahul.fieldflow.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.core.common.components.AppTextField
import com.rahul.fieldflow.core.common.components.HeaderSection
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.ui.theme.*

@Composable
fun CompanyFoundScreen(
    onBack: () -> Unit,
    onSendRequest: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            HeaderSection(
                title = "Company Found ✓",
                subtitle = "You're requesting to join this company.",
                onBack = onBack
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E5EE))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = BackgroundLight
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = PrimaryBlue)
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "ABC Services",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Company ID: 58321476",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Employee details:",
                style = MaterialTheme.typography.titleMedium,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AppTextField(
                value = fullName,
                onValueChange = { 
                    fullName = it
                    fullNameError = null
                },
                label = "Full Name *",
                placeholder = "Jane Doe",
                leadingIcon = Icons.Default.Person,
                errorText = fullNameError
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AppTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = null
                },
                label = "Work Email *",
                placeholder = "jane@company.com",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                errorText = emailError
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AppTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                placeholder = "+1 234 567 890",
                leadingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            PrimaryButton(
                text = "Send Join Request",
                onClick = {
                    if (fullName.isBlank()) fullNameError = "Full name is required"
                    if (email.isBlank()) emailError = "Email is required"
                    
                    if (fullNameError == null && emailError == null) {
                        onSendRequest()
                    }
                },
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}
