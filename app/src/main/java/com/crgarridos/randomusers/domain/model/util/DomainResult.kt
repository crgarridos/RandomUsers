package com.crgarridos.randomusers.domain.model.util

sealed class DomainResult<ErrorModel, SuccessModel> {
    data class Success<SuccessModel>(val data: SuccessModel) : DomainResult<Nothing, SuccessModel>()
    data class Error<ErrorModel>(val error: ErrorModel) : DomainResult<ErrorModel, Nothing>()
}
