package com.esh7enly.esh7enlyuser.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esh7enly.esh7enlyuser.BuildConfig
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.activity.PaytabsPolicy
import com.esh7enly.esh7enlyuser.databinding.FragmentProfileBinding
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private val ui by lazy{
        FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        ui.userName.text = sharedHelper?.getStoreName()

        ui.userPhone.text = sharedHelper?.getUserName()

        ui.transactions.setOnClickListener { openTransactionsActivity() }

        ui.btnLogout.setOnClickListener{ logOut() }

        ui.privacy.setOnClickListener {
         //   openWebsite()
            val termsActivity = Intent(requireActivity(),PaytabsPolicy::class.java)
            termsActivity.putExtra("terms","policy")
            requireActivity().startActivity(termsActivity)
        }

        ui.terms.setOnClickListener {
            val termsActivity = Intent(requireActivity(),PaytabsPolicy::class.java)
            termsActivity.putExtra("terms","terms")
            requireActivity().startActivity(termsActivity)
        }

        ui.refund.setOnClickListener {
            val termsActivity = Intent(requireActivity(),PaytabsPolicy::class.java)
            termsActivity.putExtra("terms","refund")
            requireActivity().startActivity(termsActivity)
        }

        ui.help.setOnClickListener { makeCall() }

        ui.share.setOnClickListener { shareApp() }

        ui.accountSetting.setOnClickListener {
            NavigateToActivity.navigateToAccountSettingActivity(requireActivity())
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
            val shareMessage = "Recommend you this Esh7enly"
            """
            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
           
            """.trimIndent()

            //shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            //e.toString();
        }
    }

    private fun makeCall() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${resources.getString(R.string.hotline)}"))
        startActivity(intent)
    }

    private fun logOut() {
        sharedHelper?.setUserToken("")
        sharedHelper?.isRememberUser(false)
        NavigateToActivity.navigateToMainActivity(requireActivity())
    }

    private fun openWebsite()
    {
        val uri = Uri.parse(BuildConfig.PRIVACY_POLICY)
        val site = Intent(Intent.ACTION_VIEW,uri)
        startActivity(site)
    }

    private fun openTransactionsActivity() {
        NavigateToActivity.navigateToTransactionsActivity(requireActivity())
    }
}