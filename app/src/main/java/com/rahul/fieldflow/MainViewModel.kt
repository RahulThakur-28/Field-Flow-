package com.rahul.fieldflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.AppTheme
import com.rahul.fieldflow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val theme: StateFlow<AppTheme> = settingsRepository.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )
}
