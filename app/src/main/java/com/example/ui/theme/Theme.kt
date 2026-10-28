package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SoraDarkColorScheme =
    darkColorScheme(
        primary = AccentBlue,
        secondary = AccentViolet,
        tertiary = AccentCyan,
        background = DarkBackground,
        surface = DarkSurface,
        surfaceVariant = DarkSurfaceVariant,
        onPrimary = TextPrimary,
        onSecondary = TextPrimary,
        onTertiary = TextPrimary,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
        onSurfaceVariant = TextSecondary,
        error = ErrorRed,
        onError = TextPrimary,
        outline = BorderColor
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SoraDarkColorScheme,
        typography = Typography,
        content = content
    )
}
