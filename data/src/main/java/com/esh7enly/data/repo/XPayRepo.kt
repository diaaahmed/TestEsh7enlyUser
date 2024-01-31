package com.esh7enly.data.repo

import com.esh7enly.data.remote.ApiService
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.domain.entity.chargebalanceresponse.ChargeBalanceResponse
import com.esh7enly.domain.entity.startsessionresponse.StartSessionResponse
import com.esh7enly.domain.entity.totalamountxpayresponse.GetTotalAmountXPayResponse
import retrofit2.Response
import javax.inject.Inject

class XPayRepo @Inject constructor(private val apiService: ApiService)
{
    suspend fun chargeBalanceWithPaytabs(token: String, chargeBalanceRequest: ChargeBalanceRequestPaytabs)
            : Response<ChargeBalanceResponse> = apiService.chargeBalanceWithPaytabs(token, chargeBalanceRequest)

    suspend fun startSessionForPay(
        payment_method_type:String,
        transaction_type:String,
        token: String,
        amount: String,
        ip: String
    ): Response<StartSessionResponse> = apiService.startSessionForPay(
        payment_method_type,transaction_type,
        token, amount, ip)

    suspend fun getTotalXPay(token: String, amount: String,
                             payment_method_type:String,
                             transaction_type:String):
            Response<GetTotalAmountXPayResponse> =
        apiService.getTotalXpay(token, amount,payment_method_type,transaction_type)

}