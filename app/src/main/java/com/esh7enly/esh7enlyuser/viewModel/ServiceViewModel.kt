package com.esh7enly.esh7enlyuser.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.esh7enly.data.local.DatabaseRepo
import com.esh7enly.data.repo.ServicesRepoImpl
import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.entity.*
import com.esh7enly.domain.entity.categoriesNew.CategoriesResponse
import com.esh7enly.domain.repo.ServicesRepo
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "ServiceViewModel"

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val repo: ServicesRepoImpl,
    private val databaseRepo: DatabaseRepo,
    private val servicesRepo: ServicesRepo
) :
    ViewModel() {

    fun getProvidersNew(token: String, categoryId: String, listner: OnResponseListener) {
        viewModelScope.launch {
            try {
                val providers = servicesRepo.getProviders(token, categoryId)

                if (providers?.data?.isNotEmpty() == true) {
                    listner.onSuccess(providers.code, providers.message, providers.data)

                } else {
                    listner.onFailed(providers?.code!!, providers.message)
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(),"Get providers new Method serviceViewModel")
            }
        }
    }

    fun serviceSearch(token: String, serviceName: String, page: Int, listner: OnResponseListener) {
        viewModelScope.launch {
            try {
                val serviceSearch = servicesRepo.serviceSearch(token, serviceName, page)

                if (serviceSearch?.data?.data?.isNotEmpty() == true) {
                    listner.onSuccess(
                        serviceSearch.code,
                        serviceSearch.message,
                        serviceSearch.data?.data
                    )

                } else {
                    listner.onFailed(serviceSearch?.code!!, serviceSearch.message)
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(),"Service search Method serviceViewModel")

            }
        }
    }

    fun getCategoriesNew(token: String, listner: OnResponseListener) {
        viewModelScope.launch {
            try {
                val categories = servicesRepo.getCategories(token)

                if (categories?.data?.isNotEmpty() == true) {
                    listner.onSuccess(categories.code, categories.message, categories.data)

                } else {
                    listner.onFailed(categories?.code!!, categories.message)
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(),"get Categories new Method serviceViewModel")

            }
        }
    }

    fun getServicesNew(token: String, providerID: String, listner: OnResponseListener) {
        viewModelScope.launch {
            try {
                val services = servicesRepo.getServices(token, providerID)

                if (services?.data?.isNotEmpty() == true) {
                    listner.onSuccess(services.code, services.message, services.data)

                } else {
                    listner.onFailed(services?.code!!, services.message)
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(),"Get services new serviceViewModel")


            }
        }
    }

    private var categoriesResponse: MutableStateFlow<NetworkResult<CategoriesResponse>?> =
        MutableStateFlow(null)

    fun getCategoriesNewFlow(token: String) {

        viewModelScope.launch {
            servicesRepo.getCategoriesFlow(token)
                .onEach { result ->
                    when (result) {
                        is NetworkResult.Error -> {
                            categoriesResponse.update {
                                NetworkResult.Error(result.message, result.code)
                            }
                        }

                        is NetworkResult.Loading -> {
                            categoriesResponse.update {
                                NetworkResult.Loading()
                            }

                        }

                        is NetworkResult.Success -> {
                            categoriesResponse.update {
                                NetworkResult.Success(result.data!!)
                            }
                        }
                    }
                }
                .launchIn(viewModelScope)
        }

    }

    fun getParametersNew(token: String, serviceID: String, listner: OnResponseListener) {
        viewModelScope.launch {
            try {
                val parameters = servicesRepo.getParameters(token, serviceID)

                if (parameters?.data?.isNotEmpty() == true) {
                    listner.onSuccess(parameters.code, parameters.message, parameters.data)

                } else {
                    listner.onFailed(parameters?.code!!, parameters.message)
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(),"Get parameters new serviceViewModel")

            }
        }
    }

    var serviceType = 0
    var providerName: String? = null
    var serviceTypeCode = ""
    var serviceName: String? = null
    var serviceNameEN: String? = null
    var servicesId = 0
    var acceptAmountinput = 0
    var priceType = 0
    var priceValue: String? = null
    var acceptAmountChange = 0
    var image: String? = null
    val acceptCheckIntegrationProviderStatus = 0
    var transactionId = ""
    var transactionType = 0

    private var _response: MutableLiveData<NetworkResult<TotalAmountEntity>> = MutableLiveData()

    var response: LiveData<NetworkResult<TotalAmountEntity>> = _response

    fun insertToFawryDao(fawryEntity: FawryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepo.insertFawryDao(fawryEntity)
        }
    }

    fun clearFawryOperations() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepo.clearFawryOperations()
        }
    }

    fun deleteFawryOperations(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepo.deleteFawryOperation(id)
        }
    }

    fun scheduleInquire(
        token: String,
        serviceId: String,
        invoice_number: String,
        listner: OnResponseListener
    ) {
        viewModelScope.launch {
            try {
                val scheduleInquireResponse = repo.scheduleInquire(token, serviceId, invoice_number)

                if (scheduleInquireResponse.isSuccessful) {
                    if (scheduleInquireResponse.body()!!.status) {
                        listner.onSuccess(
                            scheduleInquireResponse.body()!!.code,
                            scheduleInquireResponse.body()!!.message,
                            scheduleInquireResponse.body()!!.data
                        )
                    } else {
                        listner.onFailed(
                            scheduleInquireResponse.body()!!.code,
                            scheduleInquireResponse.body()!!.message
                        )
                    }
                } else {
                    listner.onFailed(
                        scheduleInquireResponse.code(),
                        scheduleInquireResponse.message()
                    )
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(),"Schedule inquire Method serviceViewModel")

            }
        }
    }

    fun getFawryOperations(): LiveData<List<FawryEntity>> {
        return databaseRepo.getFawryOperations()
    }

    fun getImageAds(token: String, listner: OnResponseListener) {
        viewModelScope.launch {
            try {
                val imageAdResponse = repo.getImageAdResponse(token)

                if (imageAdResponse.isSuccessful) {
                    if (imageAdResponse.body()!!.status) {
                        listner.onSuccess(
                            imageAdResponse.body()!!.code,
                            imageAdResponse.body()?.message,
                            imageAdResponse.body()?.data
                        )
                    } else {
                        listner.onFailed(
                            imageAdResponse.body()!!.code,
                            imageAdResponse.body()!!.message
                        )
                    }
                } else {
                    listner.onFailed(imageAdResponse.code(), imageAdResponse.message())
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(),"getImageAds serviceViewModel")

            }
        }
    }

    fun getTotalAmount(
        token: String,
        totalAmountPojoModel: TotalAmountPojoModel,
        listner: OnResponseListener
    ) {
        viewModelScope.launch {
            try {
                repo.getTotalAmount(token, totalAmountPojoModel)
                    .catch { Log.d(TAG, "diaa getTotalAmount: catch ${it.message}") }
                    .buffer()
                    .collect { response ->
                        if (!response.status) {
                            listner.onFailed(response.code, response.message)

                        } else {
                            listner.onSuccess(response.code, response.message, response.data)
                        }
                    }
            } catch (e: Exception) {
                listner.onFailed(Constants.CODE_UNAUTH_NEW, e.message)
                sendIssueToCrashlytics(e.message.toString(),"getTotalAmount serviceViewModel")
            }

        }

    }

    fun pay(token: String, paymentPojoModel: PaymentPojoModel, listner: OnResponseListener) {
        viewModelScope.launch {

            try {
                val response = repo.pay(token, paymentPojoModel)

                if (response.isSuccessful) {
                    if (response.body()?.status == false) {
                        listner.onFailed(response.body()!!.code, response.body()!!.message)
                    } else {
                        listner.onSuccess(
                            response.body()!!.code, response.body()!!.message,
                            response.body()!!.data
                        )

                    }
                } else {
                    listner.onFailed(response.code(), response.message())
                    Log.d(TAG, "diaa pay error: ${response.code()} ${response.errorBody()}")

                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(),"pay Method serviceViewModel")


            }
        }
    }

    fun cancelTransaction(
        token: String,
        transactionId: String,
        imei: String,
        listner: OnResponseListener
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val response = repo.cancelTransaction(token, transactionId, imei)

            if (response.isSuccessful) {
                val code: String
                val msg: String

                if (response.body()?.isJsonNull == false) {
                    var jObject = response.body()!!.asJsonObject as JsonObject

                    if (jObject.has("status")) {
                        val status = jObject.get("status").asBoolean
                        msg = jObject.get("message").asString
                        code = jObject.get("code").asString

                        if (status) {
                            if (jObject.has("data")) {
                                jObject = jObject.getAsJsonObject("data")
                            }
                            listner.onSuccess(code.toInt(), msg, jObject)
                        } else {
                            listner.onFailed(code.toInt(), msg)
                        }
                    }

                } else {
                    listner.onFailed(response.code(), response.message())
                }
            } else {
                listner.onFailed(response.code(), response.message())
            }
        }
    }


    fun inquire(token: String, paymentPojoModel: PaymentPojoModel, listner: OnResponseListener) {
        viewModelScope.launch {

            //   try{
            val inquireResponse = repo.inquire(token, paymentPojoModel)

            if (inquireResponse.isSuccessful) {
                if (inquireResponse.body()?.status == false) {
                    listner.onFailed(
                        inquireResponse.body()!!.code,
                        inquireResponse.body()!!.message
                    )
                } else {
                    listner.onSuccess(
                        inquireResponse.body()!!.code, inquireResponse.body()!!.message,
                        inquireResponse.body()!!.data
                    )

                }
            } else {
                listner.onFailed(inquireResponse.code(), inquireResponse.message())
            }
//            } catch(e: Exception)
//            {
//
//                listner.onFailed(404, "Inquire error 404")
//
//            }
        }
    }

    fun checkIntegration(
        token: String,
        transaction_id: String,
        imei: String,
        listner: OnResponseListener
    ) {
        viewModelScope.launch {

            val response = repo.checkIntegration(token, transaction_id, imei)

            if (response.isSuccessful) {

                val code: String
                val msg: String

                if (response.body()?.isJsonNull == false) {
                    val jObject = response.body()!!.asJsonObject as JsonObject

                    if (jObject.has("status")) {
                        val status = jObject.get("status").asBoolean
                        msg = jObject.get("message").asString
                        code = jObject.get("code").asString

                        if (status) {
                            listner.onSuccess(code.toInt(), msg, jObject)
                        } else {
                            listner.onFailed(code.toInt(), msg)
                        }

                    }
                }
            } else {
                listner.onFailed(response.code(), response.message())

            }
        }
    }

    fun getScheduleList(token: String, listner: OnResponseListener) {
        viewModelScope.launch {
            try {
                val scheduleListResponse = repo.getScheduleList(token)

                if (scheduleListResponse.isSuccessful) {
                    if (scheduleListResponse.body()?.status == true) {
                        listner.onSuccess(
                            scheduleListResponse.body()!!.code!!,
                            scheduleListResponse.body()!!.message,
                            scheduleListResponse.body()!!.data
                        )

                    } else {
                        listner.onFailed(
                            scheduleListResponse.body()!!.code!!,
                            scheduleListResponse.body()!!.message
                        )
                    }
                } else {
                    listner.onFailed(scheduleListResponse.code(), scheduleListResponse.message())
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(),"getScheduleList")


            }
        }
    }


    var userPointsState: MutableStateFlow<NetworkResult<String>?> = MutableStateFlow(null)

    fun getUserPointsFlow(token: String) {
        viewModelScope.launch {
            try {
                servicesRepo.getUserPointsFlow(token)
                    .onEach { userPointsResponse ->
                        when (userPointsResponse) {
                            is NetworkResult.Error -> {
                                userPointsState.update {
                                    NetworkResult.Error(
                                        userPointsResponse.message,
                                        userPointsResponse.code
                                    )
                                }
                            }

                            is NetworkResult.Loading -> {
                                userPointsState.update { NetworkResult.Loading() }

                            }

                            is NetworkResult.Success -> {
                                userPointsState.update {
                                    NetworkResult.Success(userPointsResponse.data?.data?.points!!)
                                }

                            }
                        }
                    }
                    .launchIn(viewModelScope)
            } catch (e: Exception) {
                userPointsState.update {
                    NetworkResult.Error(
                        e.message,
                        Constants.EXCEPTION_CODE
                    )
                }
                sendIssueToCrashlytics(e.message.toString(),"getUserPointsFlow")

            }
        }
    }

    fun scheduleInvoice(
        token: String,
        serviceId: String,
        scheduleDay: String,
        invoice_number: String,
        listner: OnResponseListener
    ) {
        viewModelScope.launch {
            val scheduleResponse =
                repo.scheduleInvoice(token, serviceId, scheduleDay, invoice_number)

            if (scheduleResponse.isSuccessful) {
                if (scheduleResponse.body()!!.status) {
                    listner.onSuccess(
                        scheduleResponse.body()!!.code,
                        scheduleResponse.body()!!.message,
                        scheduleResponse.body()!!.data
                    )
                } else {
                    listner.onFailed(
                        scheduleResponse.body()!!.code,
                        scheduleResponse.body()!!.message
                    )

                }
            } else {
                listner.onFailed(scheduleResponse.code(), scheduleResponse.message())

            }
        }
    }


    fun replaceUserPoints(token: String, listner: OnResponseListener) {
        viewModelScope.launch {

            try {
                val replaceResponse = repo.replaceUserPoints(token)
                if (replaceResponse.isSuccessful) {
                    if (replaceResponse.body()!!.status == true) {
                        listner.onSuccess(
                            replaceResponse.body()!!.code!!,
                            replaceResponse.body()!!.message,
                            replaceResponse.body()!!.data
                        )
                    } else {
                        listner.onFailed(
                            replaceResponse.body()!!.code!!,
                            replaceResponse.body()!!.message
                        )
                    }
                } else {
                    listner.onFailed(replaceResponse.code(), replaceResponse.message())
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(),"replaceUserPoints")

            }
        }
    }


}