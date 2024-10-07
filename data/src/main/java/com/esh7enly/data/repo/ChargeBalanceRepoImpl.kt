package com.esh7enly.data.repo

import com.esh7enly.data.remote.ApiService
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.domain.entity.chargebalanceresponse.ChargeBalanceResponse
import com.esh7enly.domain.entity.startsessionresponse.StartSessionResponse
import com.esh7enly.domain.entity.totalamountxpayresponse.GetTotalAmountXPayResponse
import com.esh7enly.domain.repo.ChargeBalanceRepo
import retrofit2.Response

class ChargeBalanceRepoImpl(private val apiService: ApiService): ChargeBalanceRepo {
    override suspend fun chargeBalanceWithPaytabs(
        url:String,
        token: String,
        chargeBalanceRequest: ChargeBalanceRequestPaytabs
    ): Response<ChargeBalanceResponse> = apiService.chargeBalanceWithPaytabs(
        url,token, chargeBalanceRequest
    )

    override suspend fun startSessionForPay(
        paymentMethodType: String,
        transactionType: String,
        token: String,
        amount: String,
        ip: String
    ): Response<StartSessionResponse> = apiService.startSessionForPay(
        paymentMethodType, transactionType,
        token, amount, ip
    )

    override suspend fun getTotalXPay(
        token: String, amount: String,
        paymentMethodType: String,
        transactionType: String
    ): Response<GetTotalAmountXPayResponse> =
        apiService.getTotalXpay(token, amount, paymentMethodType, transactionType)

}