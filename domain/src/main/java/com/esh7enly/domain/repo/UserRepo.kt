package com.esh7enly.domain.repo

import android.net.Uri
import androidx.lifecycle.LiveData
import com.esh7enly.domain.ApiResponse
import com.esh7enly.domain.NetworkResult
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
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface UserRepo {

    fun login(
        mobile: String,
        password: String,
        deviceToken: String,
        imei: String
    ): LiveData<ApiResponse<LoginResponse>>

    fun loginWithState(
        mobile: String,
        password: String,
        deviceToken: String,
        imei: String
    ): Flow<NetworkResult<LoginResponse>>

    suspend fun sendOtp(mobile: String): Response<NewOtpResponse>

    suspend fun createNewPassword(
        mobile: String,
        password: String,
        confirmationPassword: String,
        key: String,
        token: String,
    ): Response<ForgetPasswordResponse>

    suspend fun verifyAccount(mobile: String, otpCode: String, key: String): VerifyOtpResponse

    suspend fun getDeposits(token: String, page: Int): Response<DepositResponse>

    suspend fun getUserWallet(token: String): Response<UserWalletResponse>

    suspend fun updatePassword(
        token: String,
        currentPassword: String,
        newPassword: String
    ): Response<ChargeBalanceResponse>

    suspend fun updateProfile(
        token: String,
        name: String,
        mobile: String,
        email: String
    ): Response<ChargeBalanceResponse>

    suspend fun forgetPasswordSendOTP(mobile: String): Response<ForgetPasswordOTPResponse>

    suspend fun registerNewAccount(registerModel: RegisterModel): RegisterResponse


    suspend fun verifyForgetPassword(
        mobile: String,
        token: String,
        key: String
    ): Response<VerifyOtpResponse>
}