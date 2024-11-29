package com.esh7enly.esh7enlyuser.util;

import com.fawry.nfc.NFC.Shared.NFCConstants;


public class Constants {

    //region General

    public static NFCConstants.CardType nfcCard = NFCConstants.CardType.ELECT;

    //endregion

    // region user

    public static final int EXCEPTION_CODE = 400;
    public static final String OTP = "otp";
    public static final String PHONE = "phone";
    public static  String BILLING_ACCOUNT_CARD = "billing_account_card";
    public static  int VODAFONE_CASH_ID = 3968;

    public static final String USER_PHONE = "user_phone";
    public static  String USER_KEY = "USER_KEY";
    public static  String IMEI = "imei";

    //endregion


    //region ids

    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String PROVIDER_ID = "provider_id";
    public static final String PROVIDER_NAME = "provider_name";
    public static final String SERVICE_ID = "service_id";
    public static final String SERVICE_NAME = "service_name";
    public static  String SERVICE_NAME_AR = "service_name";
    public static  String SERVICE_NAME_EN = "service_name";
    public static final String SERVICE_TYPE_CODE = "service_type_code";


    //endregion

    //region REQUEST CODE AND RESULT CODE
    public static final int REQUEST_CODE__PERMISSION_CODE = 1016;
    //endregion

    //region Noti

    //endregion

    //region Params Types

    public static final int Number = 1;
    public static final int Char = 2;
    public static final int Date = 3; //(YYYY-MM-DD)
    public static final int textarea = 4;
    public static final int Select = 5;
    public static final int Radio = 6;


    //endregion

    // region SERVICE TYPE
    public static final int PAYMENT = 1;
    public static final int INQUIRY_PAYMENT = 2;
    public static final int PREPAID_CARD = 3;

    // endregion

    // region Display Params
    public static final String DISPLAY_FOR_ALL = "1";
    public static final String DISPLAY_FOR_INQUIRY = "2";
    public static final String DISPLAY_FOR_PAYMENT = "3";
    public static  int START_SESSION_ID = 0;
    public static  String TOTAL_AMOUNT_PAYTABS = "0";
    public static  String HASH_GENERATED = "";
    public static  String HASH_ID = "";

    // endregion

    // region Transaction Status

    public static final int PENDING = 1;
    public static final int DONE = 2;
    public static final int FAIL = 3;


    // endregion

    // region bulk status

    // endregion

    //region Settings

    public static final String AR = "ar";
    public static final String BILLING_ACCOUNT = "billing_account";


    //endregion

    public static  String LANG = "lang";
    public static  Boolean isSkip = true;
    public static  String FORGET_PASSWORD = "FORGET_PASSWORD";

    public static final String IMAGE = "image";

    //region Bank

    //endregion

    //region Wallet
    public static final String TRASACTION_ID = "transaction_id";

    //endregion

    //endregion

    public static final String SERVICE_TYPE = "service_type";
    public static final String ACCEPT_AMOUNT_INPUT = "accept_amount_input";
    public static final String PRICE_TYPE = "price_type";
    public static final String ACCEPT_CHECK_INTEGRATION_PROVIDER_STATUS = "accept_check_integration_provider_status";
    public static final String PRICE_VALUE = "price_value";
    public static final String ACCEPT_AMOUNT_CHANGE = "accept_amount_change";

    //endregion

    //region Error Codes
    public static final String CODE_HTTP_UNAUTHORIZED = "401";
    public static final int CODE_UNAUTH_NEW = 330001;

    //endregion
    public static final String INTERNAL_ID = "internal_id";

    //endregion
}
