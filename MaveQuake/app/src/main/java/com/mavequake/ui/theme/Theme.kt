package com.mavequake.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = QuakeBlue, onPrimary = Color.White, secondary = QuakeAmber,
    background = Color.White, surface = Color.White,
    surfaceVariant = Color(0xFFE6EDF9),
    primaryContainer = Color(0xFFD6E3FF), onPrimaryContainer = QuakeNavy,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4F86E8), onPrimary = Color.White, secondary = QuakeAmber,
    background = Color.Black, surface = Color(0xFF0E1117),
    surfaceVariant = Color(0xFF1B2230),
    primaryContainer = Color(0xFF15305F), onPrimaryContainer = Color(0xFFD6E3FF),
)
@Composable
fun MaveQuakeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
