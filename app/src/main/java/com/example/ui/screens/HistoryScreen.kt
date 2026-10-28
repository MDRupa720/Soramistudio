package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HistoryRecordEntity
import com.example.ui.theme.*
import com.example.viewmodel.SoraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: SoraViewModel,
    onNavigateBack: () -> Unit
) {
    val historyList by viewModel.historyList.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Processing History", color = TextPrimary) },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Persistent records of previous ZIP processing sessions",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    if (historyList.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearAllHistory() }) {
                            Text("Clear All", color = ErrorRed, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (historyList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No history records yet.", color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                items(historyList) { record ->
                    HistoryCard(record, dateFormatter, viewModel)
                }
            }
        }
    }
}

@Composable
fun HistoryCard(record: HistoryRecordEntity, formatter: SimpleDateFormat, viewModel: SoraViewModel) {
    val isSuccess = record.status == "SUCCESS"
    val statusColor = if (isSuccess) SuccessGreen else ErrorRed

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.fileName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = record.status,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Platform: ${record.platform} | Type: ${record.projectType}",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                text = "Timestamp: ${formatter.format(Date(record.timestamp))}",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSuccess) "APK: ${record.apkFileName ?: "app.apk"} (${record.apkSize ?: "14 MB"})" else "Errors: ${record.errorCount}, Warnings: ${record.warningCount}",
                    fontSize = 11.sp,
                    color = if (isSuccess) SuccessGreen else WarningAmber
                )
                IconButton(
                    onClick = { viewModel.deleteHistoryRecord(record.id) },
                    modifier = Modifier.size(32.dp).testTag("delete_history_${record.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
