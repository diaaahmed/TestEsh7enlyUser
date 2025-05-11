package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.repo.UserRepoImpl
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.repo.UserRepo
import com.esh7enly.esh7enlyuser.util.isValidEmailId
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val userRepo: UserRepo,
) : ViewModel() {


    val updatedMobile = MutableStateFlow("")
    val updatedFirstName = MutableStateFlow("")
    val updatedLastName = MutableStateFlow("")
    val updatedEmail = MutableStateFlow("")

    private val isDataValid = combine(
        updatedMobile,
        updatedFirstName,
        updatedLastName,
        updatedEmail
    ) { mobile, first, last, email ->
        mobile.isBlank() && first.isBlank() && last.isBlank() && email.isBlank()
    }

    var updateProfileState: MutableStateFlow<NetworkResult<String>?> = MutableStateFlow(null)

    fun updateProfile() {
        viewModelScope.launch {
            val userEmail = updatedEmail.value

            if (
                isDataValid.first()
            ) {
                updateProfileState.update { NetworkResult.Error("Data empty", 0) }
            } else if (userEmail.isNotEmpty()) {
                if (!isValidEmailId(userEmail)) {
                    updateProfileState.update { NetworkResult.Error("Email not valid", 0) }
                } else {
                    sendUpdateRequest()
                }
            } else {
                sendUpdateRequest()
            }
        }
    }

    private fun sendUpdateRequest() {
        updateProfileState.update { NetworkResult.Loading() }

        viewModelScope.launch {
            try {
                val updateResponse = userRepo.updateProfile(
                    mobile = updatedMobile.value,
                    name = "${updatedFirstName.value} ${updatedLastName.value}",
                    email = updatedEmail.value
                )

                if (updateResponse.isSuccessful) {
                    if (!updateResponse.body()!!.status) {
                        updateProfileState.update {
                            NetworkResult.Error(
                                updateResponse.body()!!.message,
                                updateResponse.body()!!.code
                            )
                        }

                    } else {
                        updateProfileState.update { NetworkResult.Success(updateResponse.body()!!.message) }

                    }
                } else {
                    updateProfileState.update {
                        NetworkResult.Error(
                            updateResponse.message(),
                            updateResponse.code()
                        )
                    }
                }
            } catch (e: Exception) {
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "sendUpdateRequest from UpdateProfile viewModel"
                )
            }
        }

    }
}