package com.crgarridos.randomusers.domain.usecase

import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.util.DomainResult
import com.crgarridos.randomusers.domain.model.util.DomainSuccess
import com.crgarridos.randomusers.domain.repository.UserRepository
import javax.inject.Inject

class LoadUsersPageUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        page: Int,
        results: Int,
    ): DomainResult<UserError, List<User>> {
        return userRepository.fetchUsersPage(page = page, results = results)
    }
}
