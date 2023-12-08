package com.esh7enly.domain.entity.startsessionresponse

data class StartSessionResponse(
    val code: Int,
    val `data`: Data,
    val message: String,
    val status: Boolean
)