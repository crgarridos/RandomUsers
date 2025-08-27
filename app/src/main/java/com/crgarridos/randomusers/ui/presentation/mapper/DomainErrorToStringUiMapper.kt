package com.crgarridos.randomusers.ui.presentation.mapper

import android.content.Context
import com.crgarridos.randomusers.R
import com.crgarridos.randomusers.domain.model.util.DomainError
import com.crgarridos.randomusers.domain.model.util.NetworkError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface DomainErrorToStringUiMapper {
    fun resolve(error: DomainError<*>): String
}

class DomainErrorToStringResourceMapper @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context
): DomainErrorToStringUiMapper {
        override fun resolve(error: DomainError<*>): String {
            return when (error) {
                is NetworkError.ConnectivityError  -> applicationContext.getString(R.string.error_network_connectivity)
                is NetworkError.ServerError  -> applicationContext.getString(R.string.error_network_server)
                else -> applicationContext.getString(R.string.error_unknown)
            }
        }
    }