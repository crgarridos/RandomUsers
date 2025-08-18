package com.crgarridos.randomusers.domain.repository

import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.util.DomainResult

interface UserRepository {
    suspend fun getUsers(page: Int, results: Int, seed: String): DomainResult<UserError, List<User>>
    suspend fun getUserById(id: String): DomainResult<UserError, User>
    suspend fun saveUsers(users: List<User>): DomainResult<UserError, User>
}
