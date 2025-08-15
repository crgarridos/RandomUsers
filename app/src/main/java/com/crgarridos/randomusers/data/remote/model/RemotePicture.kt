package com.crgarridos.randomusers.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemotePicture(
    @param:Json(name = "large") val large: String?,
    @param:Json(name = "medium") val medium: String?,
    @param:Json(name = "thumbnail") val thumbnail: String?
)
