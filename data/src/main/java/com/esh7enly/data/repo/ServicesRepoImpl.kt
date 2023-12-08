package com.esh7enly.data.repo

import androidx.lifecycle.LiveData
import com.esh7enly.data.local.DatabaseRoom
import com.esh7enly.data.remote.ApiService
import com.esh7enly.data.remote.NotificationService
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
import com.esh7enly.domain.repo.ServicesRepo
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import retrofit2.Response

class ServicesRepoImpl(private val apiService: ApiService,
                       private val notificationService: NotificationService,
private val databaseRoom: DatabaseRoom
)
    : ServicesRepo{
    override suspend fun scheduleInvoice(
        token: String,
        serviceId: String,
        scheduleDay: String,
        invoice_number:String
    ): Response<ScheduleInvoiceResponse> = apiService.scheduleInvoice(token,serviceId,scheduleDay,invoice_number)

    override suspend fun getScheduleList(token: String): Response<ScheduleListResponse> = apiService.getScheduleList(token)

    override suspend fun scheduleInquire(
        token: String,
        serviceId: String,
        invoice_number: String
    ): Response<ScheduleInquireResponse> = apiService.scheduleInquire(token,serviceId,invoice_number)

    override suspend fun getServicesFromRemoteUser(token: String): Flow<UserServicesResponse> = flow {
        val response = apiService.getServicesFromRemoteUser(token)
        emit(response)
    }.flowOn(Dispatchers.IO)


    override suspend fun updatePassword(
        token: String,
        currentPassword: String,
        newPassword: String
    ): Response<ChargeBalanceResponse> = apiService.updatePassword(token, currentPassword, newPassword)

    override suspend fun updateProfile(
        token: String,
        name: String,
        mobile: String,
        email: String
    ): Response<ChargeBalanceResponse> = apiService.updateProfile(token = token,
        mobile,name, email = email)

    override suspend fun getFilteredCategories(): List<Category> {
        return databaseRoom.categoryDao().getCategories()
    }


    override suspend fun sendOtp(mobile: String): Response<NewOtpResponse> = apiService.sendOtp(mobile)

    override suspend fun getTotalAmount(
        token: String,
        totalAmountPojoModel: TotalAmountPojoModel
    ): Flow<TotalAmountEntity> = flow{
        val response = apiService.getTotalAmount(token,totalAmountPojoModel)
        emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun getUserPoints(token: String): Response<PointsResponse> = apiService.getUserPoints(token)
    override suspend fun replaceUserPoints(token: String): Response<ReplacePointsResponse> = apiService.replaceUserPoints(token)



    override suspend fun pay(token: String, paymentPojoModel: PaymentPojoModel): Response<PaymentEntity>
    = apiService.pay(token,paymentPojoModel)

    override suspend fun getDeposits(token: String,page:Int): Response<DepositResponse> = apiService.getDeposits(token,page)
    override suspend fun createNewPassword(
        mobile: String,
        password: String,
        confirmationPassword: String,
        otp: String
    ): Response<ForgetPasswordResponse> = apiService.createNewPassword(mobile,password,confirmationPassword,otp)

    override suspend fun inquire(token: String, paymentPojoModel: PaymentPojoModel): Response<PaymentEntity> = apiService.inquire(token,paymentPojoModel)

    override suspend fun checkIntegration(token: String, id: String, imei: String): Response<JsonElement>
    = apiService.checkIntegration(token,id,imei)

    override suspend fun getUserWallet(token: String): Response<UserWalletResponse>  = apiService.getUserWallet(token)

    override fun login(mobile: String, password: String,device_token:String): LiveData<ApiResponse<LoginResponse>> = apiService.login(mobile,password,device_token)
    override suspend fun getImageAdResponse(token: String): Response<ImageAdResponse> = apiService.getImageAdResponse(token)

    override suspend fun verifyAccount(mobile: String, otpCode: String, key: String): VerifyOtpResponse = apiService.verifyAccount(mobile,otpCode,key)

    override suspend fun getTransactions(token: String,page:Int): TransactionApiResponse =
        apiService.getTransactions(token,page)

    override suspend fun getTransactionDetails(
        token: String,
        transactionId: String
    ): Flow<TransactionDetailsEntity> = flow {
        val response = apiService.getTransactionDetails(token,transactionId)
        emit(response)
    }.flowOn(Dispatchers.IO)

    override suspend fun cancelTransaction(
        token: String,
        transaction_id: String,
        imei: String
    ): Response<JsonElement> = apiService.cancelTransaction(token,transaction_id, imei)

    override suspend fun sendNotification(notificationModel: NotificationModel): Response<ResponseBody> =
        notificationService.sendNotification(notificationModel)

    override suspend fun forgetPasswordSendOTP(mobile: String): Response<ForgetPasswordOTPResponse> =
        apiService.forgetPassword(mobile)

    override suspend fun registerNewAccount(registerModel: RegisterModel):RegisterResponse = apiService.registerNewAccount(registerModel)

    override suspend fun chargeBalance(token: String, chargeBalanceRequest: ChargeBalanceRequest)
    :Response<ChargeBalanceResponse>  = apiService.chargeBalance(token, chargeBalanceRequest)
    override suspend fun startSessionForPay(
        token: String,
        amount: String,
        ip: String
    ): Response<StartSessionResponse> = apiService.startSessionForPay(token, amount, ip)

    override suspend fun getTotalXPay(token: String, amount: String): Response<GetTotalAmountXPayResponse> = apiService.getTotalXpay(token, amount)


}