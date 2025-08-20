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
import com.crgarridos.randomusers.ui.compose.userlist.UserListUiState
import com.crgarridos.randomusers.ui.presentation.mapper.toUiUserList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val RESULTS_PER_PAGE = 20

@HiltViewModel
class UserListViewModel @Inject constructor(
    observeAllUsersUseCase: ObserveAllUsersUseCase,
    private val fetchUsersPageUseCase: FetchUsersPageUseCase,
) : ViewModel() {

    private sealed class PaginationState(
        open val nextPageToLoad: Int,
        open val loadMoreErrorMessage: String? = null,
    ) {
        object InitialLoading : PaginationState(nextPageToLoad = 1)
        data class IsLoadingMore(override val nextPageToLoad: Int) : PaginationState(nextPageToLoad)
        data class Idle(
            val canLoadMore: Boolean,
            override val nextPageToLoad: Int,
            override val loadMoreErrorMessage: String? = null,
        ) : PaginationState(nextPageToLoad, loadMoreErrorMessage)

        data class Error(
            override val loadMoreErrorMessage: String,
        ) : PaginationState(nextPageToLoad = 1, loadMoreErrorMessage)
    }

    private val paginationState = MutableStateFlow<PaginationState>(PaginationState.InitialLoading)

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
                    UserListUiState.Error(message = state.loadMoreErrorMessage)
                } else {
                    UserListUiState.Success(
                        users = users.toUiUserList(),
                        canLoadMore = true,
                        isLoadingMore = false,
                        loadMoreErrorMessage = state.loadMoreErrorMessage
                    )
                }
            }

            is PaginationState.IsLoadingMore -> UserListUiState.Success(
                users = users.toUiUserList(),
                canLoadMore = false,
                isLoadingMore = true,
                loadMoreErrorMessage = state.loadMoreErrorMessage
            )

            is PaginationState.Idle -> UserListUiState.Success(
                users = users.toUiUserList(),
                canLoadMore = state.canLoadMore,
                isLoadingMore = false,
                loadMoreErrorMessage = state.loadMoreErrorMessage
            )
        }

    }

    init {
        fetchUsers()
    }

    fun loadMoreUsers() {
        fetchUsers()
    }

    fun clearLoadMoreErrorMessage() {
        paginationState.update { currentState ->
            if (currentState is PaginationState.Idle && currentState.loadMoreErrorMessage != null) {
                currentState.copy(loadMoreErrorMessage = null)
            } else {
                currentState
            }
        }
    }

    private fun fetchUsers() = viewModelScope.launch {

        val lastPaginationState = paginationState.value

        paginationState.value = when (lastPaginationState) {
            is PaginationState.IsLoadingMore -> return@launch

            is PaginationState.InitialLoading,
            is PaginationState.Error,
            is PaginationState.Idle,
                -> PaginationState.IsLoadingMore(
                nextPageToLoad = lastPaginationState.nextPageToLoad
            )
        }

        val result = fetchUsersPageUseCase(
            pageNumber = lastPaginationState.nextPageToLoad,
            resultsPerPage = RESULTS_PER_PAGE
        )
        processFetchPageResult(result)
    }

    private fun processFetchPageResult(
        result: DomainResult<UserError, PaginatedUserList>,
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
                paginationState.value = PaginationState.Error(errorMessage)
            }
        }
    }

}
