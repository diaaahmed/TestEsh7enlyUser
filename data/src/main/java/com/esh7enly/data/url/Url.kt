package com.esh7enly.data.url

object Url {

    // Start Auth region

    const val LOGIN = "auth/login"
    const val FORGET_PASSWORD = "auth/send-forget-password-otp"
    const val VERIFY_FORGET_PASSWORD = "auth/forget-password"
    const val NEW_PASSWORD = "auth/forget-change-password"
    const val OTP = "auth/otp"
    const val REGISTER = "auth/register"
    const val SEND_OTP = "auth/send-otp"

    // End Auth region


    const val UPDATE_PASSWORD = "misc/change-password"

    const val PROVIDERS = "service/providers"
    const val SERVICE_SEARCH = "service/search"
    const val SERVICES_NEW = "service/services"
    const val PARAMETERS = "service/parameters"
    const val CATEGORIES = "service/categories"

    const val UPDATE_PROFILE = "misc/update-profile"
    const val GET_DEPOSITS = "visa/list-wallet"


    const val SCHEDULE_INVOICE = "schedule/add"
    const val SCHEDULE_INQUIRE = "schedule/check"
    const val SCHEDULE_LIST = "schedule/list"
    const val IMAGE_ADS = "news/all"
    const val USER_POINTS = "points/get-points"
    const val REPLACE_POINTS = "points/redeem-points"

    const val WALLETS = "wallet/all"

    const val TRANSACTIONS = "service/transactions"

    const val TRANSACTION_DETAILS = "service/show-transaction/{TRANSACTION_ID}"

    const val INQUIRY = "service/inquiry"

    const val PAYMENT = "service/payment"
    const val VISA_WALLET = "visa/check-complete"

    const val TOTAL_AMOUNT = "service/total-amount"

    const val START_SESSION = "newvisa/start-session-wallet"

    const val TOTAL_XPAY = "newvisa/get-amount-wallet"


    const val CANCEL_SERVICE = "service/cancel"

    const val CHECK_INTEGRATION_PROVIDER_STATUS =
        "service/check-integration-provider-status"
}