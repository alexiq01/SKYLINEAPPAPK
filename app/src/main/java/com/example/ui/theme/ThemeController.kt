package com.example.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Global Theme Controller for toggling between Light and Dark mode.
 * Persists user preference across app sessions.
 */
object ThemeController {
    private const val PREFS_NAME = "skyline_theme_prefs"
    private const val KEY_DARK_MODE = "is_dark_mode"

    var isDarkModeOverride by mutableStateOf<Boolean?>(null)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_DARK_MODE)) {
            isDarkModeOverride = prefs.getBoolean(KEY_DARK_MODE, false)
        }
    }

    fun toggle(context: Context, currentIsDark: Boolean) {
        val next = !currentIsDark
        isDarkModeOverride = next
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, next)
            .apply()
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        isDarkModeOverride = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
    }
}

/**
 * Convenience helper to determine if the app is currently in dark mode.
 */
@Composable
fun isAppInDarkTheme(): Boolean {
    return ThemeController.isDarkModeOverride ?: isSystemInDarkTheme()
}
