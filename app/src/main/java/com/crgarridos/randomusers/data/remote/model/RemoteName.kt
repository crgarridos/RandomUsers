package com.crgarridos.randomusers.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteName(
    @param:Json(name = "title") val title: String?,
    @param:Json(name = "first") val first: String?,
    @param:Json(name = "last") val last: String?
)
