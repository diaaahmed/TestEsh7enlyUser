package com.esh7enly.data.repo

import com.esh7enly.data.remote.ApiService
import com.esh7enly.domain.entity.PaymentEntity
import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.domain.entity.TotalAmountEntity
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.imageadsresponse.ImageAdResponse
import com.esh7enly.domain.entity.replacepointsresponse.ReplacePointsResponse
import com.esh7enly.domain.entity.scedulelistresponse.ScheduleListResponse
import com.esh7enly.domain.entity.scheduleinquireresoponse.ScheduleInquireResponse
import com.esh7enly.domain.entity.scheduleinvoice.ScheduleInvoiceResponse
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class ServicesRepoImpl(
    private val apiService: ApiService) {
    suspend fun scheduleInvoice(
        token: String,
        serviceId: String,
        scheduleDay: String,
        invoice_number: String
    ): Response<ScheduleInvoiceResponse> =
        apiService.scheduleInvoice(token, serviceId, scheduleDay, invoice_number)

    suspend fun getScheduleList(token: String): Response<ScheduleListResponse> =
        apiService.getScheduleList(token)

    suspend fun scheduleInquire(
        token: String,
        serviceId: String,
        invoice_number: String
    ): Response<ScheduleInquireResponse> =
        apiService.scheduleInquire(token, serviceId, invoice_number)


    suspend fun getTotalAmount(
        token: String,
        totalAmountPojoModel: TotalAmountPojoModel
    ): Flow<TotalAmountEntity> = flow {
        val response = apiService.getTotalAmount(token, totalAmountPojoModel)
        emit(response)
    }.flowOn(Dispatchers.IO)


    suspend fun replaceUserPoints(token: String): Response<ReplacePointsResponse> =
        apiService.replaceUserPoints(token)

    suspend fun pay(token: String, paymentPojoModel: PaymentPojoModel): Response<PaymentEntity> =
        apiService.pay(token, paymentPojoModel)


    suspend fun inquire(
        token: String,
        paymentPojoModel: PaymentPojoModel
    ): Response<PaymentEntity> = apiService.inquire(token, paymentPojoModel)

    suspend fun checkIntegration(token: String, id: String, imei: String): Response<JsonElement> =
        apiService.checkIntegration(token, id, imei)


    suspend fun getImageAdResponse(token: String): Response<ImageAdResponse> =
        apiService.getImageAdResponse(token)

    suspend fun cancelTransaction(
        token: String,
        transaction_id: String,
        imei: String
    ): Response<JsonElement> = apiService.cancelTransaction(token, transaction_id, imei)

}