package com.mavequake.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavequake.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import com.mavequake.repository.AssessmentRepository

enum class CapturePass { NADIR, OBLIQUE_60, OBLIQUE_45, FACADE }

sealed interface SubmitState {
    data object Idle : SubmitState
    data object Loading : SubmitState
    data class Success(val assessmentId: String) : SubmitState
    data class Error(val message: String) : SubmitState
}

data class CaptureUiState(
    val images: Map<CapturePass, Uri> = emptyMap(),
    val label: String = "",
    val neighborhood: String = "",
    val submitState: SubmitState = SubmitState.Idle
) {
    val allCaptured: Boolean get() = images.size == CapturePass.values().size
}

class CaptureViewModel(
    private val repository: AssessmentRepository = AppContainer.assessmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaptureUiState())
    val uiState: StateFlow<CaptureUiState> = _uiState

    fun onImagePicked(pass: CapturePass, uri: Uri) {
        _uiState.value = _uiState.value.copy(
            images = _uiState.value.images + (pass to uri)
        )
    }

    fun onLabelChanged(value: String) {
        _uiState.value = _uiState.value.copy(label = value)
    }

    fun onNeighborhoodChanged(value: String) {
        _uiState.value = _uiState.value.copy(neighborhood = value)
    }

    /** [resolveFile] converts a content:// Uri into a real File the repository/Retrofit can upload. */
    fun submit(resolveFile: (Uri) -> File) {
        val state = _uiState.value
        if (!state.allCaptured) return

        _uiState.value = state.copy(submitState = SubmitState.Loading)
        viewModelScope.launch {
            val result = repository.submitAssessment(
                nadir = resolveFile(state.images.getValue(CapturePass.NADIR)),
                oblique60 = resolveFile(state.images.getValue(CapturePass.OBLIQUE_60)),
                oblique45 = resolveFile(state.images.getValue(CapturePass.OBLIQUE_45)),
                facade = resolveFile(state.images.getValue(CapturePass.FACADE)),
                label = state.label,
                neighborhood = state.neighborhood
            )
            _uiState.value = _uiState.value.copy(
                submitState = result.fold(
                    onSuccess = { SubmitState.Success(it.id) },
                    onFailure = { SubmitState.Error(it.message ?: "Something went wrong") }
                )
            )
        }
    }
}
