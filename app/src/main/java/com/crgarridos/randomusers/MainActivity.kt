package com.crgarridos.randomusers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crgarridos.randomusers.ui.compose.theme.RandomUsersTheme
import com.crgarridos.randomusers.ui.compose.userlist.UserListScreen
import com.crgarridos.randomusers.ui.presentation.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RandomUsersTheme {
                val viewModel: UserListViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                UserListScreen(
                    uiState = uiState,
                    onUserClick = { userId ->
                        println("User clicked: $userId. Implement navigation to detail screen.")
                        // Example: navController.navigate("user_detail/$userId")
                    },
                    onLoadMoreRequested = viewModel::loadMoreUsers,
                    onRefresh = viewModel::refresh,
                    onClearLoadMoreError = viewModel::clearLoadMoreErrorMessage
                )
            }
        }
    }
}
