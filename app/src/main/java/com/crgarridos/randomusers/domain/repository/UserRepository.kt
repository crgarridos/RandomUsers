package com.crgarridos.randomusers.domain.repository

import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.util.DomainResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getObservableUsers(): Flow<List<User>>
    suspend fun fetchUsersPage(
        page: Int,
        results: Int,
    ): DomainResult<UserError, List<User>>

    suspend fun getUserById(id: String): DomainResult<UserError, User>
}