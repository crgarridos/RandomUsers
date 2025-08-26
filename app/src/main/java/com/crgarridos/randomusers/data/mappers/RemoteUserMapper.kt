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
    return map(RemoteUser::toDomainUser)
}

fun User.toRemoteUser(): RemoteUser {
    val remoteName = RemoteName(
        title = title,
        first = firstName,
        last = lastName
    )

    val remotePicture = RemotePicture(
        large = largePictureUrl,
        medium = null,
        thumbnail = thumbnailUrl
    )

    val remoteLocation = RemoteUserLocation(
        street = RemoteStreet(
            number = location.streetNumber,
            name = location.streetName
        ),
        city = location.city,
        state = location.state,
        country = location.country,
        postcode = location.postcode
    )

    return RemoteUser(
        name = remoteName,
        email = email,
        phone = phone,
        picture = remotePicture,
        nationality = nationality,
        location = remoteLocation,
    )
}

private fun RemoteUserLocation.toDomainUserLocation(): UserLocation {
    requireNotNull(street)

    return UserLocation(
        streetNumber = requireNotNull(street.number),
        streetName = requireNotNull(street.name),
        city = requireNotNull(city),
        state = requireNotNull(state),
        country = requireNotNull(country),
        postcode = requireNotNull(postcode),
    )
}


fun List<User>.toRemoteUserList(): List<RemoteUser> {
    return map(User::toRemoteUser)
}
