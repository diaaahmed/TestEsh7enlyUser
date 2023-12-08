package com.esh7enly.domain.entity.loginresponse

data class Data(
    val token: String,
    val id:Int,
    val name:String,
    val mobile:String,
    val email:String,
    val last_login:String,
    val points:String,
    val device_token:String
)