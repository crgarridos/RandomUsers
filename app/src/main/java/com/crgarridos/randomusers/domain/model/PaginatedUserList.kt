package com.crgarridos.randomusers.domain.model

data class PaginatedUserList(
    val users: List<User>,
    val nextPage: Int,
)
