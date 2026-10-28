package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BuildJobState
import com.example.ui.theme.*
import com.example.viewmodel.SoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessingScreen(
    viewModel: SoraViewModel,
    onNavigateToResult: () -> Unit
) {
    val buildJob by viewModel.currentBuildJob.collectAsState()

    BackHandler(enabled = true) {
        // Do nothing during processing
    }

    LaunchedEffect(buildJob?.state) {
        if (buildJob?.state == BuildJobState.SUCCESS || buildJob?.state == BuildJobState.FAILED) {
            onNavigateToResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Processing & Build", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Target: ${buildJob?.platform ?: "Android"}", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(buildJob?.fileName ?: "Project.zip", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                        }
                        CircularProgressIndicator(
                            progress = { buildJob?.progress ?: 0.1f },
                            modifier = Modifier.size(40.dp),
                            color = AccentBlue,
                            trackColor = DarkSurfaceVariant,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { buildJob?.progress ?: 0.1f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AccentBlue,
                        trackColor = DarkSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = buildJob?.currentStage ?: "Initializing...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AccentCyan
                        )
                        Text(
                            text = "${((buildJob?.progress ?: 0.1f) * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = AccentViolet, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Build Worker Logs", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBackground, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(buildJob?.logs ?: emptyList()) { log ->
                                Text(
                                    text = log,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = if (log.contains("ERROR")) ErrorRed else if (log.contains("SUCCESS")) SuccessGreen else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
