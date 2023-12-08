package com.esh7enly.domain.entity.chargebalanceresponse

data class ChargeBalanceResponse(
    val code: Int,
    val data: Data,
    val message: String,
    val status: Boolean
)