package com.esh7enly.domain.usecase

import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.repo.ServicesRepo

class MainUseCase(private val servicesRepo: ServicesRepo)
{
    suspend  fun getTotalAmount(token:String,totalAmountPojoModel: TotalAmountPojoModel)
            = servicesRepo.getTotalAmount(token,totalAmountPojoModel)

    suspend fun pay(token: String,paymentPojoModel: PaymentPojoModel) = servicesRepo.pay(token,paymentPojoModel)

    suspend fun inquire(token: String,paymentPojoModel: PaymentPojoModel) = servicesRepo.inquire(token,paymentPojoModel)

    suspend fun checkIntegration(token:String,id:String,imei:String) = servicesRepo.checkIntegration(token,id,imei)

    suspend fun getImageAdResponse(token: String) = servicesRepo.getImageAdResponse(token)

    suspend fun cancelTransaction(token:String,transaction_id:String,imei: String) =
        servicesRepo.cancelTransaction(token,transaction_id,imei)

}