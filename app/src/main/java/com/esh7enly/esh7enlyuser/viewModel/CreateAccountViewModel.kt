package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.repo.UserRepo
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.entity.RegisterModel
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sharedHelper: SharedHelper
) : ViewModel()
{
    val userFirstName = MutableStateFlow("")
    val userLastName = MutableStateFlow("")
    val userEmail = MutableStateFlow("")
    val userPassword = MutableStateFlow("")
    val fullName = MutableStateFlow("${userFirstName.value} ${userLastName.value}")

    fun registerNewAccount(userPhoneNumber:String, listener: OnResponseListener) {
        viewModelScope.launch {
            try {
                val registerResponse = userRepo.registerNewAccount(
                    RegisterModel(fullName.value,
                        userPhoneNumber,
                        userPassword.value,
                        userEmail.value)
                )

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
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "registerNewAccount from CreateAccountViewModel"
                )

            }
        }
    }


}