package com.esh7enly.esh7enlyuser.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityOtpactivityBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Connectivity
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject

private const val TAG = "OTPActivity"

@AndroidEntryPoint
class OTPActivity : AppCompatActivity()
{

    private val ui by lazy {
        ActivityOtpactivityBinding.inflate(layoutInflater)
    }

    private val userViewModel: UserViewModel by viewModels()
    var connectivity: Connectivity? = null
        @Inject set

    private val alertDialog by lazy {
        AppDialogMsg(this, false)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        ui.verification.text = resources.getString(R.string.verification_code)
        ui.txtOtpMsg.text = resources.getString(R.string.otp_msg)
        ui.btnVerifyOtp.text = resources.getString(R.string.verify_otp)

        getIntentData()

        //To change focus as soon as 1 digit is entered in edit text, "shiftRequest" method is created.
        shiftRequest(ui.otp1, ui.otp2)
        shiftRequest(ui.otp2, ui.otp3)
        shiftRequest(ui.otp3, ui.otp4)
        shiftRequest(ui.otp4, ui.otp5)
        shiftRequest(ui.otp5, ui.otp6)
        shiftRequest(ui.otp6, ui.otp6)

        ui.btnVerifyOtp.setOnClickListener {

            if (connectivity?.isConnected == true) {
                val otp1 = ui.otp1.text.toString().trim()
                val otp2 = ui.otp2.text.toString().trim()
                val otp3 = ui.otp3.text.toString().trim()
                val otp4 = ui.otp4.text.toString().trim()
                val otp5 = ui.otp5.text.toString().trim()
                val otp6 = ui.otp6.text.toString().trim()


                if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty()
                    || otp4.isEmpty() || otp5.isEmpty() || otp6.isEmpty()
                ) {
                    alertDialog.showWarningDialog(
                        resources.getString(R.string.error_message__blank_otp),
                        resources.getString(R.string.app__ok)
                    )
                    alertDialog.show()
                } else {
                    val code = otp1 + otp2 + otp3 + otp4 + otp5 + otp6

                    val otpIntent = intent.getIntExtra("otp",0)
                    val phone = intent.getStringExtra("phone")

                    Log.d(TAG, "diaa otp intent: $otpIntent")

                    if(otpIntent != 0)
                    {
                        if(otpIntent.toString() == code)
                        {
                            // Go to change password
                            val updatePassword = Intent(this,ForgetPasswordActivity::class.java)
                            updatePassword.putExtra("phone",phone)
                            updatePassword.putExtra("otp",otpIntent.toString())
                            startActivity(updatePassword)
                            Log.d(TAG, "diaa otp done: ")
                        }
                        else
                        {
                            alertDialog.showErrorDialogWithAction(
                                "OTP not correct",
                                resources.getString(R.string.app__ok)
                            )
                            {
                                alertDialog.cancel()
                            }.show()

                            Log.d(TAG, "diaa otp failed: ")

                        }
                    }
                    else
                    {
                        userViewModel.verifyAccount(
                            userViewModel.phoneNumber!!,
                            code,
                            Constants.USER_KEY,
                            object : OnResponseListener {
                                override fun onSuccess(code: Int, msg: String?, obj: Any?)
                                {
                                    val signupActivity = Intent(this@OTPActivity,SignupActivity::class.java)
                                    signupActivity.putExtra(Constants.USER_PHONE,userViewModel.phoneNumber)
                                    startActivity(signupActivity)
                                    finish()
                                }

                                override fun onFailed(code: Int, msg: String?)
                                {
                                    alertDialog.showErrorDialogWithAction(
                                        msg,
                                        resources.getString(R.string.app__ok)
                                    )
                                    {
                                        alertDialog.cancel()
                                    }.show()
                                }
                            })
                    }

                }
            } else {

                alertDialog.showWarningDialog(
                    resources.getString(R.string.no_internet_error),
                    resources.getString(R.string.app__ok)
                )
                alertDialog.show()

            }

        }

    }

    private fun getIntentData() {
        userViewModel.phoneNumber = intent.getStringExtra(Constants.USER_ID)
        userViewModel.password = intent.getStringExtra(Constants.USER_PASSWORD)
        userViewModel.imei = intent.getStringExtra(Constants.IMEI)
    }

    private fun shiftRequest(from: EditText, to: EditText) {
        from.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val otp1 = from.text.toString()
                if (otp1.isNotEmpty()) {
                    from.clearFocus()
                    to.requestFocus()
                    to.isCursorVisible = true
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }
}