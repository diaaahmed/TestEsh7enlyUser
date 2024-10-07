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
    suspend fun getNewImageAdResponse(token: String): Flow<NetworkResult<ImageAdResponse>>

    suspend fun checkIntegration(token: String, id: String, imei: String): Response<JsonElement>

    suspend fun cancelTransaction(
        token: String,
        transactionId: String,
        imei: String
    ): Response<JsonElement>

    suspend fun inquire(
        token: String,
        paymentPojoModel: PaymentPojoModel
    ): Response<PaymentEntity>

    suspend fun pay(token: String, paymentPojoModel: PaymentPojoModel): Response<PaymentEntity>

    suspend fun replaceUserPoints(token: String): Response<ReplacePointsResponse>

    suspend fun getTotalAmount(
        token: String,
        totalAmountPojoModel: TotalAmountPojoModel
    ): Flow<TotalAmountEntity>

    suspend fun scheduleInquire(
        token: String,
        serviceId: String,
        invoiceNumber: String
    ): Response<ScheduleInquireResponse>
    suspend fun getScheduleList(token: String): Response<ScheduleListResponse>

    suspend fun scheduleInvoice(
        token: String,
        serviceId: String,
        scheduleDay: String,
        invoiceNumber: String
    ): Response<ScheduleInvoiceResponse>
    suspend fun getProviders(token: String, categoryId: String): ProviderResponse?
    suspend fun getCategories(token: String): CategoriesResponse?
    fun getCategoriesFlow(token: String): Flow<NetworkResult<CategoriesResponse>>
    suspend fun getServices(token: String, providerId: String): ServiceResponse?
    suspend fun getParameters(token: String, serviceID: String): ParametersResponse?
    suspend fun serviceSearch(token: String, serviceName: String, page: Int): SearchResponse?
    suspend fun getUserPointsFlow(token: String): Flow<NetworkResult<PointsResponse>>

}