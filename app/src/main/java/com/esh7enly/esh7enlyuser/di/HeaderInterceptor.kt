package com.esh7enly.esh7enlyuser.di

import com.esh7enly.data.sharedhelper.SharedHelper

import okhttp3.Interceptor
import okhttp3.Response


class HeaderInterceptor(
    private val sharedHelper: SharedHelper?
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response
    {
        val request = chain.request()

        val builder = request.newBuilder()

        if(!request.url.toString().contains("auth"))
        {
            sharedHelper?.let {
                builder.addHeader("Authorization",it.getUserToken())
            }
        }

        // Build the new request
        val newRequest = builder.build()


        return chain.proceed(newRequest)
    }
}