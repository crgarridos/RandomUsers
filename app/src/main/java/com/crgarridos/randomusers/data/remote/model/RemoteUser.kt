package com.crgarridos.randomusers.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteUser(
    @param:Json(name = "name") val name: RemoteName?,
    @param:Json(name = "email") val email: String?,
    @param:Json(name = "phone") val phone: String?,
    @param:Json(name = "picture") val picture: RemotePicture?,
    @param:Json(name = "nat") val nationality: String?
)
