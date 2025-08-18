package com.crgarridos.randomusers.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO get rid of nullables ??
data class LocalUserName(
    val title: String?,
    val first: String?,
    val last: String?
)

data class LocalUserPicture(
    val large: String?,
    val medium: String?,
    val thumbnail: String?
)

@Entity(tableName = "users")
data class LocalUser(
    @PrimaryKey val email: String,
    @Embedded(prefix = "name_") val name: LocalUserName?,
    val phone: String?,
    @Embedded(prefix = "picture_") val picture: LocalUserPicture?,
    val nationality: String?,
)
