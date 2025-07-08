package com.esh7enly.domain.entity.userwallet

data class UserWalletResponse(
    val code: Int,
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val app_version:String

)

data class Data(
    val balance: String,
    val id: Int,
    val status: Int,
    val updated_at: String,

)