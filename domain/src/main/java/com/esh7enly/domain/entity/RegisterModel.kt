package com.esh7enly.domain.entity

data class RegisterModel(
    val name:String,
    val mobile:String,
    val password:String,
    val password_confirm:String,
    val email:String)
