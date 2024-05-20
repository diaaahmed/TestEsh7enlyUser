package com.esh7enly.domain.entity.pointsresponse

import com.esh7enly.domain.entity.baseresponse.BaseResponse


data class PointsResponse(
    val `data`: Data,
    val service_update_num: String,

):BaseResponse()