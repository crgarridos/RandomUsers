package com.crgarridos.randomusers.data.injection

import com.crgarridos.randomusers.data.UserRepositoryImpl
import com.crgarridos.randomusers.data.local.UserLocalDataSource
import com.crgarridos.randomusers.data.local.UserLocalDataSourceImpl
import com.crgarridos.randomusers.data.remote.UserRemoteDataSource
import com.crgarridos.randomusers.data.remote.UserRemoteDataSourceImpl
import com.crgarridos.randomusers.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds fun userLocalDataSource(impl: UserLocalDataSourceImpl): UserLocalDataSource
    @Binds fun userRemoteDataSource(impl: UserRemoteDataSourceImpl): UserRemoteDataSource
    @Binds fun userRepository(impl: UserRepositoryImpl): UserRepository
}
