package com.mavequake.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavequake.data.BuildingRecord
import com.mavequake.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.mavequake.repository.AssessmentRepository

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Loaded(val records: List<BuildingRecord>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

class HistoryViewModel(
    private val repository: AssessmentRepository = AppContainer.assessmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState

    init { refresh() }

    fun refresh() {
        _uiState.value = HistoryUiState.Loading
        viewModelScope.launch {
            val result = repository.getHistory()
            _uiState.value = result.fold(
                onSuccess = { HistoryUiState.Loaded(it) },
                onFailure = { HistoryUiState.Error(it.message ?: "Could not load history") }
            )
        }
    }
}
