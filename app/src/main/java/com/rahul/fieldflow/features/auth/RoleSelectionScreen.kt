package com.rahul.fieldflow.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.core.common.components.HeaderSection
import com.rahul.fieldflow.core.common.components.PrimaryButton
import com.rahul.fieldflow.ui.theme.*

@Composable
fun RoleSelectionScreen(
    onBack: () -> Unit,
    onNavigateToOwnerReg: () -> Unit,
    onNavigateToEmployeeJoin: () -> Unit
) {
    var selectedRole by remember { mutableStateOf<Role?>(null) }

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
                title = "Create your account",
                subtitle = "Choose how you'll use FieldFlow",
                onBack = onBack
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            RoleCard(
                title = "I'm a Company Owner",
                description = "Create your company, manage employees, assign field work and review reports.",
                icon = Icons.Default.BusinessCenter,
                isSelected = selectedRole == Role.OWNER,
                onClick = { selectedRole = Role.OWNER }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            RoleCard(
                title = "I'm an Employee",
                description = "Join your company, complete assigned tasks and submit field work reports.",
                icon = Icons.Default.Person,
                isSelected = selectedRole == Role.EMPLOYEE,
                onClick = { selectedRole = Role.EMPLOYEE }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            PrimaryButton(
                text = "Continue",
                onClick = {
                    when (selectedRole) {
                        Role.OWNER -> onNavigateToOwnerReg()
                        Role.EMPLOYEE -> onNavigateToEmployeeJoin()
                        null -> {}
                    }
                },
                enabled = selectedRole != null,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Already have an account? ", color = TextSecondary)
                TextButton(onClick = onBack) {
                    Text(
                        text = "Log In",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryBlue else Color(0xFFE1E5EE)
        )
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else BackgroundLight
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) PrimaryBlue else TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

enum class Role {
    OWNER, EMPLOYEE
}
