package com.esh7enly.data.repo

import com.esh7enly.data.remote.ApiService
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequest
import com.esh7enly.domain.entity.chargebalanceresponse.ChargeBalanceResponse
import com.esh7enly.domain.entity.startsessionresponse.StartSessionResponse
import com.esh7enly.domain.entity.totalamountxpayresponse.GetTotalAmountXPayResponse
import retrofit2.Response

class XPayRepo(private val apiService: ApiService)
{
    suspend fun chargeBalance(token: String, chargeBalanceRequest: ChargeBalanceRequest)
            : Response<ChargeBalanceResponse> = apiService.chargeBalance(token, chargeBalanceRequest)
    suspend fun startSessionForPay(
        token: String,
        amount: String,
        ip: String
    ): Response<StartSessionResponse> = apiService.startSessionForPay(token, amount, ip)

    suspend fun getTotalXPay(token: String, amount: String): Response<GetTotalAmountXPayResponse> = apiService.getTotalXpay(token, amount)

}