package com.mavequake.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mavequake.ui.i18n.AppLanguage
import com.mavequake.ui.i18n.LocalLanguage

@Composable
fun LanguageSwitch(modifier: Modifier = Modifier) {
    val controller = LocalLanguage.current
    val haptic = LocalHapticFeedback.current
    val isBangla = controller.language == AppLanguage.BN

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        ) {
            Text(
                "EN",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (!isBangla) FontWeight.Bold else FontWeight.Normal
            )
            Switch(
                checked = isBangla,
                onCheckedChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    controller.onChange(if (it) AppLanguage.BN else AppLanguage.EN)
                },
                modifier = Modifier.padding(horizontal = 4.dp).scale(0.8f)
            )
            Text(
                "বাংলা",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isBangla) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}