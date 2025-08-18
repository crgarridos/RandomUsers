package com.crgarridos.randomusers.data.mappers

import com.crgarridos.randomusers.data.remote.model.RemoteName
import com.crgarridos.randomusers.data.remote.model.RemotePicture
import com.crgarridos.randomusers.data.remote.model.RemoteUser
import com.crgarridos.randomusers.domain.model.User

fun RemoteUser.toDomainUser(): User {
    this.email ?: throw IllegalArgumentException("User email cannot be null")

    return User(
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

fun User.toRemoteUser(): RemoteUser {
    val remoteName = RemoteName(
        title = this.title,
        first = this.firstName,
        last = this.lastName
    )

    val remotePicture = RemotePicture(
        large = this.largePictureUrl,
        medium = null,
        thumbnail = this.thumbnailUrl
    )

    return RemoteUser(
        name = remoteName,
        email = this.email,
        phone = this.phone,
        picture = remotePicture,
        nationality = this.nationality
    )
}

fun List<User>.toRemoteUserList(): List<RemoteUser> {
    return this.map(User::toRemoteUser)
}