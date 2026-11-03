package com.mavequake.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavequake.data.BuildingRecord
import com.mavequake.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.mavequake.repository.AssessmentRepository
sealed interface ResultsUiState {
    data object Loading : ResultsUiState
    data class Loaded(val record: BuildingRecord) : ResultsUiState
    data class Error(val message: String) : ResultsUiState
}

class ResultsViewModel(
    private val assessmentId: String,
    private val repository: AssessmentRepository = AppContainer.assessmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val uiState: StateFlow<ResultsUiState> = _uiState

    init {
        viewModelScope.launch {
            val result = repository.getAssessment(assessmentId)
            _uiState.value = result.fold(
                onSuccess = { ResultsUiState.Loaded(it) },
                onFailure = { ResultsUiState.Error(it.message ?: "Could not load result") }
            )
        }
    }
}
