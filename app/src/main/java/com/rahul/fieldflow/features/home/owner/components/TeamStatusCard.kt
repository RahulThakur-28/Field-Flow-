package com.rahul.fieldflow.features.home.owner.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.features.profile.components.ProfileAvatar
import com.rahul.fieldflow.features.home.components.StatusBadge
import com.rahul.fieldflow.features.home.model.TeamMemberUiModel
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary

@Composable
fun TeamStatusCard(
    member: TeamMemberUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                initials = member.initials,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name, 
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold, 
                    color = TextDark
                )
                Text(
                    text = member.taskName, 
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            StatusBadge(type = member.status)
        }
    }
}
