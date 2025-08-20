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
    ) {

        object InitialLoading : PaginationState(nextPageToLoad = 1)

        data class InitialError(val message: String) : PaginationState(nextPageToLoad = 1)

        data class IsLoadingMore(override val nextPageToLoad: Int) : PaginationState(nextPageToLoad)

        data class Success(
            val canLoadMore: Boolean,
            override val nextPageToLoad: Int,
        ) : PaginationState(nextPageToLoad)
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
    ) = when (state) {
        is PaginationState.InitialError -> UserListUiState.Error(state.message)
        is PaginationState.InitialLoading -> UserListUiState.Loading
        is PaginationState.IsLoadingMore -> UserListUiState.Success(
            users = users.toUiUserList(),
            canLoadMore = false,
            isLoadingMore = true
        )

        is PaginationState.Success -> UserListUiState.Success(
            users = users.toUiUserList(),
            canLoadMore = state.canLoadMore,
            isLoadingMore = false
        )
    }

    init {
        fetchUsers()
    }

    fun loadMoreUsers() {
        fetchUsers()
    }

    private fun fetchUsers() = viewModelScope.launch {

        val lastPaginationState = paginationState.value

        paginationState.value = when (lastPaginationState) {
            is PaginationState.IsLoadingMore -> return@launch

            is PaginationState.InitialLoading,
            is PaginationState.InitialError,
            is PaginationState.Success -> PaginationState.IsLoadingMore(
                nextPageToLoad = lastPaginationState.nextPageToLoad
            )
        }

        val result = fetchUsersPageUseCase(
            pageNumber = lastPaginationState.nextPageToLoad,
            resultsPerPage = RESULTS_PER_PAGE
        )
        processFetchPageResult(result, lastPaginationState)
    }

    private fun processFetchPageResult(
        result: DomainResult<UserError, PaginatedUserList>,
        lastPaginationState: PaginationState,
    ) {
        when (result) {
            is DomainSuccess -> {
                val nextPage = result.data.nextPage
                paginationState.value = PaginationState.Success(
                    canLoadMore = nextPage > 0,
                    nextPageToLoad = nextPage,
                )
            }

            is DomainError -> {
                processFetchPageError(result, lastPaginationState)
            }
        }
    }

    private fun processFetchPageError(
        result: DomainError<UserError>,
        lastPaginationState: PaginationState,
    ) {
        val errorMessage = when (result) {
            is NetworkError.ConnectivityError -> "Network connection error. Please check your connection."// TODO Android resources
            is NetworkError.ServerError -> "Server error. Please try again later."
            else -> "Failed to load users."
        }

        if (lastPaginationState is PaginationState.InitialLoading) {
            paginationState.value = PaginationState.InitialError(errorMessage)
        } else {
            paginationState.value = PaginationState.Success(
                canLoadMore = lastPaginationState.nextPageToLoad > 0,
                nextPageToLoad = lastPaginationState.nextPageToLoad,
            )
            // Handle error for load more, TODO snackbar
        }
    }
}
