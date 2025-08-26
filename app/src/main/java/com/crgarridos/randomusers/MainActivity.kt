package com.crgarridos.randomusers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crgarridos.randomusers.ui.navigation.AppDestinations
import com.crgarridos.randomusers.ui.compose.common.MainNavHost
import com.crgarridos.randomusers.ui.compose.theme.RandomUsersTheme
import com.crgarridos.randomusers.ui.compose.userdetail.UserDetailScreen
import com.crgarridos.randomusers.ui.compose.userlist.UserListScreen
import com.crgarridos.randomusers.ui.presentation.UserDetailViewModel
import com.crgarridos.randomusers.ui.presentation.UserListViewModel
import com.crgarridos.randomusers.ui.presentation.UserListViewModel.UserListUiEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RandomUsersTheme {
                val navController = rememberNavController()

                MainNavHost(
                    navController = navController,
                    startDestination = AppDestinations.UserList.ROUTE,
                ) {
                    composable(
                        route = AppDestinations.UserList.ROUTE,
                    ) {
                        val viewModel: UserListViewModel = hiltViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                        val uiEvent by viewModel.uiEvent.collectAsStateWithLifecycle(null)

                        LaunchedEffect(uiEvent) {
                            navController.handleNavigationIfNeeded(uiEvent)
                        }

                        UserListScreen(
                            uiState = uiState,
                            uiEvent = uiEvent,
                            callbacks = viewModel,
                        )
                    }
                    composable(
                        route = AppDestinations.UserDetail.ROUTE,
                        arguments = AppDestinations.UserDetail.navArguments(),
                    ) { backStackEntry ->
                        val viewModel: UserDetailViewModel = hiltViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        UserDetailScreen(
                            uiState = uiState,
                            onNavigateBack = { navController.popBackStack() },
                            onRetry = viewModel::retry
                        )
                    }
                }
            }
        }
    }

    private fun NavHostController.handleNavigationIfNeeded(uiEvent: UserListUiEvent?) {
        if (uiEvent is UserListUiEvent.NavigateToUserDetail) {
            navigate(
                AppDestinations.UserDetail.createRouteWithArgs(userId = uiEvent.userId)
            )
        }
    }


}
