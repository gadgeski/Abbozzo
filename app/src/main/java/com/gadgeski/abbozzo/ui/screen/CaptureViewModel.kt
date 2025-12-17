package com.gadgeski.abbozzo.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gadgeski.abbozzo.data.LogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val repository: LogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CaptureUiState>(CaptureUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun saveContent(content: String, tag: String = "Auto-Capture") {
        viewModelScope.launch {
            _uiState.value = CaptureUiState.Saving
            try {
                repository.addLog(content, tag)
                _uiState.value = CaptureUiState.Success
            } catch (e: Exception) {
                _uiState.value = CaptureUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _uiState.value = CaptureUiState.Idle
    }
}

sealed interface CaptureUiState {
    data object Idle : CaptureUiState
    data object Saving : CaptureUiState
    data object Success : CaptureUiState
    data class Error(val message: String) : CaptureUiState
}
