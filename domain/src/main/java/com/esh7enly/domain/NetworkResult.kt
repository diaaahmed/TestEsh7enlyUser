package com.esh7enly.domain

sealed class NetworkResult<T>(
    val data: T? = null,
    val code:Int?= null,
    val message: String? = null,
) {

    class Success<T>(data: T) : NetworkResult<T>(data)

    class Error<T>(message: String?,code: Int?, data: T? = null) :
        NetworkResult<T>(data, code,message)

    class Loading<T> : NetworkResult<T>()

}