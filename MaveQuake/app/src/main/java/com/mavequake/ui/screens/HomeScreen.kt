package com.mavequake.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavequake.ui.components.LanguageSwitch
import com.mavequake.ui.components.ThemeSwitch
import com.mavequake.ui.i18n.LocalStrings

@Composable
fun HomeScreen(
    onStartAssessment: () -> Unit,
    onViewHistory: () -> Unit
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val emblemScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "emblem"
    )
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
            MaterialTheme.colorScheme.background
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(gradient)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp)) {

            // Top bar: greeting, language switch, logout
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    (if (s.isBangla) "হ্যালো, " else "Hello, ") + (SimpleAuth.userName ?: ""),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                LanguageSwitch()
                TextButton(onClick = { SimpleAuth.logout(context) }) {
                    Text(if (s.isBangla) "লগআউট" else "Log out")
                }
            }

            // Light / Dark / System switch
            Spacer(Modifier.height(8.dp))
            ThemeSwitch()

            // Center content
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Placeholder emblem: replace with your real logo later.
                Surface(
                    modifier = Modifier.size(96.dp).scale(emblemScale),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "ভ",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                AnimatedVisibility(
                    visible,
                    enter = fadeIn(tween(700)) + slideInVertically(tween(700)) { it / 4 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            s.appName,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            s.fullName,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            s.slogan,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            s.tagline,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                // 3-step "how it works" cards
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(s.stepCapture, s.stepAnalyze, s.stepReview).forEachIndexed { i, title ->
                        Card(modifier = Modifier.weight(1f)) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    s.num(i + 1),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(title, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }

            Button(onClick = onStartAssessment, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text(s.newAssessment)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onViewHistory, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text(s.viewHistory)
            }
        }
    }
}