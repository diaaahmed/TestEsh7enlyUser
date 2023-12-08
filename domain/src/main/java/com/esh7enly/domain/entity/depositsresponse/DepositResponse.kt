package com.esh7enly.domain.entity.depositsresponse

data class DepositResponse(
    val code: Int,
    val `data`: Data,
    val message: String,
    val status: Boolean
)