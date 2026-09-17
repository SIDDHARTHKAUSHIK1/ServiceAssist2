package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val ServoraLightColorScheme = lightColorScheme(
    primary = ServoraCoral,
    onPrimary = Color.White,
    primaryContainer = ServoraPeach,
    onPrimaryContainer = ServoraCoralDark,
    secondary = ServoraHoney,
    onSecondary = ServoraCharcoal,
    secondaryContainer = ServoraPeachLight,
    onSecondaryContainer = ServoraCharcoal,
    tertiary = ServoraCharcoal,
    onTertiary = Color.White,
    background = ServoraCanvas,
    onBackground = ServoraCharcoal,
    surface = ServoraSurface,
    onSurface = ServoraCharcoal,
    surfaceVariant = ServoraSurfaceSubtle,
    onSurfaceVariant = ServoraSubtext,
    outline = ServoraBorder,
    outlineVariant = Color(0xFFDED8CE)
)

private val ServoraDarkColorScheme = darkColorScheme(
    primary = AssistGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = Color(0xFF10B981),
    onSecondary = Color.Black,
    background = Color(0xFF0F1713),
    onBackground = Color(0xFFE6F4EA),
    surface = Color(0xFF16231C),
    onSurface = Color(0xFFE6F4EA),
    surfaceVariant = Color(0xFF1F3228),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF2D4A3C)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ServoraDarkColorScheme else ServoraLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
