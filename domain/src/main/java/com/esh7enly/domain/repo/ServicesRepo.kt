package com.esh7enly.domain.repo

import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.entity.categoriesNew.CategoriesResponse
import com.esh7enly.domain.entity.parametersNew.ParametersResponse
import com.esh7enly.domain.entity.pointsresponse.PointsResponse
import com.esh7enly.domain.entity.providersNew.ProviderResponse
import com.esh7enly.domain.entity.searchresponse.SearchResponse
import com.esh7enly.domain.entity.servicesNew.ServiceResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ServicesRepo
{
    suspend fun getProviders(token: String,categoryId:String): ProviderResponse?
    suspend fun getCategories(token: String): CategoriesResponse?

    suspend fun getCategoriesFlow(token: String): Flow<NetworkResult<CategoriesResponse>>
    suspend fun getServices(token: String,providerId:String): ServiceResponse?
    suspend fun getParameters(token: String,serviceID:String): ParametersResponse?
    suspend fun serviceSearch(token: String,serviceName:String,page:Int): SearchResponse?

    suspend fun getUserPointsFlow(token: String): Flow<NetworkResult<PointsResponse>>
}