package com.esh7enly.domain.usecase

import com.esh7enly.domain.repo.ServicesRepo

class LoginUseCase(private val servicesRepo: ServicesRepo)
{
    operator fun invoke(mobile:String,password:String,device_token:String) = servicesRepo.login(mobile,password,device_token)
}