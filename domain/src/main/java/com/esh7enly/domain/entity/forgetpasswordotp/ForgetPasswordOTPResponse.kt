package com.esh7enly.domain.entity.forgetpasswordotp

data class ForgetPasswordOTPResponse(
    val code: Int,
    val `data`: Data,
    val message: String,
    val status: Boolean
)