package com.rahul.fieldflow.features.auth.employee.screen

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rahul.fieldflow.core.common.components.AppTextField
import com.rahul.fieldflow.core.common.components.HeaderSection
import com.rahul.fieldflow.core.common.components.PasswordTextField
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.features.auth.viewmodel.EmployeeRegistrationViewModel
import com.rahul.fieldflow.ui.theme.*

@Composable
fun EmployeeRegistrationScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: EmployeeRegistrationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            onSuccess()
            viewModel.resetSuccess()
        }
    }

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
                subtitle = "You're joining ${uiState.companyName}",
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
                        Text(text = uiState.companyName, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(text = "Your account will be linked to this company.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AppTextField(
                value = uiState.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = "Full Name *",
                placeholder = "John Doe",
                leadingIcon = Icons.Default.Person,
                errorText = uiState.fullNameError
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AppTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = "Work Email *",
                placeholder = "john@company.com",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                errorText = uiState.emailError
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AppTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = "Phone Number",
                placeholder = "+1 234 567 890",
                leadingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PasswordTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Password *",
                placeholder = "Create a password",
                leadingIcon = Icons.Default.Lock,
                errorText = uiState.passwordError
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = "Confirm Password *",
                placeholder = "Repeat your password",
                leadingIcon = Icons.Default.Lock,
                errorText = uiState.confirmPasswordError
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            PrimaryButton(
                text = "Create Employee Account",
                onClick = viewModel::register,
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(bottom = 48.dp)
            )
        }
    }
}
