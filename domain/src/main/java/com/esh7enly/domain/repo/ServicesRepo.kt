package com.esh7enly.domain.repo

import androidx.lifecycle.LiveData
import com.esh7enly.domain.ApiResponse
import com.esh7enly.domain.entity.*
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequest
import com.esh7enly.domain.entity.chargebalanceresponse.ChargeBalanceResponse
import com.esh7enly.domain.entity.depositsresponse.DepositResponse
import com.esh7enly.domain.entity.forgetpasswordotp.ForgetPasswordOTPResponse
import com.esh7enly.domain.entity.forgetpasswordresponse.ForgetPasswordResponse
import com.esh7enly.domain.entity.imageadsresponse.ImageAdResponse
import com.esh7enly.domain.entity.loginresponse.LoginResponse
import com.esh7enly.domain.entity.otpresponse.NewOtpResponse
import com.esh7enly.domain.entity.pointsresponse.PointsResponse
import com.esh7enly.domain.entity.registerresponse.RegisterResponse
import com.esh7enly.domain.entity.replacepointsresponse.ReplacePointsResponse
import com.esh7enly.domain.entity.scedulelistresponse.ScheduleListResponse
import com.esh7enly.domain.entity.scheduleinquireresoponse.ScheduleInquireResponse
import com.esh7enly.domain.entity.scheduleinvoice.ScheduleInvoiceResponse
import com.esh7enly.domain.entity.startsessionresponse.StartSessionResponse
import com.esh7enly.domain.entity.totalamountxpayresponse.GetTotalAmountXPayResponse
import com.esh7enly.domain.entity.userservices.Category
import com.esh7enly.domain.entity.userservices.UserServicesResponse
import com.esh7enly.domain.entity.userwallet.UserWalletResponse
import com.esh7enly.domain.entity.verifyotp.VerifyOtpResponse
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface ServicesRepo
{
    suspend fun scheduleInvoice(token: String,serviceId:String,scheduleDay:String,invoice_number:String):Response<ScheduleInvoiceResponse>

    suspend fun getScheduleList(token:String):Response<ScheduleListResponse>

    suspend fun scheduleInquire(token: String,serviceId: String,invoice_number:String):Response<ScheduleInquireResponse>

    suspend fun getServicesFromRemoteUser(token:String):Flow<UserServicesResponse>

    suspend fun sendOtp(mobile:String): Response<NewOtpResponse>

    suspend fun getTotalAmount(token:String, totalAmountPojoModel: TotalAmountPojoModel):Flow<TotalAmountEntity>

    suspend fun getUserPoints(token:String):Response<PointsResponse>

    suspend fun replaceUserPoints(token:String):Response<ReplacePointsResponse>

    suspend fun pay(token: String,paymentPojoModel: PaymentPojoModel):Response<PaymentEntity>

    suspend fun getDeposits(token: String,page:Int):Response<DepositResponse>

    suspend fun createNewPassword(mobile:String,password: String,confirmationPassword:String,otp: String):Response<ForgetPasswordResponse>

    suspend fun inquire(token: String,paymentPojoModel: PaymentPojoModel):Response<PaymentEntity>

    suspend fun checkIntegration(token: String,id:String,imei:String):Response<JsonElement>

    suspend fun getUserWallet(token:String):Response<UserWalletResponse>

    fun login(mobile:String,password:String,device_token:String):LiveData<ApiResponse<LoginResponse>>

    suspend fun getImageAdResponse(token: String):Response<ImageAdResponse>

    suspend fun verifyAccount(mobile:String,otpCode:String,key:String):VerifyOtpResponse

    suspend fun getTransactions(token:String,page:Int):TransactionApiResponse

    suspend fun getTransactionDetails(token:String, transactionId:String):Flow<TransactionDetailsEntity>

    suspend fun cancelTransaction(token:String,transaction_id: String,imei:String):Response<JsonElement>

    suspend fun sendNotification(notificationModel: NotificationModel):Response<ResponseBody>

    suspend fun forgetPasswordSendOTP(mobile:String):Response<ForgetPasswordOTPResponse>

    suspend fun registerNewAccount(registerModel: RegisterModel):RegisterResponse

    suspend fun chargeBalance(token:String, chargeBalanceRequest: ChargeBalanceRequest):Response<ChargeBalanceResponse>

    suspend fun startSessionForPay(token:String,amount:String,ip:String):Response<StartSessionResponse>

    suspend fun getTotalXPay(token:String, amount:String):Response<GetTotalAmountXPayResponse>

    suspend fun updatePassword(token:String,currentPassword:String,newPassword:String):Response<ChargeBalanceResponse>

    suspend fun updateProfile(token:String,mobile:String,name:String,email:String):Response<ChargeBalanceResponse>

    suspend fun getFilteredCategories():List<Category>
}