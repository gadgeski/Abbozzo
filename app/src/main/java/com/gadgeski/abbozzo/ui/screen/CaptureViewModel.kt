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
    private val repository: LogRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val logId: Long = savedStateHandle.get<Long>("logId") ?: -1L
    private val _uiState = MutableStateFlow<CaptureUiState>(CaptureUiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        if (logId != -1L) {
            loadLog(logId)
        }
    }

    private fun loadLog(id: Long) {
        viewModelScope.launch {
            val log = repository.getLogById(id)
            if (log != null) {
                // Initialize state with existing content
                // We need a way to pass this to UI.
                // Let's modify Idle state to carry initial content or use a separate state
                _uiState.value = CaptureUiState.Editing(log.content)
            }
        }
    }

    fun saveContent(content: String, tag: String = "Auto-Capture") {
        viewModelScope.launch {
            _uiState.value = CaptureUiState.Saving
            try {
                // If editing (logId != -1), use that ID. Otherwise 0 (new)
                val targetId = if (logId != -1L) logId else 0L
                repository.addLog(content, tag, targetId)
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
    data class Editing(val initialContent: String) : CaptureUiState
    data object Saving : CaptureUiState
    data object Success : CaptureUiState
    data class Error(val message: String) : CaptureUiState
}
