package com.esh7enly.esh7enlyuser.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.esh7enly.data.local.DatabaseRepo
import com.esh7enly.domain.entity.*
import com.esh7enly.domain.entity.userservices.*
import com.esh7enly.domain.usecase.MainUseCase
import com.esh7enly.domain.usecase.GetServicesUseCase
import com.esh7enly.domain.usecase.ScheduleUseCase
import com.esh7enly.domain.usecase.UserDataUseCase
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.NetworkResult
import com.esh7enly.esh7enlyuser.util.ServiceStatus
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "ServiceViewModel"

@HiltViewModel
class ServiceViewModel @Inject constructor(private val getServices: MainUseCase,
                                           private val databaseRepo: DatabaseRepo,
                                           private val getServicesUseCase: GetServicesUseCase,
                                           private val userDataUseCase: UserDataUseCase,
                                           private val scheduleUseCase: ScheduleUseCase) :
    ViewModel() {

    var buttonClicked:MutableLiveData<String> = MutableLiveData("")
    var _buttonClicked:LiveData<String> = buttonClicked

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

    private var _response: MutableLiveData<NetworkResult<TotalAmountEntity>> = MutableLiveData( )

    var response:LiveData<NetworkResult<TotalAmountEntity>> = _response

    private val _services: MutableStateFlow<UserServicesResponse?> = MutableStateFlow(null)
    val services: StateFlow<UserServicesResponse?> = _services

    private val _login: MutableLiveData<Boolean?> = MutableLiveData()
    val login: MutableLiveData<Boolean?> = _login


    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun searchService(serviceName:String):LiveData<List<Service>> {
        return databaseRepo.searchService(serviceName)
    }

    suspend fun getDbVersion():String
    {
        return databaseRepo.getServiceUpdateNumber()
    }

    fun insertToFawryDao(fawryEntity:FawryEntity)
    {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepo.insertFawryDao(fawryEntity)
        }
    }


    fun clearFawryOperations()
    {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepo.clearFawryOperations()
        }
    }

    fun deleteFawryOperations(id:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepo.deleteFawryOperation(id)
        }
    }

    fun scheduleInquire(token:String,serviceId: String,invoice_number:String,listner: OnResponseListener) {
        viewModelScope.launch {
            val scheduleInquireResponse = scheduleUseCase.scheduleInquire(token, serviceId, invoice_number)

            if(scheduleInquireResponse.isSuccessful)
            {
                if(scheduleInquireResponse.body()!!.status)
                {
                    listner.onSuccess(scheduleInquireResponse.body()!!.code,scheduleInquireResponse.body()!!.message,scheduleInquireResponse.body()!!.data)
                }
                else
                {
                    listner.onFailed(scheduleInquireResponse.body()!!.code,scheduleInquireResponse.body()!!.message)
                }
            }
            else
            {
                listner.onFailed(scheduleInquireResponse.code(),scheduleInquireResponse.message())
            }
        }
    }

    fun getFawryOperations():LiveData<List<FawryEntity>> {
        return databaseRepo.getFawryOperations()
    }

    private var dataStatus:MutableLiveData<ServiceStatus> = MutableLiveData(ServiceStatus.LOADING)

    private val serviceStatusStateFlow:MutableStateFlow<ServiceStatus> = MutableStateFlow(ServiceStatus.LOADING)


    var _dataStatus:LiveData<ServiceStatus> = dataStatus

    suspend fun getFilteredList() = getServicesUseCase.getFilteredCategories()

    fun getAllCategories() = databaseRepo.getCategories()

    fun getService(token: String) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                getServicesUseCase.getServicesFromRemoteUser(token)
                    .catch { Log.d(TAG, "diaa getService catch: ${it.message}") }
                    .buffer()
                    .collect {

                        databaseRepo.deleteImage()
                        databaseRepo.deleteCategories()
                        databaseRepo.deleteServices()
                        databaseRepo.deleteProviders()
                        databaseRepo.deleteParameters()
                        databaseRepo.deleteVersionEntity()

                        databaseRepo.insertCategories(it.data.categories)

                        databaseRepo.insertProviders(it.data.providers)

                        databaseRepo.insertServices(it.data.services)

                        databaseRepo.insertParameters(it.data.parameters)

                        databaseRepo.insertImages(it.data.images)

                        val versionEntity = VersionEntity("","",it.service_update_num)

                        databaseRepo.insertVersionEntity(versionEntity)

                        Log.d(TAG, "diaa new getService: ${databaseRepo.getServiceUpdateNumber()}")

                        val count = databaseRepo.getImagesCount()

                        Log.d(TAG, "getServices viewModel done: ")
                        Log.d(TAG, "getServices viewModel image: $count")

                        withContext(Dispatchers.Main)
                        {
                            dataStatus.value = ServiceStatus.SUCCESS
                            serviceStatusStateFlow.emit(ServiceStatus.SUCCESS)
                           // serviceStatusStateFlow.value = ServiceStatus.SUCCESS
                        }
                    }


            } catch (e: Exception)
            {
                Log.d(TAG, "getServices viewModel error: ${e.message}")
                withContext(Dispatchers.Main)
                {
                    dataStatus.value = ServiceStatus.ERROR
                    serviceStatusStateFlow.value = ServiceStatus.ERROR
                }
            }
        }
    }

    fun getImageAds(token: String, listner: OnResponseListener) {
        viewModelScope.launch {
            val imageAdResponse = getServices.getImageAdResponse(token)

            if(imageAdResponse.isSuccessful)
            {
                if(imageAdResponse.body()!!.status)
                {
                    listner.onSuccess(imageAdResponse.body()!!.code,imageAdResponse.body()?.message,imageAdResponse.body()?.data)
                }
                else
                {
                    listner.onFailed(imageAdResponse.body()!!.code,imageAdResponse.body()!!.message)
                }
            }
            else
            {
                listner.onFailed(imageAdResponse.code(),imageAdResponse.message())
            }
        }
    }

    fun getCategoriesFromDB(): List<Category> {
        return databaseRepo.getCategories()
    }


    fun getProvidersFromDB(categoryId:Int):LiveData<List<Provider>> {
        return databaseRepo.getProviders(categoryId)
    }

    fun getServicesFromDB(providerId:String):LiveData<List<Service>> {
        return databaseRepo.getServices(providerId)
    }

    fun getParametersFromDB(serviceId: String): LiveData<List<Parameter>> {

        return databaseRepo.getParameters(serviceId)
    }

    fun getImagesFromDB(serviceId: String): LiveData<List<Image>> {

        return databaseRepo.getImages(serviceId)
    }

    fun getTotalAmount(
        token: String,
        totalAmountPojoModel: TotalAmountPojoModel,
        listner: OnResponseListener
    ) {
        viewModelScope.launch {
            try {
                getServices.getTotalAmount(token, totalAmountPojoModel)
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
            }

        }

    }

    fun pay(token: String, paymentPojoModel: PaymentPojoModel, listner: OnResponseListener) {
       viewModelScope.launch {

            try{
                val response = getServices.pay(token, paymentPojoModel)

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
            }catch (e:Exception)
            {
                listner.onFailed(404, "Payment error 404")

            }
        }
    }

    fun cancelTransaction(token: String,transactionId: String,imei: String, listner:OnResponseListener) {
        viewModelScope.launch(Dispatchers.IO) {

            val response = getServices.cancelTransaction(token,transactionId,imei)

            if(response.isSuccessful)
            {
                val code:String
                val msg:String

                if (response.body()?.isJsonNull == false)
                {
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

                }
                else
                {
                    listner.onFailed(response.code(),response.message())
                }
            }
            else
            {
                listner.onFailed(response.code(),response.message())
            }
        }
    }


    fun inquire(token: String, paymentPojoModel: PaymentPojoModel, listner: OnResponseListener) {
        viewModelScope.launch {

         //   try{
                val inquireResponse = getServices.inquire(token, paymentPojoModel)

                if (inquireResponse.isSuccessful)
                {
                    if (inquireResponse.body()?.status == false) {
                        listner.onFailed(inquireResponse.body()!!.code, inquireResponse.body()!!.message)
                    } else {
                        listner.onSuccess(
                            inquireResponse.body()!!.code, inquireResponse.body()!!.message,
                            inquireResponse.body()!!.data
                        )

                    }
                }
                else {
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

            val response = getServices.checkIntegration(token, transaction_id, imei)

            if (response.isSuccessful) {

                val code:String
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


    fun validateTokenResponseUser(token: String) {
        viewModelScope.launch {

            try{
                val response = userDataUseCase.getUserWallet(token)

                if (response.isSuccessful)
                {
                    _login.value = response.body()!!.status
                    Constants.SERVICE_UPDATE_NUMBER = response.body()!!.service_update_num

                } else {
                    _login.value = false
                }
            }
            catch (e: Exception)
            {
                _login.value = false

            }

        }
    }

    fun getScheduleList(token: String,listner: OnResponseListener) {
        viewModelScope.launch {
            val scheduleListResponse = scheduleUseCase.getScheduleList(token)

            if(scheduleListResponse.isSuccessful)
            {
                if(scheduleListResponse.body()?.status == true)
                {
                    listner.onSuccess(scheduleListResponse.body()!!.code!!,
                        scheduleListResponse.body()!!.message,scheduleListResponse.body()!!.data)

                }
                else
                {
                    listner.onFailed(scheduleListResponse.body()!!.code!!,scheduleListResponse.body()!!.message)
                }
            }
            else
            {
                listner.onFailed(scheduleListResponse.code(),scheduleListResponse.message())
            }
        }
    }

    private val _points :MutableLiveData<String> = MutableLiveData("0")
    val points :LiveData<String> = _points

    fun getUserPoints(token: String)
    {
        viewModelScope.launch {
            val pointsResponse = userDataUseCase.getUserPoints(token)

            if(pointsResponse.isSuccessful)
            {
                if(pointsResponse.body()!!.status == true)
                {
                    _points.value = pointsResponse.body()?.data?.points
                }
                else
                {
                    Log.d(TAG, "diaa points error ${pointsResponse.message()}")
                }

            }
            else{
                Log.d(TAG, "diaa points error ${pointsResponse.message()}")

            }
        }
    }

    fun scheduleInvoice(token: String,serviceId:String,scheduleDay:String,invoice_number:String,listner: OnResponseListener) {
        viewModelScope.launch {
            val scheduleResponse = scheduleUseCase.scheduleInvoice(token, serviceId, scheduleDay,invoice_number)

            if(scheduleResponse.isSuccessful)
            {
                if(scheduleResponse.body()!!.status)
                {
                    listner.onSuccess(scheduleResponse.body()!!.code,scheduleResponse.body()!!.message,scheduleResponse.body()!!.data)
                }
                else
                {
                    listner.onFailed(scheduleResponse.body()!!.code,scheduleResponse.body()!!.message)

                }
            }
            else
            {
                listner.onFailed(scheduleResponse.code(),scheduleResponse.message())
            }
        }
    }

    fun replaceUserPoints(token: String, listner: OnResponseListener) {
        viewModelScope.launch {
            val replaceResponse = userDataUseCase.replaceUserPoints(token)
            if(replaceResponse.isSuccessful)
            {
                if(replaceResponse.body()!!.status == true)
                {
                    listner.onSuccess(replaceResponse.body()!!.code!!,replaceResponse.body()!!.message,replaceResponse.body()!!.data)
                }
                else
                {
                    listner.onFailed(replaceResponse.body()!!.code!!,replaceResponse.body()!!.message)
                }
            }
            else
            {
                listner.onFailed(replaceResponse.code(),replaceResponse.message())
            }
        }
    }
}