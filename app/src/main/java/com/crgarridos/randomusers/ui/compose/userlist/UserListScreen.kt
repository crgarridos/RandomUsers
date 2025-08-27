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
import androidx.compose.material3.Button
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import com.crgarridos.randomusers.ui.compose.userlist.UserListScreenTestTags.TEST_TAG_LOAD_MORE_BUTTON
import com.crgarridos.randomusers.ui.compose.userlist.UserListScreenTestTags.buildUserItemTestTag
import com.crgarridos.randomusers.ui.presentation.UserListViewModel.UserListUiEvent
import org.jetbrains.annotations.VisibleForTesting

private const val ITEMS_COUNT_THRESHOLD = 5

@VisibleForTesting
internal object UserListScreenTestTags {
    const val TEST_TAG_LIST_SCREEN_CONTENT = "UserListScreen_Content"
    const val TEST_TAG_SNACKBAR = "UserListScreen_Snackbar"
    const val TEST_TAG_LOAD_MORE_BUTTON = "UserListScreen_LoadMoreButton"

    fun buildUserItemTestTag(index: Int) = "UserListScreen_Item$index"
}

sealed class UserListUiState {
    object Loading : UserListUiState()
    object Empty : UserListUiState()
    data class Success(
        val users: List<UiUser>,
        val canLoadMore: Boolean,
        val isLoadingMore: Boolean,
        val loadMoreErrorMessage: String? = null,
        val isRefreshing: Boolean = false,
    ) : UserListUiState()

    data class Error(val message: String) : UserListUiState()
}

interface UserListUiCallbacks {
    fun onUserClicked(userId: String)
    fun onLoadMoreUsersRequested()
    fun onRefresh()
    fun onRetry()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    uiState: UserListUiState,
    uiEvent: UserListUiEvent? = null,
    callbacks: UserListUiCallbacks = EmptyUserListUiCallbacks,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiEvent) {
        if (uiEvent is UserListUiEvent.ShowSnackbar) {
            snackbarHostState.showSnackbar(
                message = uiEvent.message,
                duration = SnackbarDuration.Short,
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.testTag(UserListScreenTestTags.TEST_TAG_SNACKBAR)
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
                onRetry = callbacks::onRetry,
                modifier = Modifier.padding(paddingValues)
            )

            is UserListUiState.Success -> ListScreenContent(
                uiState = uiState,
                onUserClick = callbacks::onUserClicked,
                onLoadMoreRequested = callbacks::onLoadMoreUsersRequested,
                onRefresh = callbacks::onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )

            UserListUiState.Empty -> EmptyScreen(
                onRetry = callbacks::onRetry,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScreenContent(
    modifier: Modifier = Modifier,
    uiState: UserListUiState.Success,
    onUserClick: (userId: String) -> Unit,
    onLoadMoreRequested: () -> Unit,
    onRefresh: () -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .testTag(UserListScreenTestTags.TEST_TAG_LIST_SCREEN_CONTENT),
            state = listState,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            itemsIndexed(uiState.users, key = { _, user -> user.id }) { index, user ->
                UserListItem(
                    modifier = Modifier
                        .testTag(buildUserItemTestTag(index)),
                    user = user,
                    onClick = { onUserClick(user.id) },
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoadingMore) {
                        CircularProgressIndicator()

                    } else {
                        Button(
                            modifier = Modifier
                                .testTag(TEST_TAG_LOAD_MORE_BUTTON),
                            onClick = onLoadMoreRequested
                        ) {
                            Text("Load more")
                        }
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
}

@Composable
private fun UserListItem(
    modifier: Modifier = Modifier,
    user: UiUser,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
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
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
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
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun UserListScreenPreview_Loading() {
    RandomUsersTheme {
        UserListScreen(
            uiState = UserListUiState.Loading,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun UserListScreenPreview_Error() {
    RandomUsersTheme {
        UserListScreen(
            uiState = UserListUiState.Error("Failed to load users."),
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
        fullName = (if (index % 2 == 0) "Mr" else "Ms") + " User " + index,
        email = "user$index@example.com",
        phone = "555-010$index",
        thumbnailUrl = "https://actuallywhatever-as-preview-dont-perform-network-calls.com",
        largePictureUrl = "https://actuallywhatever-as-preview-dont-perform-network-calls.com",
        location = "147 Rue Chantilly, 75008 Paris, France"
    )
}

internal object EmptyUserListUiCallbacks : UserListUiCallbacks {
    override fun onUserClicked(userId: String) = Unit
    override fun onLoadMoreUsersRequested() = Unit
    override fun onRefresh() = Unit
    override fun onRetry() = Unit
}

/// TODO Preview dark mode
/// TODO param tests
/// TODO screenshot test + UI
/// TODO interface for callbacks
/// TODO Test UI for composables??
/// TODO Test UI for states
/// TODO skeletons loader ?


//TODO test navigation
