package com.esh7enly.data.local

import androidx.lifecycle.LiveData
import com.esh7enly.domain.entity.FawryEntity
import com.esh7enly.domain.entity.VersionEntity
import com.esh7enly.domain.entity.userservices.*

class DatabaseRepo (private val database: DatabaseRoom)
{
    fun insertFawryDao(fawryEntity: FawryEntity) = database.fawryDao().insert(fawryEntity)

    fun clearFawryOperations() = database.fawryDao().clearFawryOperations()

    fun deleteFawryOperation(id:Int) = database.fawryDao().deleteFawryOperations(id)

    fun getFawryOperations():LiveData<List<FawryEntity>> = database.fawryDao().getFawryOperations()

    fun getCategories() = database.categoryDao().getCategories()

    fun getProviders(categoryId: Int) = database.providerDao().getProviders(categoryId)

    fun getServices(providerId: String) = database.serviceDao().getServices(providerId)

    fun searchService(serviceName:String) = database.serviceDao().searchService(serviceName)

    fun getParameters(serviceId: String) = database.parameterDao().getParametersLive(serviceId)

    fun getImages(serviceId: String) = database.imageDao().getImages(serviceId)

    suspend fun insertCategories(categories: List<Category>) = database.categoryDao().insert(categories)

    fun getImagesCount() = database.imageDao().getImagesCount()

    fun deleteImage() = database.imageDao().deleteImages()

    fun deleteServices() = database.serviceDao().deleteServices()

    fun deleteProviders() = database.providerDao().deleteProviders()

    fun deleteParameters() = database.parameterDao().deleteParameters()


    fun deleteCategories() = database.categoryDao().deleteCategories()

    fun deleteVersionEntity() = database.userDao().deleteVersion()


    suspend fun insertProviders(providers: List<Provider>) = database.providerDao().insert(providers)

    suspend fun insertServices(services: List<Service>) = database.serviceDao().insert(services)

    suspend fun insertVersionEntity(versionEntity: VersionEntity) = database.userDao().insert(versionEntity)

    suspend fun getServiceUpdateNumber():String = database.userDao().getServiceUpdateNumber()

    suspend fun insertParameters(parameters: List<Parameter>) = database.parameterDao().insert(parameters)

    suspend fun insertImages(images: List<Image>) = database.imageDao().insert(images)

}