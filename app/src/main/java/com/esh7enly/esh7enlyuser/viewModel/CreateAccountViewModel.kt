package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.repo.UserRepoImpl
import com.esh7enly.domain.entity.RegisterModel
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.isValidEmailId
import com.esh7enly.esh7enlyuser.util.isValidPassword
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val userRepo: UserRepoImpl
) : ViewModel()
{
    var phoneNumber: String? = null

     val userFirstName = MutableStateFlow("")
     val userLastName = MutableStateFlow("")
     val userEmail = MutableStateFlow("")
     val userPassword = MutableStateFlow("")
     val confirmationPassword = MutableStateFlow("")

    private val isRegisterDataValid = combine(userFirstName,userLastName,userEmail,userPassword,confirmationPassword)
    { userFirstName, userLastName, userEmail, userPassword, confirmationPassword ->
        userFirstName.isEmpty() || userLastName.isEmpty() ||
                userPassword.isEmpty() || confirmationPassword.isEmpty() ||
                userPassword != confirmationPassword ||
                !isValidPassword(userPassword) || !isValidEmailId(userEmail)
    }

    fun registerNewAccount(userPhoneNumber:String, listener: OnResponseListener) {
        viewModelScope.launch {
            if(!isRegisterDataValid.first())
            {
                try {
                    val registerResponse = userRepo.registerNewAccount(
                        RegisterModel(
                            name = userFirstName.value + " " + userLastName.value ,
                            mobile = userPhoneNumber,
                            password = userPassword.value,
                            password_confirm = confirmationPassword.value,
                            email = userEmail.value)
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
            else
            {
                listener.onFailed(404,"Invalid data")
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
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "register new account from CreateAccountViewModel"
                )

            }
        }
    }
}