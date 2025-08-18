package com.crgarridos.randomusers.data

import com.crgarridos.randomusers.data.local.UserLocalDataSource
import com.crgarridos.randomusers.data.remote.UserRemoteDataSource
import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.util.DomainResult
import com.crgarridos.randomusers.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) : UserRepository {

    override suspend fun fetchUsersPage(
        page: Int,
        results: Int,
    ): DomainResult<UserError, List<User>> {
        TODO()
    }

    override fun getObservableUsers(): Flow<DomainResult<UserError, List<User>>> {
        TODO()
    }

    override suspend fun getUserById(id: String): DomainResult<UserError, User> {
        TODO()
    }
}