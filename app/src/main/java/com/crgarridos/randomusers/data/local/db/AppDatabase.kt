package com.crgarridos.randomusers.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.crgarridos.randomusers.data.local.dao.UserDao
import com.crgarridos.randomusers.data.local.model.LocalUser

@Database(entities = [LocalUser::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "random_users_db"
    }
}
