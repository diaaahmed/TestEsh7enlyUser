package com.esh7enly.domain.entity.userwallet

data class UserWalletResponse(
    val code: Int,
    val `data`: List<Data>,
    val message: String,
    val status: Boolean,
    val service_update_num:String
)