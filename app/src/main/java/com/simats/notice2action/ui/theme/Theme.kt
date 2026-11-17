package com.simats.notice2action.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3B82F6),
    secondary = Color(0xFF60A5FA),
    tertiary = Color(0xFF818CF8),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color.White,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    secondary = Color(0xFF4F46E5),
    tertiary = Color(0xFF4338CA),
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

object AppThemeManager {
    var selectedAppearance by mutableStateOf("Light")

    val isDark: Boolean
        @Composable
        get() = when (selectedAppearance) {
            "Dark" -> true
            "Light" -> false
            else -> isSystemInDarkTheme()
        }

    val backgroundColor: Color
        @Composable
        get() = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)

    val cardColor: Color
        @Composable
        get() = if (isDark) Color(0xFF1E293B) else Color.White

    val textColor: Color
        @Composable
        get() = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)

    val subTextColor: Color
        @Composable
        get() = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
}

@Composable
fun Notice2actionTheme(
    content: @Composable () -> Unit
) {
    val isDark = AppThemeManager.isDark
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
