package com.crgarridos.randomusers.domain.model

data class User(
    val title: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val thumbnailUrl: String,
    val largePictureUrl: String?,
    val nationality: String?
)

open class UserError(message: String)
