package com.crgarridos.randomusers.data

import com.crgarridos.randomusers.data.local.UserLocalDataSource
import com.crgarridos.randomusers.data.remote.UserRemoteDataSource
import com.crgarridos.randomusers.domain.model.PaginatedUserList
import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.UserNotFound
import com.crgarridos.randomusers.domain.model.util.DomainResult
import com.crgarridos.randomusers.domain.model.util.DomainSuccess
import com.crgarridos.randomusers.domain.model.util.NetworkError
import com.crgarridos.randomusers.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) : UserRepository {

    override suspend fun fetchUsersPage(
        pageNumber: Int,
        resultsPerPage: Int,
    ): DomainResult<UserError, PaginatedUserList> {
        val result = remoteDataSource.getUserPage(
            resultsPerPage = resultsPerPage,
            pageNumber = pageNumber
        )
        if (result is DomainSuccess) {
            if (pageNumber == 1) {
                userLocalDataSource.deleteAll()
            }
            userLocalDataSource.insertOrReplace(result.data.users)
        }

        return result
    }

    override fun getObservableUsers(): Flow<List<User>> {
        return userLocalDataSource.getObservableUsers()
            .distinctUntilChanged()
    }

    override suspend fun getUserById(id: String): DomainResult<UserError, User> {
        return userLocalDataSource.getUserById(id)
            ?.let(::DomainSuccess)
            ?: UserNotFound
    }
}