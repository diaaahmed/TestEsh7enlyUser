package com.esh7enly.domain

sealed class NetworkResult<T>(
    val data: T? = null,
    val code:Int?= null,
    val message: String? = null,
) {

    class Success<T>(data: T) : NetworkResult<T>(data)

    class Error<T>(message: String?,code: Int?, data: T? = null) :
        NetworkResult<T>(data, code,message){
        fun parseError(): String {
            return if (message!!.contains("failed to connect to")) "Failed to Connect to the Server"
            else if (message.contains("timeout")) "Connection Timeout. Please try again."
            else if(message.contains("<html")) "Error happened. Please try again."
            else message
        }
        }

    class Loading<T> : NetworkResult<T>()

}