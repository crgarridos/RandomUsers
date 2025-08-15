package com.crgarridos.randomusers.data.remote

import com.crgarridos.randomusers.data.remote.model.RemoteApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val DEFAULT_SEED = "lydia"
private const val API_VERSION = "1.3"

interface RandomUserApiService {

    @GET("api/$API_VERSION/")
    suspend fun getUsers(
        @Query("seed") seed: String = DEFAULT_SEED,
        @Query("results") results: Int,
        @Query("page") page: Int
    ): RemoteApiResponse

}
