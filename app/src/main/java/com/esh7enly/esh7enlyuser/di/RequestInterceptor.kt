package com.esh7enly.esh7enlyuser.di

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class RequestInterceptor: Interceptor
{
    override fun intercept(chain: Interceptor.Chain): Response
    {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val url =
            originalUrl.newBuilder()
                .build()

        val requestBuilder: Request.Builder = originalRequest.newBuilder().url(url)
        val request: Request = requestBuilder.build()
        return chain.proceed(request)

    }
}