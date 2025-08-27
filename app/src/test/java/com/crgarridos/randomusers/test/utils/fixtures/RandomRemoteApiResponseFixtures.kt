package com.crgarridos.randomusers.test.utils.fixtures

import com.crgarridos.randomusers.data.mappers.toRemoteUser
import com.crgarridos.randomusers.data.remote.model.RemoteApiInfo
import com.crgarridos.randomusers.data.remote.model.RemoteApiResponse
import com.crgarridos.randomusers.data.remote.model.RemoteUser
import com.crgarridos.randomusers.test.utils.fixtures.UserFixtures.generateRandomUser
import java.util.UUID

object RandomRemoteApiResponseFixtures {

    fun generateRandomRemoteApiResponse(
        page: Int = 1,
        resultsPerPage: Int = 10,
        users: List<RemoteUser> = List(resultsPerPage) { generateRandomUser().toRemoteUser() },
    ): RemoteApiResponse {
        val info = generateRandomRemoteApiInfo(page = page, resultsPerPage = resultsPerPage)
        return RemoteApiResponse(
            results = users,
            info = info
        )
    }

    fun generateRandomRemoteApiInfo(page: Int, resultsPerPage: Int): RemoteApiInfo {
        val versions = listOf("1.3", "1.4", "2.0")
        return RemoteApiInfo(
            seed = UUID.randomUUID().toString().take(16),
            results = resultsPerPage,
            page = page,
            version = versions.random()
        )
    }
}