package com.crgarridos.randomusers.ui.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crgarridos.randomusers.domain.model.util.DomainSuccess
import com.crgarridos.randomusers.domain.usecase.GetUserByIdUseCase
import com.crgarridos.randomusers.ui.navigation.AppDestinations.UserDetail.getUserId
import com.crgarridos.randomusers.ui.compose.userdetail.UserDetailUiState
import com.crgarridos.randomusers.ui.presentation.mapper.toUiUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserByIdUseCase: GetUserByIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    private val userId: String = requireNotNull(savedStateHandle.getUserId())

    init {
        fetchUserDetails()
    }

    fun fetchUserDetails() {
        viewModelScope.launch {
            _uiState.value = UserDetailUiState.Loading
            try {
                val result = getUserByIdUseCase(userId)
                if (result is DomainSuccess) {
                    _uiState.value = UserDetailUiState.Success(result.data.toUiUser())
                } else {
                    _uiState.value = UserDetailUiState.Error("User not found")
                }
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error(e.message ?: "An unknown error occurred during fetch.")
            }
        }
    }

    fun retry() {
        fetchUserDetails()
    }

}
