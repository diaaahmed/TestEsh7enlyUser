package com.esh7enly.data.remote

import androidx.lifecycle.LiveData
import com.esh7enly.data.url.Url.CANCEL_SERVICE
import com.esh7enly.data.url.Url.CATEGORIES
import com.esh7enly.data.url.Url.CHECK_INTEGRATION_PROVIDER_STATUS
import com.esh7enly.data.url.Url.FORGET_PASSWORD
import com.esh7enly.data.url.Url.GET_DEPOSITS
import com.esh7enly.data.url.Url.IMAGE_ADS
import com.esh7enly.data.url.Url.INQUIRY
import com.esh7enly.data.url.Url.LOGIN
import com.esh7enly.data.url.Url.NEW_PASSWORD
import com.esh7enly.data.url.Url.OTP
import com.esh7enly.data.url.Url.PARAMETERS
import com.esh7enly.data.url.Url.PAYMENT
import com.esh7enly.data.url.Url.PROVIDERS
import com.esh7enly.data.url.Url.REGISTER
import com.esh7enly.data.url.Url.REPLACE_POINTS
import com.esh7enly.data.url.Url.SCHEDULE_INQUIRE
import com.esh7enly.data.url.Url.SCHEDULE_INVOICE
import com.esh7enly.data.url.Url.SCHEDULE_LIST
import com.esh7enly.data.url.Url.SEND_OTP
import com.esh7enly.data.url.Url.SERVICES_NEW
import com.esh7enly.data.url.Url.SERVICE_SEARCH
import com.esh7enly.data.url.Url.START_SESSION
import com.esh7enly.data.url.Url.TOTAL_AMOUNT
import com.esh7enly.data.url.Url.TOTAL_XPAY
import com.esh7enly.data.url.Url.TRANSACTIONS
import com.esh7enly.data.url.Url.TRANSACTION_DETAILS
import com.esh7enly.data.url.Url.UPDATE_PASSWORD
import com.esh7enly.data.url.Url.UPDATE_PROFILE
import com.esh7enly.data.url.Url.USER_POINTS
import com.esh7enly.data.url.Url.VERIFY_FORGET_PASSWORD
import com.esh7enly.data.url.Url.VISA_WALLET
import com.esh7enly.data.url.Url.WALLETS
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart

