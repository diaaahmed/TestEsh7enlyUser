package com.esh7enly.domain.usecase

import com.esh7enly.domain.entity.RegisterModel
import com.esh7enly.domain.entity.forgetpasswordotp.ForgetPasswordOTPResponse
import com.esh7enly.domain.entity.otpresponse.NewOtpResponse
import com.esh7enly.domain.repo.ServicesRepo
import retrofit2.Response

class AccountCreationUpdateUseCase(private val servicesRepo: ServicesRepo)
{
    suspend fun registerNewAccount(registerModel: RegisterModel) = servicesRepo.registerNewAccount(registerModel)

    suspend fun createNewPassword(mobile:String,password: String,confirmationPassword:String,otp:String) = servicesRepo.createNewPassword(mobile, password, confirmationPassword, otp)

    suspend fun updatePassword(token:String,currentPassword:String,newPassword:String) = servicesRepo.updatePassword(token, currentPassword, newPassword)

    suspend fun updateProfile(token:String,mobile:String,name:String,email:String) = servicesRepo.updateProfile(token, mobile, name,email)

    suspend fun verifyAccount(mobile:String,otpCode:String,key:String) =
        servicesRepo.verifyAccount(mobile,otpCode,key)

    suspend fun sendOtp(mobile: String): Response<NewOtpResponse> = servicesRepo.sendOtp(mobile)

    suspend fun forgetPasswordSendOtp(mobile: String): Response<ForgetPasswordOTPResponse> = servicesRepo.forgetPasswordSendOTP(mobile)
}