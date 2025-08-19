package com.crgarridos.randomusers.ui.compose.model

// Simplified User model for UI - adapt from your domain model
data class UiUser(
    val id: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val thumbnailUrl: String,
    val largePictureUrl: String?,
    val nationality: String?,
    val city: String?,
    val gender: String?
) {
    val fullName: String
        get() = "$title $firstName $lastName"

    val fullLocation: String
        get() = city ?: "Location unknown" // Example, expand as needed
}