import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface ApiService
{
   // Start Auth region

    @FormUrlEncoded
    @POST(LOGIN)
    fun login(
        @Field("mobile") mobile: String,
        @Field("password") userPassword: String,
        @Field("device_token") deviceToken: String,
        @Field("type") type: String = "mobile",
        @Field("imei") imei: String
    ): LiveData<ApiResponse<LoginResponse>>

    @FormUrlEncoded
    @POST(LOGIN)
    suspend fun loginWithState(
        @Field("mobile") mobile: String,
        @Field("password") userPassword: String,
        @Field("device_token") deviceToken: String,
        @Field("type") type: String = "mobile",
        @Field("imei") imei: String
    ): Response<LoginResponse>


    @FormUrlEncoded
    @POST(SEND_OTP)
    suspend fun sendOtp(
        @Field("mobile") mobile: String,
    ): Response<NewOtpResponse>

    @FormUrlEncoded
    @POST(OTP)
    suspend fun verifyAccount(
        @Field("mobile") mobile: String,
        @Field("token") otpCode: String,
        @Field("key") userKey: String
    ): VerifyOtpResponse


    @POST(REGISTER)
    suspend fun registerNewAccount(
        @Body registerModel: RegisterModel
    ): RegisterResponse

    @Multipart
    @POST(REGISTER)
    suspend fun registerNewAccountWithFiles(
        @Part registerModel: RequestBody,
        @Part frontImage: MultipartBody.Part,
    ): RegisterResponse

    @FormUrlEncoded
    @POST(FORGET_PASSWORD)
    suspend fun forgetPassword(
        @Field("mobile") mobile: String?,
    ): Response<ForgetPasswordOTPResponse>

    @FormUrlEncoded
    @POST(VERIFY_FORGET_PASSWORD)
    suspend fun verifyForgetPassword(
        @Field("mobile") mobile: String?,
        @Field("token") token: String?,
        @Field("key") key: String?,
    ): Response<VerifyOtpResponse>


    // End Auth region


    @FormUrlEncoded
    @POST(SCHEDULE_INQUIRE)
    suspend fun scheduleInquire(
        @Field("service_id") serviceId: String,
        @Field("invoice_number") invoiceNumber:String
    ): Response<ScheduleInquireResponse>

    @POST(SCHEDULE_LIST)
    suspend fun getScheduleList(
    ): Response<ScheduleListResponse>

    @FormUrlEncoded
    @POST(SCHEDULE_INVOICE)
    suspend fun scheduleInvoice(
        @Field("service_id") serviceId: String,
        @Field("schedule_date") scheduleDay:String,
        @Field("invoice_number") invoiceNumber:String
    ):Response<ScheduleInvoiceResponse>

    @POST(IMAGE_ADS)
    suspend fun getImageAdResponse(
    ): Response<ImageAdResponse>

    @POST(USER_POINTS)
    suspend fun getUserPoints(
    ): Response<PointsResponse>

    @POST(REPLACE_POINTS)
    suspend fun replaceUserPoints(
    ): Response<ReplacePointsResponse>

    @FormUrlEncoded
    @POST(TRANSACTIONS)
    suspend fun getTransactions(
        @Field("page") page: Int
    ): TransactionApiResponse


    @POST(TRANSACTION_DETAILS)
    suspend fun getTransactionDetails(
        @Path("TRANSACTION_ID") transactionId: String
    ): TransactionDetailsEntity


    @POST(TOTAL_AMOUNT)
    suspend fun getTotalAmount(
        @Body totalAmountPojoModel: TotalAmountPojoModel
    ): TotalAmountEntity


    @FormUrlEncoded
    @POST(UPDATE_PASSWORD)
    suspend fun updatePassword(
        @Field("currant_password") currentPassword:String,
        @Field("new_password") newPassword:String
    ): Response<ChargeBalanceResponse>

    @FormUrlEncoded
    @POST(PROVIDERS)
    suspend fun getProviders(
        @Field("id") id:String
    ):Response<ProviderResponse>

    @FormUrlEncoded
    @POST(SERVICE_SEARCH)
    suspend fun serviceSearch(
        @Field("name") serviceName:String,
        @Field("page") page: Int
    ):Response<SearchResponse>

    @FormUrlEncoded
    @POST(SERVICES_NEW)
    suspend fun getServices(
        @Field("id") id:String
    ):Response<ServiceResponse>

    @FormUrlEncoded
    @POST(PARAMETERS)
    suspend fun getParameters(
        @Field("id") id:String
    ):Response<ParametersResponse>

    @POST(CATEGORIES)
    suspend fun getCategories(
    ):Response<CategoriesResponse>

    @FormUrlEncoded
    @POST(UPDATE_PROFILE)
    suspend fun updateProfile(
        @Field("mobile") mobile:String,
        @Field("name") name:String,
        @Field("email") email:String
    ): Response<ChargeBalanceResponse>

    @POST(GET_DEPOSITS)
    suspend fun getDeposits(
        @Query("page") page:Int
    ): Response<DepositResponse>


    @FormUrlEncoded
    @POST(NEW_PASSWORD)
    suspend fun createNewPassword(
        @Field("mobile") mobile: String?,
        @Field("password") password: String?,
        @Field("password_confirmation") passwordConfirmation: String?,
        @Field("key") key: String?,
        @Field("token") token: String?,
    ): Response<ForgetPasswordResponse>

    @FormUrlEncoded
    @POST(TOTAL_XPAY)
    suspend fun getTotalXpay(
        @Field("amount") amount: String,
        @Field("payment_method_type") paymentMethodType:String,
        @Field("transaction_type") transactionType :String,
    ): Response<GetTotalAmountXPayResponse>

    @FormUrlEncoded
    @POST(START_SESSION)
    suspend fun startSessionForPay(
        @Field("payment_method_type") paymentMethodType:String,
        @Field("transaction_type") transactionType:String,
        @Field("amount") amount: String,
        @Field("total_amount") total_amount: String,
        @Field("ip") ip: String
    ): Response<StartSessionResponse>


    @POST
    suspend fun chargeBalanceWithPaytabs(
        @Url urlString: String,
        @Body chargeBalanceRequest: ChargeBalanceRequestPaytabs,
    ): Response<ChargeBalanceResponse>


    @POST(VISA_WALLET)
    suspend fun checkWalletStatus(
        @Body chargeBalanceRequest: ChargeBalanceRequestPaytabs
    ):Response<ChargeBalanceResponse>


    @POST(PAYMENT)
    suspend fun pay(
        @Body paymentPojoModel: PaymentPojoModel
    ): Response<PaymentEntity>

    @POST(INQUIRY)
    suspend fun inquire(
        @Body paymentPojoModel: PaymentPojoModel
    ): Response<PaymentEntity>


    @POST(CHECK_INTEGRATION_PROVIDER_STATUS)
    @FormUrlEncoded
    suspend fun checkIntegration(
        @Field("id") transactionId: String,
        @Field("imei") imei: String
    ): Response<JsonElement>

    @POST(WALLETS)
    suspend fun getUserWallet(
    ): Response<UserWalletResponse>

    @POST(CANCEL_SERVICE)
    @FormUrlEncoded
    suspend fun cancelTransaction(
        @Field("transaction_id") transactionId: String,
        @Field("imei") imei: String
    ): Response<JsonElement>

}