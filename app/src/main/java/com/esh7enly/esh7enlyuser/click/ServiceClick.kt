package com.esh7enly.esh7enlyuser.click

import com.esh7enly.domain.entity.servicesNew.ServiceData
import com.esh7enly.domain.entity.userservices.Service

interface ServiceClick
{
    fun click(service: ServiceData)
}