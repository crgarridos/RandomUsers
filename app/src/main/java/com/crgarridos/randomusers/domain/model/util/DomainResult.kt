package com.crgarridos.randomusers.domain.model.util

sealed interface DomainResult<out ErrorModel, out SuccessModel>

data class DomainSuccess<out ErrorModel, out SuccessModel>(val data: SuccessModel) :
    DomainResult<ErrorModel, SuccessModel>

fun <T : Any> T.toDomainSuccess(): DomainResult<Nothing, T> = DomainSuccess(this)

interface DomainError<out ErrorModel> : DomainResult<ErrorModel, Nothing>

sealed class NetworkError : DomainError<Nothing> {
    object ConnectivityError : NetworkError()
    object ServerError : NetworkError()
}