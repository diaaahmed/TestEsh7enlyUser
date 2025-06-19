package com.esh7enly.domain.entity.userwallet

data class UserWalletResponse(
    val code: Int,
    val `data`: List<Data>,
    val message: String,
    val status: Boolean,
)

data class Data(
    val balance: String,
    val id: Int,
    val status: Int,
    val updated_at: String
)