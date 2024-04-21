package com.esh7enly.domain.repo

import com.esh7enly.domain.entity.providersNew.ProviderResponse

interface ServicesRepo
{
    suspend fun getProviders(token: String,id:String): ProviderResponse?
}