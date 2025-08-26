package com.crgarridos.randomusers.data.mappers

import com.crgarridos.randomusers.data.remote.model.RemoteName
import com.crgarridos.randomusers.data.remote.model.RemotePicture
import com.crgarridos.randomusers.data.remote.model.RemoteStreet
import com.crgarridos.randomusers.data.remote.model.RemoteUser
import com.crgarridos.randomusers.data.remote.model.RemoteUserLocation
import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserLocation

fun RemoteUser.toDomainUser(): User {
    requireNotNull(name)
    requireNotNull(picture)
    requireNotNull(location)

    return User(
        title = requireNotNull(name.title),
        firstName = requireNotNull(name.first),
        lastName = requireNotNull(name.last),
        email = requireNotNull(email),
        phone = requireNotNull(phone),
        thumbnailUrl = requireNotNull(picture.thumbnail),
        largePictureUrl = requireNotNull(picture.large),
        nationality = requireNotNull(nationality),
        location = location.toDomainUserLocation()

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

    val remoteLocation = RemoteUserLocation(
        street = RemoteStreet(
            number = this.location.streetNumber,
            name = this.location.streetName
        ),
        city = this.location.city,
        state = this.location.state,
        country = this.location.country,
        postcode = this.location.postcode
    )

    return RemoteUser(
        name = remoteName,
        email = this.email,
        phone = this.phone,
        picture = remotePicture,
        nationality = this.nationality,
        location = remoteLocation,
    )
}

private fun RemoteUserLocation.toDomainUserLocation(): UserLocation {
    requireNotNull(this.street)

    return UserLocation(
        streetNumber = requireNotNull(this.street.number),
        streetName = requireNotNull(this.street.name),
        city = requireNotNull(this.city),
        state = requireNotNull(this.state),
        country = requireNotNull(this.country),
        postcode = requireNotNull(this.postcode),
    )
}


fun List<User>.toRemoteUserList(): List<RemoteUser> {
    return this.map(User::toRemoteUser)
}