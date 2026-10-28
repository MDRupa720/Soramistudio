package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

enum class PlatformTarget(val displayName: String, val isSupported: Boolean, val description: String) {
    ANDROID("Android", true, "Primary support: packages web or gradle projects into installable APKs."),
    OS("OS (Generic)", false, "Direct OS binary compilation requires custom toolchains."),
    KAIOS("KaiOS", false, "Smart feature phone runtime packaging available in enterprise tier."),
    JAVA("Java", false, "Pure Java bytecode compilation requires Maven/Ant build files."),
    WINDOWS("Windows", false, "Windows EXE bundling requires MinGW or MSVC worker environment.")
}

data class DiagnosticIssue(
    val filePath: String,
    val title: String,
    val severity: String, // "ERROR", "WARNING", "PASSED"
    val explanation: String,
    val suggestedCorrection: String
) : Serializable

data class ProjectAnalysisResult(
    val fileName: String,
    val fileSize: String,
    val detectedTechStack: String,
    val fileCount: Int,
    val totalExtractedSize: String,
    val issues: List<DiagnosticIssue>,
    val canBuildDirectly: Boolean
) : Serializable

enum class BuildJobState {
    QUEUED,
    ANALYZING,
    PREPARING,
    BUILDING,
    VERIFYING,
    SUCCESS,
    FAILED,
    CANCELLED
}

data class BuildJob(
    val jobId: String,
    val fileName: String,
    val platform: String,
    val state: BuildJobState,
    val progress: Float, // 0.0 to 1.0
    val currentStage: String,
    val logs: List<String>,
    val apkFileName: String?,
    val apkSize: String?,
    val buildDuration: String?,
    val issues: List<DiagnosticIssue>
) : Serializable

@Entity(tableName = "history_records")
data class HistoryRecordEntity(
    @PrimaryKey val id: String,
    val fileName: String,
    val timestamp: Long,
    val platform: String,
    val projectType: String,
    val status: String, // "SUCCESS" or "FAILED"
    val errorCount: Int,
    val warningCount: Int,
    val apkFileName: String?,
    val apkSize: String?,
    val repairPrompt: String?
)
