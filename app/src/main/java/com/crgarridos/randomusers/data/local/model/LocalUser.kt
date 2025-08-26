package com.crgarridos.randomusers.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

data class LocalUserName(
    val title: String,
    val first: String,
    val last: String
)

data class LocalUserPicture(
    val large: String,
    val thumbnail: String
)

data class LocalUserLocation(
    val streetNumber: String,
    val streetName: String,
    val city: String,
    val state: String,
    val country: String,
    val postcode: String,
)

@Entity(tableName = "users")
data class LocalUser(
    @PrimaryKey val email: String,
    @Embedded(prefix = "name_") val name: LocalUserName,
    val phone: String,
    @Embedded(prefix = "picture_") val picture: LocalUserPicture,
    val nationality: String,
    @Embedded(prefix = "location_") val location: LocalUserLocation,
)
