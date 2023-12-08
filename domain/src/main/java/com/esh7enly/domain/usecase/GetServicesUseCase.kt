package com.esh7enly.domain.usecase

import com.esh7enly.domain.entity.userservices.Category
import com.esh7enly.domain.repo.ServicesRepo

class GetServicesUseCase(private val servicesRepo: ServicesRepo)
{
    suspend fun getServicesFromRemoteUser(token:String) = servicesRepo.getServicesFromRemoteUser(token)

    suspend fun getFilteredCategories():List<Category>
    {
        val allCategories = servicesRepo.getFilteredCategories()
        val other = Category(
            "", 0, "خدمات أخرى",
            "Other services", 0
        )
        return listOf(allCategories[0],allCategories[1],other)
    }

}