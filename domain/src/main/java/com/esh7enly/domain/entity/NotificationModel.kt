package com.esh7enly.domain.entity


data class NotificationModel(val to:String, val data:NotificationData)

data class NotificationData(val title:String, val message:String)

