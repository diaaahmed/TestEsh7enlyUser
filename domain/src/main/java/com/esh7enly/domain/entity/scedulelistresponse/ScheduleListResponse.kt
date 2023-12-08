package com.esh7enly.domain.entity.scedulelistresponse

import com.esh7enly.domain.entity.baseresponse.BaseResponse

data class ScheduleListResponse(
  //  val code: Int,
    val `data`: List<Data>,
   // val message: String,
    val service_update_num: String,
   // val status: Boolean
):BaseResponse()