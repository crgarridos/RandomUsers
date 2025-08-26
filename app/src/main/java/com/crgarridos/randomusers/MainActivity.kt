package com.crgarridos.randomusers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crgarridos.randomusers.ui.AppDestinations
import com.crgarridos.randomusers.ui.compose.common.MainNavHost
import com.crgarridos.randomusers.ui.compose.theme.RandomUsersTheme
import com.crgarridos.randomusers.ui.compose.userdetail.UserDetailScreen
import com.crgarridos.randomusers.ui.compose.userlist.UserListScreen
import com.crgarridos.randomusers.ui.presentation.UserDetailViewModel
import com.crgarridos.randomusers.ui.presentation.UserListViewModel
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

                        UserListScreen(
                            uiState = uiState,
                            onUserClick = { userId ->
                                navController.navigate(
                                    AppDestinations.UserDetail.createRouteWithArgs(userId = userId)
                                )
                            },
                            onLoadMoreRequested = viewModel::loadMoreUsers,
                            onRefresh = viewModel::refresh,
                            onClearLoadMoreError = viewModel::clearLoadMoreErrorMessage,
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


}
