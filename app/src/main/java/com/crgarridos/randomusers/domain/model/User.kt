package com.crgarridos.randomusers.domain.model

import com.crgarridos.randomusers.domain.model.util.DomainError

data class User(
    val title: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val thumbnailUrl: String,
    val largePictureUrl: String,
    val nationality: String,
    val location: UserLocation,
)

data class UserLocation(
    val streetNumber: String,
    val streetName: String,
    val city: String,
    val state: String,
    val country: String,
    val postcode: String
)

open class UserError(message: String): DomainError<Nothing>
object UserNotFound : UserError("User not found")
