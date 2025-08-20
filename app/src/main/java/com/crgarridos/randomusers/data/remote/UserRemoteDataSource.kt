package com.crgarridos.randomusers.data.remote

import com.crgarridos.randomusers.data.mappers.toDomainUserList
import com.crgarridos.randomusers.domain.model.PaginatedUserList
import com.crgarridos.randomusers.domain.model.UserError
import com.crgarridos.randomusers.domain.model.util.DomainResult
import com.crgarridos.randomusers.domain.model.util.NetworkError
import com.crgarridos.randomusers.domain.model.util.toDomainSuccess
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface UserRemoteDataSource {
    suspend fun getUserPage(pageNumber: Int, resultsPerPage: Int): DomainResult<UserError, PaginatedUserList>
}

class UserRemoteDataSourceImpl @Inject constructor(
    private val apiService: RandomUserApiService,
) : UserRemoteDataSource {
    override suspend fun getUserPage(
        pageNumber: Int,
        resultsPerPage: Int,
    ): DomainResult<UserError, PaginatedUserList> {

        try {
            val remoteUsers = apiService.getUsers(
                results = resultsPerPage,
                page = pageNumber
            )
            val nextPage = remoteUsers.info.page
                .let { if (it > 0) it + 1 else it }

            return PaginatedUserList(
                users = remoteUsers.results.toDomainUserList(),
                nextPage = nextPage
            ).toDomainSuccess()
        } catch (_: HttpException) {
            return NetworkError.ServerError
        } catch (_: IOException) {
            return NetworkError.ConnectivityError
        }
    }
}