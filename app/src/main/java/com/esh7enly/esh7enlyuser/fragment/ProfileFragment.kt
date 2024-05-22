package com.esh7enly.esh7enlyuser.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.esh7enly.esh7enlyuser.BuildConfig
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.activity.PaytabsPolicy
import com.esh7enly.esh7enlyuser.databinding.FragmentProfileBinding
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.google.firebase.Firebase
import com.google.firebase.dynamiclinks.androidParameters
import com.google.firebase.dynamiclinks.dynamicLink
import com.google.firebase.dynamiclinks.dynamicLinks
import com.google.firebase.dynamiclinks.iosParameters
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

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        ui.accountSettingText.text = resources.getString(R.string.account_setting)
        ui.transactionsText.text = resources.getString(R.string.transactions)
        ui.shareAppText.text = resources.getString(R.string.share)
        ui.policyText.text = resources.getString(R.string.privacy_policy)
        ui.contactUsText.text = resources.getString(R.string.contact_us)
        ui.termsText.text = resources.getString(R.string.terms)
        ui.refundText.text = resources.getString(R.string.refund)
        ui.btnLogout.text = resources.getString(R.string.logout)

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

        ui.share.setOnClickListener {
           // createFirebaseDynamicLink()
            shareApp()
        }

        ui.accountSetting.setOnClickListener {
            NavigateToActivity.navigateToAccountSettingActivity(requireActivity())
        }
    }

    private fun createFirebaseDynamicLink()
    {
       // https://example.page.link?apn=com.esh7enly.esh7enlyuser&ibi=com.example.ios&link=https%3A%2F%2Fwww.example.com%2F


        // Create dynamic link
//                DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                        .setLink(Uri.parse("https://4-fresh.com/"))
//                        .setDomainUriPrefix("https://egfresh.page.link")
//                        // Open links with this app on Android
//                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                        // Open links with com.example.ios on iOS
//                        .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
//                        .buildDynamicLink();
//
//                Uri dynamicLinkUri = dynamicLink.getUri();
//                Log.d("more", "Long referal"+dynamicLinkUri);


        // https://egfresh.page.link?apn=com.egfresh.egfresh&ibi=com.example.ios&link=https%3A%2F%2F4-fresh.com%2F

        // https://example.page.link?apn=com.esh7enly.esh7enlyuser&ibi=com.example.ios&link=https%3A%2F%2Fwww.example.com%2F

        val manual_link = "https://esh7enly.page.link?" +
                "link=https://esh7enly.com=" + 10 +
                "&apn=" + requireActivity().packageName +
                "&st=" + "Esh7enly" +
                "&sd=" + "Earn money" +
                "&si=" + "https://www.blueappsoftware.com/logo-1.png"

        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("https://www.example.com/")
            domainUriPrefix = "https://esh7enly.page.link"
            // Open links with this app on Android
            androidParameters { }
            // Open links with com.example.ios on iOS
            iosParameters("com.esh7enly.ios") { }
        }

        val dynamicLinkUri = dynamicLink.uri
        Log.d("TAG", " diaa createFirebaseDynamicLink: ${dynamicLinkUri.toString()}")
        Log.d("TAG", " diaa createFirebaseDynamicLink: $manual_link")
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

    private fun openTransactionsActivity()
    {
        NavigateToActivity.navigateToTransactionsActivity(requireActivity())
    }
}