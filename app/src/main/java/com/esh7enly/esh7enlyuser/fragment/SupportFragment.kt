package com.esh7enly.esh7enlyuser.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.esh7enly.esh7enlyuser.BuildConfig
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.databinding.FragmentSupportBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language


class SupportFragment : BaseFragment() {

    private val ui by lazy{
        FragmentSupportBinding.inflate(layoutInflater)
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
    { super.onViewCreated(view, savedInstanceState)

        Language.setLanguageNew(requireActivity(), Constants.LANG)

        ui.btnSendMsg.text = resources.getString(R.string.send_message)
        ui.supportToolbar.title = resources.getString(R.string.support)
        ui.chatWhatsText.text = resources.getString(R.string.chatting)
        ui.callUsText.text = resources.getString(R.string.call_us)
        ui.emailMsg.hint = resources.getString(R.string.enter_message)
        ui.sendEmailText.text = resources.getString(R.string.send_email)

        ui.callUs.setOnClickListener { callSupport() }

        ui.chatWhatsapp.setOnClickListener { setClickToChat() }

        ui.sendEmail.setOnClickListener { checkSendEmailVisibility() }

        ui.btnSendMsg.setOnClickListener { sendEmail() }

    }

    private fun checkSendEmailVisibility()
    {
        if(ui.sendMessageLayout.visibility == View.GONE)
        {
            ui.sendMessageLayout.visibility = View.VISIBLE
        }
        else
        {
            ui.sendMessageLayout.visibility = View.GONE
        }
    }

    private fun sendEmail() {
        val esh7enlyEmail = BuildConfig.ESH7ENLY_EMAIL
        val subject = "Need support"
        val body = ui.emailMsg.text.toString()

        if(body.isEmpty())
        {
            ui.emailMsg.error = resources.getString(R.string.required)
        }
        else
        {
            val intent = Intent(Intent.ACTION_SEND)

            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(esh7enlyEmail))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, body)

            // set type of intent
            intent.type = "message/rfc822"

            // startActivity with intent with chooser as Email client using createChooser function
            startActivity(Intent.createChooser(intent, "Choose an Email client :"))
        }
    }

    private fun callSupport() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${resources.getString(R.string.hotline)}"))
        startActivity(intent)
    }

    private fun setClickToChat() {
        val url = "https://api.whatsapp.com/send?phone=${resources.getString(R.string.phone_number_call)}"
        try {
            val pm = requireActivity().packageManager
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            requireActivity().startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

}