package com.crgarridos.randomusers.ui.compose.userlist

import androidx.compose.ui.test.junit4.createComposeRule
import com.crgarridos.randomusers.fixtures.UserFixtures
import com.crgarridos.randomusers.ui.presentation.UserListViewModel.UserListUiEvent
import com.crgarridos.randomusers.ui.presentation.mapper.toUiUser
import com.crgarridos.randomusers.ui.presentation.mapper.toUiUserList
import org.junit.Rule
import org.junit.Test


class UserListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun userListScreen_whenStateIsLoading_displaysLoadingIndicator() {
        val uiState = UserListUiState.Loading

        UserListScreenRobot(composeTestRule)
            .setUserListScreenContent(uiState)
            .assertLoadingIndicatorIsDisplayed()
    }


    @Test
    fun userListScreen_whenStateIsError_displaysErrorScreen() {
        val uiState = UserListUiState.Error("This an error from UI tests")

        UserListScreenRobot(composeTestRule)
            .setUserListScreenContent(uiState)
            .assertErrorScreenIsDisplayed()
            .assertErrorScreenContainText("This an error from UI tests")
    }

    @Test
    fun userListScreen_whenStateIsSuccess_displaysListScreenContent() {
        UserListScreenRobot(composeTestRule)
            .setUserListScreenContent(
                uiState = UserListUiState.Success(
                    users = UserFixtures.generateRandomUsers(10).toUiUserList(),
                    canLoadMore = true,
                    isLoadingMore = false
                )
            )
            .assertListScreenContentIsDisplayed()

    }


    @Test
    fun userListScreen_whenStateIsSuccess_displaysChichibertoAtTheCorrectPosition() {
        val users = UserFixtures.generateRandomUsers(20)
            .toUiUserList().toMutableList()
        val chichiberto = UserFixtures.generateRandomUser().toUiUser().copy(
           fullName = "Mr Chichiberto de las Mercedez"
        )
        val chichibertoIndex = 12
        users.add(chichibertoIndex, chichiberto)

        UserListScreenRobot(composeTestRule)
            .setUserListScreenContent(
                uiState = UserListUiState.Success(
                    users = users,
                    canLoadMore = true,
                    isLoadingMore = false
                ),
            )
            .assertListScreenContentIsDisplayed()
            .assertItemFullNameAtPosition(chichibertoIndex, "Mr Chichiberto de las Mercedez")
    }

    @Test
    fun userListScreen_whenReceivingErrorEvent_displaysSnackbar() {
        UserListScreenRobot(composeTestRule)
            .setUserListScreenContent(
                uiState = UserListUiState.Success(
                    users = UserFixtures.generateRandomUsers(10).toUiUserList(),
                    canLoadMore = true,
                    isLoadingMore = false
                ),
                uiEvent = UserListUiEvent.ShowSnackbar("Error loading users"),
            )
            .assertListScreenContentIsDisplayed()
            .assertSnackbarIsDisplayed()
    }

    @Test
    fun userListScreen_whenIsNotLoadingMore_displaysLoadMoreButton() {
        UserListScreenRobot(composeTestRule)
            .setUserListScreenContent(
                uiState = UserListUiState.Success(
                    users = UserFixtures.generateRandomUsers(5).toUiUserList(),
                    canLoadMore = true,
                    isLoadingMore = false
                ),
            )
            .assertListScreenContentIsDisplayed()
            .assertLoadMoreButtonIsDisplayed()
    }
}
