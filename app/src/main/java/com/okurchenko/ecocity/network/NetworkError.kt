package com.okurchenko.ecocity.network

private const val UNKNOWN_ERROR_CODE = -1

data class NetworkError(
    val code: Int = UNKNOWN_ERROR_CODE,
    val msg: String?,
    val throwable: Throwable?
) : Exception()