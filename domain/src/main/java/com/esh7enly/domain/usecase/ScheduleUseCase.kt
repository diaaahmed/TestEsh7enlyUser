package com.esh7enly.domain.usecase

import com.esh7enly.domain.repo.ServicesRepo

class ScheduleUseCase(private val servicesRepo: ServicesRepo)
{
    suspend fun scheduleInquire(token: String,serviceId: String,invoice_number:String) = servicesRepo.scheduleInquire(token, serviceId, invoice_number)

    suspend fun getScheduleList(token: String) = servicesRepo.getScheduleList(token)

    suspend fun scheduleInvoice(token: String,serviceId:String,scheduleDay:String,invoice_number:String) = servicesRepo.scheduleInvoice(token, serviceId, scheduleDay,invoice_number)
}