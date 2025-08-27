package com.crgarridos.randomusers.ui.presentation.mapper

import com.crgarridos.randomusers.domain.model.util.DomainError

class FakeDomainErrorToStringUiMapper(private val map: Map<DomainError<*>, String>) : DomainErrorToStringUiMapper {

    override fun resolve(error: DomainError<*>): String {
        return map[error] ?: "Unknown error"
    }
}