package com.esh7enly.domain.entity.otpresponse

import com.esh7enly.domain.entity.baseresponse.BaseResponse

data class NewOtpResponse(
  //  val code: Int,
    val `data`: Data,
   // val message: String,
    val pos_brand: List<PosBrand>,
    //val status: Boolean,
    val version: Version
):BaseResponse()