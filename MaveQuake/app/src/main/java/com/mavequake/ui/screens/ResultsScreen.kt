package com.mavequake.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mavequake.ui.components.LanguageSwitch
import com.mavequake.ui.i18n.LocalStrings
import com.mavequake.ui.i18n.vulnColor
import com.mavequake.viewmodel.ResultsUiState
import com.mavequake.viewmodel.ResultsViewModel
import kotlinx.coroutines.delay

@Composable
fun ResultsScreen(viewModel: ResultsViewModel, onBack: () -> Unit) {
    val s = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back) }
            Text(s.assessmentResult, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            LanguageSwitch()
        }

        when (val state = uiState) {
            is ResultsUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            is ResultsUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text(state.message) }
            is ResultsUiState.Loaded -> {
                val record = state.record
                val color = vulnColor(record.vulnerability)
                var showSoon by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Text(record.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(record.neighborhood, style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(16.dp))
                    Surface(color = color.copy(alpha = 0.12f), shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                            ConfidenceRing(confidence = record.confidence.toFloat(), color = color)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    s.vulnerabilityFull(record.vulnerability),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = color,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(s.modelConfidence, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(s.nextStep, style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(6.dp))
                            Text(s.recommendation(record.vulnerability), style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Text(s.indicators, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    if (record.indicators.isEmpty()) {
                        Text(s.noneFlagged, style = MaterialTheme.typography.bodyMedium)
                    } else {
                        record.indicators.forEachIndexed { index, indicator ->
                            Staggered(index) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 5.dp)) {
                                    Surface(color = color, shape = CircleShape, modifier = Modifier.size(8.dp)) {}
                                    Spacer(Modifier.width(10.dp))
                                    Text(s.indicator(indicator), style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    OutlinedButton(onClick = { showSoon = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(s.viewExplain)
                    }
                    AnimatedVisibility(showSoon) {
                        Text(
                            s.comingSoon,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        s.disclaimer,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfidenceRing(confidence: Float, color: androidx.compose.ui.graphics.Color) {
    val s = LocalStrings.current
    var start by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { start = true }
    val target = confidence.coerceIn(0f, 1f)
    val animated by animateFloatAsState(if (start) target else 0f, tween(1200), label = "confidence")

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { animated },
            modifier = Modifier.size(88.dp),
            strokeWidth = 8.dp,
            color = color,
            trackColor = color.copy(alpha = 0.18f)
        )
        Text(
            "${s.num((target * 100).toInt())}%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun Staggered(index: Int, content: @Composable () -> Unit) {
    var show by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 90L)
        show = true
    }
    AnimatedVisibility(show, enter = fadeIn(tween(350)) + slideInHorizontally(tween(350)) { it / 4 }) {
        content()
    }
}