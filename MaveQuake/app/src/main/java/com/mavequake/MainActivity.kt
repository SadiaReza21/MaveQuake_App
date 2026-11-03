package com.mavequake

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mavequake.navigation.MaveQuakeNavGraph
import com.mavequake.ui.i18n.AppLanguage
import com.mavequake.ui.i18n.LanguageController
import com.mavequake.ui.i18n.LocalLanguage
import com.mavequake.ui.i18n.LocalStrings
import com.mavequake.ui.i18n.Strings
import com.mavequake.ui.theme.MaveQuakeTheme
import com.mavequake.ui.screens.AuthScreen
import com.mavequake.ui.screens.SimpleAuth
import androidx.compose.foundation.isSystemInDarkTheme
import com.mavequake.ui.theme.LocalTheme
import com.mavequake.ui.theme.ThemeController
import com.mavequake.ui.theme.ThemeMode
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("bhobonsathi_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getString("lang", null)
        // Default = Bangla. Change AppLanguage.BN to AppLanguage.EN to default to English.
        val initial = AppLanguage.values().firstOrNull { it.name == saved } ?: AppLanguage.BN
        val savedTheme = prefs.getString("theme", null)
        val initialTheme = ThemeMode.values().firstOrNull { it.name == savedTheme } ?: ThemeMode.SYSTEM
        SimpleAuth.init(this)

        setContent {
            var language by remember { mutableStateOf(initial) }
            val controller = remember(language) {
                LanguageController(language) { newLang ->
                    language = newLang
                    prefs.edit().putString("lang", newLang.name).apply()
                }
            }
            val strings = remember(language) { Strings(language == AppLanguage.BN) }

            var themeMode by remember { mutableStateOf(initialTheme) }
            val themeController = remember(themeMode) {
                ThemeController(themeMode) { m ->
                    themeMode = m
                    prefs.edit().putString("theme", m.name).apply()
                }
            }
            val dark = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            CompositionLocalProvider(
                LocalLanguage provides controller,
                LocalStrings provides strings,
                LocalTheme provides themeController
            ) {
                MaveQuakeTheme(darkTheme = dark) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        if (SimpleAuth.userName == null) AuthScreen() else MaveQuakeNavGraph()
                    }
                }
            }
        }
    }
}