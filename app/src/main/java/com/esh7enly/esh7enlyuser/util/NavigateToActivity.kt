package com.esh7enly.esh7enlyuser.util

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.esh7enly.domain.entity.userservices.Category
import com.esh7enly.esh7enlyuser.activity.*

class NavigateToActivity
{
    companion object{

        fun navigateToMainActivity(activity:Activity)
        {
            val toMain = Intent(activity,MainActivity::class.java)
            activity.startActivity(toMain)
            activity.finish()
        }

        fun navigateToPhoneActivity(activity: Activity)
        {
            val toPhoneActivity = Intent(activity,PhoneActivity::class.java)
            activity.startActivity(toPhoneActivity)
            activity.finish()

        }

        fun navigateToIntroCreateAccountActivity(activity:Activity)
        {
            val toCreateAccount = Intent(activity,IntroCreateAccount::class.java)
            activity.startActivity(toCreateAccount)
            activity.finish()
        }


        fun navigateToAddBalanceActivity(activity:Activity)
        {
            val toAddBalance = Intent(activity,AddBalance::class.java)
            activity.startActivity(toAddBalance)
        }


        fun navigateToTransactionsActivity(activity:Activity)
        {
            val toTransactions = Intent(activity,TransactionsActivity::class.java)
            activity.startActivity(toTransactions)
          //  activity.finish()
        }

        fun navigateToAccountSettingActivity(activity:Activity)
        {
            val accountSetting = Intent(activity,AccountSettingActivity::class.java)
            activity.startActivity(accountSetting)
           // activity.finish()
        }

        fun navigateToChangeUserNameActivity(activity:Activity)
        {
            val changeUserNameActivity = Intent(activity,ChangeUserNameActivity::class.java)
            activity.startActivity(changeUserNameActivity)
        }

        fun navigateToParametersActivity(activity:Activity,serviceType:Int,
                                         providerName:String,serviceId:Int,
        nameAr:String,nameEn:String,accept_amount_input:Int,price_type:Int,
                                         accept_check_integration_provider_status:Int,
                                         price_value:String,accept_change_paid_amount:Int,
                                         icon:String,type_code:String)
        {
            val parametersActivity = Intent(activity,ParametersActivity::class.java)

            parametersActivity.putExtra(Constants.SERVICE_TYPE,serviceType)
            parametersActivity.putExtra(Constants.PROVIDER_NAME,providerName)

            Constants.SERVICE_NAME_AR = nameAr
            Constants.SERVICE_NAME_EN = nameEn

            parametersActivity.putExtra(Constants.SERVICE_ID,serviceId)
            parametersActivity.putExtra(Constants.SERVICE_NAME_AR,nameAr)
            parametersActivity.putExtra(Constants.SERVICE_NAME_EN,nameEn)

            Log.d("TAG", "diaa service navigate again: english $nameEn arabic $nameAr")
            parametersActivity.putExtra(Constants.ACCEPT_AMOUNT_INPUT, accept_amount_input)
            parametersActivity.putExtra(Constants.PRICE_TYPE,price_type)
            parametersActivity.putExtra(Constants.ACCEPT_CHECK_INTEGRATION_PROVIDER_STATUS,accept_check_integration_provider_status)
            parametersActivity.putExtra(Constants.PRICE_VALUE,price_value)
            parametersActivity.putExtra(Constants.ACCEPT_AMOUNT_CHANGE,accept_change_paid_amount)
            parametersActivity.putExtra(Constants.IMAGE,icon)
            parametersActivity.putExtra(Constants.SERVICE_TYPE_CODE,type_code)

            activity.startActivity(parametersActivity)
        }

        fun navigateToChangePasswordActivity(activity:Activity)
        {
            val changePasswordActivity = Intent(activity,ChangePasswordActivity::class.java)
            activity.startActivity(changePasswordActivity)
        }

        fun navigateToHomeActivity(activity:Activity)
        {
            val toMain = Intent(activity,HomeActivity::class.java)
            activity.startActivity(toMain)
            activity.finish()
        }

        fun navigateToProviderActivity(activity:Activity,category:Category)
        {
            val providerActivity = Intent(activity, ProviderActivity::class.java)
            providerActivity.putExtra(Constants.CATEGORY_ID, category.id)

            if (Constants.LANG == Constants.AR) {
                providerActivity.putExtra(Constants.CATEGORY_NAME, category.name_ar)
            } else {
                providerActivity.putExtra(Constants.CATEGORY_NAME, category.name_en)

            }
            activity.startActivity(providerActivity)
        }

    }
}