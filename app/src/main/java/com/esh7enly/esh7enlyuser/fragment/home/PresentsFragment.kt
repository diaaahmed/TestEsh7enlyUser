package com.esh7enly.esh7enlyuser.fragment.home

import android.annotation.SuppressLint
import android.util.Log

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.NetworkResult
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.FragmentPresentsBinding
import com.esh7enly.esh7enlyuser.util.showErrorDialogWithAction
import com.esh7enly.esh7enlyuser.viewModel.ServiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PresentsFragment : BaseFragment<FragmentPresentsBinding, ServiceViewModel>() {


    override val viewModel: ServiceViewModel by viewModels()

    override fun getLayoutResID() = R.layout.fragment_presents
    override fun init() {
        binding.presentsToolbar.title = resources.getString(R.string.presents)
        binding.btnReplacePoints.text = resources.getString(R.string.replace_points)

        getData()

        listenPoints()

        binding.btnReplacePoints.setOnClickListener {
            replaceUserPoints()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun listenPoints() {
        lifecycleScope.launch {
            viewModel.userPointsState.collect { result ->
                when (result) {
                    is NetworkResult.Error -> {

                        binding.userPoints.text = result.parseError()
                        Log.d("diaa", "listenPoints: error ${result.parseError()}")
                    }

                    is NetworkResult.Loading -> {
                        binding.userPoints.text =
                            "${resources.getString(R.string.user_points)} 0.00 ${
                                resources.getString(R.string.points)
                            }"

                    }

                    is NetworkResult.Success -> {
                        binding.userPoints.text =
                            "${resources.getString(R.string.user_points)} ${result.data} ${
                                resources.getString(R.string.points)
                            }"

                    }

                    null -> {
                        binding.userPoints.text =
                            "${resources.getString(R.string.user_points)} 0.0 ${
                                resources.getString(R.string.points)
                            }"

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        sharedHelper!!.getUserToken().let {
            viewModel.getUserPointsFlow(it)
        }
    }

    private fun replaceUserPoints() {
        pDialog.show()

        lifecycleScope.launch {
            viewModel.replaceUserPoints(sharedHelper?.getUserToken().toString(),
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        pDialog.cancel()

                        dialog.showSuccessDialog(
                            resources.getString(R.string.balance_added),
                            resources.getString(R.string.app__ok)
                        )
                        {
                            dialog.cancel()

                        }
                        dialog.show()
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        pDialog.cancel()

                        showErrorDialogWithAction(
                            activity = requireActivity(),
                            dialog = dialog,
                            msg = msg.toString(),
                            okTitle = resources.getString(R.string.app__ok),
                            code = code
                        )
                    }
                })
        }
    }
}