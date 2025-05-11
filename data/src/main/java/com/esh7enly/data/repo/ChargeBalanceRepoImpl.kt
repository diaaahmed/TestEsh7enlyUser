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
        chargeBalanceRequest: ChargeBalanceRequestPaytabs
    ): Response<ChargeBalanceResponse> = apiService.chargeBalanceWithPaytabs(
        urlString = url, chargeBalanceRequest = chargeBalanceRequest
    )

    override suspend fun checkWalletStatus(chargeBalanceRequest: ChargeBalanceRequestPaytabs): Response<ChargeBalanceResponse>  =
        apiService.checkWalletStatus(chargeBalanceRequest)

    override suspend fun startSessionForPay(
        paymentMethodType: String,
        transactionType: String,
        amount: String,
        total_amount:String,
        ip: String
    ): Response<StartSessionResponse> = apiService.startSessionForPay(
        paymentMethodType = paymentMethodType, transactionType = transactionType,
        amount = amount,total_amount = total_amount, ip = ip
    )

    override suspend fun getTotalXPay(
        amount: String,
        paymentMethodType: String,
        transactionType: String
    ): Response<GetTotalAmountXPayResponse> =
        apiService.getTotalXpay(amount = amount, paymentMethodType =paymentMethodType,
            transactionType = transactionType)

}