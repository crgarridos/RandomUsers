package com.crgarridos.randomusers.ui.presentation

import app.cash.turbine.test
import com.crgarridos.randomusers.domain.model.PaginatedUserList
import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.util.DomainResult
import com.crgarridos.randomusers.domain.model.util.DomainSuccess
import com.crgarridos.randomusers.domain.model.util.NetworkError
import com.crgarridos.randomusers.domain.usecase.FetchUsersPageUseCase
import com.crgarridos.randomusers.domain.usecase.ObserveAllUsersUseCase
import com.crgarridos.randomusers.test.util.rules.MainCoroutineRule
import com.crgarridos.randomusers.test.utils.fixtures.UserFixtures
import com.crgarridos.randomusers.ui.compose.userlist.UserListUiState
import com.crgarridos.randomusers.ui.presentation.mapper.toUiUserList
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UserListViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val resultsPerPage = 20

    @Test
    fun `init - emits Loading then Success with users on successful first page fetch`() = runTest {
        val page1Users = UserFixtures.generateRandomUsers(resultsPerPage)

        val viewModel = buildUserListViewModel(
            stubForObservedUsers = flowOf(page1Users),
            stuForFetchUsersPageUseCase = listOf(
                DomainSuccess(
                    PaginatedUserList(page1Users, 2)
                )
            )
        )

        viewModel.uiState.test {
            assertEquals("Initial state should be Loading", UserListUiState.Loading, awaitItem())

            assertIsLoadingMore(awaitItem())

            val successState = awaitItem() as UserListUiState.Success
            assertEquals("Users should match fetched data",
                page1Users.toUiUserList(),
                successState.users
            )
            assertTrue(
                "Should be able to load more if full page received",
                successState.canLoadMore
            )
            assertFalse("Should not be loading more initially", successState.isLoadingMore)

            expectNoEvents()
        }
    }

    @Test
    fun `init - emits Loading then Error on failed first page fetch`() = runTest {
        val networkError = NetworkError.ConnectivityError

        val viewModel = buildUserListViewModel(
            stubForObservedUsers = flowOf(emptyList()),
            stuForFetchUsersPageUseCase = listOf(networkError)
        )

        viewModel.uiState.test {
            assertEquals("Initial state should be Loading", UserListUiState.Loading, awaitItem())

            assertIsLoadingMore(awaitItem())
            val errorState = awaitItem() as UserListUiState.Error
            assertTrue(
                "Error message should reflect network error",
                errorState.message.contains("Network connection error") /// TODO ???
            )
            expectNoEvents()
        }
    }

    @Test
    fun `onLoadMoreUsersRequested - fetches next page and appends users on success`() = runTest {
        val page1Users = UserFixtures.generateRandomUsers(resultsPerPage)
        val page2Users = UserFixtures.generateRandomUsers(resultsPerPage)

        val observedUsers = MutableStateFlow<List<User>>(emptyList())

        val viewModel = buildUserListViewModel(
            stubForObservedUsers = observedUsers,
            stuForFetchUsersPageUseCase = listOf(
                DomainSuccess(PaginatedUserList(page1Users, 2)),
                DomainSuccess(PaginatedUserList(page2Users, 3))
            )
        )

        viewModel.uiState.test {
            awaitItem() as UserListUiState.Loading
            observedUsers.value = page1Users

            awaitItem() as UserListUiState.Success

            viewModel.onLoadMoreUsersRequested()

            val loadingMoreState = awaitItem() as UserListUiState.Success
            assertEquals(
                "Users should be page 1 users while loading more",
                page1Users.toUiUserList(),
                loadingMoreState.users
            )

            observedUsers.value = page1Users + page2Users

            val finalSuccessState = awaitItem() as UserListUiState.Success
            assertEquals(
                "Users should be combined from page 1 and 2",
                (page1Users + page2Users).toUiUserList(),
                finalSuccessState.users
            )

            expectNoEvents()
        }
    }

    @Test
    fun `onLoadMoreUsersRequested - fetches next page should indicate loading more before success`() = runTest {
        val page1Users = UserFixtures.generateRandomUsers(resultsPerPage)
        val page2Users = UserFixtures.generateRandomUsers(resultsPerPage)

        val observedUsers = MutableStateFlow<List<User>>(emptyList())

        val viewModel = buildUserListViewModel(
            stubForObservedUsers = observedUsers,
            stuForFetchUsersPageUseCase = listOf(
                DomainSuccess(PaginatedUserList(page1Users, 2)),
                DomainSuccess(PaginatedUserList(page2Users, 3))
            )
        )

        viewModel.uiState.test {
            awaitItem() as UserListUiState.Loading
            assertIsLoadingMore(awaitItem())
            awaitItem() as UserListUiState.Success

            viewModel.onLoadMoreUsersRequested()

            val loadingMoreState = awaitItem() as UserListUiState.Success
            assertTrue(
                "isLoadingMore should be true while loading",
                loadingMoreState.isLoadingMore
            )

            val finalSuccessState = awaitItem() as UserListUiState.Success
            assertFalse(
                "isLoadingMore should be false after success",
                finalSuccessState.isLoadingMore
            )

            expectNoEvents()
        }
    }

    @Test
    fun `onLoadMoreUsersRequested - handles empty next page correctly`() = runTest {
        val page1Users = UserFixtures.generateRandomUsers(resultsPerPage)
        val emptyPage2Users = emptyList<User>()

        val viewModel = buildUserListViewModel(
            stubForObservedUsers = flowOf(page1Users),
            stuForFetchUsersPageUseCase = listOf(
                DomainSuccess(PaginatedUserList(page1Users, 2)),
                DomainSuccess(PaginatedUserList(emptyPage2Users, -1))
            )
        )


        viewModel.uiState.test {
            awaitItem() as UserListUiState.Loading
            assertIsLoadingMore(awaitItem())
            awaitItem() as UserListUiState.Success

            viewModel.onLoadMoreUsersRequested()

            awaitItem() as UserListUiState.Success // is loading

            val finalState = awaitItem() as UserListUiState.Success
            assertEquals(page1Users.toUiUserList(), finalState.users)
            assertFalse(
                "Should not be able to load more if last page was empty",
                finalState.canLoadMore
            )
            assertFalse(finalState.isLoadingMore)
            expectNoEvents()
        }
    }


    @Test
    fun `onLoadMoreUsersRequested - does not fetch if cannot load more`() = runTest {
        val page1Users = UserFixtures.generateRandomUsers(resultsPerPage)
        val page2Users = UserFixtures.generateRandomUsers(resultsPerPage)

        val viewModel = buildUserListViewModel(
            stubForObservedUsers = flowOf(emptyList()),
            stuForFetchUsersPageUseCase = listOf(
                DomainSuccess(
                    PaginatedUserList(page1Users, 2)
                ),
                DomainSuccess(
                    PaginatedUserList(page2Users, -1)
                )
            )
        )

        viewModel.uiState.test {
            awaitItem() as UserListUiState.Loading
            assertIsLoadingMore(awaitItem())
            awaitItem() as UserListUiState.Success

            viewModel.onLoadMoreUsersRequested()

            awaitItem() as UserListUiState.Success // is loading

            val successState = awaitItem() as UserListUiState.Success
            assertFalse(successState.canLoadMore)
            expectNoEvents()
        }
    }


    @Test
    fun `onLoadMoreUsersRequested - handles error during fetch, keeps existing users`() = runTest {
        val page1Users = UserFixtures.generateRandomUsers(resultsPerPage)
        val page2Error = NetworkError.ConnectivityError

        val viewModel = buildUserListViewModel(
            stubForObservedUsers = flowOf(page1Users),
            stuForFetchUsersPageUseCase = listOf(
                DomainSuccess(
                    PaginatedUserList(page1Users, 2)
                ),
                page2Error
            )
        )

        viewModel.uiState.onEach { println(it) }.test {
            awaitItem() as UserListUiState.Loading
            assertIsLoadingMore(awaitItem())
            awaitItem() as UserListUiState.Success

            viewModel.onLoadMoreUsersRequested()

            val loadingMoreState = awaitItem() as UserListUiState.Success
            assertTrue("isLoadingMore should be true", loadingMoreState.isLoadingMore)
            assertEquals(page1Users.toUiUserList(), loadingMoreState.users)

            val errorWhileLoadingMoreState = awaitItem() as UserListUiState.Success
            assertEquals(
                "Users should still be page 1 users",
                page1Users.toUiUserList(),
                errorWhileLoadingMoreState.users
            )
            assertFalse(
                "Should not still be able to retry loading more",
                errorWhileLoadingMoreState.canLoadMore
            )
            assertFalse(
                "isLoadingMore should be false after error",
                errorWhileLoadingMoreState.isLoadingMore
            )
            expectNoEvents()
        }
    }

    private fun buildUserListViewModel(
        stubForObservedUsers: Flow<List<User>>,
        stuForFetchUsersPageUseCase: List<DomainResult<UserError, PaginatedUserList>>,
    ): UserListViewModel {
        val observeAllUsersUseCase = mockk<ObserveAllUsersUseCase>(relaxed = true) {
            coEvery { this@mockk.invoke() } returns stubForObservedUsers
        }
        val fetchUsersPageUseCase = mockk<FetchUsersPageUseCase>(relaxed = true) {
            stuForFetchUsersPageUseCase.forEachIndexed { index, page ->
                val pageNumber = index + 1
                coEvery {
                    this@mockk.invoke(pageNumber = pageNumber, resultsPerPage = any())
                } coAnswers {
                    yield()
                    page
                }
            }
        }
        return UserListViewModel(observeAllUsersUseCase, fetchUsersPageUseCase)
    }
    private fun assertIsLoadingMore(state: UserListUiState) {
        val loadingMoreState = state as UserListUiState.Success
        assertTrue("Is loading more", loadingMoreState.isLoadingMore)

    }
}
