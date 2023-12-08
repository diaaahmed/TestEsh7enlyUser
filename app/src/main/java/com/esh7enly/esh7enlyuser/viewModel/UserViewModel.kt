package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.domain.ApiResponse
import com.esh7enly.domain.entity.RegisterModel
import com.esh7enly.domain.entity.loginresponse.LoginResponse
import com.esh7enly.domain.usecase.AccountCreationUpdateUseCase
import com.esh7enly.domain.usecase.LoginUseCase
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(private val loginUseCase: LoginUseCase,
                                        private val accountCreationUpdateUseCase: AccountCreationUpdateUseCase):
    ViewModel()
{
    var phoneNumber:String?= null
    var password:String?= null
    var imei:String?= null

    var token = ""


    fun login(mobile:String,password:String,device_token:String): LiveData<ApiResponse<LoginResponse>> {
        return loginUseCase(mobile,password,device_token)
    }

    fun sendOtp(mobile:String,listener: OnResponseListener) {
        viewModelScope.launch {

            val otpResponse = accountCreationUpdateUseCase.sendOtp(mobile)

            if(otpResponse.isSuccessful)
            {
                if (!otpResponse.body()!!.status!!)
                {
                    listener.onFailed(otpResponse.body()!!.code!!,otpResponse.body()!!.message)
                }
                else
                {
                    Constants.USER_KEY = otpResponse.body()!!.data.key
                    listener.onSuccess(otpResponse.body()!!.code!!,otpResponse.body()!!.message,otpResponse.body()!!.data)
                }
            }
            else
            {
                listener.onFailed(otpResponse.code(),otpResponse.message())
            }
        }
    }

    fun forgetPasswordSendOTP(mobile:String,listener: OnResponseListener) {
        viewModelScope.launch {
            val otpResponse = accountCreationUpdateUseCase.forgetPasswordSendOtp(mobile)

            if(otpResponse.isSuccessful)
            {
                if(!otpResponse.body()!!.status)
                {
                    listener.onFailed(otpResponse.body()!!.code,otpResponse.body()!!.message)
                }
                else
                {
                    listener.onSuccess(otpResponse.body()!!.code,otpResponse.body()!!.message,otpResponse.body()!!.data)
                }
            }
            else{
                listener.onFailed(otpResponse.code(),otpResponse.message())

            }
        }

        }

    fun verifyAccount(mobile:String,otpCode:String,key:String,listener: OnResponseListener) {
        viewModelScope.launch {
            val response = accountCreationUpdateUseCase.verifyAccount(mobile,otpCode,key)

            if(!response.status)
            {
                listener.onFailed(response.code,response.message)
            }
            else
            {
                listener.onSuccess(response.code,response.message,response.data)
            }
        }
    }

    fun registerNewAccount(registerModel: RegisterModel,listener: OnResponseListener) {
        viewModelScope.launch {
            val registerResponse = accountCreationUpdateUseCase.registerNewAccount(registerModel)

            if(!registerResponse.status!!)
            {
                listener.onFailed(registerResponse.code!!,registerResponse.message)
            }
            else
            {
                listener.onSuccess(registerResponse.code!!,registerResponse.message,registerResponse.data)

            }
        }
    }

    fun createNewPassword(mobile: String,password: String,confirmationPassword:String,otpCode: String,listener: OnResponseListener) {
        viewModelScope.launch {
            val forgetPasswordResponse = accountCreationUpdateUseCase.createNewPassword(mobile,password,confirmationPassword,otpCode)
            if(forgetPasswordResponse.isSuccessful)
            {
                if(!forgetPasswordResponse.body()!!.status)
                {
                    listener.onFailed(forgetPasswordResponse.body()!!.code,forgetPasswordResponse.body()!!.message)
                }
                else
                {
                    listener.onSuccess(forgetPasswordResponse.body()!!.code,
                        forgetPasswordResponse.body()!!.message,
                    forgetPasswordResponse.body()!!.data)
                }
            }
            else
            {
                listener.onFailed(forgetPasswordResponse.code(),forgetPasswordResponse.message())
            }
        }
    }

    fun updatePassword(token: String, currentPassword:String,newPassword:String, listener: OnResponseListener) {
        viewModelScope.launch {
            val updateResponse = accountCreationUpdateUseCase.updatePassword(token, currentPassword, newPassword)

            if(updateResponse.isSuccessful)
            {
                if(!updateResponse.body()!!.status)
                {
                    listener.onFailed(updateResponse.body()!!.code,updateResponse.body()!!.message)
                }
                else
                {
                    listener.onSuccess(updateResponse.body()!!.code,updateResponse.body()!!.message,updateResponse.body()!!.data)
                }
            }
            else
            {
                listener.onFailed(updateResponse.code(),updateResponse.message())
            }
        }
    }

    fun updateProfile(token: String, mobile:String,name:String,email:String, listener: OnResponseListener) {
        viewModelScope.launch {
            val updateResponse = accountCreationUpdateUseCase.updateProfile(token, mobile, name, email)

            if(updateResponse.isSuccessful)
            {
                if(!updateResponse.body()!!.status)
                {
                    listener.onFailed(updateResponse.body()!!.code,updateResponse.body()!!.message)
                }
                else
                {
                    listener.onSuccess(updateResponse.body()!!.code,updateResponse.body()!!.message,updateResponse.body()!!.data)
                }
            }
            else
            {
                listener.onFailed(updateResponse.code(),updateResponse.message())
            }
        }
    }
}