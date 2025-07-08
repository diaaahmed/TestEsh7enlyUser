package com.esh7enly.esh7enlyuser.fragment.auth

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.databinding.FragmentRegisterBinding
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.isValidEmailId
import com.esh7enly.esh7enlyuser.util.isValidPassword
import com.esh7enly.esh7enlyuser.viewModel.CreateAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding, CreateAccountViewModel>() {

    override val viewModel: CreateAccountViewModel by viewModels()

    override fun getLayoutResID() = R.layout.fragment_register

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun init() {
        Language.setLanguageNew(requireActivity(), Constants.LANG)

        binding.txtRegister.text = resources.getString(R.string.register)
        binding.haveAccount.text = resources.getString(R.string.have_account)
        binding.btnSignup.text = resources.getString(R.string.signUp)
        binding.checkBox.text = resources.getString(R.string.accept_privacy)
        binding.fillFirstName.hint = resources.getString(R.string.first_name)
        binding.fillLastName.hint = resources.getString(R.string.last_name)
        binding.fillPassword.hint = resources.getString(R.string.your_password)
        binding.fillConfirmPassword.hint = resources.getString(R.string.confirm_password)
        binding.fillEmail.hint = resources.getString(R.string.email)

        getIntentData()

        binding.haveAccount.setOnClickListener {
            // Navigate to Login fragment
            findNavController().navigateUp()
        }

        binding.btnSignup.setOnClickListener {
            pDialog.show()
            createAccount()
        }

    }

    private fun createAccount() {
        val firstName = binding.userFirstName.text.toString().trim()
        val lastName = binding.userLastName.text.toString().trim()
        val password = binding.userPassword.text.toString().trim()
        val confirmPassword = binding.confirmPassword.text.toString().trim()
        val email = binding.userEmail.text.toString()

        pDialog.cancel()

        if (firstName.isEmpty()) {
            dialog.showWarningDialog(
                resources.getString(R.string.error_message__blank_first_name),
                resources.getString(R.string.app__ok)
            )
            dialog.show()
        } else if (lastName.isEmpty()) {
            dialog.showWarningDialog(
                resources.getString(R.string.error_message__blank_last_name),
                resources.getString(R.string.app__ok)
            )
            dialog.show()
        } else if (password.isEmpty() || confirmPassword.isEmpty()) {
            dialog.showWarningDialog(
                resources.getString(R.string.error_message__blank_password),
                resources.getString(R.string.app__ok)
            )
            dialog.show()
        } else if (password != confirmPassword) {

            dialog.showWarningDialog(
                resources.getString(R.string.password_not_match_confirm_password),
                resources.getString(R.string.app__ok)
            )
            dialog.show()
        } else if (!binding.checkBox.isChecked) {
            binding.checkBox.error = getString(R.string.required)
        } else if (!isValidPassword(password)) {
            dialog.showWarningDialog(
                getString(R.string.password_is_not_valid_please_use_aa1),
                resources.getString(R.string.app__ok)
            )
            dialog.show()
        } else if (!isValidEmailId(email)) {
            dialog.showWarningDialog(
                getString(R.string.email_is_not_valid),
                resources.getString(R.string.app__ok)
            )
            dialog.show()
        } else {
            viewModel.registerNewAccount(
                viewModel.phoneNumber.toString(),
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        dialog.showSuccessDialog(msg, resources.getString(R.string.app__ok))
                        {
                            dialog.cancel()
                            lifecycleScope.launch {
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            }
                        }
                        dialog.show()
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        dialog.showWarningDialog(
                            msg,
                            resources.getString(R.string.app__ok)
                        )
                        dialog.show()
                    }
                })
        }

    }

    private fun getIntentData() {
        viewModel.phoneNumber = arguments?.getString(Constants.USER_PHONE)
    }

}