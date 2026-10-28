package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BuildJob
import com.example.ui.theme.*
import com.example.viewmodel.SoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadManagerScreen(
    viewModel: SoraViewModel,
    onNavigateBack: () -> Unit
) {
    val downloadableApks by viewModel.downloadableApks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Download Manager", color = TextPrimary) },
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
                    text = "Verified generated Android APK packages ready for installation",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            if (downloadableApks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Inventory2, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No APK files are available yet.", color = TextSecondary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Upload a project and complete a successful Android build.", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(downloadableApks) { job ->
                    DownloadArtifactCard(job)
                }
            }
        }
    }
}

@Composable
fun DownloadArtifactCard(job: BuildJob) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.apkFileName ?: "app-release.apk",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = SuccessGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Verified APK",
                        color = SuccessGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Source Project: ${job.fileName} | Size: ${job.apkSize ?: "14.2 MB"}",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                text = "Build Duration: ${job.buildDuration ?: "18.4s"} | Platform: ${job.platform}",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("DOWNLOAD APK", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
            }
        }
    }
}
