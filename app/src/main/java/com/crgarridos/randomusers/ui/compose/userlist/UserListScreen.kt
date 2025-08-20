package com.crgarridos.randomusers.ui.compose.userlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.crgarridos.randomusers.R
import com.crgarridos.randomusers.ui.compose.common.ErrorScreen
import com.crgarridos.randomusers.ui.compose.common.FullScreenLoading
import com.crgarridos.randomusers.ui.compose.common.FullScreenStatusWithRetry
import com.crgarridos.randomusers.ui.compose.model.UiUser
import com.crgarridos.randomusers.ui.compose.theme.RandomUsersTheme

private const val ITEMS_COUNT_THRESHOLD = 5

sealed class UserListUiState {
    object Loading : UserListUiState()
    object Empty : UserListUiState()
    data class Success(
        val users: List<UiUser>,
        val canLoadMore: Boolean,
        val isLoadingMore: Boolean,
        val loadMoreErrorMessage: String? = null,
    ) : UserListUiState()

    data class Error(val message: String) : UserListUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    uiState: UserListUiState,
    onUserClick: (userId: String) -> Unit,
    onLoadMoreRequested: () -> Unit,
    onRetry: () -> Unit = {},
    onClearLoadMoreError: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    if (uiState is UserListUiState.Success && uiState.loadMoreErrorMessage != null) {
        val currentLoadMoreErrorMessage = uiState.loadMoreErrorMessage
        LaunchedEffect(currentLoadMoreErrorMessage) {
            snackbarHostState.showSnackbar(
                message = currentLoadMoreErrorMessage,
                duration = SnackbarDuration.Short
            )
            onClearLoadMoreError()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is UserListUiState.Loading -> FullScreenLoading()
            is UserListUiState.Error -> ErrorScreen(
                message = uiState.message,
                onRetry = onRetry,
                modifier = Modifier.padding(paddingValues)
            )

            is UserListUiState.Success -> ListScreenContent(
                uiState = uiState,
                onUserClick = onUserClick,
                onLoadMoreRequested = onLoadMoreRequested,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )

            UserListUiState.Empty -> EmptyScreen(
                onRetry = onRetry,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun ListScreenContent(
    modifier: Modifier = Modifier,
    uiState: UserListUiState.Success,
    onUserClick: (userId: String) -> Unit,
    onLoadMoreRequested: () -> Unit,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        itemsIndexed(uiState.users, key = { _, user -> user.id }) { _, user ->
            UserListItem(user = user, onClick = { onUserClick(user.id) })
        }

        if (uiState.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    val currentUsers = uiState.users
    LaunchedEffect(
        listState,
        currentUsers,
        uiState.canLoadMore,
        uiState.isLoadingMore
    ) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && !uiState.isLoadingMore && uiState.canLoadMore) {
                    val lastVisibleItemIndex = visibleItems.last().index
                    if (lastVisibleItemIndex >= currentUsers.size - ITEMS_COUNT_THRESHOLD) {
                        onLoadMoreRequested()
                    }
                }
            }
    }
}

@Composable
private fun UserListItem(
    user: UiUser,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.thumbnailUrl)
                    .crossfade(true)
                    // TODO placeholder
                    .build(),
                contentDescription = "${user.fullName} thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
private fun EmptyScreen(modifier: Modifier, onRetry: () -> Unit) {
    FullScreenStatusWithRetry(
        message = "No users available",
        icon = Icons.Filled.Face,
        color = MaterialTheme.colorScheme.error,
        onRetry = onRetry,
        modifier = modifier
    )
}
@Composable
@Preview(showBackground = true)
fun UserListScreenPreview_Success() {
    RandomUsersTheme {
        UserListScreen(
            uiState = UserListUiState.Success(
                previewUserList,
                canLoadMore = true,
                isLoadingMore = false
            ),
            onUserClick = {},
            onLoadMoreRequested = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun UserListScreenPreview_Loading() {
    RandomUsersTheme {
        UserListScreen(
            uiState = UserListUiState.Loading,
            onUserClick = {},
            onLoadMoreRequested = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun UserListScreenPreview_Error() {
    RandomUsersTheme {
        UserListScreen(
            uiState = UserListUiState.Error("Failed to load users."),
            onUserClick = {},
            onLoadMoreRequested = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun UserListItemPreview() {
    RandomUsersTheme {
        UserListItem(user = previewUserList.first(), onClick = {})
    }
}

internal val previewUserList = List(20) { index ->
    UiUser(
        id = "id_$index",
        title = if (index % 2 == 0) "Mr" else "Ms",
        firstName = "User",
        lastName = "$index",
        email = "user$index@example.com",
        phone = "555-010$index",
        thumbnailUrl = "https://randomuser.me/api/portraits/thumb/${if (index % 2 == 0) "men" else "women"}/$index.jpg",//TODO overkill?
        largePictureUrl = "https://randomuser.me/api/portraits/${if (index % 2 == 0) "men" else "women"}/$index.jpg",
        nationality = "US",
        city = "Anytown",
        gender = if (index % 2 == 0) "male" else "female"
    )
}

