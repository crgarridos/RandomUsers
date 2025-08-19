package com.crgarridos.randomusers.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteApiResponse(
    @param:Json(name = "results") val results: List<RemoteUser>,
    @param:Json(name = "info") val info: RemoteApiInfo,
)

@JsonClass(generateAdapter = true)
data class RemoteApiInfo(
    @param:Json(name = "seed") val seed: String,
    @param:Json(name = "results") val results: Int,
    @param:Json(name = "page") val page: Int,
    @param:Json(name = "version") val version: String,
)
