package com.esh7enly.domain.entity.scedulelistresponse

data class Data(
    val id: Int,
    val invoice_number: String,
    val schedule_date: Int,
    val service_id: Int,
    val user_id: Int,
    val name_ar: String,
    val name_en:String
)