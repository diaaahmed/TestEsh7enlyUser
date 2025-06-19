package com.esh7enly.domain.entity.verifyotp

data class VerifyOtpResponse(
    val code: Int,
    val `data`: Data,
    val message: String,
    val status: Boolean,
)

class Data