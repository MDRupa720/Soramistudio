package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiHelper {
    companion object {
        private const val TAG = "GeminiHelper"

        suspend fun generateRepairSuggestion(
            projectName: String,
            projectType: String,
            targetPlatform: String,
            errors: List<DiagnosticIssue>
        ): String = withContext(Dispatchers.IO) {
            try {
                // Return structured professional AI repair prompt
                buildFallbackRepairPrompt(projectName, projectType, targetPlatform, errors)
            } catch (e: Exception) {
                Log.e(TAG, "Error generating repair prompt", e)
                buildFallbackRepairPrompt(projectName, projectType, targetPlatform, errors)
            }
        }

        private fun buildFallbackRepairPrompt(
            projectName: String,
            projectType: String,
            targetPlatform: String,
            errors: List<DiagnosticIssue>
        ): String {
            val errorBlock = errors.joinToString("\n") { "- [${it.severity}] ${it.filePath}: ${it.title} -> ${it.suggestedCorrection}" }
            return """
                Please inspect and repair the project contained in $projectName.
                Target platform: $targetPlatform
                Project type: $projectType

                Actual build errors:
                $errorBlock

                Please investigate the root cause of each reported error. Inspect project configuration files (package.json, build.gradle, AndroidManifest.xml) before proposing changes.
                Make the smallest safe changes needed to resolve problems. Preserve unrelated working features and existing project structure.
                Verify corrected configuration and Android build process.
            """.trimIndent()
        }
    }
}
