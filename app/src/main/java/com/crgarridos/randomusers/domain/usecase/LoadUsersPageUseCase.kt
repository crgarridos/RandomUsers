package com.crgarridos.randomusers.domain.usecase

import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.util.DomainResult
import com.crgarridos.randomusers.domain.repository.UserRepository
import javax.inject.Inject

class LoadUsersPageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(page: Int, results: Int, seed: String = "lydia"): DomainResult<UserError, List<User>> {
        return userRepository.getUsers(page = page, results = results, seed = seed)
    }
}
