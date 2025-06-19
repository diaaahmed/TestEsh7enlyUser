package com.esh7enly.domain.entity.otpresponse

import com.esh7enly.domain.entity.baseresponse.BaseResponse

data class NewOtpResponse(val `data`: Data):BaseResponse()

data class Data(val key: String)
