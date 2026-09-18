package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SkylineBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF004085),
    onPrimaryContainer = Color.White,
    secondary = SkylineCyan,
    onSecondary = Color.Black,
    tertiary = SkylineSuccess,
    background = SkylineDarkBg,
    onBackground = SkylineDarkTextPrimary,
    surface = SkylineDarkSurface,
    onSurface = SkylineDarkTextPrimary,
    surfaceVariant = SkylineCardDark,
    onSurfaceVariant = SkylineDarkTextSecondary,
    outline = SkylineDarkBorder,
    error = SkylineError
)

private val LightColorScheme = lightColorScheme(
    primary = SkylineBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5F0FF),
    onPrimaryContainer = SkylineBlueDark,
    secondary = SkylineCyan,
    onSecondary = Color.White,
    tertiary = SkylineSuccess,
    background = SkylineLightBg,
    onBackground = SkylineLightTextPrimary,
    surface = SkylineLightSurface,
    onSurface = SkylineLightTextPrimary,
    surfaceVariant = Color(0xFFE5E5EA),
    onSurfaceVariant = SkylineLightTextSecondary,
    outline = SkylineLightBorder,
    error = SkylineError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = ThemeController.isDarkModeOverride ?: isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

