package com.crgarridos.randomusers.domain.usecase

import com.crgarridos.randomusers.domain.model.PaginatedUserList
import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.util.DomainResult
import com.crgarridos.randomusers.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchUsersPageUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        pageNumber: Int,
        resultsPerPage: Int,
    ): DomainResult<UserError, PaginatedUserList> {
        return userRepository.fetchUsersPage(pageNumber, resultsPerPage)
    }
}

class ObserveAllUsersUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<User>> {
        return userRepository.getObservableUsers()
    }
}
