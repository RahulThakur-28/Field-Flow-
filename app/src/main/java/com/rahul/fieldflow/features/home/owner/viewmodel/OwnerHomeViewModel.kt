package com.rahul.fieldflow.features.home.owner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.home.model.dummyOwnerHomeUiState
import com.rahul.fieldflow.features.home.owner.state.OwnerHomeUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OwnerHomeViewModel : ViewModel() {
    
    // In a real app, this would use a Repository/UseCase
    private val mockData = dummyOwnerHomeUiState()
    
    private val _uiState = MutableStateFlow(
        OwnerHomeUiState(
            isLoading = false,
            userName = mockData.userName,
            location = mockData.location,
            initials = "RT", // Extracted from mock or session later
            notificationCount = 2,
            stats = mockData.stats,
            liveVisits = mockData.liveVisits,
            teamStatus = mockData.teamStatus,
            recentActivity = mockData.recentActivity
        )
    )
    val uiState: StateFlow<OwnerHomeUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1500) // Simulate network delay
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
