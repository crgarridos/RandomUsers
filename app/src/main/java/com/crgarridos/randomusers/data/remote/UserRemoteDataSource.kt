package com.crgarridos.randomusers.data.remote

import com.crgarridos.randomusers.data.mappers.toDomainUserList
import com.crgarridos.randomusers.domain.model.User
import javax.inject.Inject

interface UserRemoteDataSource {
    suspend fun getUsers(page: Int, results: Int): List<User>
}

class UserRemoteDataSourceImpl @Inject constructor(
    private val apiService: RandomUserApiService,
) : UserRemoteDataSource {
    override suspend fun getUsers(
        page: Int,
        results: Int,
    ): List<User> {
        val remoteUsers = apiService.getUsers(results = results, page = page)
        return remoteUsers.results.toDomainUserList()
    }
}