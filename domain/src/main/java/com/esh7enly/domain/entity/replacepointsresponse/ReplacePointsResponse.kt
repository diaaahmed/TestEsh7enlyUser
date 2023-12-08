package com.esh7enly.domain.entity.replacepointsresponse

import com.esh7enly.domain.entity.baseresponse.BaseResponse

data class ReplacePointsResponse(
  //  val code: Int,
    val `data`: Data,
  //  val message: String,
    val service_update_num: String,
   // val status: Boolean
): BaseResponse()