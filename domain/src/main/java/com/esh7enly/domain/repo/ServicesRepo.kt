package com.esh7enly.domain.repo

import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.entity.PaymentEntity
import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.domain.entity.TotalAmountEntity
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.categoriesNew.CategoriesResponse
import com.esh7enly.domain.entity.imageadsresponse.ImageAdResponse
import com.esh7enly.domain.entity.parametersNew.ParametersResponse
import com.esh7enly.domain.entity.pointsresponse.PointsResponse
import com.esh7enly.domain.entity.providersNew.ProviderResponse
import com.esh7enly.domain.entity.replacepointsresponse.ReplacePointsResponse
import com.esh7enly.domain.entity.scedulelistresponse.ScheduleListResponse
import com.esh7enly.domain.entity.scheduleinquireresoponse.ScheduleInquireResponse
import com.esh7enly.domain.entity.scheduleinvoice.ScheduleInvoiceResponse
import com.esh7enly.domain.entity.searchresponse.SearchResponse
import com.esh7enly.domain.entity.servicesNew.ServiceResponse
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ServicesRepo {
    suspend fun getImageAdResponse(token: String): Response<ImageAdResponse>

    suspend fun getNewImageAdResponse(): Flow<NetworkResult<ImageAdResponse>>

    suspend fun checkIntegration(id: String, imei: String): Response<JsonElement>

    suspend fun cancelTransaction(
        transactionId: String,
        imei: String
    ): Response<JsonElement>

    suspend fun inquire(
        paymentPojoModel: PaymentPojoModel
    ): Response<PaymentEntity>

    suspend fun pay(paymentPojoModel: PaymentPojoModel): Response<PaymentEntity>

    suspend fun replaceUserPoints(): Response<ReplacePointsResponse>

    suspend fun getTotalAmount(
        totalAmountPojoModel: TotalAmountPojoModel
    ): Flow<TotalAmountEntity>

    suspend fun scheduleInquire(
        serviceId: String,
        invoiceNumber: String
    ): Response<ScheduleInquireResponse>
    suspend fun getScheduleList(): Response<ScheduleListResponse>

    suspend fun scheduleInvoice(
        serviceId: String,
        scheduleDay: String,
        invoiceNumber: String
    ): Response<ScheduleInvoiceResponse>
    suspend fun getProviders(categoryId: String): ProviderResponse?
    suspend fun getCategories(token: String): CategoriesResponse?
    fun getCategoriesFlow(): Flow<NetworkResult<CategoriesResponse>>
    suspend fun getServices(providerId: String): ServiceResponse?
    suspend fun getParameters(serviceID: String): ParametersResponse?
    suspend fun serviceSearch(serviceName: String, page: Int): SearchResponse?
    suspend fun getUserPointsFlow(): Flow<NetworkResult<PointsResponse>>

}