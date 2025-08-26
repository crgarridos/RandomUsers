package com.crgarridos.randomusers.ui.presentation.mapper

import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserLocation
import com.crgarridos.randomusers.ui.compose.model.UiUser

fun User.toUiUser(): UiUser {
    return UiUser(
        id = email,
        fullName = getUiFullName(),
        email = email,
        phone = phone,
        thumbnailUrl = thumbnailUrl,
        largePictureUrl = largePictureUrl,
        location = location.toUiUserLocation(),
    )
}

private fun User.getUiFullName(): String {
    return "$title $firstName $lastName"
}
private fun UserLocation.toUiUserLocation(): String {
    return "$streetNumber $streetName, $postcode $city, $state, $country"
}
fun List<User>.toUiUserList(): List<UiUser> {
    return map { it.toUiUser() }
}