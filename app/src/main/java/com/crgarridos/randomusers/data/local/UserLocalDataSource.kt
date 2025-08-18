package com.crgarridos.randomusers.data.local

import com.crgarridos.randomusers.data.local.dao.UserDao
import com.crgarridos.randomusers.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserLocalDataSource {

    fun getObservableUsers(): Flow<List<User>>
    suspend fun getUserById(id: String): User?
    suspend fun insertOrReplace(users: List<User>)
    suspend fun deleteAll()
}

class UserLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao,
) : UserLocalDataSource {
    override fun getObservableUsers(): Flow<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(id: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrReplace(users: List<User>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }

}