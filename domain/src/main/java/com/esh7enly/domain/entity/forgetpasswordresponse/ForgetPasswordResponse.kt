package com.esh7enly.domain.entity.forgetpasswordresponse

data class ForgetPasswordResponse(
    val code: Int,
    val `data`: Data,
    val message: String,
    val status: Boolean
)