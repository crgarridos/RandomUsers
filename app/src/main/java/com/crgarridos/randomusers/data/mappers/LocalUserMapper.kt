package com.crgarridos.randomusers.data.mappers

import com.crgarridos.randomusers.data.local.model.LocalUser
import com.crgarridos.randomusers.data.local.model.LocalUserName
import com.crgarridos.randomusers.data.local.model.LocalUserPicture
import com.crgarridos.randomusers.domain.model.User

fun User.toLocalUser(): LocalUser {
    return LocalUser(
        email = this.email,
        name = LocalUserName(title = this.title, first = this.firstName, last = this.lastName),
        phone = this.phone,
        picture = LocalUserPicture(
            large = this.largePictureUrl,
            medium = null,
            thumbnail = this.thumbnailUrl
        ),
        nationality = this.nationality
    )
}

fun List<User>.toLocalUserList(): List<LocalUser> {
    return this.map { it.toLocalUser() }
}

fun LocalUser.toDomainUser(): User {
    return User(
        id = this.email,
        title = this.name?.title ?: "",
        firstName = this.name?.first ?: "",
        lastName = this.name?.last ?: "",
        email = this.email,
        phone = this.phone,
        thumbnailUrl = this.picture?.thumbnail ?: "",
        largePictureUrl = this.picture?.large,
        nationality = this.nationality
    )
}

fun List<LocalUser>.toDomainUserList(): List<User> {
    return this.map { it.toDomainUser() }
}
