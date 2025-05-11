package com.esh7enly.domain.repo

import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.domain.entity.chargebalanceresponse.ChargeBalanceResponse
import com.esh7enly.domain.entity.startsessionresponse.StartSessionResponse
import com.esh7enly.domain.entity.totalamountxpayresponse.GetTotalAmountXPayResponse
import retrofit2.Response

interface ChargeBalanceRepo
{
    suspend fun chargeBalanceWithPaytabs(
        url:String,
        chargeBalanceRequest: ChargeBalanceRequestPaytabs
    ): Response<ChargeBalanceResponse>

    suspend fun checkWalletStatus(
        chargeBalanceRequest: ChargeBalanceRequestPaytabs
    ): Response<ChargeBalanceResponse>

    suspend fun startSessionForPay(
        paymentMethodType: String,
        transactionType: String,
        amount: String,
        total_amount:String,
        ip: String
    ): Response<StartSessionResponse>

    suspend fun getTotalXPay(
        amount: String,
        paymentMethodType: String,
        transactionType: String
    ): Response<GetTotalAmountXPayResponse>
}