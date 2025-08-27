package com.crgarridos.randomusers.ui.compose.userlist

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import com.crgarridos.randomusers.ui.compose.common.CommonStateTestTags.TEST_TAG_ERROR_SCREEN
import com.crgarridos.randomusers.ui.compose.common.CommonStateTestTags.TEST_TAG_FULL_SCREEN_LOADING
import com.crgarridos.randomusers.ui.compose.theme.RandomUsersTheme
import com.crgarridos.randomusers.ui.compose.userlist.UserListScreenTestTags.TEST_TAG_LIST_SCREEN_CONTENT
import com.crgarridos.randomusers.ui.compose.userlist.UserListScreenTestTags.TEST_TAG_LOAD_MORE_BUTTON
import com.crgarridos.randomusers.ui.compose.userlist.UserListScreenTestTags.TEST_TAG_SNACKBAR
import com.crgarridos.randomusers.ui.compose.userlist.UserListScreenTestTags.buildUserItemTestTag
import com.crgarridos.randomusers.ui.presentation.UserListViewModel

class UserListScreenRobot(private val composeTestRule: ComposeContentTestRule) {
    fun setUserListScreenContent(
        uiState: UserListUiState,
        uiEvent: UserListViewModel.UserListUiEvent? = null,
        callbacks: UserListUiCallbacks = EmptyUserListUiCallbacks,
    ) = apply {
        composeTestRule.setContent {
            RandomUsersTheme {
                UserListScreen(
                    uiState = uiState,
                    uiEvent = uiEvent,
                    callbacks = callbacks
                )
            }
        }
    }

    fun assertLoadingIndicatorIsDisplayed() = apply {
        composeTestRule.onNodeWithTag(TEST_TAG_FULL_SCREEN_LOADING)
            .assertIsDisplayed()
    }

    fun assertLoadingIndicatorIsNotDisplayed() = apply {
        composeTestRule.onNodeWithTag(TEST_TAG_FULL_SCREEN_LOADING)
            .assertDoesNotExist()
    }

    fun assertListScreenContentIsDisplayed() = apply {
        composeTestRule.onNodeWithTag(TEST_TAG_LIST_SCREEN_CONTENT)
            .assertIsDisplayed()
    }

    fun assertSnackbarIsDisplayed() = apply {
        composeTestRule.onNodeWithTag(TEST_TAG_SNACKBAR)
            .assertIsDisplayed()
    }

    fun assertErrorScreenIsDisplayed() = apply {
        composeTestRule.onNodeWithTag(TEST_TAG_ERROR_SCREEN)
            .assertIsDisplayed()
    }


    fun assertErrorScreenContainText(errorMessage: String) = apply {
        composeTestRule.onNodeWithTag(TEST_TAG_ERROR_SCREEN, useUnmergedTree = true)
            .assert(hasAnyDescendant(hasText(errorMessage)))
    }

    fun assertLoadMoreButtonIsDisplayed() = apply {
        composeTestRule.onNodeWithTag(TEST_TAG_LIST_SCREEN_CONTENT, useUnmergedTree = true)
            .assert(hasAnyDescendant(hasTestTag(TEST_TAG_LOAD_MORE_BUTTON)))
            .assertIsDisplayed()
    }

    fun assertItemFullNameAtPosition(itemIndex: Int, fullName: String) {
        composeTestRule.onNodeWithTag(TEST_TAG_LIST_SCREEN_CONTENT, useUnmergedTree = true)
            .performScrollToIndex(itemIndex)

        composeTestRule.onNodeWithTag(buildUserItemTestTag(itemIndex), useUnmergedTree = true)
            .assert(hasAnyDescendant(hasTextExactly(fullName)))
            .assertIsDisplayed()

    }
}