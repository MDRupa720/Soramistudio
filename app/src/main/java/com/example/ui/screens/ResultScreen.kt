package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BuildJobState
import com.example.ui.theme.*
import com.example.viewmodel.SoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: SoraViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToBugReport: () -> Unit,
    onNavigateToRepairPrompt: () -> Unit
) {
    val buildJob by viewModel.currentBuildJob.collectAsState()
    val isSuccess = buildJob?.state == BuildJobState.SUCCESS

    BackHandler {
        onNavigateToDashboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSuccess) "APK Build Successful" else "Build Failed", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(if (isSuccess) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (isSuccess) SuccessGreen else ErrorRed,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (isSuccess) "APK BUILD SUCCESSFUL" else "BUILD FAILED",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = if (isSuccess) SuccessGreen else ErrorRed,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSuccess) 
                    "Your APK artifact has been successfully compiled and verified." 
                else 
                    "The build worker encountered errors during compilation.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ResultRow("Project Name", buildJob?.fileName ?: "N/A")
                    ResultRow("Target Platform", buildJob?.platform ?: "Android")
                    if (isSuccess) {
                        ResultRow("APK Filename", buildJob?.apkFileName ?: "app-release.apk")
                        ResultRow("Artifact Size", buildJob?.apkSize ?: "14.2 MB")
                        ResultRow("Build Duration", buildJob?.buildDuration ?: "18.4s")
                    } else {
                        ResultRow("Failure Stage", buildJob?.currentStage ?: "Compilation")
                        ResultRow("Issues Detected", "${buildJob?.issues?.size ?: 0} potential items")
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isSuccess) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("download_apk_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DOWNLOAD APK", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = {
                            buildJob?.let { viewModel.generateAiRepairPrompt(it) }
                            onNavigateToRepairPrompt()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("ai_repair_prompt_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentViolet)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GENERATE AI REPAIR PROMPT", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                OutlinedButton(
                    onClick = onNavigateToBugReport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("view_report_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Icon(Icons.Default.BugReport, contentDescription = null, tint = AccentCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Build Bug Report")
                }

                TextButton(
                    onClick = onNavigateToDashboard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK (Return to Dashboard)", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
