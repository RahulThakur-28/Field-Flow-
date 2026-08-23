package com.rahul.fieldflow.features.reports.owner.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rahul.fieldflow.features.profile.components.ProfileAvatar
import com.rahul.fieldflow.features.reports.components.*
import com.rahul.fieldflow.features.reports.model.ReportStatus
import com.rahul.fieldflow.features.reports.owner.viewmodel.OwnerReportsViewModel
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerReportDetailsScreen(
    reportId: String,
    onBackClick: () -> Unit,
    viewModel: OwnerReportsViewModel = viewModel()
) {
    val uiState by viewModel.detailsState.collectAsState()

    LaunchedEffect(reportId) {
        viewModel.loadReportDetails(reportId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FIELD VISIT REPORT", style = MaterialTheme.typography.labelLarge, color = TextSecondary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.report?.status == ReportStatus.NEEDS_REVIEW) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Button(
                        onClick = { viewModel.markAsReviewed(reportId, onBackClick) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !uiState.isReviewing
                    ) {
                        if (uiState.isReviewing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Mark as Reviewed", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.report != null) {
            val report = uiState.report!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Employee Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileAvatar(
                        initials = report.employee.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase(),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = report.employee.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "${report.employee.role} • FieldFlow Inc.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Submitted",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = report.submittedTime,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                VoiceReportCard(duration = report.voiceDuration)

                Spacer(modifier = Modifier.height(24.dp))

                report.transcript?.let {
                    TranscriptCard(transcript = it)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (report.actionItems.isNotEmpty()) {
                    ActionItemsCard(items = report.actionItems)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                report.followUpDate?.let {
                    FollowUpCard(date = it)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (report.proofs.isNotEmpty()) {
                    ProofSubmittedCard(proofs = report.proofs)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerReportDetailsScreenPreview() {
    FieldFlowTheme {
        OwnerReportDetailsScreen(reportId = "1", onBackClick = {})
    }
}
