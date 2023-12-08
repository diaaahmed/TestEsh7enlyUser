package com.esh7enly.esh7enlyuser.activity

import android.app.ProgressDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Connectivity
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.viewModel.BalanceViewModel
import com.esh7enly.esh7enlyuser.viewModel.ServiceViewModel
import javax.inject.Inject

open class BaseFragment: Fragment()
{
    val serviceViewModel: ServiceViewModel by viewModels()
    val balanceViewModel: BalanceViewModel by viewModels()

    val dialog by lazy {
        AppDialogMsg(requireActivity(),false)
    }

    var sharedHelper: SharedHelper? = null
        @Inject set

    var connectivity: Connectivity? = null
        @Inject set

     val pDialog by lazy{
        ProgressDialog(requireActivity(), R.style.MyAlertDialogStyle)
    }

    override fun onStart() {
        super.onStart()

        pDialog.setMessage(
            Utils.getSpannableString(
                this@BaseFragment.requireActivity(),
                resources.getString(R.string.message__please_wait)
            )
        )
        pDialog.setCancelable(false)
    }
}