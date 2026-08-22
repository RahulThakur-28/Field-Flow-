package com.rahul.fieldflow.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.rahul.fieldflow.R
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationComplete: () -> Unit = {}) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2196F3), // Blue
            Color(0xFF3F51B5), // Indigo
            Color(0xFF673AB7)  // Purple
        )
    )

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        delay(1200)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fieldflow_logo),
                contentDescription = "FieldFlow Logo",
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "FieldFlow",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Assign. Reach. Work. Report.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "v1.0.0 • FieldFlow",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    FieldFlowTheme {
        SplashScreen()
    }
}
