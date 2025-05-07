package com.esh7enly.esh7enlyuser.util

import android.app.Activity
import android.content.Intent
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.categoriesNew.CategoryData
import com.esh7enly.domain.entity.searchresponse.SearchData
import com.esh7enly.domain.entity.servicesNew.ServiceData
import com.esh7enly.esh7enlyuser.activity.*
import com.esh7enly.esh7enlyuser.util.Constants.PROVIDER_NAME
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_AMOUNT
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_CHARGE
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_ICON
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_MODEL
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_NAME
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_PAID_AMOUNT
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_TOTAL_AMOUNT
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_TO_PARAMETER_MODEL

class NavigateToActivity
{
    companion object{

        fun navigateToAuthActivity(activity:Activity) {
            val toMain = Intent(activity,AuthActivity::class.java)
            activity.startActivity(toMain)
            activity.finish()
        }

        fun navigateToIntroCreateAccountActivity(activity:Activity) {
            val toCreateAccount = Intent(activity,IntroCreateAccount::class.java)
            activity.startActivity(toCreateAccount)
            activity.finish()
        }

        fun navigateToTransactionsActivity(activity:Activity) {
            val toTransactions = Intent(activity,TransactionsActivity::class.java)
            activity.startActivity(toTransactions)
          //  activity.finish()
        }

        fun navigateToAccountSettingActivity(activity:Activity) {
            val accountSetting = Intent(activity,AccountSettingActivity::class.java)
            activity.startActivity(accountSetting)
           // activity.finish()
        }

        fun navigateToChangeUserNameActivity(activity:Activity) {
            val changeUserNameActivity = Intent(activity,ChangeUserNameActivity::class.java)
            activity.startActivity(changeUserNameActivity)
        }

        fun navigateToParametersPayActivity(
            activity:Activity,amount:String,totalAmount:String,
            serviceCharge:String,
            serviceName: String,
            providerName: String,
            serviceIcon:String,
            paidAmount: String,
            totalAmountPojoModel: TotalAmountPojoModel,
        )
        {
            val intent = Intent(activity,PrepaidCardActivity::class.java)

            intent.putExtra(SERVICE_AMOUNT,amount)
            intent.putExtra(SERVICE_TOTAL_AMOUNT,totalAmount)
            intent.putExtra(SERVICE_PAID_AMOUNT,paidAmount)
            intent.putExtra(SERVICE_MODEL, totalAmountPojoModel)
            intent.putExtra(SERVICE_CHARGE,serviceCharge)
            intent.putExtra(SERVICE_NAME,serviceName)
            intent.putExtra(PROVIDER_NAME,providerName)
            intent.putExtra(SERVICE_ICON,serviceIcon)
            activity.startActivity(intent)
        }

        fun navigateToParametersActivityFromSearch(activity:Activity,
                                         providerName:String,
                                                   searchData:SearchData
        )
        {
            val parametersActivity = Intent(activity,ParametersActivity::class.java)

            parametersActivity.putExtra(Constants.SERVICE_TYPE,searchData.type)
            parametersActivity.putExtra(PROVIDER_NAME,providerName)

            Constants.SERVICE_NAME_AR = searchData.name_ar
            Constants.SERVICE_NAME_EN = searchData.name_en

            parametersActivity.putExtra(Constants.SERVICE_ID,searchData.id)
            parametersActivity.putExtra(Constants.SERVICE_NAME_AR,searchData.name_ar)
            parametersActivity.putExtra(Constants.SERVICE_NAME_EN,searchData.name_en)

            parametersActivity.putExtra(Constants.ACCEPT_AMOUNT_INPUT, searchData.accept_amount_input)
            parametersActivity.putExtra(Constants.PRICE_TYPE,searchData.price_type)
            parametersActivity.putExtra(Constants.ACCEPT_CHECK_INTEGRATION_PROVIDER_STATUS,searchData.accept_check_integration_provider_status)
            parametersActivity.putExtra(Constants.PRICE_VALUE,searchData.price_value)
            parametersActivity.putExtra(Constants.ACCEPT_AMOUNT_CHANGE,searchData.accept_change_paid_amount)
            parametersActivity.putExtra(Constants.IMAGE,searchData.icon)
            parametersActivity.putExtra(Constants.SERVICE_TYPE_CODE,searchData.type_code)

            activity.startActivity(parametersActivity)
        }

        fun navigateToParametersActivity(activity:Activity,
                                         providerName:String,
                                         service:ServiceData
        )
        {
            val parametersActivity = Intent(activity,ParametersActivity::class.java)
            parametersActivity.putExtra(PROVIDER_NAME,providerName)
            parametersActivity.putExtra(SERVICE_TO_PARAMETER_MODEL,service)
            Constants.SERVICE_NAME_AR = service.nameAr
            Constants.SERVICE_NAME_EN = service.nameEn
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

        fun navigateToProviderActivity(activity:Activity,category:CategoryData)
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