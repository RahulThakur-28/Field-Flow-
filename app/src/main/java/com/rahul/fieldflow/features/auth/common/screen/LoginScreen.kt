package com.rahul.fieldflow.features.auth.common.screen

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rahul.fieldflow.R
import com.rahul.fieldflow.core.common.components.AppTextField
import com.rahul.fieldflow.core.common.components.PasswordTextField
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.features.auth.viewmodel.LoginViewModel
import com.rahul.fieldflow.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            
            Image(
                painter = painterResource(id = R.drawable.fieldflow_logo),
                contentDescription = "FieldFlow Logo",
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Welcome back 👋",
                style = MaterialTheme.typography.headlineMedium,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Sign in to your FieldFlow account",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            AppTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = "Work Email",
                placeholder = "you@company.com",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                errorText = uiState.emailError
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            PasswordTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Password",
                placeholder = "Enter your password",
                leadingIcon = Icons.Default.Lock,
                errorText = uiState.passwordError
            )
            
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = { /* TODO: Forgot Password */ }) {
                    Text(
                        text = "Forgot Password?",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PrimaryButton(
                text = "Log In",
                onClick = viewModel::login,
                isLoading = uiState.isLoading
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                modifier = Modifier.padding(vertical = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Don't have an account? ", color = TextSecondary)
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Create Account",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FieldFlowTheme {
        LoginScreen(onNavigateToRegister = {}, onLoginSuccess = {})
    }
}
