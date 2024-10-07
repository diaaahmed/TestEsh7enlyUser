package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.domain.repo.UserRepo
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val userRepo: UserRepo,

    ) : ViewModel() {

    var phoneNumber: String? = null

    fun verifyForgetPassword(
        mobile:String,
        token:String,
        listener: OnResponseListener
    )
    {
        viewModelScope.launch {
            try {
                val response = userRepo.verifyForgetPassword(mobile, token, Constants.USER_KEY)

                if (!response.isSuccessful) {
                    listener.onFailed(response.code(), response.message())
                } else {
                    listener.onSuccess(
                        response.body()!!.code,
                        response.body()!!.message, response.body()!!.data)
                }
            } catch (e: Exception) {
                listener.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(e.message.toString(), "forget password from Otp viewModel")

            }
        }
    }

    fun verifyAccount(
        mobile: String,
        otpCode: String,
        key: String, listener: OnResponseListener
    ) {
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
                sendIssueToCrashlytics(e.message.toString(), "verify account from Otp viewModel")

            }
        }
    }

}