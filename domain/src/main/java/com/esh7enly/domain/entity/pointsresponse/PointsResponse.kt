package com.esh7enly.domain.entity.pointsresponse

import com.esh7enly.domain.entity.baseresponse.BaseResponse

data class PointsResponse(val `data`: Data, ):BaseResponse()

data class Data(
    val points: String
)