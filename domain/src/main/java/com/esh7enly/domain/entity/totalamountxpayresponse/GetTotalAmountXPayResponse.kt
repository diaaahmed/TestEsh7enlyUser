package com.esh7enly.domain.entity.totalamountxpayresponse

data class GetTotalAmountXPayResponse(
    val code: Int,
    val `data`: Data,
    val message: String,
    val status: Boolean
)

data class Data(
    val amount: Double,
    val fees: Double
)