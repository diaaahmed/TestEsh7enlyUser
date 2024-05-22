package com.esh7enly.data.repo

import com.esh7enly.data.remote.ApiService
import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.entity.categoriesNew.CategoriesResponse
import com.esh7enly.domain.entity.parametersNew.ParametersResponse
import com.esh7enly.domain.entity.pointsresponse.PointsResponse
import com.esh7enly.domain.entity.providersNew.ProviderResponse
import com.esh7enly.domain.entity.searchresponse.SearchResponse
import com.esh7enly.domain.entity.servicesNew.ServiceResponse
import com.esh7enly.domain.repo.ServicesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class ServiceRepoImplNewTest(
    private val apiService: ApiService

) : ServicesRepo {

    override suspend fun getProviders(token: String, categoryId: String): ProviderResponse? {

        val providers = apiService.getProviders(token, categoryId)

        return if (providers.isSuccessful) {
            providers.body()
        } else {
            ProviderResponse(code = providers.code(), message = providers.message())
        }

    }

    override suspend fun getCategories(token: String): CategoriesResponse? {
        val categories = apiService.getCategories(token)

        return if (categories.isSuccessful) {
            categories.body()
        } else {
            CategoriesResponse(code = categories.code(), message = categories.message())
        }
    }

    override suspend fun getCategoriesFlow(token: String): Flow<NetworkResult<CategoriesResponse>> =
        flow {
            try {
                emit(NetworkResult.Loading())

                val categories = apiService.getCategories(token)
                if (categories.isSuccessful) {
                    emit(NetworkResult.Success(categories.body()!!))
                } else {
                    emit(NetworkResult.Error(categories.message(),categories.code()))
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error(e.message,0))
            }
        }

    override suspend fun getServices(token: String, providerId: String): ServiceResponse? {
        val services = apiService.getServices(token, providerId)

        return if (services.isSuccessful) {
            services.body()
        } else {
            ServiceResponse(code = services.code(), message = services.message())
        }
    }

    override suspend fun getParameters(token: String, serviceID: String): ParametersResponse? {
        val parameters = apiService.getParameters(token, serviceID)

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
        val serviceSearch = apiService.serviceSearch(token, serviceName, page)

        return if (serviceSearch.isSuccessful) {
            serviceSearch.body()
        } else {
            SearchResponse(code = serviceSearch.code(), message = serviceSearch.message())
        }
    }

    override suspend fun getUserPointsFlow(token: String): Flow<NetworkResult<PointsResponse>> = flow{

        try {
            emit(NetworkResult.Loading())

            val userPoints = apiService.getUserPoints(token)
            if (userPoints.isSuccessful) {
                emit(NetworkResult.Success(userPoints.body()!!))
            } else {
                emit(NetworkResult.Error(userPoints.message(),userPoints.code()))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message,0))
        }
    }

}