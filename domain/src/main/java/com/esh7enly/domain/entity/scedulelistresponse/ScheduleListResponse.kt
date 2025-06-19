package com.esh7enly.domain.entity.scedulelistresponse

import com.esh7enly.domain.entity.baseresponse.BaseResponse

data class ScheduleListResponse(val `data`: List<Data>):BaseResponse()

data class Data(
    val id: Int,
    val invoice_number: String,
    val schedule_date: Int,
    val service_id: Int,
    val user_id: Int,
    val name_ar: String,
    val name_en:String
)