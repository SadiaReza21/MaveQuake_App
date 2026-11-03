package com.mavequake.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mavequake.data.BuildingRecord
import com.mavequake.data.VulnerabilityClass
import com.mavequake.ui.components.LanguageSwitch
import com.mavequake.ui.i18n.LocalStrings
import com.mavequake.ui.i18n.vulnColor
import com.mavequake.viewmodel.HistoryUiState
import com.mavequake.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBack: () -> Unit,
    onOpenAssessment: (id: String) -> Unit
) {
    val s = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf<VulnerabilityClass?>(null) }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back) }
            Text(s.pastAssessments, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            IconButton(onClick = { viewModel.refresh() }) { Icon(Icons.Filled.Refresh, contentDescription = s.refresh) }
            LanguageSwitch()
        }

        when (val state = uiState) {
            is HistoryUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

            is HistoryUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message)
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { viewModel.refresh() }) { Text(s.retry) }
                }
            }

            is HistoryUiState.Loaded -> {
                if (state.records.isEmpty()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text(s.noAssessments) }
                } else {
                    val filtered = state.records.filter { r ->
                        (filter == null || r.vulnerability == filter) &&
                                (query.isBlank() || r.label.contains(query, true) || r.neighborhood.contains(query, true))
                    }

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text(s.searchHint) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Close, contentDescription = null) }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filter == null,
                            onClick = { filter = null },
                            label = { Text("${s.all} (${s.num(state.records.size)})") }
                        )
                        VulnerabilityClass.values().forEach { v ->
                            val count = state.records.count { it.vulnerability == v }
                            FilterChip(
                                selected = filter == v,
                                onClick = { filter = if (filter == v) null else v },
                                label = { Text("${s.vulnerability(v)} (${s.num(count)})") }
                            )
                        }
                    }

                    if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) { Text(s.noMatches) }
                    } else {
                        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                            items(filtered, key = { it.id }) { record ->
                                HistoryRow(record) { onOpenAssessment(record.id) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(record: BuildingRecord, onClick: () -> Unit) {
    val s = LocalStrings.current
    val color = vulnColor(record.vulnerability)
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(MaterialTheme.shapes.medium).clickable(onClick = onClick)
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            leadingContent = { Surface(color = color, shape = CircleShape, modifier = Modifier.size(14.dp)) {} },
            headlineContent = { Text(record.label) },
            supportingContent = { Text(record.neighborhood) },
            trailingContent = {
                Surface(color = color.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) {
                    Text(
                        s.vulnerability(record.vulnerability),
                        color = color,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        )
    }
}