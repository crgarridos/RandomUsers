package com.crgarridos.randomusers.ui.compose.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


val BluePrimary = Color(0xFF1565C0)

val BlueSecondary = Color(0xFF42A5F5)
val BlueLight = Color(0xFFBBDEFB)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    tertiary = BlueLight,
    background = White,
    surface = LightGray,
    onPrimary = White,
    onSecondary = White,
    onTertiary = DarkGray,
    onBackground = DarkGray,
    onSurface = DarkGray,
    error = RedError,
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    tertiary = BlueLight,
    background = DeepDarkGray,
    surface = DarkGray,
    onPrimary = White,
    onSecondary = White,
    onTertiary = LightGray,
    onBackground = White,
    onSurface = White,
    error = RedError,
    onError = White
)

@Composable
fun RandomUsersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
