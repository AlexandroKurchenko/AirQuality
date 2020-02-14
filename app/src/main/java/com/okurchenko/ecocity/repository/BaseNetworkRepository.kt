package com.okurchenko.ecocity.repository

import com.okurchenko.ecocity.network.NetworkError
import retrofit2.Response

abstract class BaseNetworkRepository {

    protected suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful) {
                return NetworkResult.Success(response.body()!!)
            }
            return NetworkResult.Error(NetworkError(code = response.code(), msg = response.message(), throwable = null))
        } catch (error: Throwable) {
            NetworkResult.Error(NetworkError(throwable = error, msg = null))
        }
    }
}

sealed class NetworkResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : NetworkResult<T>()
    data class Error(val networkError: NetworkError) : NetworkResult<Nothing>()
}