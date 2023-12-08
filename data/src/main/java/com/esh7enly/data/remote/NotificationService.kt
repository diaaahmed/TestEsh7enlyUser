package com.esh7enly.data.remote

import com.esh7enly.domain.entity.NotificationModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService
{
    @Headers(
        "Authorization: key=AAAAnXnjYlw:APA91bF_c348te5LEJO8hzkmVfSijq9EPqXsI141qbJ2oHIwgCxlXbWbIhRxiQLHQWBVzfYoTkdI1v-7frS8-5TAHuCnrVjXC_artWxeet58nvMhyzsGQmARjDTIXHtuoGs6mdpqqlt5"
        ,
        "Content-Type:application/json"
    )
    @POST("fcm/send")
    suspend fun sendNotification(@Body notificationModel: NotificationModel):Response<ResponseBody>
}