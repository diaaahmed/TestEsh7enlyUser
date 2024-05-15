package com.esh7enly.esh7enlyuser.viewModel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.repo.UserRepo
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val userRepo: UserRepo,

    ): ViewModel() {


    val updatedMobile = MutableStateFlow("")
    val updatedFirstName = MutableStateFlow("")
    val updatedLastName = MutableStateFlow("")
    val updatedEmail = MutableStateFlow("")

    private val isDataValid = combine(updatedMobile,updatedFirstName,updatedLastName,updatedEmail) {
            mobile,first,last,email->
        mobile.isBlank() && first.isBlank() && last.isBlank() && email.isBlank()
    }


    fun updateProfile(
        token: String,
        listener: OnResponseListener
    ) {
        viewModelScope.launch {
            val userEmail = updatedEmail.value

            if(
                isDataValid.first()
            )
            {
                listener.onFailed(2,"Data empty")
            }
            else if(userEmail.isNotEmpty())
            {
                if (!isValidEmailId(userEmail))
                {
                    listener.onFailed(1,"Email is not valid")
                } else {
                    sendUpdateRequest(token, listener)
                }
            }
            else
            {
                sendUpdateRequest(token, listener)
            }
        }
    }

    private fun sendUpdateRequest(token:String, listener: OnResponseListener)
    {
        viewModelScope.launch {
            val updateResponse = userRepo.updateProfile(
                token = token, mobile = updatedMobile.value,
                name = "${updatedFirstName.value} ${updatedLastName.value}",
                email = updatedEmail.value)

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


    private fun isValidEmailId(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

}