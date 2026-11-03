package com.mavequake.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

enum class ThemeMode { LIGHT, DARK, SYSTEM }

class ThemeController(val mode: ThemeMode, val onChange: (ThemeMode) -> Unit)

val LocalTheme = staticCompositionLocalOf { ThemeController(ThemeMode.SYSTEM) {} }