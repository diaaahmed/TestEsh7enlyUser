package com.esh7enly.esh7enlyuser.fragment.auth

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.recreate
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.entity.loginresponse.LoginResponse
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.databinding.FragmentLoginBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.CrashlyticsUtils
import com.esh7enly.esh7enlyuser.util.CryptoData
import com.esh7enly.esh7enlyuser.util.EncryptionUtils
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.LoginException
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.showErrorDialogWithAction
import com.esh7enly.esh7enlyuser.viewModel.UserViewModel
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, UserViewModel>() {

    override val viewModel: UserViewModel by viewModels()

    private lateinit var appUpdateManager: AppUpdateManager

    private val updateType = AppUpdateType.IMMEDIATE

    override fun getLayoutResID() = R.layout.fragment_login

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun init() {

        val appLanguage = sharedHelper?.getAppLanguage()

        Constants.LANG = appLanguage

        appUpdateManager = AppUpdateManagerFactory.create(requireContext())

        checkForAppUpdate()

        Language.setLanguageNew(requireContext(), Constants.LANG)

        binding.textView.text = resources.getString(R.string.welcome_back)
        binding.checkBox.text = resources.getString(R.string.remember_me)
        binding.forgetPassword.text = resources.getString(R.string.forget_password)
        binding.btnLogin.text = resources.getString(R.string.login_btn)
        binding.language.text = resources.getString(R.string.language)
        binding.noAccount.text = resources.getString(R.string.no_account)
        binding.fillPhoneNumber.hint = resources.getString(R.string.enter_phone_number)
        binding.fillPassword.hint = resources.getString(R.string.enter_password)

        binding.forgetPassword.setOnClickListener {
            val bundle = bundleOf(
                Constants.FORGET_PASSWORD to Constants.FORGET_PASSWORD

            )
            findNavController().navigate(R.id.action_loginFragment_to_phoneFragment, bundle)
        }

        binding.language.setOnClickListener {
            showLanguage()
        }

        binding.noAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_phoneFragment)
        }


        binding.btnLogin.setOnClickListener {
            testEncrypt()
            login()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun testEncrypt() {

        val crypto_data = CryptoData()
        val key_test = crypto_data.getKey()
        Log.e("key_test", "key_test ${key_test.encoded}")

        val alias = "test"
        val actualData = "Akash"

        // getPublicKey will call generateKey internally and return the public key
        val publicKey = EncryptionUtils.getPublicKey(alias)
        // base64 encoded publicKey, decodePublicKey will decode the value
        val decodedPublicKey = EncryptionUtils.decodePublicKey(publicKey)
        //encrypt data with public key
        val encryptedData = EncryptionUtils.encrypt(actualData, decodedPublicKey)
        // decrypt data with private key
        val decryptedData = EncryptionUtils.decrypt(encryptedData, EncryptionUtils.getPrivateKey(alias))
        Log.e("diaa", "actualData $actualData")
        Log.e("diaa", "encryptedData $encryptedData")
        Log.e("diaa", "decryptedData $decryptedData")

    }

    override fun onResume() {
        super.onResume()
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() ==
                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {

                    if (isAdded) {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            updateType,
                            requireActivity(),
                            123
                        )
                    }

                }
            }
        }
    }

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->

            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.IMMEDIATE -> {

                    info.isImmediateUpdateAllowed
                }

                AppUpdateType.FLEXIBLE -> {
                    info.isFlexibleUpdateAllowed
                }

                else -> {
                    false
                }
            }

            if (isUpdateAvailable && isUpdateAllowed) {

                if (isAdded) {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateType,
                        requireActivity().parent,
                        123
                    )
                }

            }
        }
    }

    init {
        System.loadLibrary("esh7enlyuser")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun login() {

        if (connectivity?.isConnected == true) {

            val phoneNumber = binding.phoneNumber.text.toString().trim()

            val password = binding.password.text.toString().trim()

            phoneNumber.takeIf { it.isEmpty() }?.let {
                dialog.showWarningDialog(
                    resources.getString(R.string.error_message__blank_phone),
                    resources.getString(R.string.app__ok)
                )
                dialog.show()
            } ?: password.takeIf { it.isEmpty() }?.let {
                dialog.showWarningDialog(
                    resources.getString(R.string.error_message__blank_password),
                    resources.getString(R.string.app__ok)
                )
                dialog.show()
            } ?: phoneNumber.takeIf { it.length > 11 || it.length < 11 }?.let {
                dialog.showWarningDialog(
                    resources.getString(R.string.error_message__wrong_phone),
                    resources.getString(R.string.app__ok)
                )
                dialog.show()
            } ?: run {
                pDialog.show()
                getUserTokenFromFirebase()
            }

        } else {
            dialog.showErrorDialogWithAction(
                resources.getString(R.string.no_internet_error),
                resources.getString(R.string.app__ok)
            ) {
                dialog.cancel()
            }.show()
        }
    }

    private fun getUserTokenFromFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                getIMEI()
                userLogin(token)
            }
        }
    }

    private var imei = ""

    @SuppressLint("HardwareIds")
    private fun getIMEI() {
        imei = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun userLogin(token: String) {
        try {
            lifecycleScope.launch {

                viewModel.loginWithState(token, imei)

                viewModel.loginStateSharedFlow.collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            pDialog.cancel()
                            logAuthIssueToCrashlytics(it.message!!)

                            if (it.message!!.contains("<html")) {
                                showDialogWithAction(resources.getString(R.string.manyRequests))
                            } else {
                                showDialogWithAction(it.message!!)

                            }
                        }

                        is NetworkResult.Loading -> {
                        }

                        is NetworkResult.Success -> {
                            pDialog.cancel()
                            successLoginNavigateToHome(it.data!!)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            pDialog.cancel()
            logAuthIssueToCrashlytics(e.message.toString())
            showDialogWithAction(e.message.toString())
        }
    }

    private fun logAuthIssueToCrashlytics(msg: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to "$msg with ${binding.phoneNumber.text.toString()}",
            CrashlyticsUtils.LOGIN_PROVIDER to "Login with Phone and password",
        )
    }

    @SuppressLint("NewApi")
    private fun successLoginNavigateToHome(response: LoginResponse) {

        sharedHelper?.setUserToken(response.data.token)

        sharedHelper?.setStoreName(response.data.name)

        sharedHelper?.setUserEmail(response.data.email)
        sharedHelper?.isRememberUser(true)

        sharedHelper?.setUserName(response.data.name)

        sharedHelper?.setUserPhone(response.data.mobile)

        if (binding.checkBox.isChecked) {
            sharedHelper?.setRememberPassword(true)
            saveUserPassword()
        } else {
            sharedHelper?.setRememberPassword(false)
            removeUserPassword()
        }

        NavigateToActivity.navigateToHomeActivity(requireActivity())
    }

    private fun saveUserPassword() {

        viewModel.saveUserPassword()
    }

    private fun removeUserPassword() {
        viewModel.removeUserPassword()
    }

    @SuppressLint("HardwareIds")
    private fun showDialogWithAction(message: String) {
        showErrorDialogWithAction(
            activity = requireActivity(),
            dialog = dialog,
            msg = message,
            okTitle = resources.getString(R.string.app__ok),
            code = 0
        )
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private fun showLanguage() {
        val lang = sharedHelper?.getAppLanguage()

        val checkedItem = if (lang == "ar") {
            0
        } else {
            1
        }
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(resources.getString(R.string.chooseLanguage))
        dialog.setSingleChoiceItems(
            resources.getStringArray(R.array.data), checkedItem
        ) { dialogInterface, i ->
            when (i) {
                0 -> {
                    if (checkedItem == 0) {
                        return@setSingleChoiceItems
                    } else {
                        setLanguage("ar")
                    }
                }

                1 -> {
                    if (checkedItem == 1) {
                        return@setSingleChoiceItems
                    } else {
                        setLanguage("en")
                    }
                }
            }
            dialogInterface.dismiss()
        }

        val mDialog = dialog.create()
        mDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private fun setLanguage(language: String?) {
        val locale = Locale(language.toString())
        val dm = resources.displayMetrics
        // val conf = resources.configuration
        val conf = resources.configuration
        conf.setLocale(locale)
        //       conf.locale = locale
        resources.updateConfiguration(conf, dm)
        Constants.LANG = language
        sharedHelper?.setAppLanguage(language!!)
        recreate(requireActivity())

    }

}