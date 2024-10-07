package com.esh7enly.esh7enlyuser.fragment.auth

import android.os.Build
import android.text.TextUtils

import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.FragmentForgetPasswordBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.isValidPassword
import com.esh7enly.esh7enlyuser.util.showErrorDialogWithAction
import com.esh7enly.esh7enlyuser.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetPasswordFragment : BaseFragment<FragmentForgetPasswordBinding, UserViewModel>(),
    IToolbarTitle {

    override val viewModel: UserViewModel by viewModels()

    override fun getLayoutResID() = R.layout.fragment_forget_password

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun init() {
        Language.setLanguageNew(requireActivity(), Constants.LANG)

        binding.fillNewPassword.hint = resources.getString(R.string.enter_new_password)
        binding.fillConfirmPassword.hint = resources.getString(R.string.confirm_new_password)
        binding.userPasswordInstructions.text = resources.getString(R.string.password_instructions)
        binding.btnChangePassword.text = resources.getString(R.string.change_password)

        initToolBar()

        binding.btnChangePassword.setOnClickListener {
            createNewPassword()
        }
    }

    private fun createNewPassword()
    {
        val newPassword = binding.newPassword.text.toString().trim()
        val confirmNewPassword = binding.confirmNewPassword.text.toString().trim()

        if (TextUtils.isEmpty(newPassword)) {
            binding.newPassword.error = resources.getString(R.string.type_old_password)
        } else if (TextUtils.isEmpty(confirmNewPassword)) {
            binding.confirmNewPassword.error = resources.getString(R.string.type_new_password)
        } else if (newPassword != confirmNewPassword) {
            binding.confirmNewPassword.error =
                resources.getString(R.string.type_confirm_new_password)
            binding.newPassword.error = resources.getString(R.string.type_new_password)

        } else if (!isValidPassword(newPassword)) {
            binding.newPassword.error = "Not valid"
            binding.confirmNewPassword.error = "Not valid"
        } else {
            if (connectivity?.isConnected == true) {
                pDialog.show()

                sendRequestForgetPassword(newPassword, confirmNewPassword)
            } else {
                dialog.showWarningDialog(
                    resources.getString(R.string.error_message__blank_phone_number),
                    resources.getString(R.string.app__ok)
                )
                dialog.show()
            }

        }
    }

    private fun sendRequestForgetPassword(newPassword: String, confirmNewPassword: String)
    {
        val phone = arguments?.getString(Constants.PHONE)
        val otp = arguments?.getString(Constants.OTP)

        viewModel.createNewPassword(
            phone.toString(),
            newPassword,
            confirmNewPassword,
            Constants.USER_KEY,
            otp.toString(),
            object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    successNewPassword(msg)
                }

                override fun onFailed(code: Int, msg: String?) {

                    failedNewPassword(msg, code)
                }
            })
    }

    private fun successNewPassword(msg: String?) {
        pDialog.cancel()
        dialog.showSuccessDialog(msg, resources.getString(R.string.app__ok))
        {
            dialog.cancel()
            sharedHelper?.setUserToken("")
            findNavController().navigate(R.id.action_forgetPasswordFragment_to_loginFragment)

        }
        dialog.show()
    }

    private fun failedNewPassword(msg: String?, code: Int?)
    {
        pDialog.cancel()

        showErrorDialogWithAction(
            activity = requireActivity(),
            dialog = dialog,
            msg = msg.toString(),
            okTitle = resources.getString(R.string.app__ok),
            code = code!!
        )
    }

    override fun initToolBar() {
        binding.newPasswordToolbar.title = resources.getString(R.string.change_password)
        binding.newPasswordToolbar.setNavigationOnClickListener{
            findNavController().popBackStack()
        }
    }

}