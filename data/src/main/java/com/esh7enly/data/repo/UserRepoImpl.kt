package com.esh7enly.data.repo

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
import com.esh7enly.domain.repo.UserRepo
import retrofit2.Response

class UserRepoImpl (private val apiService: ApiService): UserRepo
{
    override fun login(
        mobile: String,
        password: String,
        deviceToken:String,
        imei:String): LiveData<ApiResponse<LoginResponse>> =
        apiService.login(mobile,password,deviceToken, imei = imei)


    override suspend fun sendOtp(mobile: String): Response<NewOtpResponse> = apiService.sendOtp(mobile)

    override suspend fun createNewPassword(
        mobile: String,
        password: String,
        confirmationPassword: String,
        key: String,
        token: String,
    ): Response<ForgetPasswordResponse> = apiService.createNewPassword(
        mobile,password,confirmationPassword,key,token)

    override suspend fun verifyAccount(mobile: String, otpCode: String, key: String): VerifyOtpResponse = apiService.verifyAccount(mobile,otpCode,key)

    override suspend fun getDeposits(token: String, page: Int): Response<DepositResponse> =
        apiService.getDeposits(page)

    override suspend fun getUserWallet(token: String): Response<UserWalletResponse> =
        apiService.getUserWallet()

    override suspend fun updatePassword(
        token: String,
        currentPassword: String,
        newPassword: String
    ): Response<ChargeBalanceResponse> =
        apiService.updatePassword(
            currentPassword,
            newPassword)

    override suspend fun updateProfile(
        token: String,
        name: String,
        mobile: String,
        email: String
    ): Response<ChargeBalanceResponse> =
        apiService.updateProfile(
        mobile,name, email = email)

    override suspend fun forgetPasswordSendOTP(mobile: String): Response<ForgetPasswordOTPResponse> =
        apiService.forgetPassword(mobile)

    override suspend fun registerNewAccount(registerModel: RegisterModel): RegisterResponse = apiService.registerNewAccount(registerModel)
    override suspend fun verifyForgetPassword(
        mobile: String,
        token: String,
        key: String
    ): Response<VerifyOtpResponse>  = apiService.verifyForgetPassword(mobile,token,key)


}