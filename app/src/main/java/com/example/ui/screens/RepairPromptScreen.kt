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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.SoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairPromptScreen(
    viewModel: SoraViewModel,
    onNavigateBack: () -> Unit
) {
    val repairPrompt by viewModel.currentRepairPrompt.collectAsState()
    val buildJob by viewModel.currentBuildJob.collectAsState()
    val context = LocalContext.current
    var editablePrompt by remember(repairPrompt) { mutableStateOf(repairPrompt) }
    var showCopiedToast by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Repair Prompt", color = TextPrimary) },
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
            Text(
                text = "Generated using Gemini API based on actual build logs and project diagnostics.",
                fontSize = 13.sp,
                color = TextSecondary
            )

            OutlinedTextField(
                value = editablePrompt,
                onValueChange = { editablePrompt = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .testTag("repair_prompt_text_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentViolet,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                )
            )

            if (showCopiedToast) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SuccessGreen.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Prompt copied to clipboard successfully!",
                        color = SuccessGreen,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("AI Repair Prompt", editablePrompt)
                        clipboard.setPrimaryClip(clip)
                        showCopiedToast = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("copy_prompt_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentViolet)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("COPY PROMPT", fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = {
                        buildJob?.let { viewModel.generateAiRepairPrompt(it) }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("regenerate_prompt_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("REGENERATE")
                }
            }
        }
    }
}
