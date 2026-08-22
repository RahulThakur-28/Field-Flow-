package com.rahul.fieldflow.features.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.core.common.components.AppTextField
import com.rahul.fieldflow.core.common.components.HeaderSection
import com.rahul.fieldflow.core.common.components.PasswordTextField
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.ui.theme.BackgroundLight
import com.rahul.fieldflow.ui.theme.TextSecondary

@Composable
fun OwnerRegistrationScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var companyNameError by remember { mutableStateOf<String?>(null) }
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
                title = "Create Company Account",
                subtitle = "Set up your FieldFlow workspace",
                onBack = onBack
            )
            
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
            
            AppTextField(
                value = companyName,
                onValueChange = { 
                    companyName = it
                    companyNameError = null
                },
                label = "Company Name *",
                placeholder = "ABC Services Inc.",
                leadingIcon = Icons.Default.Business,
                errorText = companyNameError
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Your unique Company ID will be generated after your company is created.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PrimaryButton(
                text = "Create Company",
                onClick = {
                    if (fullName.isBlank()) fullNameError = "Full name is required"
                    if (email.isBlank()) emailError = "Email is required"
                    if (companyName.isBlank()) companyNameError = "Company name is required"
                    if (password.length < 6) passwordError = "Password must be at least 6 characters"
                    if (confirmPassword != password) confirmPasswordError = "Passwords do not match"
                    
                    if (fullNameError == null && emailError == null && 
                        companyNameError == null && passwordError == null && 
                        confirmPasswordError == null) {
                        onSuccess()
                    }
                },
                modifier = Modifier.padding(bottom = 48.dp)
            )
        }
    }
}
