package com.esh7enly.esh7enlyuser.util

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException

class NoInternetInterceptor(
    private val internetRepo: InternetAvailabilityRepository
) : Interceptor {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!internetRepo.isConnected.value) {
            throw IOException("No internet connection")
        }
        return chain.proceed(chain.request())
    }
}