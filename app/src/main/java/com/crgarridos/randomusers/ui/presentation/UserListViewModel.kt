package com.crgarridos.randomusers.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crgarridos.randomusers.domain.model.PaginatedUserList
import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.util.DomainError
import com.crgarridos.randomusers.domain.model.util.DomainResult
import com.crgarridos.randomusers.domain.model.util.DomainSuccess
import com.crgarridos.randomusers.domain.model.util.NetworkError
import com.crgarridos.randomusers.domain.usecase.FetchUsersPageUseCase
import com.crgarridos.randomusers.domain.usecase.ObserveAllUsersUseCase
import com.crgarridos.randomusers.ui.compose.userlist.UserListUiCallbacks
import com.crgarridos.randomusers.ui.compose.userlist.UserListUiState
import com.crgarridos.randomusers.ui.presentation.mapper.toUiUserList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val RESULTS_PER_PAGE = 20

@HiltViewModel
class UserListViewModel @Inject constructor(
    observeAllUsersUseCase: ObserveAllUsersUseCase,
    private val fetchUsersPageUseCase: FetchUsersPageUseCase,
) : ViewModel(), UserListUiCallbacks {

    private sealed class PaginationState(
        open val nextPageToLoad: Int,
    ) {
        object InitialLoading : PaginationState(nextPageToLoad = 1)
        data class IsLoadingMore(override val nextPageToLoad: Int) : PaginationState(nextPageToLoad)
        object IsRefreshing : PaginationState(nextPageToLoad = 1)
        data class Idle(            val canLoadMore: Boolean,
            override val nextPageToLoad: Int,
        ) : PaginationState(nextPageToLoad)

        data class Error(
            override val nextPageToLoad: Int,
            val message: String,
        ) : PaginationState(nextPageToLoad = nextPageToLoad)
    }

    sealed interface UserListUiEvent {
        class ShowSnackbar(val message: String) : UserListUiEvent
        class NavigateToUserDetail(val userId: String) : UserListUiEvent
    }

    private val paginationState = MutableStateFlow<PaginationState>(PaginationState.InitialLoading)

    private val _uiEvent = MutableSharedFlow<UserListUiEvent>()
    val uiEvent: SharedFlow<UserListUiEvent> = _uiEvent.asSharedFlow()

    val uiState: StateFlow<UserListUiState> = combine(
        flow = observeAllUsersUseCase(),
        flow2 = paginationState,
        transform = ::combineUsersWithPaginationState
    ).distinctUntilChanged()
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), UserListUiState.Loading
        )

    private fun combineUsersWithPaginationState(
        users: List<User>,
        state: PaginationState,
    ): UserListUiState {
        return when (state) {
            is PaginationState.InitialLoading -> UserListUiState.Loading

            is PaginationState.Error -> {
                if (users.isEmpty()) {
                    UserListUiState.Error(message = state.message)
                } else {
                    UserListUiState.Success(
                        users = users.toUiUserList(),
                        canLoadMore = false,
                        isLoadingMore = false,
                    )
                }
            }

            is PaginationState.IsLoadingMore -> UserListUiState.Success(
                users = users.toUiUserList(),
                canLoadMore = false,
                isLoadingMore = true,
            )

            is PaginationState.IsRefreshing -> UserListUiState.Success(
                users = users.toUiUserList(),
                canLoadMore = false,
                isLoadingMore = false,
                isRefreshing = true,
            )

            is PaginationState.Idle -> UserListUiState.Success(
                users = users.toUiUserList(),
                canLoadMore = state.canLoadMore,
                isLoadingMore = false,
            )
        }

    }

    init {
        fetchUsers()
    }

    override fun onLoadMoreUsersRequested() {
        fetchUsers()
    }

    override fun onRefresh() {
        fetchUsers(refresh = true)
    }

    override fun onRetry() {
        fetchUsers()
    }

    override fun onUserClicked(userId: String) {
        viewModelScope.launch {
            _uiEvent.emit(UserListUiEvent.NavigateToUserDetail(userId))
        }
    }

    private fun fetchUsers(refresh: Boolean = false) = viewModelScope.launch {

        val lastPaginationState = paginationState.value
        val nextPageToLoad = if (refresh) 1 else lastPaginationState.nextPageToLoad

        paginationState.value = when (lastPaginationState) {
            is PaginationState.IsLoadingMore -> return@launch
            is PaginationState.IsRefreshing -> return@launch

            is PaginationState.InitialLoading,
            is PaginationState.Error,
            is PaginationState.Idle,
                -> {
                if (refresh) {
                    PaginationState.IsRefreshing
                } else {
                    PaginationState.IsLoadingMore(
                        nextPageToLoad = nextPageToLoad
                    )
                }
            }
        }
        delay(200) // to display the loader smoothly

        val result = fetchUsersPageUseCase(
            pageNumber = nextPageToLoad,
            resultsPerPage = RESULTS_PER_PAGE
        )
        processFetchPageResult(result, nextPageToLoad)
    }

    private suspend fun processFetchPageResult(
        result: DomainResult<UserError, PaginatedUserList>,
        requestedPage: Int,
    ) {
        when (result) {
            is DomainSuccess -> {
                val nextPage = result.data.nextPage
                paginationState.value = PaginationState.Idle(
                    canLoadMore = nextPage > 0,
                    nextPageToLoad = nextPage,
                )
            }

            is DomainError -> {
                val errorMessage = when (result) {
                    is NetworkError.ConnectivityError -> "Network connection error. Please check your connection."// TODO Android resources (not here in the VM please)
                    is NetworkError.ServerError -> "Server error. Please try again later."
                    else -> "Failed to load users. Please try again."
                }
                paginationState.value = PaginationState.Error(
                    nextPageToLoad = requestedPage,
                    message = errorMessage,
                )
                showSnackbarIfNecessary(errorMessage)
            }
        }
    }

    private suspend fun showSnackbarIfNecessary(errorMessage: String) {
        val currentUiState = uiState.value
        if (currentUiState is UserListUiState.Success && currentUiState.users.isNotEmpty()) {
            _uiEvent.emit(UserListUiEvent.ShowSnackbar(errorMessage))
        }
    }

}
