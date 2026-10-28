package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DiagnosticIssue
import com.example.ui.theme.*
import com.example.viewmodel.SoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugReportScreen(
    viewModel: SoraViewModel,
    onNavigateBack: () -> Unit
) {
    val buildJob by viewModel.currentBuildJob.collectAsState()
    val issues = buildJob?.issues ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Build Bug Report", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Detailed diagnostic report for ${buildJob?.fileName ?: "Project"}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            if (issues.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No diagnostic issues recorded.", color = TextSecondary)
                    }
                }
            } else {
                items(issues) { issue ->
                    BugCard(issue)
                }
            }
        }
    }
}

@Composable
fun BugCard(issue: DiagnosticIssue) {
    val statusColor = when (issue.severity) {
        "ERROR" -> ErrorRed
        "WARNING" -> WarningAmber
        else -> SuccessGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = issue.filePath,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = AccentCyan
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = issue.severity,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = issue.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = issue.explanation,
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text("Suggested Correction:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentViolet)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(issue.suggestedCorrection, fontSize = 12.sp, color = TextPrimary)
                }
            }
        }
    }
}
