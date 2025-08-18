package com.crgarridos.randomusers.data

import com.crgarridos.randomusers.data.local.UserLocalDataSource
import com.crgarridos.randomusers.data.remote.UserRemoteDataSource
import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.UserNotFound
import com.crgarridos.randomusers.domain.model.util.DomainResult
import com.crgarridos.randomusers.domain.model.util.DomainSuccess
import com.crgarridos.randomusers.domain.model.util.NetworkError
import com.crgarridos.randomusers.domain.model.util.toDomainSuccess
import com.crgarridos.randomusers.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) : UserRepository {

    override suspend fun fetchUsersPage(
        page: Int,
        results: Int,
    ): DomainResult<UserError, List<User>> {
        try {
            val users = remoteDataSource.getUsers(results = results, page = page)
            if (page == 1) {
                userLocalDataSource.deleteAll()
            }
            userLocalDataSource.insertOrReplace(users)
            return users.toDomainSuccess()
        } catch (_: HttpException) {
            return NetworkError.ServerError
        } catch (_: IOException) {
            return NetworkError.ConnectivityError
        }
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