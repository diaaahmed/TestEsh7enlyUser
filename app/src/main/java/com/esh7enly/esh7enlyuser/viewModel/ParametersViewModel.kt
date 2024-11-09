package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.local.DatabaseRepo
import com.esh7enly.domain.repo.ServicesRepo
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor(
    private val databaseRepo: DatabaseRepo,
    private val servicesRepo: ServicesRepo
): ViewModel()
{

    var servicesId = 0
    var serviceType = 0
    var serviceName: String? = null
    var serviceNameEN: String? = null
    var providerName: String? = null
    var priceType = 0
    var priceValue: String? = null
    var acceptAmountChange = 0
    var image: String? = null
    var acceptAmountinput = 0

    fun getParametersNew(serviceID: String, listner: OnResponseListener) {
        viewModelScope.launch {
            try {
                val parameters = servicesRepo.getParameters(serviceID)

                if (parameters?.data?.isNotEmpty() == true) {
                    listner.onSuccess(parameters.code, parameters.message, parameters.data)

                } else {
                    listner.onFailed(parameters?.code!!, parameters.message)
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, "No data found")
                sendIssueToCrashlytics(e.message.toString(),"Get parameters new serviceViewModel")

            }
        }
    }
}