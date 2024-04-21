package com.esh7enly.esh7enlyuser.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.esh7enly.domain.entity.forgetpasswordotp.Data
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityPhoneBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneActivity : BaseActivity()
{
    private val ui by lazy{
        ActivityPhoneBinding.inflate(layoutInflater)
    }

    private val alertDialog by lazy {
        AppDialogMsg(this,false)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        ui.phoneNumberMsg.text = resources.getString(R.string.phone_number_msg)
        ui.btnSendOTP.text = resources.getString(R.string.send_otp)
        ui.fillPhone.hint = resources.getString(R.string.enter_phone_number)

        Language.setLanguageNew(this, Constants.LANG)


        ui.btnSendOTP.setOnClickListener{

            if(connectivity?.isConnected == true)
            {
                pDialog.show()

                val userPhoneNumber = ui.userPhoneNumber.text.toString().trim()

                val otp = Intent(this, OTPActivity::class.java)

                otp.putExtra(Constants.USER_ID,userPhoneNumber)

                if(userPhoneNumber.isEmpty() || userPhoneNumber.length < 11 || userPhoneNumber.length > 11)
                {
                    pDialog.cancel()
                    alertDialog.showWarningDialog(resources.getString(R.string.error_message__blank_phone_number),
                        resources.getString(R.string.app__ok))
                    alertDialog.show()
                }
                else
                {
                    pDialog.cancel()

                    val forgetPassword = intent.getStringExtra(Constants.FORGET_PASSWORD)

                    if(forgetPassword != null)
                    {
                        userViewModel.forgetPasswordSendOTP(userPhoneNumber,
                            object : OnResponseListener {
                                override fun onSuccess(code: Int, msg: String?, obj: Any?)
                                {
                                    val response = obj as Data
                                    otp.putExtra("otp",response.otp)
                                    otp.putExtra("phone",userPhoneNumber)
                                    startActivity(otp)

                                }

                                override fun onFailed(code: Int, msg: String?)
                                {
                                    alertDialog.showErrorDialogWithAction(msg,resources.getString(R.string.app__ok))
                                    {
                                        alertDialog.cancel()
                                    }.show()
                                }
                            })
                    }
                    else
                    {
                        userViewModel.sendOtp(userPhoneNumber, object : OnResponseListener
                        {
                            override fun onSuccess(code: Int, msg: String?, obj: Any?)
                            {
                                startActivity(otp)
                            }

                            override fun onFailed(code: Int, msg: String?)
                            {
                                alertDialog.showErrorDialogWithAction(msg,resources.getString(R.string.app__ok))
                                {
                                    alertDialog.cancel()
                                }.show()
                            }
                        })
                    }
                }
            }
            else
            {
                alertDialog.showErrorDialogWithAction(resources.getString(R.string.no_internet_error),
                    resources.getString(R.string.app__ok))
                {
                    alertDialog.cancel()
                }.show()
            }
        }
    }
}