package com.rahul.fieldflow.features.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.features.home.owner.screen.OwnerHomeScreen
import com.rahul.fieldflow.features.tasks.owner.screen.OwnerTasksScreen
import com.rahul.fieldflow.features.team.screen.OwnerTeamScreen
import com.rahul.fieldflow.features.reports.owner.screen.OwnerReportsScreen
import com.rahul.fieldflow.features.profile.owner.screen.OwnerProfileScreen
import com.rahul.fieldflow.features.home.employee.screen.EmployeeHomeScreen
import com.rahul.fieldflow.features.tasks.employee.screen.EmployeeTasksScreen
import com.rahul.fieldflow.features.reports.employee.screen.EmployeeReportsScreen
import com.rahul.fieldflow.features.profile.employee.screen.EmployeeProfileScreen
import kotlinx.coroutines.launch

@Composable
fun OwnerMainPagerScreen(
    navController: NavController,
    initialPage: Int = 0,
    taskFilter: String? = null
) {
    val items = BottomNavigationConfig.ownerItems
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(initialPage) {
        if (pagerState.currentPage != initialPage) {
            pagerState.scrollToPage(initialPage)
        }
    }

    Scaffold(
        bottomBar = {
            FieldFlowBottomNavigation(
                items = items,
                navController = navController,
                pagerState = pagerState,
                onPageSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(bottom = 0.dp),
            beyondViewportPageCount = 1
        ) { page ->
            when (page) {
                0 -> OwnerHomeScreen(navController, paddingValues = paddingValues)
                1 -> OwnerTasksScreen(
                    navController = navController,
                    onTaskClick = { taskId -> navController.navigate(AppRoutes.TaskDetails(taskId)) },
                    onCreateTaskClick = { navController.navigate(AppRoutes.CreateTask) },
                    paddingValues = paddingValues,
                    taskFilter = taskFilter
                )
                2 -> OwnerTeamScreen(
                    navController = navController,
                    onMemberClick = { employeeId -> navController.navigate(AppRoutes.EmployeeDetails(employeeId)) },
                    onNavigateToRequests = { navController.navigate(AppRoutes.EmployeeRequests) },
                    paddingValues = paddingValues
                )
                3 -> OwnerReportsScreen(
                    navController = navController,
                    onReportClick = { taskId -> navController.navigate(AppRoutes.TaskReport(taskId)) },
                    paddingValues = paddingValues
                )
                4 -> OwnerProfileScreen(navController, paddingValues = paddingValues)
            }
        }
    }
}

@Composable
fun EmployeeMainPagerScreen(
    navController: NavController,
    initialPage: Int = 0,
    taskFilter: String? = null
) {
    val items = BottomNavigationConfig.employeeItems
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(initialPage) {
        if (pagerState.currentPage != initialPage) {
            pagerState.scrollToPage(initialPage)
        }
    }

    Scaffold(
        bottomBar = {
            FieldFlowBottomNavigation(
                items = items,
                navController = navController,
                pagerState = pagerState,
                onPageSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(bottom = 0.dp),
            beyondViewportPageCount = 1
        ) { page ->
            when (page) {
                0 -> EmployeeHomeScreen(navController, paddingValues = paddingValues)
                1 -> EmployeeTasksScreen(
                    navController = navController,
                    onTaskClick = { taskId -> navController.navigate(AppRoutes.EmployeeTaskDetails(taskId)) },
                    paddingValues = paddingValues,
                    taskFilter = taskFilter
                )
                2 -> EmployeeReportsScreen(
                    navController = navController,
                    onReportClick = { taskId -> navController.navigate(AppRoutes.TaskReport(taskId)) },
                    paddingValues = paddingValues
                )
                3 -> EmployeeProfileScreen(navController, paddingValues = paddingValues)
            }
        }
    }
}
