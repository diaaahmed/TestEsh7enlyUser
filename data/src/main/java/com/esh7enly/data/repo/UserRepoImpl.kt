package com.esh7enly.data.repo

import androidx.lifecycle.LiveData
import com.esh7enly.data.remote.ApiService
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
import com.esh7enly.domain.repo.UserRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class UserRepoImpl (
    private val apiService: ApiService,
): UserRepo
{

    override fun loginWithState(
        mobile:String,
        password:String,
        deviceToken:String,
        imei:String
    ): Flow<NetworkResult<LoginResponse>> = flow {
        try{
            emit(NetworkResult.Loading())

            val getLoginResponse = apiService.loginWithState(
                mobile = mobile,
                userPassword = password,
                imei = imei,
                deviceToken = deviceToken
            )

            if(getLoginResponse.isSuccessful)
            {
                if(getLoginResponse.body()?.status== true)
                {
                    emit(NetworkResult.Success(getLoginResponse.body()!!))
                }
                else
                {
                    emit(NetworkResult.Error(
                        message = getLoginResponse.body()?.message,
                        code = getLoginResponse.body()?.code
                    ))
                }
            }
            else
            {
                emit(NetworkResult.Error(
                    message = getLoginResponse.message(),
                    code = getLoginResponse.code()
                ))
            }
        }
        catch (e: Exception)
        {
            emit(NetworkResult.Error(
                message = e.message,
                code = 404
            ))
        }
    }

    
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

    override suspend fun getDeposits(page: Int): Response<DepositResponse> =
        apiService.getDeposits(page)

    override suspend fun getUserWallet(): Response<UserWalletResponse> =
        apiService.getUserWallet()

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): Response<ChargeBalanceResponse> =
        apiService.updatePassword(
            currentPassword,
            newPassword)

    override suspend fun updateProfile(
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