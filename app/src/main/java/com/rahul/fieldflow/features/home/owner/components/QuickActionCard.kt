package com.rahul.fieldflow.features.home.owner.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.ui.theme.TextDark

@Composable
fun QuickActionCard(icon: ImageVector, title: String, accentColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .width(104.dp)
            .height(116.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = 0.08f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon, 
                        contentDescription = null, 
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title, 
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold, 
                color = TextDark,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
