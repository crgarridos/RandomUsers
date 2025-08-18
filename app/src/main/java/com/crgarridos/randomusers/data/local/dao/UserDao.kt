package com.crgarridos.randomusers.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crgarridos.randomusers.data.local.model.LocalUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<LocalUser>)

    @Query("SELECT * FROM users")
    fun observeAllUsers(): Flow<List<LocalUser>>

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserById(email: String): LocalUser?

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
