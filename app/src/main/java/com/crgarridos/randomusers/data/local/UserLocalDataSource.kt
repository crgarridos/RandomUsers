package com.crgarridos.randomusers.data.local

import com.crgarridos.randomusers.data.local.dao.UserDao
import com.crgarridos.randomusers.data.mappers.toDomainUser
import com.crgarridos.randomusers.data.mappers.toDomainUserList
import com.crgarridos.randomusers.data.mappers.toLocalUserList
import com.crgarridos.randomusers.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        return userDao.observeAllUsers().map { it.toDomainUserList() }
    }

    override suspend fun getUserById(id: String): User? {
        return userDao.getUserById(id)?.toDomainUser()
    }

    override suspend fun insertOrReplace(users: List<User>) {
        userDao.insertUsers(users.toLocalUserList())
    }

    override suspend fun deleteAll() {
        userDao.deleteAllUsers()
    }

}