package com.esh7enly.esh7enlyuser.fragment.auth

import android.os.Build
import android.util.Log

import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esh7enly.domain.entity.forgetpasswordotp.Data
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.FragmentPhoneBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.showErrorDialogWithAction
import com.esh7enly.esh7enlyuser.viewModel.PhoneViewModel
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class PhoneFragment : BaseFragment<FragmentPhoneBinding, PhoneViewModel>() {

    override val viewModel: PhoneViewModel by viewModels()

    override fun getLayoutResID() = R.layout.fragment_phone

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun init() {
        Language.setLanguageNew(requireContext(), Constants.LANG)

        binding.phoneNumberMsg.text = resources.getString(R.string.phone_number_msg)
        binding.btnSendOTP.text = resources.getString(R.string.send_otp)
        binding.fillPhone.hint = resources.getString(R.string.enter_phone_number)

        binding.btnSendOTP.setOnClickListener {

            sendOtpClicked()
        }
    }

    private fun sendOtpClicked() {
        if (connectivity?.isConnected == true) {
            pDialog.show()
            Log.d("TAG", "diaa sendOtpClicked: ")

            arguments?.getString(Constants.FORGET_PASSWORD)?.let {
                forgetPasswordSendOtp()
            } ?: createAccountSendOtp()
        } else {
            showErrorDialogWithAction()
        }
    }

    private fun forgetPasswordSendOtp() {
        viewModel.forgetPasswordSendOTP(
            object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    pDialog.cancel()

                    val bundle = bundleOf(
                        Constants.OTP to 40,
                        Constants.PHONE to binding.userPhoneNumber.text.toString()
                    )

                    findNavController().navigate(
                        R.id.action_phoneFragment_to_otpFragment,
                        bundle)
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()

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


    private fun createAccountSendOtp()
    {
        viewModel.sendOtp(object : OnResponseListener {
            override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                pDialog.cancel()

                val bundle = bundleOf(
                    Constants.PHONE to binding.userPhoneNumber.text.toString(),
                    Constants.OTP to 33)

                findNavController().navigate(R.id.action_phoneFragment_to_otpFragment,bundle)

            }

            override fun onFailed(code: Int, msg: String?) {
                pDialog.cancel()

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


    private fun showErrorDialogWithAction() {

        showErrorDialogWithAction(
            activity = requireActivity(),
            dialog = dialog,
            msg = resources.getString(R.string.no_internet_error),
            okTitle = resources.getString(R.string.app__ok),
            code = 0
        )
    }

}