package com.rahul.fieldflow.features.auth.employee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: EmployeeRegistrationViewModel = hiltViewModel()
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
                .imePadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            HeaderSection(
                title = "Create Employee Account",
                subtitle = "Join your company on FieldFlow",
                onBack = onBack
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AppTextField(
                value = uiState.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = "Full Name *",
                placeholder = "e.g., John Doe",
                leadingIcon = Icons.Default.Person,
                errorText = uiState.fullNameError,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            AppTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = "Work Email *",
                placeholder = "john@company.com",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                errorText = uiState.emailError,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            AppTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = "Phone Number",
                placeholder = "e.g., +1 234 567 890",
                leadingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            PasswordTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Password *",
                placeholder = "Create a strong password",
                leadingIcon = Icons.Default.Lock,
                errorText = uiState.passwordError,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            PasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = "Confirm Password *",
                placeholder = "Repeat your password",
                leadingIcon = Icons.Default.Lock,
                errorText = uiState.confirmPasswordError,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
            
            PrimaryButton(
                text = "Create Account",
                onClick = viewModel::register,
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(bottom = 48.dp)
            )
        }
    }
}
