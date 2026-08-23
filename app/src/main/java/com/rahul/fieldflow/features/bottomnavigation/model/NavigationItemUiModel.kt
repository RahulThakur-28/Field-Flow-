package com.rahul.fieldflow.features.bottomnavigation.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.rahul.fieldflow.core.navigation.AppRoutes

data class NavigationItemUiModel(
    val title: String,
    val icon: ImageVector,
    val route: AppRoutes
)
