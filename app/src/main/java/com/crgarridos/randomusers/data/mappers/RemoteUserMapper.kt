package com.crgarridos.randomusers.data.mappers

import com.crgarridos.randomusers.data.remote.model.RemoteUser
import com.crgarridos.randomusers.domain.model.User

fun RemoteUser.toDomainUser(): User {
    val userId = this.email ?: throw IllegalArgumentException("User email cannot be null")
    return User(
        id = userId,
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

fun List<RemoteUser>.toDomainUserList(): List<User> {
    return this.map(RemoteUser::toDomainUser)
}
