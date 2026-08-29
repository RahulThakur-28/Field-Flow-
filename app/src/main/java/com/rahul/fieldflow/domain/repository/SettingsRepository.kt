package com.rahul.fieldflow.domain.repository

import com.rahul.fieldflow.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)
}
