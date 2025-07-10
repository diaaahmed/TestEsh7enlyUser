package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.local.DatabaseRepo
import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.entity.FawryEntity
import com.esh7enly.domain.entity.categoriesNew.CategoriesResponse
import com.esh7enly.domain.entity.imageadsresponse.ImageAdResponse
import com.esh7enly.domain.repo.ServicesRepo
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val databaseRepo: DatabaseRepo,
    private val servicesRepo: ServicesRepo
) : ViewModel() {

    private var _categoriesResponse: MutableStateFlow<NetworkResult<CategoriesResponse>?> =
        MutableStateFlow(NetworkResult.Loading())

    val categoriesResponse = this._categoriesResponse.asStateFlow()

    var data: Boolean = false

    fun fetchData() {
        if (!data) {
            getCategoriesNewFlow()
            getImageAds()
            data = true
        }
    }
    private fun getCategoriesNewFlow() {

        viewModelScope.launch {
            servicesRepo.getCategoriesFlow()
                .onEach { result ->
                    when (result) {
                        is NetworkResult.Error -> {
                            _categoriesResponse.update {
                                NetworkResult.Error(result.message, result.code)
                            }
                        }

                        is NetworkResult.Loading -> {
                           _categoriesResponse.update {
                                NetworkResult.Loading()
                            }

                        }

                        is NetworkResult.Success -> {
                            _categoriesResponse.update {
                                NetworkResult.Success(result.data!!)
                            }
                        }
                    }
                }
                .launchIn(viewModelScope)
        }

    }

    private var _dynamicAdsState: MutableStateFlow<NetworkResult<ImageAdResponse>?> =
        MutableStateFlow(null)

    val dynamicAdsState = _dynamicAdsState.asStateFlow()

    private fun getImageAds() {

        viewModelScope.launch {
            servicesRepo.getNewImageAdResponse()
                .collect{response->
                    when(response)
                    {
                        is NetworkResult.Error -> {
                            _dynamicAdsState.update {
                                NetworkResult.Error(response.message,response.code)
                            }

                            sendIssueToCrashlytics(response.message.toString(),
                                "getImageAds serviceViewModel")

                        }
                        is NetworkResult.Loading -> {
                            _dynamicAdsState.update {
                                NetworkResult.Loading()
                            }
                        }
                        is NetworkResult.Success -> {
                            _dynamicAdsState.update {
                                NetworkResult.Success(response.data!!)
                            }

                        }
                    }
                }
        }
    }

    fun getFawryOperations(): LiveData<List<FawryEntity>> {
        return databaseRepo.getFawryOperations()
    }

    fun cancelTransaction(
        transactionId: String,
        imei: String,
        listner: OnResponseListener
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val response = servicesRepo.cancelTransaction(transactionId, imei)

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

    fun deleteFawryOperations(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepo.deleteFawryOperation(id)
        }
    }

    fun clearFawryOperations() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepo.clearFawryOperations()
        }
    }
}