package com.esh7enly.data.repo

import com.esh7enly.data.remote.ApiService
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
import com.esh7enly.domain.repo.ServicesRepo
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response


class ServiceRepoImpl(
    private val apiService: ApiService

) : ServicesRepo {
    override suspend fun getImageAdResponse(token: String): Response<ImageAdResponse> =
        apiService.getImageAdResponse()

    override suspend fun getNewImageAdResponse():
            Flow<NetworkResult<ImageAdResponse>> = flow {
        try {
            emit(NetworkResult.Loading())
            val dynamicAdsResponse = apiService.getImageAdResponse()
            if(dynamicAdsResponse.isSuccessful)
            {
             emit(NetworkResult.Success(dynamicAdsResponse.body()!!))
            }
            else
            {
                emit(NetworkResult.Error(dynamicAdsResponse.message(),dynamicAdsResponse.code()))

            }
        }
        catch (e: Exception)
        {
            emit(NetworkResult.Error(e.message,0))
        }
    }

    override suspend fun checkIntegration(
        token: String,
        id: String,
        imei: String
    ): Response<JsonElement> = apiService.checkIntegration(id, imei)

    override suspend fun cancelTransaction(
        token: String,
        transactionId: String,
        imei: String
    ): Response<JsonElement> = apiService.cancelTransaction(transactionId, imei)

    override suspend fun inquire(
        token: String,
        paymentPojoModel: PaymentPojoModel
    ): Response<PaymentEntity> = apiService.inquire(paymentPojoModel)

    override suspend fun pay(
        token: String,
        paymentPojoModel: PaymentPojoModel
    ): Response<PaymentEntity> = apiService.pay(paymentPojoModel)


    override suspend fun replaceUserPoints(token: String): Response<ReplacePointsResponse> =
        apiService.replaceUserPoints()

    override suspend fun getTotalAmount(
        token: String,
        totalAmountPojoModel: TotalAmountPojoModel
    ): Flow<TotalAmountEntity> = flow {
        val response = apiService.getTotalAmount(totalAmountPojoModel)
        emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun scheduleInquire(
        token: String,
        serviceId: String,
        invoiceNumber: String
    ): Response<ScheduleInquireResponse> =
        apiService.scheduleInquire(serviceId, invoiceNumber)

    override suspend fun getScheduleList(token: String): Response<ScheduleListResponse> =
        apiService.getScheduleList()

    override suspend fun scheduleInvoice(
        token: String,
        serviceId: String,
        scheduleDay: String,
        invoiceNumber: String
    ): Response<ScheduleInvoiceResponse> =
        apiService.scheduleInvoice(serviceId, scheduleDay, invoiceNumber)

    override suspend fun getProviders(token: String, categoryId: String): ProviderResponse? {

        val providers = apiService.getProviders(categoryId)

        return if (providers.isSuccessful) {
            providers.body()
        } else {
            ProviderResponse(code = providers.code(), message = providers.message())
        }

    }

    override suspend fun getCategories(token: String): CategoriesResponse? {
        val categories = apiService.getCategories()

        return if (categories.isSuccessful) {
            categories.body()
        } else {
            CategoriesResponse(code = categories.code(), message = categories.message())
        }
    }

    override fun getCategoriesFlow(): Flow<NetworkResult<CategoriesResponse>> =
        flow {
            try {
                emit(NetworkResult.Loading())

                val categories = apiService.getCategories()
                if (categories.isSuccessful) {
                    emit(NetworkResult.Success(categories.body()!!))
                } else {
                    emit(NetworkResult.Error(categories.message(), categories.code()))
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error(e.message, 0))
            }
        }

    override suspend fun getServices(token: String, providerId: String): ServiceResponse? {
        val services = apiService.getServices(providerId)

        return if (services.isSuccessful) {
            services.body()
        } else {
            ServiceResponse(code = services.code(), message = services.message())
        }
    }

    override suspend fun getParameters(serviceID: String): ParametersResponse? {
        val parameters = apiService.getParameters(serviceID)

        return if (parameters.isSuccessful) {
            parameters.body()
        } else {
            ParametersResponse(code = parameters.code(), message = parameters.message())
        }
    }

    override suspend fun serviceSearch(
        token: String,
        serviceName: String,
        page: Int
    ): SearchResponse? {
        val serviceSearch = apiService.serviceSearch(serviceName, page)

        return if (serviceSearch.isSuccessful) {
            serviceSearch.body()
        } else {
            SearchResponse(code = serviceSearch.code(), message = serviceSearch.message())
        }
    }

    override suspend fun getUserPointsFlow(token: String): Flow<NetworkResult<PointsResponse>> =
        flow {

            try {
                emit(NetworkResult.Loading())

                val userPoints = apiService.getUserPoints()

                if (userPoints.isSuccessful) {
                    emit(NetworkResult.Success(userPoints.body()!!))
                } else {
                    emit(NetworkResult.Error(userPoints.message(), userPoints.code()))
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error(e.message, 0))
            }
        }
}