package com.crgarridos.randomusers.ui.presentation.mapper

import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.ui.compose.model.UiUser

fun User.toUiUser(): UiUser {
    return UiUser(
        id = this.email,
        title = this.title,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        phone = this.phone,
        thumbnailUrl = this.thumbnailUrl,
        largePictureUrl = this.largePictureUrl,
        nationality = this.nationality,
        city = "this.city", // TODO
        gender = "this.gender" // TODO
    )
}

fun List<User>.toUiUserList(): List<UiUser> {
    return this.map { it.toUiUser() }
}