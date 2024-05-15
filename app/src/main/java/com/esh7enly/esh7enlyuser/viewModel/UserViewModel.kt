package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.repo.UserRepo
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.ApiResponse
import com.esh7enly.domain.entity.RegisterModel
import com.esh7enly.domain.entity.loginresponse.LoginResponse

import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sharedHelper: SharedHelper
) :
    ViewModel() {
    var phoneNumber: String? = null
    var password: String? = null

    var imei: String? = null

    var token = ""

    val userPhoneNumber = MutableStateFlow("")
    val userPassword = MutableStateFlow("")

    fun saveUserPassword() {
        sharedHelper.setUserPassword(userPassword.value)
    }

    fun userLogin(device_token: String):
            LiveData<ApiResponse<LoginResponse>>
    {
        return userRepo.login(
            userPhoneNumber.value, userPassword.value, device_token
        )
    }

    fun sendOtp(mobile: String, listener: OnResponseListener) {
        viewModelScope.launch {

            val otpResponse = userRepo.sendOtp(mobile)

            if (otpResponse.isSuccessful) {
                if (!otpResponse.body()!!.status!!) {
                    listener.onFailed(otpResponse.body()!!.code!!, otpResponse.body()!!.message)
                } else {
                    Constants.USER_KEY = otpResponse.body()!!.data.key
                    listener.onSuccess(
                        otpResponse.body()!!.code!!,
                        otpResponse.body()!!.message,
                        otpResponse.body()!!.data
                    )
                }
            } else {
                listener.onFailed(otpResponse.code(), otpResponse.message())
            }
        }
    }

    fun forgetPasswordSendOTP(mobile: String, listener: OnResponseListener) {
        viewModelScope.launch {
            try {
                val otpResponse = userRepo.forgetPasswordSendOTP(mobile)

                if (otpResponse.isSuccessful) {
                    if (!otpResponse.body()!!.status) {
                        listener.onFailed(otpResponse.body()!!.code, otpResponse.body()!!.message)
                    } else {
                        listener.onSuccess(
                            otpResponse.body()!!.code,
                            otpResponse.body()!!.message,
                            otpResponse.body()!!.data
                        )
                    }
                } else {
                    listener.onFailed(otpResponse.code(), otpResponse.message())

                }
            } catch (e: Exception) {
                listener.onFailed(Constants.EXCEPTION_CODE, e.message)

            }
        }

    }

    fun verifyAccount(mobile: String, otpCode: String, key: String, listener: OnResponseListener) {
        viewModelScope.launch {
            try {
                val response = userRepo.verifyAccount(mobile, otpCode, key)

                if (!response.status) {
                    listener.onFailed(response.code, response.message)
                } else {
                    listener.onSuccess(response.code, response.message, response.data)
                }
            } catch (e: Exception) {
                listener.onFailed(Constants.EXCEPTION_CODE, e.message)

            }
        }
    }

    fun registerNewAccount(registerModel: RegisterModel, listener: OnResponseListener) {
        viewModelScope.launch {
            try {
                val registerResponse = userRepo.registerNewAccount(registerModel)

                if (!registerResponse.status!!) {
                    listener.onFailed(registerResponse.code!!, registerResponse.message)
                } else {
                    listener.onSuccess(
                        registerResponse.code!!,
                        registerResponse.message,
                        registerResponse.data
                    )

                }
            } catch (e: Exception) {
                listener.onFailed(Constants.EXCEPTION_CODE, e.message)

            }
        }
    }

    fun createNewPassword(
        mobile: String,
        password: String,
        confirmationPassword: String,
        otpCode: String,
        listener: OnResponseListener
    ) {
        viewModelScope.launch {
            val forgetPasswordResponse =
                userRepo.createNewPassword(mobile, password, confirmationPassword, otpCode)
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
                listener.onFailed(forgetPasswordResponse.code(), forgetPasswordResponse.message())
            }
        }
    }

    fun updatePassword(
        token: String,
        currentPassword: String,
        newPassword: String,
        listener: OnResponseListener
    ) {
        viewModelScope.launch {
            val updateResponse = userRepo.updatePassword(token, currentPassword, newPassword)

            if (updateResponse.isSuccessful) {
                if (!updateResponse.body()!!.status) {
                    listener.onFailed(updateResponse.body()!!.code, updateResponse.body()!!.message)
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
        }
    }


    private val _login: MutableLiveData<Boolean?> = MutableLiveData()
    val login: MutableLiveData<Boolean?> = _login

    fun validateTokenResponseUser(token: String) {
        viewModelScope.launch {

            try {
                val response = userRepo.getUserWallet(token)

                if (response.isSuccessful) {
                    _login.value = response.body()!!.status
                    Constants.SERVICE_UPDATE_NUMBER = response.body()!!.service_update_num

                } else {
                    _login.value = false
                }
            } catch (e: Exception) {
                _login.value = false

            }

        }
    }

}