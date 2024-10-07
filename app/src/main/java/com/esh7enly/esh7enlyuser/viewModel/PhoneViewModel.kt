package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.domain.repo.UserRepo
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PhoneViewModel @Inject constructor(
    private val userRepo: UserRepo
): ViewModel()
{
    val userPhoneNumber = MutableStateFlow("")

    private val isPhoneNumberValid = userPhoneNumber.map { phoneNumber ->
        phoneNumber.length == 11
    }

    fun forgetPasswordSendOTP(listener: OnResponseListener) {
        viewModelScope.launch {

            if(isPhoneNumberValid.first())
            {
                try {
                    val otpResponse = userRepo.forgetPasswordSendOTP(userPhoneNumber.value)

                    if (otpResponse.isSuccessful) {
                        if (!otpResponse.body()!!.status) {
                            listener.onFailed(otpResponse.body()!!.code, otpResponse.body()!!.message)
                        } else {

                            Constants.USER_KEY = otpResponse.body()!!.data.key

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
                    sendIssueToCrashlytics(
                        e.message.toString(),
                        "forgetPasswordSendOTP from Phone viewModel"
                    )
                }
            }
            else
            {
                listener.onFailed(404,"Invalid number")

            }
        }
    }

    fun sendOtp(listener: OnResponseListener) {
        viewModelScope.launch {
            if (isPhoneNumberValid.first())
            {
                try {
                    val otpResponse = userRepo.sendOtp(userPhoneNumber.value)

                    if (otpResponse.isSuccessful) {
                        if (!otpResponse.body()!!.status!!)
                        {
                            listener.onFailed(otpResponse.body()!!.code!!, otpResponse.body()!!.message)
                        } else
                        {
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
                } catch (e: Exception) {
                    sendIssueToCrashlytics(
                        e.message.toString(),
                        "send otp from Phone viewModel"
                    )

                }
            }
            else
            {
                listener.onFailed(404,"Invalid number")
            }
        }
    }

    fun sendOtp(mobile: String, listener: OnResponseListener) {
        viewModelScope.launch {

            try {
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
            } catch (e: Exception) {
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "send otp from User viewModel"
                )

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
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "forgetPasswordSendOTP from User viewModel"
                )
            }
        }
    }
}

