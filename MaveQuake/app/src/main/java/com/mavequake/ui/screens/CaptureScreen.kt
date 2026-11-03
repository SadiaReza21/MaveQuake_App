package com.mavequake.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mavequake.ui.components.LanguageSwitch
import com.mavequake.ui.i18n.LocalStrings
import com.mavequake.util.copyUriToCacheFile
import com.mavequake.viewmodel.CapturePass
import com.mavequake.viewmodel.CaptureViewModel
import com.mavequake.viewmodel.SubmitState

private val capturePasses = CapturePass.values().toList()

@Composable
fun CaptureScreen(
    viewModel: CaptureViewModel,
    onBack: () -> Unit,
    onSubmitted: (assessmentId: String) -> Unit
) {
    val context = LocalContext.current
    val s = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()

    var activePass by remember { mutableStateOf<CapturePass?>(null) }
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val pass = activePass
        if (uri != null && pass != null) viewModel.onImagePicked(pass, uri)
    }

    LaunchedEffect(uiState.submitState) {
        val state = uiState.submitState
        if (state is SubmitState.Success) onSubmitted(state.assessmentId)
    }

    val total = capturePasses.size
    val done = uiState.images.size
    val progress by animateFloatAsState(done / total.toFloat(), label = "progress")
    val isLoading = uiState.submitState is SubmitState.Loading

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back) }
            Text(s.newAssessment, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            LanguageSwitch()
        }

        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)))
        Text(
            if (uiState.allCaptured) s.allPhotosReady else s.photosProgress(done, total),
            style = MaterialTheme.typography.labelMedium,
            color = if (uiState.allCaptured) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                OutlinedTextField(
                    value = uiState.label,
                    onValueChange = viewModel::onLabelChanged,
                    label = { Text(s.buildingLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = uiState.neighborhood,
                    onValueChange = viewModel::onNeighborhoodChanged,
                    label = { Text(s.neighborhood) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp)
                )
            }
            items(capturePasses) { pass ->
                PassCard(
                    pass = pass,
                    uri = uiState.images[pass],
                    number = pass.ordinal + 1,
                    onClick = {
                        activePass = pass
                        pickImage.launch("image/*")
                    }
                )
            }
        }

        AnimatedVisibility(uiState.submitState is SubmitState.Error) {
            val msg = (uiState.submitState as? SubmitState.Error)?.message ?: ""
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.submit { uri -> copyUriToCacheFile(context, uri, "${uri.lastPathSegment}.jpg") }
            },
            enabled = uiState.allCaptured && !isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
                Text(s.analyzing)
            } else {
                Text(s.runAssessment)
            }
        }
    }
}

@Composable
private fun PassCard(pass: CapturePass, uri: Uri?, number: Int, onClick: () -> Unit) {
    val s = LocalStrings.current
    val captured = uri != null
    val container by animateColorAsState(
        if (captured) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        label = "cardColor"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = container),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(MaterialTheme.shapes.medium).clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (uri != null) {
                    AsyncImage(
                        model = uri,
                        contentDescription = s.passTitle(pass),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxSize()) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(s.num(number), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(s.passTitle(pass), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    if (captured) {
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                }
                Text(s.passDesc(pass), style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onClick) { Text(if (captured) s.change else s.add) }
        }
    }
}