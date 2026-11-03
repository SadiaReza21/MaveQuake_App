package com.mavequake.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.mavequake.ui.i18n.LocalStrings
import com.mavequake.ui.theme.LocalTheme
import com.mavequake.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSwitch() {
    val c = LocalTheme.current
    val bn = LocalStrings.current.isBangla
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            ThemeMode.LIGHT to (if (bn) "লাইট" else "Light"),
            ThemeMode.DARK to (if (bn) "ডার্ক" else "Dark"),
            ThemeMode.SYSTEM to (if (bn) "সিস্টেম" else "System")
        ).forEach { (mode, label) ->
            FilterChip(selected = c.mode == mode, onClick = { c.onChange(mode) }, label = { Text(label) })
        }
    }
}