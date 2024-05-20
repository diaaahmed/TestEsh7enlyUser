package com.esh7enly.esh7enlyuser.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.NetworkResult
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.FragmentPresentsBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PresentsFragment : BaseFragment() {

    private val ui by lazy{
        FragmentPresentsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        return ui.root
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        ui.presentsToolbar.title = resources.getString(R.string.presents)
        ui.btnReplacePoints.text = resources.getString(R.string.replace_points)

        if(connectivity?.isConnected == true)
        {
            getData()
        }
        else
        {
            dialog.showErrorDialogWithAction(resources.getString(R.string.no_internet_error),
                resources.getString(R.string.app__ok))
            {
                dialog.cancel()
            }.show()
        }
        listenPoints()

        ui.btnReplacePoints.setOnClickListener {
            replaceUserPoints()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun listenPoints() {
        lifecycleScope.launch {
            serviceViewModel.userPointsState.collect{result->
                when(result)
                {
                    is NetworkResult.Error -> {
                        ui.userPoints.text = "Error ${result.message}"

                    }
                    is NetworkResult.Loading -> {
                        ui.userPoints.text = "${resources.getString(R.string.user_points)} 0.00 ${resources.getString(R.string.points)}"

                    }
                    is NetworkResult.Success -> {
                        ui.userPoints.text = "${resources.getString(R.string.user_points)} ${result.data} ${resources.getString(R.string.points)}"

                    }
                    null -> {
                        ui.userPoints.text = "${resources.getString(R.string.user_points)} 0.0 ${resources.getString(R.string.points)}"

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData()
    {
        sharedHelper!!.getUserToken().let {
            serviceViewModel.getUserPointsFlow(it)
        }
    }

    private fun replaceUserPoints()
    {
        pDialog.show()

        if(connectivity?.isConnected == true)
        {
            lifecycleScope.launch {
                serviceViewModel.replaceUserPoints(sharedHelper?.getUserToken().toString(),
                    object : OnResponseListener {
                        override fun onSuccess(code: Int, msg: String?, obj: Any?)
                        {
                            pDialog.cancel()

                            dialog.showSuccessDialog(resources.getString(R.string.balance_added),resources.getString(R.string.app__ok))
                            {
                                dialog.cancel()

                            }
                            dialog.show()
                        }

                        override fun onFailed(code: Int, msg: String?)
                        {
                            pDialog.cancel()

                            dialog.showErrorDialogWithAction(msg,
                                resources.getString(R.string.app__ok)
                            ) {
                                dialog.cancel()
                                if (code.toString() == Constants.CODE_UNAUTH || code.toString() == Constants.CODE_HTTP_UNAUTHORIZED) {
                                    NavigateToActivity.navigateToMainActivity(requireActivity())
                                }
                            }.show()
                        }
                    })
            }

        }
        else
        {
            pDialog.cancel()
            dialog.showErrorDialogWithAction(resources.getString(R.string.no_internet_error),
                resources.getString(R.string.app__ok))
            {
                dialog.cancel()
            }.show()
        }
    }

}