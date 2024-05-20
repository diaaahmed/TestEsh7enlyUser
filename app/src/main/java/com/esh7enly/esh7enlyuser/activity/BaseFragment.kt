package com.esh7enly.esh7enlyuser.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Connectivity
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.ProgressDialog
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
        ProgressDialog.createProgressDialog(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Language.setLanguageNew(requireActivity(), Constants.LANG)

    }
}