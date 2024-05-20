package com.esh7enly.data.remote

import androidx.lifecycle.LiveData
import com.esh7enly.data.url.Urls
import com.esh7enly.domain.ApiResponse
import com.esh7enly.domain.entity.*
import com.esh7enly.domain.entity.categoriesNew.CategoriesResponse
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.domain.entity.chargebalanceresponse.ChargeBalanceResponse
import com.esh7enly.domain.entity.depositsresponse.DepositResponse
import com.esh7enly.domain.entity.forgetpasswordotp.ForgetPasswordOTPResponse
import com.esh7enly.domain.entity.forgetpasswordresponse.ForgetPasswordResponse
import com.esh7enly.domain.entity.imageadsresponse.ImageAdResponse
import com.esh7enly.domain.entity.loginresponse.LoginResponse
import com.esh7enly.domain.entity.otpresponse.NewOtpResponse
import com.esh7enly.domain.entity.parametersNew.ParametersResponse
import com.esh7enly.domain.entity.pointsresponse.PointsResponse
import com.esh7enly.domain.entity.providersNew.ProviderResponse
import com.esh7enly.domain.entity.registerresponse.RegisterResponse
import com.esh7enly.domain.entity.replacepointsresponse.ReplacePointsResponse
import com.esh7enly.domain.entity.scedulelistresponse.ScheduleListResponse
import com.esh7enly.domain.entity.scheduleinquireresoponse.ScheduleInquireResponse
import com.esh7enly.domain.entity.scheduleinvoice.ScheduleInvoiceResponse
import com.esh7enly.domain.entity.searchresponse.SearchResponse
import com.esh7enly.domain.entity.servicesNew.ServiceResponse
import com.esh7enly.domain.entity.startsessionresponse.StartSessionResponse
import com.esh7enly.domain.entity.totalamountxpayresponse.GetTotalAmountXPayResponse
import com.esh7enly.domain.entity.userwallet.UserWalletResponse
import com.esh7enly.domain.entity.verifyotp.VerifyOtpResponse
import com.google.gson.JsonElement
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService
{
    @FormUrlEncoded
    @POST(Urls.LOGIN)
    fun login(
        @Field("mobile") mobile: String,
        @Field("password") userPassword: String,
        @Field("device_token") device_token: String
    ): LiveData<ApiResponse<LoginResponse>>

    @FormUrlEncoded
    @POST(Urls.SEND_OTP)
    suspend fun sendOtp(
        @Field("mobile") mobile: String,
    ): Response<NewOtpResponse>

    @FormUrlEncoded
    @POST(Urls.SCHEDULE_INQUIRE)
    suspend fun scheduleInquire(
        @Header("Authorization") token: String?,
        @Field("service_id") service_id: String,
        @Field("invoice_number") invoice_number:String
    ): Response<ScheduleInquireResponse>

    @POST(Urls.SCHEDULE_LIST)
    suspend fun getScheduleList(
        @Header("Authorization") token: String?,
    ): Response<ScheduleListResponse>

    @FormUrlEncoded
    @POST(Urls.SCHEDULE_INVOICE)
    suspend fun scheduleInvoice(
        @Header("Authorization") token: String?,
        @Field("service_id") service_id: String,
        @Field("schedule_date") scheduleDay:String,
        @Field("invoice_number") invoice_number:String
    ):Response<ScheduleInvoiceResponse>

    @POST(Urls.IMAGE_ADS)
    suspend fun getImageAdResponse(
        @Header("Authorization") token: String?,
    ): Response<ImageAdResponse>

    @POST(Urls.USER_POINTS)
    suspend fun getUserPoints(
        @Header("Authorization") token: String?,
    ): Response<PointsResponse>

    @POST(Urls.REPLACE_POINTS)
    suspend fun replaceUserPoints(
        @Header("Authorization") token: String?,
    ): Response<ReplacePointsResponse>

    @FormUrlEncoded
    @POST(Urls.TRANSACTIONS)
    suspend fun getTransactions(
        @Header("Authorization") token: String?,
        @Field("page") page: Int
    ): TransactionApiResponse


    @POST(Urls.TRANSACTION_DETAILS)
    suspend fun getTransactionDetails(
        @Header("Authorization") token: String?,
        @Path("TRANSACTION_ID") transactionId: String
    ): TransactionDetailsEntity


    @FormUrlEncoded
    @POST(Urls.OTP)
    suspend fun verifyAccount(
        @Field("mobile") mobile: String,
        @Field("token") otpCode: String,
        @Field("key") userKey: String
    ): VerifyOtpResponse


    @POST(Urls.REGISTER)
    suspend fun registerNewAccount(
        @Body registerModel: RegisterModel
    ): RegisterResponse

    @POST(Urls.TOTAL_AMOUNT)
    suspend fun getTotalAmount(
        @Header("Authorization") token: String?,
        @Body totalAmountPojoModel: TotalAmountPojoModel
    ): TotalAmountEntity

    @POST(Urls.CHARGE_BALANCE)
    suspend fun chargeBalanceWithPaytabs(
        @Header("Authorization") token: String?,
        @Body chargeBalanceRequest: ChargeBalanceRequestPaytabs,
    ): Response<ChargeBalanceResponse>

    @FormUrlEncoded
    @POST(Urls.UPDATE_PASSWORD)
    suspend fun updatePassword(
        @Header("Authorization") token: String?,
        @Field("currant_password") currentPassword:String,
        @Field("new_password") newPassword:String
    ): Response<ChargeBalanceResponse>

    @FormUrlEncoded
    @POST(Urls.PROVIDERS)
    suspend fun getProviders(
        @Header("Authorization") token: String?,
        @Field("id") id:String
    ):Response<ProviderResponse>

    @FormUrlEncoded
    @POST(Urls.SERVICE_SEARCH)
    suspend fun serviceSearch(
        @Header("Authorization") token: String?,
        @Field("name") serviceName:String,
        @Field("page") page: Int
    ):Response<SearchResponse>

    @FormUrlEncoded
    @POST(Urls.SERVICES_NEW)
    suspend fun getServices(
        @Header("Authorization") token: String?,
        @Field("id") id:String
    ):Response<ServiceResponse>

    @FormUrlEncoded
    @POST(Urls.PARAMETERS)
    suspend fun getParameters(
        @Header("Authorization") token: String?,
        @Field("id") id:String
    ):Response<ParametersResponse>

    @POST(Urls.CATEGORIES)
    suspend fun getCategories(
        @Header("Authorization") token: String?
    ):Response<CategoriesResponse>

    @FormUrlEncoded
    @POST(Urls.UPDATE_PROFILE)
    suspend fun updateProfile(
        @Header("Authorization") token: String?,
        @Field("mobile") mobile:String,
        @Field("name") name:String,
        @Field("email") email:String
    ): Response<ChargeBalanceResponse>

    @POST(Urls.GET_DEPOSITS)
    suspend fun getDeposits(
        @Header("Authorization") token: String?,
        @Query("page") page:Int
    ): Response<DepositResponse>

    @FormUrlEncoded
    @POST(Urls.FORGET_PASSWORD)
    suspend fun forgetPassword(
        @Field("mobile") mobile: String?,
    ): Response<ForgetPasswordOTPResponse>

    @FormUrlEncoded
    @POST(Urls.NEW_PASSWORD)
    suspend fun createNewPassword(
        @Field("mobile") mobile: String?,
        @Field("password") password: String?,
        @Field("password_confirmation") password_confirmation: String?,
        @Field("otp") otp: String?,
    ): Response<ForgetPasswordResponse>

    @FormUrlEncoded
    @POST(Urls.START_SESSION)
    suspend fun startSessionForPay(
        @Field("payment_method_type") payment_method_type:String,
        @Field("transaction_type") transaction_type:String,
        @Header("Authorization") token: String?,
        @Field("amount") amount: String,
        @Field("ip") ip: String
    ): Response<StartSessionResponse>

    @FormUrlEncoded
    @POST(Urls.TOTAL_XPAY)
    suspend fun getTotalXpay(
        @Header("Authorization") token: String?,
        @Field("amount") amount: String,
        @Field("payment_method_type") payment_method_type:String,
        @Field("transaction_type") transaction_type :String,
    ): Response<GetTotalAmountXPayResponse>

    @POST(Urls.PAYMENT)
    suspend fun pay(
        @Header("Authorization") token: String?,
        @Body paymentPojoModel: PaymentPojoModel
    ): Response<PaymentEntity>

    @POST(Urls.INQUIRY)
    suspend fun inquire(
        @Header("Authorization") token: String?,
        @Body paymentPojoModel: PaymentPojoModel
    ): Response<PaymentEntity>


    @POST(Urls.CHECK_INTEGRATION_PROVIDER_STATUS)
    @FormUrlEncoded
    suspend fun checkIntegration(
        @Header("Authorization") token: String?,
        @Field("id") transaction_id: String,
        @Field("imei") imei: String
    ): Response<JsonElement>

    @POST(Urls.WALLETS)
    suspend fun getUserWallet(
        @Header("Authorization") token: String?
    ): Response<UserWalletResponse>

    @POST(Urls.CANCEL_SERVICE)
    @FormUrlEncoded
    suspend fun cancelTransaction(
        @Header("Authorization") token: String?,
        @Field("transaction_id") transaction_id: String,
        @Field("imei") imei: String
    ): Response<JsonElement>

}