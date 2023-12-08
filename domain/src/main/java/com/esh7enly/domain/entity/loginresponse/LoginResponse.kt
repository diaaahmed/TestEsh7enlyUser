package com.esh7enly.domain.entity.loginresponse

import com.esh7enly.domain.entity.baseresponse.BaseResponse

data class LoginResponse(
  //  val code: Int,
    val `data`: Data,
    //val message: String,
    //val status: Boolean,
    val service_update_num:String
):BaseResponse()