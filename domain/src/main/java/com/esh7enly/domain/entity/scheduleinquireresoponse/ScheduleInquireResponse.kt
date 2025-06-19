package com.esh7enly.domain.entity.scheduleinquireresoponse

data class ScheduleInquireResponse(
    val code: Int,
    val `data`: DataXX,
    val message: String,
    val status: Boolean
)

class DataXX