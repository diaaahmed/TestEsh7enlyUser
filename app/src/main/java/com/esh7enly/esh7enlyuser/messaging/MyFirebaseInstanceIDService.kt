package com.esh7enly.esh7enlyuser.messaging


import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MyFirebaseInstanceIDSer"

@AndroidEntryPoint
class MyFirebaseInstanceIDService: FirebaseMessagingService()
{
    private val channelID = "esh7elny"

    @Inject
    lateinit var mNotificationManager:MyNotificationManager

//    override fun onNewToken(token: String)
//    {
//        super.onNewToken(token)
//        Log.d(TAG, "diaa onNewToken: $token")
//    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if(message.data.isNotEmpty())
        {
            Log.d(TAG, "diaa onMessageReceived: ${message.data} ")

            try{

                val title = message.data["title"]
                val message = message.data["message"]

                mNotificationManager.textNotification(title,message)

            }
            catch(e:Exception)
            {
                Log.d(TAG, "diaa onMessageReceived exception: ${e.message}")
            }
        }
    }

}