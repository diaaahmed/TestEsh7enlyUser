package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.datastore.DataStoreHelper
import com.esh7enly.data.datastore.UserDataStore
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.ApiResponse
import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.entity.loginresponse.LoginResponse
import com.esh7enly.domain.repo.UserRepo

import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.isValidPassword
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sharedHelper: SharedHelper,
    private val dataStoreHelper: DataStoreHelper

) :
    ViewModel() {

    var phoneNumber: String? = null
    var password: String? = null

    var imei: String? = null

    var token = ""

    fun saveTokenWithDataStore(token: String)
    {
        viewModelScope.launch{
            dataStoreHelper.saveTokenKey(token)
        }
    }

    fun saveUserDataWithDataStore(userDataStore: UserDataStore)
    {
        viewModelScope.launch{
            dataStoreHelper.saveUserData(userDataStore)
        }
    }

    fun saveUserPassword() {
        sharedHelper.setUserPassword(userPassword.value)
    }

    fun removeUserPassword() {
        sharedHelper.setUserPassword("")
    }

    val userPhoneNumber = MutableStateFlow("")
    val userPassword = MutableStateFlow("")

    fun userLogin(deviceToken: String,imei:String):
            LiveData<ApiResponse<LoginResponse>> {

        return userRepo.login(
            userPhoneNumber.value, userPassword.value, deviceToken,imei
        )
    }

    private var _loginStateSharedFlow = MutableSharedFlow<NetworkResult<LoginResponse>>()
    val loginStateSharedFlow = _loginStateSharedFlow.asSharedFlow()


    fun loginWithState(
        deviceToken: String,imei:String
    )
    {
        viewModelScope.launch{
            userRepo.loginWithState(
                userPhoneNumber.value, userPassword.value,
                deviceToken,imei)
                .collect{
                    when(it){
                        is NetworkResult.Error -> {
                            _loginStateSharedFlow.emit(NetworkResult.Error(
                                message = it.message,
                                code = it.code
                            ))
                        }
                        is NetworkResult.Loading -> {
                            _loginStateSharedFlow.emit(NetworkResult.Loading())
                        }
                        is NetworkResult.Success -> {
                            _loginStateSharedFlow.emit(NetworkResult.Success(it.data!!))
                        }
                    }
                }
        }
    }

    fun createNewPassword(
        mobile: String,
        password: String,
        confirmationPassword: String,
        key: String,
        token: String,
        listener: OnResponseListener
    ) {
        viewModelScope.launch {
            try {
                val forgetPasswordResponse =
                    userRepo.createNewPassword(
                        mobile, password,
                        confirmationPassword, key,token)

                if (forgetPasswordResponse.isSuccessful) {
                    if (!forgetPasswordResponse.body()!!.status) {
                        listener.onFailed(
                            forgetPasswordResponse.body()!!.code,
                            forgetPasswordResponse.body()!!.message
                        )
                    } else {
                        listener.onSuccess(
                            forgetPasswordResponse.body()!!.code,
                            forgetPasswordResponse.body()!!.message,
                            forgetPasswordResponse.body()!!.data
                        )
                    }
                } else {
                    listener.onFailed(
                        forgetPasswordResponse.code(),
                        forgetPasswordResponse.message()
                    )
                }


            } catch (e: Exception) {
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "createNewPassword from User viewModel"
                )

            }
        }
    }

    val userOldPassword = MutableStateFlow("")
    val userNewPassword = MutableStateFlow("")
    val userNewPasswordConfirmation = MutableStateFlow("")

    private val isDataValid = combine(
        userOldPassword,
        userNewPassword,
        userNewPasswordConfirmation
    ) { userOldPassword, userNewPassword, userNewPasswordConfirmation ->
        userOldPassword.isBlank() ||
                userNewPassword.isBlank()
                || userNewPasswordConfirmation.isBlank()
    }

    private val isNewAndConfirmPasswordEqual = combine(
        userNewPassword,
        userNewPasswordConfirmation
    ) { userNewPassword, userNewPasswordConfirmation ->
        userNewPassword != userNewPasswordConfirmation
    }

//    var updatePasswordState: MutableStateFlow<NetworkResult<String>?> =
//        MutableStateFlow(null)

    fun updatePassword(
        token: String,
        listener: OnResponseListener
    ) {
        viewModelScope.launch {

            if (isDataValid.first()) {
                listener.onFailed(2, "Data empty")
            } else if (isNewAndConfirmPasswordEqual.first()) {
                listener.onFailed(2, "Passwords not match")
            } else if (!isValidPassword(userNewPassword.value)) {
                listener.onFailed(2, "Password not valid")
            } else {
                try {
                    val updateResponse = userRepo.updatePassword(
                        token, userOldPassword.value, userNewPassword.value
                    )

                    if (updateResponse.isSuccessful) {
                        if (!updateResponse.body()!!.status) {
                            listener.onFailed(
                                updateResponse.body()!!.code,
                                updateResponse.body()!!.message
                            )
                        } else {
                            listener.onSuccess(
                                updateResponse.body()!!.code,
                                updateResponse.body()!!.message,
                                updateResponse.body()!!.data
                            )
                        }
                    } else {
                        listener.onFailed(updateResponse.code(), updateResponse.message())
                    }
                } catch (e: Exception) {
                    sendIssueToCrashlytics(
                        e.message.toString(),
                        "updatePassword from User viewModel"
                    )
                }
            }
        }
    }


    private val _loginState: MutableLiveData<Boolean?> = MutableLiveData()
    val loginState: LiveData<Boolean?> = _loginState

    fun validateTokenResponseUser(token: String) {
        viewModelScope.launch {
            try {
                val response = userRepo.getUserWallet(token)

                if (response.isSuccessful) {
                    _loginState.value = response.body()!!.status

                } else {
                    _loginState.value = false
                }
            } catch (e: Exception) {
                _loginState.value = false
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "validateTokenResponseUser from User viewModel"
                )
            }
        }
    }

}