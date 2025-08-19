package com.crgarridos.randomusers.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.crgarridos.randomusers.data.local.UserLocalDataSource
import com.crgarridos.randomusers.data.local.UserLocalDataSourceImpl
import com.crgarridos.randomusers.data.local.db.AppDatabase
import com.crgarridos.randomusers.data.mappers.toLocalUserList
import com.crgarridos.randomusers.data.mappers.toRemoteUserList
import com.crgarridos.randomusers.data.remote.RandomUserApiService
import com.crgarridos.randomusers.data.remote.UserRemoteDataSource
import com.crgarridos.randomusers.data.remote.UserRemoteDataSourceImpl
import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.util.NetworkError
import com.crgarridos.randomusers.domain.repository.UserRepository
import com.crgarridos.randomusers.test.utils.fixtures.RandomRemoteApiResponseFixtures.generateRandomRemoteApiResponse
import com.crgarridos.randomusers.test.utils.fixtures.UserFixtures
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import java.io.IOException

private const val DUMMY_PAGE_SIZE = 5

@RunWith(AndroidJUnit4::class)
class UserRepositoryImplTest {

    @Test
    fun `fetchUsersPage with page 1 success, then the local users become the remote`() = runTest {
        val localUsers = UserFixtures.generateRandomUsers(DUMMY_PAGE_SIZE)
        val remoteUsers = UserFixtures.generateRandomUsers(DUMMY_PAGE_SIZE)

        val localSource = buildUserLocalDataSource(usersInDB = localUsers)
        val remoteSource = buildUserRemoteDataSource(usersToReturn = remoteUsers)
        val repository = buildUserRepository(remoteSource, localSource)


        repository.getObservableUsers().test {

            assertEquals(localUsers, awaitItem())

            repository.fetchUsersPage(1, DUMMY_PAGE_SIZE)

            assertEquals(remoteUsers, awaitItem())
        }
    }


    @Test
    fun `fetchUsersPage with page 2 success, then remote returns users, local insertOrReplace called (no deleteAll)`() =
        runTest {
            val localUsers = UserFixtures.generateRandomUsers(DUMMY_PAGE_SIZE)
            val remoteUsers = UserFixtures.generateRandomUsers(DUMMY_PAGE_SIZE)


            val localSource = buildUserLocalDataSource(usersInDB = localUsers)
            val remoteSource = buildUserRemoteDataSource(usersToReturn = remoteUsers)
            val repository = buildUserRepository(remoteSource, localSource)


            repository.getObservableUsers().test {

                assertEquals(localUsers, awaitItem())

                repository.fetchUsersPage(2, DUMMY_PAGE_SIZE)

                assertEquals(localUsers + remoteUsers, awaitItem())
            }
        }

    @Test
    fun `fetchUsersPage remote throws HttpException, then returns ServerError`() = runTest {
        val localUsers = UserFixtures.generateRandomUsers(DUMMY_PAGE_SIZE)
        val localSource = buildUserLocalDataSource(usersInDB = localUsers)

        val httpException = mockk<HttpException>()
        val remoteSource = buildUserRemoteDataSource(exceptionToThrow = httpException)

        val repository = buildUserRepository(remoteSource, localSource)


        val result = repository.fetchUsersPage(1, DUMMY_PAGE_SIZE)

        assertEquals(NetworkError.ServerError, result)
    }

    @Test
    fun `fetchUsersPage remote throws IOException, then returns ConnectivityError`() = runTest {
        val localUsers = UserFixtures.generateRandomUsers(DUMMY_PAGE_SIZE)
        val localSource = buildUserLocalDataSource(usersInDB = localUsers)

        val httpException = mockk<IOException>()
        val remoteSource = buildUserRemoteDataSource(exceptionToThrow = httpException)

        val repository = buildUserRepository(remoteSource, localSource)


        val result = repository.fetchUsersPage(1, DUMMY_PAGE_SIZE)
        assertEquals(NetworkError.ConnectivityError, result)
    }

    private fun buildUserRepository(
        remoteDataSource: UserRemoteDataSource = buildUserRemoteDataSource(),
        localDataSource: UserLocalDataSource,
    ): UserRepository {
        return UserRepositoryImpl(
            remoteDataSource = remoteDataSource,
            userLocalDataSource = localDataSource,
        )
    }

    private fun buildUserRemoteDataSource(
        usersToReturn: List<User> = UserFixtures.generateRandomUsers(DUMMY_PAGE_SIZE),
        exceptionToThrow: Exception? = null,
    ): UserRemoteDataSource {
        val apiService = mockk<RandomUserApiService> {
            val stub = coEvery {
                getUsers(
                    seed = any(),
                    results = any(),
                    page = any()
                )
            }

            if (exceptionToThrow != null) {
                stub throws exceptionToThrow
            } else {
                stub returns generateRandomRemoteApiResponse(users = usersToReturn.toRemoteUserList())
            }
        }
        return UserRemoteDataSourceImpl(apiService)
    }

    private suspend fun buildUserLocalDataSource(
        usersInDB: List<User> = UserFixtures.generateRandomUsers(
            DUMMY_PAGE_SIZE
        ),
    ): UserLocalDataSource {
        val applicationContext = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(applicationContext, AppDatabase::class.java)
            .build()

        val dao = db.userDao()
        dao.insertUsers(usersInDB.toLocalUserList())

        return UserLocalDataSourceImpl(
            db.userDao()
        )
    }
}