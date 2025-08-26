package com.crgarridos.randomusers.data.remote.model

import com.crgarridos.randomusers.domain.model.UserLocation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteUser(
    @param:Json(name = "name") val name: RemoteName?,
    @param:Json(name = "email") val email: String?,
    @param:Json(name = "phone") val phone: String?,
    @param:Json(name = "picture") val picture: RemotePicture?,
    @param:Json(name = "nat") val nationality: String?,
    @param:Json(name = "location") val location: RemoteUserLocation?,
)

data class RemoteUserLocation(
    @param:Json(name = "street") val street: RemoteStreet?,
    @param:Json(name = "city") val city: String?,
    @param:Json(name = "state") val state: String?,
    @param:Json(name = "country") val country: String?,
    @param:Json(name = "postcode") val postcode: String?
)

data class RemoteStreet(
    @param:Json(name = "number") val number: String?,
    @param:Json(name = "name") val name: String?
)