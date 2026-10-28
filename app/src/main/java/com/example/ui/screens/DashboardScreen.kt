package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlatformTarget
import com.example.ui.theme.*
import com.example.viewmodel.SoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: SoraViewModel,
    onNavigateToProcessing: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToSetupGuide: () -> Unit
) {
    val context = LocalContext.current
    val selectedFileName by viewModel.selectedFileName.collectAsState()
    val selectedFileSize by viewModel.selectedFileSize.collectAsState()
    val selectedPlatform by viewModel.selectedPlatform.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()
    val downloadableApks by viewModel.downloadableApks.collectAsState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Query file name and size from content resolver
            var name = "project.zip"
            var size = 0L
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex >= 0) name = cursor.getString(nameIndex) ?: "project.zip"
                    if (sizeIndex >= 0) size = cursor.getLong(sizeIndex)
                }
            }
            viewModel.processZipUri(context, it, name, size)
        }
    }

    val canProcess = selectedFileName != null && (analysisResult?.canBuildDirectly == true) && selectedPlatform.isSupported

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(AccentBlue, AccentViolet))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Sora mi", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                            Text("ZIP2APK Studio", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToDownloads, modifier = Modifier.testTag("download_button")) {
                        BadgedBox(
                            badge = {
                                if (downloadableApks.isNotEmpty()) {
                                    Badge { Text("${downloadableApks.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Downloads", tint = TextPrimary)
                        }
                    }
                    IconButton(onClick = onNavigateToHistory, modifier = Modifier.testTag("history_button")) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = TextPrimary)
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "From ZIP to APK — Made Easy",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Upload your project ZIP archive from your phone, inspect stack safety, and build real Android APK packages.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onNavigateToSetupGuide,
                            modifier = Modifier.testTag("setup_guide_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue)
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Build Worker & GitHub Actions Guide")
                        }
                    }
                }
            }

            // ZIP Upload Card (NO Sample Projects - user must pick own ZIP)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedFileName != null) SuccessGreen else BorderColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FolderZip, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Choose Your ZIP File", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Text("Select a project ZIP archive from your device storage", fontSize = 12.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedFileName == null) {
                            Button(
                                onClick = {
                                    filePickerLauncher.launch(arrayOf("application/zip", "application/x-zip-compressed", "application/octet-stream", "*/*"))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("choose_zip_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("CHOOSE ZIP FILE FROM DEVICE", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (analysisResult?.canBuildDirectly == true) SuccessGreen else ErrorRed)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(selectedFileName ?: "", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                                Text("Size: $selectedFileSize | Stack: ${analysisResult?.detectedTechStack ?: "Analyzing..."}", color = TextSecondary, fontSize = 11.sp)
                                            }
                                        }
                                        IconButton(
                                            onClick = { viewModel.processZipUri(context, Uri.EMPTY, "", 0) },
                                            modifier = Modifier.testTag("remove_file_button")
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = ErrorRed)
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        filePickerLauncher.launch(arrayOf("application/zip", "application/x-zip-compressed", "application/octet-stream", "*/*"))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Choose Different ZIP File")
                                }
                            }
                        }
                    }
                }
            }

            // Project Analysis Inspection Summary Card
            if (analysisResult != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (analysisResult?.canBuildDirectly == true) SuccessGreen else ErrorRed)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Project Inspection Results", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            Text("Files examined: ${analysisResult?.fileCount} | Uncompressed size: ${analysisResult?.totalExtractedSize}", fontSize = 12.sp, color = TextSecondary)
                            Text("Detected Stack: ${analysisResult?.detectedTechStack}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AccentCyan)
                            
                            if (analysisResult?.canBuildDirectly == false) {
                                Text("Warning: Project structure cannot directly produce an APK without build configuration adjustments.", fontSize = 12.sp, color = ErrorRed)
                            }
                        }
                    }
                }
            }

            // Platform Selection Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Choose Target Platform", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Android is the primary supported target for APK generation", fontSize = 12.sp, color = TextSecondary)
                        
                        Spacer(modifier = Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            PlatformTarget.entries.forEach { platform ->
                                val isSelected = selectedPlatform == platform
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) DarkSurfaceVariant else DarkBackground)
                                        .border(1.dp, if (isSelected) AccentBlue else BorderColor, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.setPlatform(platform) }
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(platform.displayName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                                                if (platform.isSupported) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = SuccessGreen.copy(alpha = 0.2f)
                                                    ) {
                                                        Text("Supported", color = SuccessGreen, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(platform.description, fontSize = 12.sp, color = TextSecondary)
                                        }
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { viewModel.setPlatform(platform) },
                                            colors = RadioButtonDefaults.colors(selectedColor = AccentBlue)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Process File Button
            item {
                Button(
                    onClick = {
                        viewModel.startBuildProcess()
                        onNavigateToProcessing()
                    },
                    enabled = canProcess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("process_file_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        disabledContainerColor = DarkSurfaceVariant
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PROCESS FILE & BUILD APK", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
