package com.crgarridos.randomusers.data.mappers

import com.crgarridos.randomusers.data.local.model.LocalUser
import com.crgarridos.randomusers.data.local.model.LocalUserLocation
import com.crgarridos.randomusers.data.local.model.LocalUserName
import com.crgarridos.randomusers.data.local.model.LocalUserPicture
import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserLocation

fun User.toLocalUser(): LocalUser {
    return LocalUser(
        email = email,
        name = LocalUserName(title = title, first = firstName, last = lastName),
        phone = phone,
        picture = LocalUserPicture(
            large = largePictureUrl,
            thumbnail = thumbnailUrl
        ),
        nationality = nationality,
        location = LocalUserLocation(
            streetNumber = location.streetNumber,
            streetName = location.streetName,
            city = location.city,
            state = location.state,
            country = location.country,
            postcode = location.postcode
        )
    )
}



fun List<User>.toLocalUserList(): List<LocalUser> {
    return map { it.toLocalUser() }
}

fun LocalUser.toDomainUser(): User {
    return User(
        title = name.title,
        firstName = name.first,
        lastName = name.last,
        email = email,
        phone = phone,
        thumbnailUrl = picture.thumbnail,
        largePictureUrl = picture.large,
        nationality = nationality,
        location = UserLocation(
            streetNumber = location.streetNumber,
            streetName = location.streetName,
            city = location.city,
            state = location.state,
            country = location.country,
            postcode = location.postcode,
        )
    )
}

fun List<LocalUser>.toDomainUserList(): List<User> {
    return map { it.toDomainUser() }
}
