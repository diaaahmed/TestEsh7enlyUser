package com.esh7enly.domain.usecase

import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequest
import com.esh7enly.domain.repo.ServicesRepo

class XPayUseCase(private val servicesRepo: ServicesRepo)
{
    suspend fun chargeBalance(token:String, chargeBalanceRequest: ChargeBalanceRequest) = servicesRepo.chargeBalance(token,chargeBalanceRequest)

    suspend fun startSessionForPay(token:String,amount:String,ip:String) = servicesRepo.startSessionForPay(token, amount, ip)

    suspend fun getTotalXPay(token:String, amount:String) = servicesRepo.getTotalXPay(token, amount)
}