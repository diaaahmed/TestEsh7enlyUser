package com.esh7enly.data.repo

import android.content.Context
import androidx.lifecycle.LiveData
import com.esh7enly.data.remote.ApiService
import com.esh7enly.domain.ApiResponse
import com.esh7enly.domain.entity.RegisterModel
import com.esh7enly.domain.entity.chargebalanceresponse.ChargeBalanceResponse
import com.esh7enly.domain.entity.depositsresponse.DepositResponse
import com.esh7enly.domain.entity.forgetpasswordotp.ForgetPasswordOTPResponse
import com.esh7enly.domain.entity.forgetpasswordresponse.ForgetPasswordResponse
import com.esh7enly.domain.entity.loginresponse.LoginResponse
import com.esh7enly.domain.entity.otpresponse.NewOtpResponse
import com.esh7enly.domain.entity.registerresponse.RegisterResponse
import com.esh7enly.domain.entity.userwallet.UserWalletResponse
import com.esh7enly.domain.entity.verifyotp.VerifyOtpResponse
import retrofit2.Response

class UserRepo(private val apiService: ApiService,
               private val context: Context
)
{
    fun login(
        mobile: String,
        password: String,
        device_token:String): LiveData<ApiResponse<LoginResponse>> =
        apiService.login(mobile,password,device_token)

    fun getString(resId: Int): String {
        return context.getString(resId)
    }

    suspend fun sendOtp(mobile: String): Response<NewOtpResponse> = apiService.sendOtp(mobile)

    suspend fun createNewPassword(
        mobile: String,
        password: String,
        confirmationPassword: String,
        otp: String
    ): Response<ForgetPasswordResponse> = apiService.createNewPassword(mobile,password,confirmationPassword,otp)

    suspend fun verifyAccount(mobile: String, otpCode: String, key: String): VerifyOtpResponse = apiService.verifyAccount(mobile,otpCode,key)

    suspend fun getDeposits(token: String, page: Int): Response<DepositResponse> =
        apiService.getDeposits(token, page)

    suspend fun getUserWallet(token: String): Response<UserWalletResponse> =
        apiService.getUserWallet(token)

    suspend fun updatePassword(
        token: String,
        currentPassword: String,
        newPassword: String
    ): Response<ChargeBalanceResponse> =
        apiService.updatePassword(
            token,
            currentPassword,
            newPassword)

    suspend fun updateProfile(
        token: String,
        name: String,
        mobile: String,
        email: String
    ): Response<ChargeBalanceResponse> =
        apiService.updateProfile(
            token = token,
        mobile,name, email = email)

    suspend fun forgetPasswordSendOTP(mobile: String): Response<ForgetPasswordOTPResponse> =
        apiService.forgetPassword(mobile)

    suspend fun registerNewAccount(registerModel: RegisterModel): RegisterResponse = apiService.registerNewAccount(registerModel)
}