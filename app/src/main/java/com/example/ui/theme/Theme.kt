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
    primary = AssistPurple,
    onPrimary = Color.White,
    primaryContainer = AssistLavender,
    onPrimaryContainer = AssistPurpleDark,
    secondary = Color(0xFF9333EA),
    onSecondary = Color.White,
    secondaryContainer = AssistLavenderLight,
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
    outlineVariant = Color(0xFFE4DFEC)
)

private val ServoraDarkColorScheme = darkColorScheme(
    primary = Color(0xFFA78BFA),
    onPrimary = Color(0xFF2E1065),
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = Color(0xFFDDD6FE),
    secondary = Color(0xFFC084FC),
    onSecondary = Color.Black,
    background = Color(0xFF0F0D17),
    onBackground = Color(0xFFF3F0F9),
    surface = Color(0xFF181524),
    onSurface = Color(0xFFF3F0F9),
    surfaceVariant = Color(0xFF242033),
    onSurfaceVariant = Color(0xFFA39DB2),
    outline = Color(0xFF38324C)
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
