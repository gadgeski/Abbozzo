package com.gadgeski.abbozzo.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = NeonCyan,
    tertiary = HotPink,
    background = BlackBackground,
    surface = DarkSurface,
    onPrimary = WhiteHighContrast,
    onSecondary = BlackBackground,
    onTertiary = BlackBackground,
    onBackground = WhiteHighContrast,
    onSurface = WhiteHighContrast,
)

@Composable
fun AbbozzoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Always dark effectively
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Force Dark Scheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}