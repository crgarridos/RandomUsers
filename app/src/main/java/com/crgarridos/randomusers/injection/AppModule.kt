package com.crgarridos.randomusers.injection

import com.crgarridos.randomusers.ui.presentation.mapper.DomainErrorToStringResourceMapper
import com.crgarridos.randomusers.ui.presentation.mapper.DomainErrorToStringUiMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    @Binds fun bindDomainErrorToStringUiMapper(impl: DomainErrorToStringResourceMapper): DomainErrorToStringUiMapper
}