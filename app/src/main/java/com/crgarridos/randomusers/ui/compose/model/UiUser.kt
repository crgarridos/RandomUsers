package com.crgarridos.randomusers.ui.compose.model

data class UiUser(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val thumbnailUrl: String,
    val largePictureUrl: String,
    val location: String,
)
