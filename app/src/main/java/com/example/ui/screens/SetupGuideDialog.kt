package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.SoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupGuideDialog(
    viewModel: SoraViewModel,
    onNavigateBack: () -> Unit
) {
    val workflow by viewModel.githubActionsWorkflow.collectAsState()
    val context = LocalContext.current
    var showCopiedToast by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Build Worker & GitHub Actions Guide", color = TextPrimary) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Building APKs on Android via GitHub Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    }

                    Text(
                        text = "Because compiling Android APK binaries requires heavy build tools (JDK 17, Android SDK, Gradle), mobile phones cannot compile APKs locally without cloud assistance. The most reliable way for an Android phone user is using **GitHub Actions**.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("1. Step-by-Step Instructions for Phone Users", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentCyan)
                    Text("• Push your project repository to GitHub from your phone or AI Studio.\n• Create a file at `.github/workflows/android-build.yml` in your repo.\n• Paste the workflow template below.\n• GitHub will automatically run the build and publish the debug APK as a downloadable artifact.", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("2. GitHub Actions Workflow Template", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentCyan)
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBackground, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = workflow.ifEmpty { "Upload a project ZIP in Dashboard to generate a workflow template." },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextPrimary
                        )
                    }

                    if (showCopiedToast) {
                        Text("Workflow copied to clipboard successfully!", color = SuccessGreen, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("GitHub Actions Workflow", workflow)
                            clipboard.setPrimaryClip(clip)
                            showCopiedToast = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .testTag("copy_workflow_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentViolet)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("COPY WORKFLOW YAML", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
