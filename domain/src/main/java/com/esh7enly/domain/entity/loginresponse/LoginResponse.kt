package com.esh7enly.domain.entity.loginresponse

import com.esh7enly.domain.entity.baseresponse.BaseResponse

data class LoginResponse(
    val `data`: Data,
    val service_update_num:String
):BaseResponse()