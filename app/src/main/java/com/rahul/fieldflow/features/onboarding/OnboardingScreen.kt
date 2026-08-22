package com.rahul.fieldflow.features.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.fieldflow.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val onboardingPages = listOf(
        OnboardingPageData(
            title = "Assign Work Easily",
            description = "Create tasks, define locations and assign work to the right employee in seconds.",
            illustrationType = IllustrationType.ASSIGN
        ),
        OnboardingPageData(
            title = "Verify Every Visit",
            description = "Know exactly when your employee reaches the assigned location with GPS precision.",
            illustrationType = IllustrationType.VERIFY
        ),
        OnboardingPageData(
            title = "Turn Visits Into Reports",
            description = "Capture voice reports and transform them into structured insights with AI automatically.",
            illustrationType = IllustrationType.REPORTS
        )
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            if (pagerState.currentPage < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, end = 24.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    TextButton(onClick = onFinish) {
                        Text(
                            text = "Skip",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) { pageIndex ->
                OnboardingPage(data = onboardingPages[pageIndex])
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator
                Row(
                    Modifier
                        .height(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { iteration ->
                        val width = animateDpAsState(
                            targetValue = if (pagerState.currentPage == iteration) 24.dp else 8.dp,
                            label = "indicatorWidth"
                        )
                        val color = if (pagerState.currentPage == iteration) PrimaryBlue else GrayLight
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .width(width.value)
                                .height(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage == 2) "Get Started" else "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(data: OnboardingPageData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PremiumIllustration(type = data.illustrationType)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = data.title,
            color = TextDark,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = data.description,
            color = TextSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun PremiumIllustration(type: IllustrationType) {
    Box(
        modifier = Modifier
            .size(280.dp)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Shared background elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = PrimaryBlue.copy(alpha = 0.05f),
                radius = size.minDimension / 2
            )
            drawCircle(
                color = PrimaryBlue.copy(alpha = 0.1f),
                radius = size.minDimension / 2.5f,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        when (type) {
            IllustrationType.ASSIGN -> AssignIllustration()
            IllustrationType.VERIFY -> VerifyIllustration()
            IllustrationType.REPORTS -> ReportsIllustration()
        }
    }
}

@Composable
fun AssignIllustration() {
    // Simplified premium illustration for Work Assignment
    Box(contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.size(120.dp, 160.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(Modifier.padding(12.dp)) {
                Box(Modifier.size(40.dp, 8.dp).clip(RoundedCornerShape(4.dp)).background(GrayLight))
                Spacer(Modifier.height(8.dp))
                Box(Modifier.size(80.dp, 8.dp).clip(RoundedCornerShape(4.dp)).background(GrayLight.copy(alpha = 0.5f)))
                Spacer(Modifier.height(16.dp))
                repeat(3) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Box(Modifier.size(16.dp).clip(CircleShape).background(if (it == 0) PrimaryBlue else GrayLight))
                        Spacer(Modifier.width(8.dp))
                        Box(Modifier.size(60.dp, 6.dp).clip(RoundedCornerShape(3.dp)).background(GrayLight.copy(alpha = 0.3f)))
                    }
                }
            }
        }
        // Floating checkmark
        Surface(
            modifier = Modifier.size(48.dp).offset(x = 40.dp, y = 60.dp),
            shape = CircleShape,
            color = PrimaryBlue,
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("✓", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun VerifyIllustration() {
    // Simplified premium illustration for GPS Verification
    Box(contentAlignment = Alignment.Center) {
        // Map Grid
        Canvas(modifier = Modifier.size(160.dp)) {
            val step = size.width / 4
            for (i in 0..4) {
                drawLine(GrayLight, start = androidx.compose.ui.geometry.Offset(i * step, 0f), end = androidx.compose.ui.geometry.Offset(i * step, size.height), strokeWidth = 1.dp.toPx())
                drawLine(GrayLight, start = androidx.compose.ui.geometry.Offset(0f, i * step), end = androidx.compose.ui.geometry.Offset(size.width, i * step), strokeWidth = 1.dp.toPx())
            }
        }
        // GPS Pin
        Surface(
            modifier = Modifier.size(40.dp).offset(y = (-20).dp),
            shape = CircleShape,
            color = PrimaryBlue,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(Modifier.size(16.dp).clip(CircleShape).background(Color.White))
            }
        }
        // Radius
        Canvas(modifier = Modifier.size(100.dp)) {
            drawCircle(PrimaryBlue.copy(alpha = 0.2f), radius = size.width / 2)
            drawCircle(PrimaryBlue, radius = size.width / 2, style = Stroke(width = 2.dp.toPx()))
        }
    }
}

@Composable
fun ReportsIllustration() {
    // Simplified premium illustration for AI Reports
    Box(contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Waveform effect
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val heights = listOf(20, 40, 60, 30, 50, 20)
                    heights.forEach { h ->
                        Box(Modifier.size(6.dp, h.dp).clip(RoundedCornerShape(3.dp)).background(PrimaryBlue))
                    }
                }
            }
        }
        // Floating Mic icon
        Surface(
            modifier = Modifier.size(44.dp).offset(x = 50.dp, y = (-40).dp),
            shape = CircleShape,
            color = SecondaryIndigo,
            shadowElevation = 10.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(Modifier.size(12.dp, 20.dp).clip(RoundedCornerShape(6.dp)).background(Color.White))
            }
        }
    }
}

data class OnboardingPageData(
    val title: String,
    val description: String,
    val illustrationType: IllustrationType
)

enum class IllustrationType {
    ASSIGN, VERIFY, REPORTS
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun OnboardingScreenPreview() {
    FieldFlowTheme {
        OnboardingScreen(onFinish = {})
    }
}
