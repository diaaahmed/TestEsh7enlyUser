package com.esh7enly.esh7enlyuser.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.entity.loginresponse.LoginResponse
import com.esh7enly.domain.repo.UserRepo
import com.esh7enly.esh7enlyuser.BuildConfig

import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.KeyPairHandler
import com.esh7enly.esh7enlyuser.util.encryptDataWithPublicKey
import com.esh7enly.esh7enlyuser.util.isValidPassword
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

import kotlinx.coroutines.launch
import java.security.PublicKey
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sharedHelper: SharedHelper,
) :
    ViewModel() {

    var imei: String? = null

    fun saveUserPassword() {
        sharedHelper.setUserPassword(userPassword.value)
    }

    fun removeUserPassword() {
        sharedHelper.setUserPassword("")
    }

    val userPhoneNumber = MutableStateFlow("")
    val userPassword = MutableStateFlow("")

    private var _loginStateSharedFlow = MutableSharedFlow<NetworkResult<LoginResponse>>()
    val loginStateSharedFlow = _loginStateSharedFlow.asSharedFlow()

    @RequiresApi(Build.VERSION_CODES.M)
    fun loginWithState(
        deviceToken: String, imei: String,
        publicKeyLast:PublicKey
    ) {
        viewModelScope.launch {
            KeyPairHandler.generateKeyPair()

            val uPassword = encryptDataWithPublicKey(
                userPassword.value,publicKeyLast)

            println("Diaa encrypted password $uPassword")

            userRepo.loginWithState(
                mobile = userPhoneNumber.value, password = uPassword,
                deviceToken = deviceToken, imei = imei
            )
                .collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            _loginStateSharedFlow.emit(
                                NetworkResult.Error(
                                    message = it.message,
                                    code = it.code
                                )
                            )
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
                        confirmationPassword, key, token
                    )

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
                         userOldPassword.value, userNewPassword.value
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

    fun validateTokenResponseUser() {
        viewModelScope.launch {
            try {
                val response = userRepo.getUserWallet()

                if (response.isSuccessful) {

                    if(BuildConfig.VERSION_NAME < response.body()?.app_version.toString())
                    {
                        _loginState.value = false

                    }
                    else{
                        _loginState.value = response.body()!!.status

                    }

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