package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SoraViewModel

class MainActivity : ComponentActivity() {
    private val soraViewModel: SoraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "dashboard") {
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = soraViewModel,
                                onNavigateToProcessing = { navController.navigate("processing") },
                                onNavigateToHistory = { navController.navigate("history") },
                                onNavigateToDownloads = { navController.navigate("downloads") },
                                onNavigateToSetupGuide = { navController.navigate("setup_guide") }
                            )
                        }
                        composable("processing") {
                            ProcessingScreen(
                                viewModel = soraViewModel,
                                onNavigateToResult = { navController.navigate("result") }
                            )
                        }
                        composable("result") {
                            ResultScreen(
                                viewModel = soraViewModel,
                                onNavigateToDashboard = { navController.popBackStack("dashboard", inclusive = false) },
                                onNavigateToBugReport = { navController.navigate("bug_report") },
                                onNavigateToRepairPrompt = { navController.navigate("repair_prompt") }
                            )
                        }
                        composable("bug_report") {
                            BugReportScreen(
                                viewModel = soraViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("repair_prompt") {
                            RepairPromptScreen(
                                viewModel = soraViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("history") {
                            HistoryScreen(
                                viewModel = soraViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("downloads") {
                            DownloadManagerScreen(
                                viewModel = soraViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("setup_guide") {
                            SetupGuideDialog(
                                viewModel = soraViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
