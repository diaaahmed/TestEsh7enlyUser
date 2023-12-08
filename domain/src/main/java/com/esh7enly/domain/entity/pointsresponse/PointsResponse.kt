package com.esh7enly.domain.entity.pointsresponse

import com.esh7enly.domain.entity.baseresponse.BaseResponse


data class PointsResponse(
  //  val code: Int,
    val `data`: Data,
  //  val message: String,
    val service_update_num: String,
   // val status: Boolean

):BaseResponse()