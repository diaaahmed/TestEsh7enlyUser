package com.esh7enly.domain.entity.scheduleinvoice

data class ScheduleInvoiceResponse(
    val code: Int,
    val `data`: Data,
    val message: String,
    val service_update_num: String,
    val status: Boolean
)