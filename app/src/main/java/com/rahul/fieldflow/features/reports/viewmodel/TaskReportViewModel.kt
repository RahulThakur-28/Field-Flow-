package com.rahul.fieldflow.features.reports.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.TaskReportContext
import com.rahul.fieldflow.domain.usecase.recording.GetSignedUrlUseCase
import com.rahul.fieldflow.domain.usecase.reports.GetFullTaskReportUseCase
import com.rahul.fieldflow.domain.usecase.reports.TriggerReportGenerationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskReportViewModel @Inject constructor(
    private val getFullTaskReportUseCase: GetFullTaskReportUseCase,
    private val triggerReportGenerationUseCase: TriggerReportGenerationUseCase,
    private val getSignedUrlUseCase: GetSignedUrlUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskReportUiState())
    val uiState: StateFlow<TaskReportUiState> = _uiState.asStateFlow()

    fun loadReport(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getFullTaskReportUseCase(taskId)
                .onSuccess { context ->
                    _uiState.update { 
                        it.copy(
                            reportContext = context, 
                            isLoading = false 
                        ) 
                    }
                    if (context.aiReport == null) {
                        // Optionally trigger generation if missing and task is completed
                        if (context.task.status.name == "COMPLETED") {
                            triggerReportGeneration(taskId)
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun triggerReportGeneration(taskId: String) {
        viewModelScope.launch {
            triggerReportGenerationUseCase(taskId)
            // Polling or waiting could be implemented here, but for now we rely on manual refresh or re-fetch
        }
    }

    suspend fun getAudioUrl(storagePath: String): String? {
        return getSignedUrlUseCase(storagePath).getOrNull()
    }
}

data class TaskReportUiState(
    val reportContext: TaskReportContext? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
