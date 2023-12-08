package com.esh7enly.esh7enlyuser.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.esh7enly.esh7enlyuser.BuildConfig
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.FragmentSupportBinding


class SupportFragment : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    { super.onViewCreated(view, savedInstanceState)

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
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${resources.getString(R.string.phone_number_call)}"))
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