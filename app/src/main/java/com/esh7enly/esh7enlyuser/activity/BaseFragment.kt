package com.esh7enly.esh7enlyuser.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.esh7enlyuser.BR
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.ProgressDialog
import javax.inject.Inject

abstract class BaseFragment<DB : ViewDataBinding, VM : ViewModel> : Fragment()
{
    val dialog by lazy {
        AppDialogMsg(requireActivity(),false)
    }

    var sharedHelper: SharedHelper? = null
        @Inject set

    val pDialog by lazy{
        ProgressDialog.createProgressDialog(requireContext())
    }

    protected abstract val viewModel: VM

    private var _binding: DB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, getLayoutResID(), container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        doDataBinding()
        init()
    }

    @LayoutRes
    abstract fun getLayoutResID(): Int

    private fun doDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner

        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()

    }

    abstract fun init()

}