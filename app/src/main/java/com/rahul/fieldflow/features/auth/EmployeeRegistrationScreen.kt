package com.rahul.fieldflow.features.auth

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
import com.rahul.fieldflow.core.common.components.PasswordTextField
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.ui.theme.*

@Composable
fun EmployeeRegistrationScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var fullNameError by remember { mutableStateOf<String?>(null) }

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
                title = "Complete your profile",
                subtitle = "You're joining ABC Services",
                onBack = onBack
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFE8F5E9).copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "ABC Services", fontWeight = FontWeight.Bold, color = TextDark)
                        Text(text = "Your account will be linked to this company.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AppTextField(
                value = fullName,
                onValueChange = { 
                    fullName = it
                    fullNameError = null
                },
                label = "Full Name *",
                placeholder = "John Doe",
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
                placeholder = "john@company.com",
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PasswordTextField(
                value = password,
                onValueChange = { 
                    password = it
                    passwordError = null
                },
                label = "Password *",
                placeholder = "Create a password",
                leadingIcon = Icons.Default.Lock,
                errorText = passwordError
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PasswordTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    confirmPasswordError = null
                },
                label = "Confirm Password *",
                placeholder = "Repeat your password",
                leadingIcon = Icons.Default.Lock,
                errorText = confirmPasswordError
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            PrimaryButton(
                text = "Create Employee Account",
                onClick = {
                    if (fullName.isBlank()) fullNameError = "Full name is required"
                    if (email.isBlank()) emailError = "Email is required"
                    if (password.length < 6) passwordError = "Password must be at least 6 characters"
                    if (confirmPassword != password) confirmPasswordError = "Passwords do not match"
                    
                    if (fullNameError == null && emailError == null && 
                        passwordError == null && confirmPasswordError == null) {
                        onSuccess()
                    }
                },
                modifier = Modifier.padding(bottom = 48.dp)
            )
        }
    }
}
