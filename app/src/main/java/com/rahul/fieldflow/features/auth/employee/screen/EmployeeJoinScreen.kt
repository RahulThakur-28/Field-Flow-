package com.rahul.fieldflow.features.auth.employee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rahul.fieldflow.core.common.components.AppTextField
import com.rahul.fieldflow.core.common.components.HeaderSection
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.features.auth.viewmodel.EmployeeJoinViewModel
import com.rahul.fieldflow.ui.theme.BackgroundLight
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextSecondary

@Composable
fun EmployeeJoinScreen(
    onBack: () -> Unit,
    onCompanyFound: () -> Unit,
    onNavigateToInvitation: () -> Unit,
    viewModel: EmployeeJoinViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.companyFound) {
        if (uiState.companyFound) {
            onCompanyFound()
            // Reset search state so if they come back it's fresh,
            // but keep companyId if that's desired behavior.
            // For now, simple transition.
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            HeaderSection(
                title = "Join your company",
                subtitle = "Enter your company's 8-digit Company ID to find your organization.",
                onBack = onBack
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            AppTextField(
                value = uiState.companyId,
                onValueChange = viewModel::onCompanyIdChange,
                label = "Company ID",
                placeholder = "0 0 0 0 0 0 0 0",
                leadingIcon = Icons.Default.Business,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                errorText = uiState.companyIdError
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PrimaryButton(
                text = "Find Company",
                onClick = viewModel::findCompany,
                isLoading = uiState.isSearching
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Don't know your Company ID?",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                TextButton(onClick = { /* TODO: Show info */ }) {
                    Text(
                        text = "Ask your company owner.",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Have an invitation?",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onNavigateToInvitation,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                ) {
                    Text(
                        text = "Accept Invitation",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
