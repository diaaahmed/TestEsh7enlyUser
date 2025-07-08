package com.esh7enly.esh7enlyuser.fragment.auth

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.click.OnResponseListener

import com.esh7enly.esh7enlyuser.databinding.FragmentOtpBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.showErrorDialogWithAction
import com.esh7enly.esh7enlyuser.viewModel.OtpViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OtpFragment : BaseFragment<FragmentOtpBinding, OtpViewModel>() {
    override val viewModel: OtpViewModel by viewModels()

    override fun getLayoutResID(): Int = R.layout.fragment_otp

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun init() {
        Language.setLanguageNew(requireActivity(), Constants.LANG)

        binding.verification.text = resources.getString(R.string.verification_code)
        binding.txtOtpMsg.text = resources.getString(R.string.otp_msg)
        binding.btnVerifyOtp.text = resources.getString(R.string.verify_otp)

        getIntentData()

        //To change focus as soon as 1 digit is entered in edit text, "shiftRequest" method is created.
        shiftRequest(binding.otp1, binding.otp2)
        shiftRequest(binding.otp2, binding.otp3)
        shiftRequest(binding.otp3, binding.otp4)
        shiftRequest(binding.otp4, binding.otp5)
        shiftRequest(binding.otp5, binding.otp6)
        shiftRequest(binding.otp6, binding.otp6)

        binding.btnVerifyOtp.setOnClickListener {
            verifyOtpRequest()

        }
    }

    private fun verifyOtpRequest() {

        val otp1 = binding.otp1.text.toString().trim()
        val otp2 = binding.otp2.text.toString().trim()
        val otp3 = binding.otp3.text.toString().trim()
        val otp4 = binding.otp4.text.toString().trim()
        val otp5 = binding.otp5.text.toString().trim()
        val otp6 = binding.otp6.text.toString().trim()

        if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty()
            || otp4.isEmpty() || otp5.isEmpty() || otp6.isEmpty()
        ) {
            dialog.showWarningDialog(
                resources.getString(R.string.error_message__blank_otp),
                resources.getString(R.string.app__ok)
            )
            dialog.show()

        } else {

            //  dialog.show()
            pDialog.show()

            val otpCode = otp1 + otp2 + otp3 + otp4 + otp5 + otp6

            val otpIntent = arguments?.getInt(Constants.OTP)

            if (otpIntent != 33) {
                viewModel.verifyForgetPassword(
                    viewModel.phoneNumber.toString(),
                    otpCode, object : OnResponseListener {
                        override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                            val bundle = bundleOf(
                                Constants.PHONE to viewModel.phoneNumber.toString(),
                                Constants.OTP to otpCode
                            )

                            findNavController().navigate(
                                R.id.action_otpFragment_to_forgetPasswordFragment, bundle
                            )
                        }

                        override fun onFailed(code: Int, msg: String?) {
                            showErrorDialogWithAction(
                                activity = requireActivity(),
                                dialog = dialog,
                                msg = msg.toString(),
                                okTitle = resources.getString(R.string.app__ok),
                                code = 0
                            )
                        }
                    }
                )

            } else {
                viewModel.verifyAccount(
                    viewModel.phoneNumber!!,
                    otpCode,
                    Constants.USER_KEY,
                    object : OnResponseListener {
                        override fun onSuccess(code: Int, msg: String?, obj: Any?) {

                            val bundle = bundleOf(Constants.USER_PHONE to viewModel.phoneNumber)

                            findNavController().navigate(
                                R.id.action_otpFragment_to_registerFragment,
                                bundle
                            )
                        }

                        override fun onFailed(code: Int, msg: String?) {
                            showErrorDialogWithAction(
                                activity = requireActivity(),
                                dialog = dialog,
                                msg = msg.toString(),
                                okTitle = resources.getString(R.string.app__ok),
                                code = 0
                            )
                        }
                    })
            }

        }
    }

    private fun getIntentData() {
        viewModel.phoneNumber = arguments?.getString(Constants.PHONE)
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